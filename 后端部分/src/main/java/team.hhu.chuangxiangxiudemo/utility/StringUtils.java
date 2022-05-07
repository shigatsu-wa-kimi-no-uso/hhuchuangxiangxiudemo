package team.hhu.chuangxiangxiudemo.utility;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * 字符串实用工具
 * 数字字符串/整型统一转换为int的实用工具
 */

public final class StringUtils
{
    public static boolean isNumeric(String @NotNull ...args)
    {
        if (args.length==0) return false;
        for(String i : args)
        {

            if(!com.mysql.cj.util.StringUtils.isStrictlyNumeric(i))
                return false;
        }
        return true;
    }


    public static @NotNull Map<String,Integer> parseIntegers(Map<String,Object> map, String @NotNull [] keys)
    {
        Map<String,Integer> result=new HashMap<>();
        for(String key: keys)
        {
            Object obj=map.get(key);
            if(obj instanceof String)
            {
                if(isNumeric((String) obj))
                {
                    result.put(key,Integer.parseInt((String) obj));
                    continue;
                }
            }

            if(obj instanceof Integer)
            {
                result.put(key,(Integer) obj);
            }
        }
        return result;
    }
}
