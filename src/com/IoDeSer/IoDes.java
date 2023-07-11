package com.IoDeSer;

import com.ClassExtensions.Extension;
import jdk.jshell.spi.ExecutionControl;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class IoDes
{
    final static Pattern ioFilePattern = Pattern.compile("^[|]((.|[\\n])*)[|]$");



    public static Object Read(String ioString, Class objectType) throws NoSuchMethodException, NoSuchFieldException, IllegalAccessException, ExecutionControl.NotImplementedException
    {
        Matcher matcher = ioFilePattern.matcher(ioString);
        matcher.find();
        ioString = matcher.group(1);

        if (objectType.isPrimitive() || Extension.isPrimitiveWrapper(objectType))
            return Extension.castToPrimitive(ioString, objectType);
        else if (objectType.isArray())
            return DesArray(ioString, objectType);
        else if(Iterable.class.isAssignableFrom(objectType))
            throw new ExecutionControl.NotImplementedException("Iterable collections are not yet supported."); //TODO
        else if(Map.class.isAssignableFrom(objectType))
            throw new ExecutionControl.NotImplementedException("Maps are not yet supported."); //TODO
        else
            return DesClass(ioString, objectType);
    }

    static Object DesArray(String ioString, Class objectType) throws NoSuchFieldException, NoSuchMethodException, IllegalAccessException, ExecutionControl.NotImplementedException
    {
        ioString = DeleteTabulator(ioString);
        String[] objects = ioString.split("\n\\+\n");
        Object obj;

        if (objects.length == 1)
        {
            if (ioString.isEmpty())
                objects = new String[0];
            else
                objects = new String[]{ioString};
        }


        obj = Array.newInstance(objectType.getComponentType(), objects.length);

        for (int i = 0; i < objects.length; i++)
        {
            Object el = Read(objects[i].trim(), objectType.getComponentType());
            Array.set(obj, i, el);
        }

        return obj;
    }

    static Object DesClass(String ioString, Class objectType) throws NoSuchFieldException, NoSuchMethodException, IllegalAccessException, ExecutionControl.NotImplementedException
    {
        Object obj;
        try
        {
            obj = objectType.getDeclaredConstructor().newInstance();
        } catch (NoSuchMethodException | IllegalAccessException err)
        {
            throw new NoSuchMethodException("Object of type *" + objectType + "* must have parameterless constructor.");
        } catch (InvocationTargetException err)
        {
            throw new NoSuchMethodException("Object of type *" + objectType + "* throws an error in parameterless constructor:\n" + err.getMessage());
        } catch (InstantiationException err)
        {
            throw new NoSuchMethodException("Object of type *" + objectType + "* can not be abstract class.");
        }


        ioString = DeleteTabulator(ioString);

        Field[] fields = objectType.getFields();
        String[] fieldsNames = IoFile.getProperFieldNames(fields);

        String[] lines = ioString.split("\n");

        for (int i = 0; i < lines.length; i++)
        {
            String[] assignment = lines[i].split("->");
            String variableName = assignment[0].trim();

            int propertyIndex = -1;
            for (int j = 0; j < fields.length; j++)
            {
                if (variableName.equals(fieldsNames[j]))
                {
                    propertyIndex = j;
                    break;
                }
            }

            if (propertyIndex == -1)
                throw new NoSuchFieldException(String.format("Object of type %s does not have property named %s.", objectType, variableName));

            Field FoundField = fields[propertyIndex];

            if (FoundField.getType().isPrimitive() || Extension.isPrimitiveWrapper(FoundField.getType()))
            {
                Object retVal = Read(assignment[1].trim(), FoundField.getType());
                try
                {
                    FoundField.set(obj, retVal);
                } catch (IllegalAccessException err)
                {
                    throw new IllegalAccessException("This class field *" + FoundField + "* can not be set.");
                }
            } else
            {
                StringBuilder newObject;
                i++;
                int newObjectStart = i;
                try
                {
                    do
                    {
                        i++;
                    } while (!lines[i].equals("|"));

                    int newObjectEnd = i;
                    newObject = new StringBuilder("|\n");

                    for (int j = newObjectStart; j < newObjectEnd; j++)
                    {
                        newObject.append(String.format("%s\n", lines[j]));
                    }
                    newObject.append("\n|");
                } catch (ArrayIndexOutOfBoundsException err)
                {
                    newObject = new StringBuilder("|\n\n|");
                }
                Object child = Read(newObject.toString().trim(), FoundField.getType());
                try
                {
                    FoundField.set(obj, child);
                } catch (IllegalAccessException err)
                {
                    throw new IllegalAccessException("This class field *" + FoundField + "* can not be set.");
                }
            }
        }
        return obj;
    }

    static String DeleteTabulator(String str)
    {
        StringBuilder ret = new StringBuilder();
        String[] lines = str.split("\n");
        for (String line : lines)
        {
            try
            {
                ret.append(String.format("%s\n", line.substring(1)));
            } catch (IndexOutOfBoundsException ignored)
            {
            }
        }
        ret = new StringBuilder(ret.toString().trim());
        return ret.toString();
    }
}
