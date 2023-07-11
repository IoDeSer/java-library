package com.IoDeSer;

import com.IoDeSer.ItemsAnnotations.IoItemOrder;
import com.IoDeSer.Ordering.ItemsOrder;

import java.lang.reflect.Field;
import java.util.Comparator;

class FieldsComparator implements Comparator<Field>
{
    ItemsOrder itemOrder;
    public FieldsComparator(ItemsOrder order)
    {
        itemOrder = order;
    }

    @Override
    public int compare(Field o1, Field o2)
    {
        switch (itemOrder){
            case ALPHABETICAL:
                return o1.getName().compareTo(o2.getName());
            case ALPHABETICAL_REVERSE :
                return o2.getName().compareTo(o1.getName());
            case LONGEST_FIRST :
                int val1 = o2.getName().length();
                int val2 = o1.getName().length();
                if(val1>val2){
                    return 1;
                }else if(val1<val2){
                    return -1;
                }else{
                    return 0;
                }
            case SHORTEST_FIRST :
                int val3 = o1.getName().length();
                int val4 = o2.getName().length();
                if(val3>val4){
                    return 1;
                }else if(val3<val4){
                    return -1;
                }else{
                    return 0;
                }
            default:
                return 0;
        }
    }
}
