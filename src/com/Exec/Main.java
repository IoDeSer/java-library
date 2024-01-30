package com.Exec;

import com.IoDeSer.IoFile;
import jdk.jshell.spi.ExecutionControl;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class Main
{
    public static void main(String[] args) throws IllegalAccessException, ExecutionControl.NotImplementedException, NoSuchFieldException, NoSuchMethodException, InvocationTargetException, InstantiationException
    {
       /* var hash = new HashMap<String, Test2>();
        hash.put("true", new Test2(new float[]{1,4,2}));
        hash.put("false asdaert", new Test2(new float[]{0,-1.1235f}));


        Test t = new Test(-12332, "Woodwork", hash);
        var io = IoFile.WriteToString(t);
        System.out.println(io);

        var io_out = IoFile.ReadFromString(io, Test.class);
        System.out.println(io_out);*/

        var t = new ArrayList<Integer>();
        t.add(5);
        t.add(-2);
        t.add(54325);
        t.add(-4321);

        var t2 = new Test3(t);

        var io = IoFile.WriteToString(t2);
        System.out.println(io);
        System.out.println("===");

        var io_out = (Test3)IoFile.ReadFromString(io, Test3.class);
        System.out.println(io_out.arrayList);
    }
}
