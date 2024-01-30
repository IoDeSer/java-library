package com.IoDeSer;

import com.ClassExtensions.Extension;
import jdk.jshell.spi.ExecutionControl;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class MapTypes<K, V> {
    final Class<Map<K, V>> mapClass;

    MapTypes(Class<Map<K, V>> mapClass) {
        this.mapClass = mapClass;
    }

    public Type[] Try() {
        Type superclass = mapClass.getGenericSuperclass();
        if (superclass instanceof ParameterizedType) {
            return ((ParameterizedType) superclass).getActualTypeArguments();
        }
        return null;
    }
}

final class IoDes {
    final static Pattern ioFilePattern = Pattern.compile("^[|]((.|[\\n])*)[|]$");


    public static Object Read(String ioString, Class objectType, Class... generics) throws NoSuchMethodException, NoSuchFieldException, IllegalAccessException, ExecutionControl.NotImplementedException, InvocationTargetException, InstantiationException {
        Matcher matcher = ioFilePattern.matcher(ioString);
        matcher.find();
        ioString = matcher.group(1);

        if (objectType.isPrimitive() || Extension.isPrimitiveWrapper(objectType))
            return Extension.castToPrimitive(ioString, objectType);
        else if (objectType.isArray())
            return DesArray(ioString, objectType);
        else if (Iterable.class.isAssignableFrom(objectType))
            return DesIterable(ioString, objectType, generics);
        else if (Map.class.isAssignableFrom(objectType))
            return DesMap(ioString, objectType, generics);
        else
            return DesClass(ioString, objectType);
    }

    private static Object DesIterable(String ioString, Class<Iterable> objectType, Class... generics) throws ExecutionControl.NotImplementedException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        //throw new ExecutionControl.NotImplementedException("Iterable collections are not yet supported."); //TODO
        if (generics.length<1){
            throw new IllegalArgumentException("Generic type of Iterable was not specified. Try passing it via 'generics' argument.");
        }

        ioString = DeleteTabulator(ioString);
        String[] objects = ioString.split("\n\\+\n");
        if (objects.length == 1) {
            if (ioString.isEmpty())
                objects = new String[0];
            else
                objects = new String[]{ioString};
        }

        var temp = new ArrayList<>();
        for (int i=0;i<objects.length;i++){
            temp.add(IoDes.Read(objects[i].trim(), generics[0]));
        }

        var out_map = objectType.getDeclaredConstructor().newInstance();
        out_map = temp; // TODO: 30.01.2024 for other types inheriting from Iterable, not just List.

