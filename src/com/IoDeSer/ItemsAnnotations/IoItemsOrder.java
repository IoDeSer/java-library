package com.IoDeSer.ItemsAnnotations;

import com.IoDeSer.Ordering.ItemsOrder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface IoItemsOrder
{
    ItemsOrder order() default ItemsOrder.DEFAULT;
}
