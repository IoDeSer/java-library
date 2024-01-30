package com.Exec;

import java.util.HashMap;
import java.util.List;

public class Test
{
    //public Test2 lista_test;
    public int Prim1;
    public String String1;
    public HashMap<String, Test2> dict;


    public Test()
    {
    }

    public Test(int prim1, String string1,HashMap<String, Test2> dict)
    {
        //this.lista_test = lista_test;
        Prim1 = prim1;
        String1 = string1;
        this.dict = dict;
    }

    @Override
    public String toString()
    {
        return "Test{" +
                "Prim1=" + Prim1 +
                ", String1='" + String1 + '\'' +
                ", dict=" + dict +
                '}';
    }
}

