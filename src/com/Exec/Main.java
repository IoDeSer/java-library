package com.IoDeSer;

import com.IoDeSer.IoFile;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Main
{

    public static void main(String[] args) throws IllegalAccessException, IOException
    {
        Test t = new Test("asdasd", 55, new int[]{1,4,-10,2});

        System.out.println(IoFile.WriteToString(t));
    }
}
