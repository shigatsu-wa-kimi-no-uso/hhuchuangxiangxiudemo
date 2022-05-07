package team.hhu.chuangxiangxiudemo.article.service;

import org.jetbrains.annotations.NotNull;
import team.hhu.chuangxiangxiudemo.standards.ResultConstants;
import team.hhu.chuangxiangxiudemo.standards.OperationResult;
import team.hhu.chuangxiangxiudemo.utility.ImageTypeParser;
import team.hhu.chuangxiangxiudemo.utility.LogUtil;
import team.hhu.chuangxiangxiudemo.utility.MD5Generator;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public final class IOService
{
    protected OperationResult<Void> writeFile(String path, String fileName, byte[] binarySrc)
    {
        System.out.println(path);
        OperationResult<Void> result=new OperationResult<>();
        try
        {
            File file = new File(path, fileName);
            file.getParentFile().mkdirs();
            if (!file.exists())
            {
                if (!file.createNewFile())
                {
                    result.setStatus(ResultConstants.IO_UNDEFINED_ERROR);
                    return result;
                }
            }
            if(file.exists() && file.length()>1024)
            {
                result.setStatus(ResultConstants.OPERATION_OK);
                return result;
            }
            LogUtil.info("写入文件:" + file);
            if (file.canWrite())
            {
                FileOutputStream fostream = new FileOutputStream(file);
                fostream.write(binarySrc);
                fostream.close();
                result.setStatus(ResultConstants.OPERATION_OK);
                return result;
            }
            LogUtil.error("IO异常 文件不可写:" + file);
            result.setStatus(ResultConstants.IO_UNDEFINED_ERROR);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            result.setStatus(ResultConstants.IO_UNDEFINED_ERROR);
            return result;
        }
        return result;
    }


    @NotNull protected OperationResult<byte[]> readFile(String path, String fileName)
    {
        File file=new File(path,fileName);
        OperationResult<byte[]> result=new OperationResult<>();
        if(!file.exists())
        {
            result.setReturnValue(null);
            result.setStatus(ResultConstants.IO_FILE_NOT_FOUND);
            return result;
        }
        if(file.length()>=Integer.MAX_VALUE)
        {
            result.setReturnValue(null);
            result.setStatus(ResultConstants.IO_FILE_TOO_LARGE);
        }
        int flength=(int)file.length();
        byte[] binaryData=new byte[flength];
        if(file.canRead())
        {
            ByteArrayInputStream bistream=new ByteArrayInputStream(binaryData);
            int cnt=bistream.read(binaryData,0,flength);
            if(cnt==binaryData.length)
            {
                result.setReturnValue(binaryData);
                result.setStatus(ResultConstants.OPERATION_OK);
            }
            else
                result.setStatus(ResultConstants.IO_UNDEFINED_ERROR);
            return result;
        }
        result.setReturnValue(null);
        result.setStatus(ResultConstants.IO_UNDEFINED_ERROR);
        return result;
    }

    public OperationResult<String> storeIMG(byte[] data,String path) throws IOException
    {
        String md5= MD5Generator.generateMD5(data);
        IOService io=new IOService();
        String suffix=ImageTypeParser.parseImageType(data);
        OperationResult<String> result=new OperationResult<>();
        if(suffix==null)
        {
            result.setStatus(ResultConstants.IMAGE_FORMAT_NOT_SUPPORTED);
            return result;
        }
        String filename=md5+"."+suffix;
        OperationResult<Void> serviceResult=io.writeFile(path,filename,data);

        result.setStatus(serviceResult.getStatus());
        result.setReturnValue(filename);
        return result;
    }

}
