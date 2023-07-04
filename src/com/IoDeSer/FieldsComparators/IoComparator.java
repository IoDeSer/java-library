package com.IoDeSer.FieldsComparators;

import com.IoDeSer.ItemsAnnotations.IoItemOrder;
import java.lang.reflect.Field;
import java.util.Comparator;

public class IoComparator implements Comparator<Field>
{
    @Override
    public int compare(Field o1, Field o2)
    {
        IoItemOrder orderAnnotation1 = o1.getAnnotation(IoItemOrder.class);
        IoItemOrder orderAnnotation2 = o2.getAnnotation(IoItemOrder.class);
        int val1 = orderAnnotation1==null?Integer.MAX_VALUE:orderAnnotation1.order();
        int val2 = orderAnnotation2==null?Integer.MAX_VALUE:orderAnnotation2.order();
        if(val1>val2){
            return 1;
        }else if(val1<val2){
            return -1;
        }else{
            return 0;
        }
    }
}
