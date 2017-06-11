package com.example.xinlv.tools;

import android.util.Log;

public abstract class ImageProcessing {
    /**
     * 4*4
     * yyyy
     * yyyy
     * yyyy
     * yyyy
     * uvuv
     * uvuv
     */
    private static int decodeYUV420SPtoRedSum(byte[] yuv420sp, int width, int height) {
        if (yuv420sp == null)
            return 0;
        final int frameSize = width * height;
        int sum = 0;
        for (int j = 0, yp = 0; j < height; j++) {
            //uvp为uv的起始下标，偏移量是排数目的一半*一排（width）
            int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
            for (int i = 0; i < width; i++, yp++) {
                int y = (0xff & ((int) yuv420sp[yp])) - 16;
                if (y < 0)
                    y = 0;
                if ((i & 1) == 0) {//0123只有偶数列uv分量减去10000000（2），奇数列扔为0，所以uvp可以+2
                    v = (0xff & yuv420sp[uvp++]) - 128;
                    u = (0xff & yuv420sp[uvp++]) - 128;
                }
                int y1192 = 1192 * y;
                int r = (y1192 + 1634 * v);
                int g = (y1192 - 833 * v - 400 * u);
                int b = (y1192 + 2066 * u);

                if (r < 0)
                    r = 0;
                else if (r > 262143)
                    r = 262143;
                if (g < 0)
                    g = 0;
                else if (g > 262143)
                    g = 262143;
                if (b < 0)
                    b = 0;
                else if (b > 262143)
                    b = 262143;//2的18次方-1

                int pixel = 0xff000000 | ((r << 6) & 0xff0000)
                        | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);//r取10~17位，g取10到17位，b取18到25位
                int red = (pixel >> 16) & 0xff;//取16到31位（0-31表示法）
                sum += red;
            }
        }
        return sum;
    }


    public static int decodeYUV420SPtoRedAvg(byte[] yuv420sp, int width,
                                             int height) {
        if (yuv420sp == null)
            return 0;
        final int frameSize = width * height;
        int sum = decodeYUV420SPtoRedSum(yuv420sp, width, height);
        int avg = sum / frameSize;
        Log.d("red", "red=" + avg);
        return avg;
    }
}