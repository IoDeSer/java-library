package com.Exec;

import java.util.Arrays;
import java.util.List;

public class Test2
{


    //public List<Integer> list;
    public float[] arr;



    public Test2()
    {
        arr=new float[]{};
    }

    public Test2(float[] arr)
    {
        //this.list = list;
        this.arr = arr;
    }

    @Override
    public String toString()
    {
        return "Test2{" +
                "arr=" + Arrays.toString(arr) +
                '}';
    }
}
