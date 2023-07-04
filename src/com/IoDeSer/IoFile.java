package com.IoDeSer;

import com.IoDeSer.ItemsAnnotations.IoItemName;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;

public final class IoFile
{
    public static String WriteToString(Object obj) throws IllegalAccessException
    {
        return IoSer.Write(obj, 0);
    }

    public static void WriteToFile(Object obj, FileOutputStream stream) throws IllegalAccessException, IOException
    {
        stream.write(IoSer.Write(obj, 0).getBytes());
    }

    public static Object ReadFromString(String ioString, Class objectType) throws NoSuchFieldException,  NoSuchMethodException,  IllegalAccessException
    {
        return IoDes.Read(ioString, objectType);
    }

    public static Object ReadFromFile(FileInputStream stream, Class objectType) throws IOException, NoSuchFieldException,  NoSuchMethodException,  IllegalAccessException
    {
        return IoDes.Read(new String(stream.readAllBytes(), StandardCharsets.UTF_8), objectType);
    }

    static String[] getProperFieldNames(Field[] fields)
    {
        String[] fieldNames = new String[fields.length];
        int i = 0;
        for (var field : fields){
            IoItemName ioItemAnnotation = field.getAnnotation(IoItemName.class);
            if (ioItemAnnotation!=null && !ioItemAnnotation.customPropertyName().equals("")){
                fieldNames[i]=ioItemAnnotation.customPropertyName();
            }else{
                fieldNames[i]=field.getName();
            }

            i++;
        }
        return fieldNames;
    }
}
