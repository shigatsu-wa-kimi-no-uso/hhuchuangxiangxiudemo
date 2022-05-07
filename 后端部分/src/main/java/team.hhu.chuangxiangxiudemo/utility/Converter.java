package team.hhu.chuangxiangxiudemo.utility;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * map-object转换器
 * @author XuJinchen
 */

public final class Converter
{
    private Converter(){}

    @Contract("null->null")
    public static <T> Map<String,Object> objectToMap(final T object) throws IllegalAccessException
    {
        Map<String,Object> map= new HashMap<>();
        if(object==null)
        {
            return null;
        }
        Field[] fields=object.getClass().getDeclaredFields();
        for (Field field : fields)
        {
            field.setAccessible(true);
            map.put(field.getName(), field.get(object));
        }
        return map;
    }

}
