package team.hhu.chuangxiangxiudemo.utility;

import java.util.Map;

/**
 * 图像文件类型识别工具
 * 基于简单的文件头辨别
 * @author XuJinchen
 */

public final class ImageTypeParser
{
    private final static Map<Integer,String> magicNumbers =Map.of(0xFFD8FF,"jpg",
            0x89504E47,"png",
            0x47494638,"gif",
            0x002A4949,"tif",
            0x424D,"bmp",
            0x5249,"webp");

    private ImageTypeParser(){}

    public static String parseImageType(final byte[] data)
    {
        if(data.length<4) return null;
        int header=0;
        for(int i=0;i<4;i++)
        {
            header ^= (0x000000ff) & data[i];
            if (magicNumbers.get(header) != null)
            {
                return magicNumbers.get(header);
            }
            header <<= 8;
        }
        return null;
    }
}
