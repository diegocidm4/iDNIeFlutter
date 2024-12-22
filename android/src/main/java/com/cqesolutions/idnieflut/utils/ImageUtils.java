package com.cqesolutions.io.idniecap.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Base64;

import com.cqesolutions.io.idniecap.jj2000.J2kStreamDecoder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Diego on 05/11/2018.
 */
public class ImageUtils {

    public static String encodeToBase64(byte[] data)
    {
        String imageEncoded = null;
        try {
            J2kStreamDecoder j2k = new J2kStreamDecoder();
            ByteArrayInputStream bis = new ByteArrayInputStream(data);
            Bitmap loadedImage = j2k.decode(bis);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            loadedImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] b = stream.toByteArray();
            imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return  imageEncoded;
    }
}
