package team.hhu.chuangxiangxiudemo.service;

import team.hhu.chuangxiangxiudemo.configuration.SourcePathConfig;

public class PathService
{
    public String generateImgPath(String fullFilename)
    {
        return SourcePathConfig.SRC_IMAGE_URL+"/"+fullFilename;
    }

    public String generateAvatarPath(String fullFilename)
    {
        return SourcePathConfig.SRC_AVATAR_URL+"/"+fullFilename;
    }
}