        return out_map;
    }

    private static Object DesMap(String ioString, Class<Map> objectType, Class... generics) throws ExecutionControl.NotImplementedException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        //throw new ExecutionControl.NotImplementedException("Maps are not yet supported.");
        if (generics.length<2){
            throw new IllegalArgumentException("Types of key and value in Map were not specified. Try passing them via 'generics' argument.");
        }

        ioString = DeleteTabulator(ioString);
        String[] objects = ioString.split("\n\\+\n");
        if (objects.length == 1) {
            if (ioString.isEmpty())
                objects = new String[0];
            else
                objects = new String[]{ioString};
        }

        var out_map = objectType.getDeclaredConstructor().newInstance();

        // TODO iam here | meaby works???
        for (int i = 0; i < objects.length; i++) {
            var p = _DesKeyValuePair(objects[i].trim(), generics[0], generics[1]);

            out_map.put(p[0], p[1]);
        }


        return out_map;
    }

    static Object[] _DesKeyValuePair(String ioString, Class keyType, Class valueType) throws ExecutionControl.NotImplementedException, NoSuchFieldException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {
        String current_ioString = DeleteTabulator(ioString);
        String[] pair = current_ioString.split("\n\\+\n");

        var k = IoDes.Read(pair[0], keyType);
        var v = IoDes.Read(pair[1], valueType);

        return new Object[]{k, v};
    }

    static Object DesArray(String ioString, Class objectType) throws NoSuchFieldException, NoSuchMethodException, IllegalAccessException, ExecutionControl.NotImplementedException, InvocationTargetException, InstantiationException {
        ioString = DeleteTabulator(ioString);
        String[] objects = ioString.split("\n\\+\n");
        Object obj;

        if (objects.length == 1) {
            if (ioString.isEmpty())
                objects = new String[0];
            else
                objects = new String[]{ioString};
        }


        obj = Array.newInstance(objectType.getComponentType(), objects.length);

        for (int i = 0; i < objects.length; i++) {
            Object el = Read(objects[i].trim(), objectType.getComponentType());
            Array.set(obj, i, el);
        }

        return obj;
    }

    static Object DesClass(String ioString, Class objectType) throws NoSuchFieldException, NoSuchMethodException, IllegalAccessException, ExecutionControl.NotImplementedException, InvocationTargetException, InstantiationException {
        // TODO: 30.01.2024 add generic classess support
        Object obj;
        try {
            obj = objectType.getDeclaredConstructor().newInstance();
        } catch (NoSuchMethodException | IllegalAccessException err) {
            throw new NoSuchMethodException("Object of type *" + objectType + "* must have parameterless constructor.");
        } catch (InvocationTargetException err) {
            throw new NoSuchMethodException("Object of type *" + objectType + "* throws an error in parameterless constructor:\n" + err.getMessage());
        } catch (InstantiationException err) {
            throw new NoSuchMethodException("Object of type *" + objectType + "* can not be abstract class.");
        }


        ioString = DeleteTabulator(ioString);

        Field[] fields = objectType.getFields();
        String[] fieldsNames = IoFile.getProperFieldNames(fields);

        String[] lines = ioString.split("\n");

        for (int i = 0; i < lines.length; i++) {
            String[] assignment = lines[i].split("->");
            String variableName = assignment[0].trim();
            Class[] generics = new Class[]{};

            int propertyIndex = -1;
            for (int j = 0; j < fields.length; j++) {
                if (variableName.equals(fieldsNames[j])) {
                    propertyIndex = j;
                    break;
                }
            }

            var f = fields[propertyIndex];
            var c = f.getGenericType();
            if (c instanceof ParameterizedType){
                var generic_types = ((ParameterizedType)c).getActualTypeArguments();
                generics=new Class[generic_types.length];
                for (int x = 0; x<generic_types.length;x++){
                    generics[i] = (Class<?>)generic_types[i];
                }
            }

            if (propertyIndex == -1)
                throw new NoSuchFieldException(String.format("Object of type %s does not have property named %s.", objectType, variableName));

            Field FoundField = fields[propertyIndex];

            if (FoundField.getType().isPrimitive() || Extension.isPrimitiveWrapper(FoundField.getType())) {
                Object retVal = Read(assignment[1].trim(), FoundField.getType());
                try {
                    FoundField.set(obj, retVal);
                } catch (IllegalAccessException err) {
                    throw new IllegalAccessException("This class field *" + FoundField + "* can not be set.");
                }
            } else {
                StringBuilder newObject;
                i++;
                int newObjectStart = i;
                try {
                    do {
                        i++;
                    } while (!lines[i].equals("|"));

                    int newObjectEnd = i;
                    newObject = new StringBuilder("|\n");

                    for (int j = newObjectStart; j < newObjectEnd; j++) {
                        newObject.append(String.format("%s\n", lines[j]));
                    }
                    newObject.append("\n|");
                } catch (ArrayIndexOutOfBoundsException err) {
                    newObject = new StringBuilder("|\n\n|");
                }
                Object child = Read(newObject.toString().trim(), FoundField.getType(), generics);
                try {
                    FoundField.set(obj, child);
                } catch (IllegalAccessException err) {
                    throw new IllegalAccessException("This class field *" + FoundField + "* can not be set.");
                }
            }
        }
        return obj;
    }

    static String DeleteTabulator(String str) {
        StringBuilder ret = new StringBuilder();
        String[] lines = str.split("\n");
        for (String line : lines) {
            try {
                ret.append(String.format("%s\n", line.substring(1)));
            } catch (IndexOutOfBoundsException ignored) {
            }
        }
        ret = new StringBuilder(ret.toString().trim());
        return ret.toString();
    }
}
