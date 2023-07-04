package com.ClassExtensions;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

public abstract class Extension
{
    public static boolean isPrimitiveWrapper(Class<?> clazz)
    {
        return WRAPPER_TYPES.contains(clazz);
    }



    static final Set<Class<?>> WRAPPER_TYPES = getWrapperTypes();
    static Set<Class<?>> getWrapperTypes()
    {
        Set<Class<?>> ret = new HashSet<Class<?>>();
        ret.add(Boolean.class);
        ret.add(Character.class);
        ret.add(Byte.class);
        ret.add(Short.class);
        ret.add(Integer.class);
        ret.add(Long.class);
        ret.add(Float.class);
        ret.add(Double.class);
        ret.add(Void.class);
        ret.add(String.class);
        return ret;
    }

    public static Object castToPrimitive(String object_string, Type objectType)
    {
        if( Boolean.class == objectType || boolean.class==objectType ) return Boolean.parseBoolean( object_string );
        else if( Byte.class == objectType ||byte.class == objectType ) return Byte.parseByte( object_string );
        else if( Short.class == objectType ||short.class == objectType ) return Short.parseShort( object_string );
        else if( Integer.class == objectType ||int.class == objectType ) return Integer.parseInt( object_string );
        else if( Long.class == objectType ||long.class == objectType) return Long.parseLong( object_string );
        else if( Float.class == objectType ||float.class == objectType) return Float.parseFloat( object_string );
        else if( Double.class == objectType ||double.class == objectType) return Double.parseDouble( object_string );
        else return object_string;
    }
}
