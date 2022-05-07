package team.hhu.chuangxiangxiudemo.utility;

import org.jetbrains.annotations.NotNull;
import org.springframework.util.DigestUtils;

/**
 * MD5生成器
 * 引用自网络
 */

public final class MD5Generator
{
    private MD5Generator(){}

    public static @NotNull String generateMD5(final byte[] data)
    {
        String md5= DigestUtils.md5DigestAsHex(data);
        return md5;
    }
}
