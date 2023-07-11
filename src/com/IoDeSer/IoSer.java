package com.IoDeSer;

import com.ClassExtensions.Extension;
import com.IoDeSer.ItemsAnnotations.IoItemIgnore;
import com.IoDeSer.ItemsAnnotations.IoItemsOrder;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.nio.channels.MembershipKey;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

final class IoSer
{
    public static String Write(Object obj, int shift) throws IllegalAccessException
    {
        if (obj == null)
            throw new NullPointerException("The passed object or some of its components are null");

        if (obj.getClass().isArray())
            return SerArray(obj, shift);
        else if (Iterable.class.isAssignableFrom(obj.getClass()))
            return SerIterable(obj, shift);
        else if (Extension.isPrimitiveWrapper(obj.getClass()))
            return String.format("|%s|", obj);
        else if (Map.class.isAssignableFrom(obj.getClass()))
            return SerMap(obj, shift);
        else//is class
            return SerClass(obj, shift);
    }

    static String SerMap(Object obj, int shift) throws IllegalAccessException
    {
        StringBuilder sb = new StringBuilder();
        boolean isFirst = true;


        for(Map.Entry<?, ?> entry : ((Map<?, ?>)obj).entrySet())
        {
            if(!isFirst)
                sb.append("\n").append(MakeShift(shift + 1)).append("+\n");
            else
                isFirst=false;

            sb.append(MakeShift(shift+1)).append("|\n")
                    .append(MakeShift(shift+2))
                    .append(IoSer.Write(entry.getKey(), shift+2))
                    .append("\n")
                    .append(MakeShift(shift+2))
                    .append("+\n")
                    .append(MakeShift(shift+2))
                    .append(IoSer.Write(entry.getValue(), shift+2))
                    .append("\n")
                    .append(MakeShift(shift+1))
                    .append("|");
        }
        return String.format("|\n%s\n%s|", sb, MakeShift(shift));
    }

    static String SerArray(Object obj, int shift) throws IllegalAccessException
    {
        String arrayStringIo = "";

        for (int i = 0; i < Array.getLength(obj); i++)
        {
            arrayStringIo += MakeShift(shift + 1) + Write(Array.get(obj, i), shift + 1);
            if (i < Array.getLength(obj) - 1)
                arrayStringIo += String.format("\n%s+\n", MakeShift(shift + 1));
        }

        return String.format("|\n%s\n%s|", arrayStringIo, MakeShift(shift));
    }

    static String SerIterable(Object obj, int shift) throws IllegalAccessException
    {
        StringBuilder arrayStringIo = new StringBuilder();
        boolean isFirst = true;

        for (var oItem : (Iterable)obj){
            if(!isFirst)
                arrayStringIo.append("\n").append(MakeShift(shift + 1)).append("+\n");
            else
                isFirst=false;
            arrayStringIo.append(MakeShift(shift + 1)).append(IoSer.Write(oItem, shift + 1));

        }

        return String.format("|\n%s\n%s|", arrayStringIo.toString(), MakeShift(shift));
    }

    static String SerClass(Object obj, int shift) throws IllegalAccessException
    {
        Field[] fields = obj.getClass().getFields();
        IoItemsOrder itemsOrder = obj.getClass().getAnnotation(IoItemsOrder.class);
        if(itemsOrder==null)
            Arrays.sort(fields, new IoComparator());
        else{
            Arrays.sort(fields, new FieldsComparator(itemsOrder.order()));
        }

        String[] fieldsNames = IoFile.getProperFieldNames(fields);

        StringBuilder classReturnIo = new StringBuilder();
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
                        classReturnIo.append("\n");
                    }
                    isFirst = false;
                    classReturnIo.append(String.format("%s%s->%s", MakeShift(shift + 1), fieldsNames[i], Write(fieldValue, shift + 1)));
                }
            }
        }


        return String.format("|\n%s\n%s|", classReturnIo.toString(), MakeShift(shift));
    }

    static String MakeShift(int number)
    {
        return "\t".repeat(Math.max(0, number));
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
