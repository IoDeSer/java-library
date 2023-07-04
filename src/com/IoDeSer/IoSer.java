package com.IoDeSer;

import com.ClassExtensions.Extension;
import com.IoDeSer.FieldsComparators.FieldsComparator;
import com.IoDeSer.FieldsComparators.IoComparator;
import com.IoDeSer.ItemsAnnotations.IoItemIgnore;
import com.IoDeSer.ItemsAnnotations.IoItemsOrder;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

final class IoSer
{
    static String MakeShift(int number)
    {
        String shift = "";
        for (int i = 0; i < number; i++)
        {
            shift += "\t";
        }
        return shift;
    }


    public static String Write(Object obj, int shift) throws IllegalAccessException
    {
        if (obj == null)
        {
            throw new NullPointerException("The passed object or some of its components are null");
        }

        String returnStringIo = "";

        if (obj.getClass().isArray())
        {
            String arrayStringIo = "";

            for (int i = 0; i < Array.getLength(obj); i++)
            {
                arrayStringIo += MakeShift(shift + 1) + Write(Array.get(obj, i), shift + 1);
                if (i < Array.getLength(obj) - 1)
                    arrayStringIo += String.format("\n%s+\n", MakeShift(shift + 1));
            }

            returnStringIo += String.format("|\n%s\n%s|", arrayStringIo, MakeShift(shift));
        } else if (List.class.isAssignableFrom(obj.getClass()))
        {
            List o = (List) obj;
            String arrayStringIo = "";

            for (int i = 0; i < o.size(); i++)
            {
                arrayStringIo += MakeShift(shift + 1) + Write(o.get(i), shift + 1);
                if (i < o.size() - 1)
                    arrayStringIo += String.format("\n%s+\n", MakeShift(shift + 1));
            }
            returnStringIo += String.format("|\n%s\n%s|", arrayStringIo, MakeShift(shift));
        } else if (Extension.isPrimitiveWrapper(obj.getClass()))
        {
            returnStringIo += String.format("|%s|", obj.toString());
        } else//is class
        {
            Field[] fields = obj.getClass().getFields();
            IoItemsOrder itemsOrder = obj.getClass().getAnnotation(IoItemsOrder.class);
            if(itemsOrder==null)
                Arrays.sort(fields, new IoComparator());
            else{
                Arrays.sort(fields, new FieldsComparator(itemsOrder.order()));
            }

            String[] fieldsNames = IoFile.getProperFieldNames(fields);

            String classReturnIo = "";
            boolean isFirst = true;
            for (int i = 0; i < fields.length; i++)
            {
                if (!ignoreFiled(fields[i]))
                {
                    Object fieldValue = fields[i].get(obj);
                    if (fieldValue != null)
                    {
                        if (!isFirst)
                        {
                            classReturnIo += "\n";
                        }
                        isFirst = false;
                        classReturnIo += String.format("%s%s->%s", MakeShift(shift + 1), fieldsNames[i], Write(fieldValue, shift + 1));
                    }
                }
            }


            returnStringIo += String.format("|\n%s\n%s|", classReturnIo, MakeShift(shift));
        }

        return returnStringIo;
    }

    static boolean ignoreFiled(Field field)
    {
        boolean ret = false;

        IoItemIgnore ioIgnored = field.getAnnotation(IoItemIgnore.class);
        if (ioIgnored != null)
        {
            ret = true;
        }
        return ret;
    }
}
