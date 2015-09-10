package com.nucleus.texture.android;

import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.nucleus.texturing.Image;
import com.nucleus.texturing.Image.ImageFormat;
import com.nucleus.texturing.ImageFactory;

public class AndroidImageFactory implements ImageFactory {

    @Override
    public Image createImage(String name, float scaleX, float scaleY) throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        Bitmap b = BitmapFactory.decodeStream(classLoader.getResourceAsStream(name));
        if (b == null) {
            throw new IOException("Could not load " + name);
        }
        if (scaleX != 1 || scaleY != 1) {
            int width = (int) (b.getWidth() * scaleX + 0.5f);
            int height = (int) (b.getHeight() * scaleY + 0.5f);
            if (height == 0) {
                height = 1;
            }
            if (width == 0) {
                width = 1;
            }
            Bitmap copy = Bitmap.createScaledBitmap(b, width, height, true);
            b = copy;
        }

        Image image = new Image(b.getWidth(), b.getHeight(), ImageFormat.RGBA);
        b.copyPixelsToBuffer(image.getBuffer().position(0));
        return image;
    }
}