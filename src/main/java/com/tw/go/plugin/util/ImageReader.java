package com.tw.go.plugin.util;

import com.tw.go.plugin.provider.github.GitHubProvider;
import org.apache.commons.io.IOUtils;
import org.brickred.socialauth.util.Base64;

import java.io.IOException;

public class ImageReader {

    public static String readImage(String path){
        try {
            return "data:image/png;base64," + Base64.encodeBytes(IOUtils.toByteArray(GitHubProvider.class.getClassLoader().getResourceAsStream(path)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
