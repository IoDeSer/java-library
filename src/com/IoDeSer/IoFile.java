package com.IoDeSer;

import com.IoDeSer.ItemsAnnotations.IoItemName;
import jdk.jshell.spi.ExecutionControl;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
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

    public static <T> T ReadFromString(String ioString, Class<T> objType, Class[] generics) throws NoSuchFieldException, NoSuchMethodException, IllegalAccessException, ExecutionControl.NotImplementedException, InvocationTargetException, InstantiationException
    {
        @SuppressWarnings("unchecked")
        T r = (T)IoDes.Read(ioString, objType, generics);
        return r;
    }

    public static <T> T ReadFromString(String ioString, Class<T> objType) throws NoSuchFieldException, NoSuchMethodException, IllegalAccessException, ExecutionControl.NotImplementedException, InvocationTargetException, InstantiationException
    {
        @SuppressWarnings("unchecked")
        T r = (T)IoDes.Read(ioString, objType, new Class[0]);
        return r;
    }

    public static <T> T ReadFromFile(FileInputStream stream, Class<T> objType, Class[] generics) throws IOException, NoSuchFieldException, NoSuchMethodException, IllegalAccessException, ExecutionControl.NotImplementedException, InvocationTargetException, InstantiationException
    {
        @SuppressWarnings("unchecked")
        T r = (T)IoDes.Read(new String(stream.readAllBytes(), StandardCharsets.UTF_8), objType, generics);
        return r;
    }

    public static <T> T ReadFromFile(FileInputStream stream, Class<T> objType) throws IOException, NoSuchFieldException, NoSuchMethodException, IllegalAccessException, ExecutionControl.NotImplementedException, InvocationTargetException, InstantiationException
    {
        @SuppressWarnings("unchecked")
        T r = (T)IoDes.Read(new String(stream.readAllBytes(), StandardCharsets.UTF_8), objType,  new Class[0]);
        return r;
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
