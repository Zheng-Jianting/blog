package com.zhengjianting.markdown;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@RestController
public class PictureController {
     private final String PICTURE_DIR = "/root/.api/picture/";

    @GetMapping(value = "/picture", produces = MediaType.IMAGE_JPEG_VALUE)
    byte[] picture(String name) {
        byte[] data = null;

        try {
            File jpg = new File(PICTURE_DIR + name + ".jpg");
            File png = new File(PICTURE_DIR + name + ".png");
            File path = (jpg.exists() && jpg.isFile()) ? jpg : png;
            if (path.exists() && path.isFile()) {
                FileInputStream inputStream = new FileInputStream(path);
                data = new byte[inputStream.available()];
                inputStream.read(data, 0, inputStream.available());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }
}
