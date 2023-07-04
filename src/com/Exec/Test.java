package com.IoDeSer;

import com.IoDeSer.ItemsAnnotations.IoItemsOrder;
import com.IoDeSer.Ordering.ItemsOrder;

import java.util.Arrays;
import java.util.Iterator;

@IoItemsOrder(order = ItemsOrder.LONGEST_FIRST)
public class Test implements Iterable<Integer>
{
    public String aaaaaaaaaaaaaaa;

    public int ailosc;

    public Test(String aaaaaaaaaaaaaaa, int ailosc, int[] array)
    {
        this.aaaaaaaaaaaaaaa = aaaaaaaaaaaaaaa;
        this.ailosc = ailosc;
        this.array = array;
    }

    public int[] array;


    public Test()
    {
    }

    @Override
    public String toString()
    {
        return aaaaaaaaaaaaaaa + " " + ailosc + " " + Arrays.stream(array).sum();
    }

    @Override
    public Iterator<Integer> iterator()
    {
        return Arrays.stream(array).iterator();
    }
}
