package com.github.yzeaho.common;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 图片压缩
 * Created by y on 2016/9/16.
 */
public class BitmapCompress {

    private static final String TAG = "BitmapCompress";

    public static InputStream compress(ContentResolver resolver, Uri uri, int ratio, boolean inMemory) throws IOException {
        InputStream in = resolver.openInputStream(uri);
        if (in == null) {
            throw new IllegalArgumentException("the uri(" + uri + ") can not be opened");
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 1;
        options.inJustDecodeBounds = true;
        decode(in, options);
        options.inSampleSize = calculateInSampleSize(options, ratio);
        options.inJustDecodeBounds = false;
        in = resolver.openInputStream(uri);
        Bitmap bitmap = decode(in, options);
        if (inMemory) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            return new ByteArrayInputStream(out.toByteArray());
        } else {
            File file = File.createTempFile(System.currentTimeMillis() + "_" + ratio, null);
            OutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            return new FileInputStream(file);
        }
    }

    public static InputStream compress(ContentResolver resolver, Uri uri, int maxWidth, int maxHeight) throws IOException {
        InputStream in = resolver.openInputStream(uri);
        if (in == null) {
            throw new IllegalArgumentException("the uri(" + uri + ") can not be opened");
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 1;
        options.inJustDecodeBounds = true;
        decode(in, options);
        options.inSampleSize = computeSampleSize(options, Math.min(maxWidth, maxHeight), maxWidth * maxHeight);
        options.inJustDecodeBounds = false;
        in = resolver.openInputStream(uri);
        Bitmap bitmap = decode(in, options);
        File file = File.createTempFile(System.currentTimeMillis() + "_" + maxWidth + maxHeight, null);
        OutputStream out = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        return new FileInputStream(file);
    }

    private static Bitmap decode(InputStream in, BitmapFactory.Options options) throws IOException {
        Bitmap bitmap;
        try {
            bitmap = BitmapFactory.decodeStream(in, null, options);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
        return bitmap;
    }

    /**
     * ImageLoader的压缩比算法
     */
    private static int calculateInSampleSize(BitmapFactory.Options options, int ratio) {
        int width = options.outWidth;
        int height = options.outHeight;
        int destWidth = Math.round(width * (ratio / 100f));
        int destHeight = Math.round(height * (ratio / 100f));
        int inSampleSize = 1;
        if (width > destWidth || height > destHeight) {
            int halfWidth = width / 2;
            int halfHeight = height / 2;
            while ((halfWidth / inSampleSize) > destWidth && (halfHeight / inSampleSize) > destHeight) {
                inSampleSize *= 2;
            }
        }
        Log.i(TAG, String.format("calculateInSampleSize %s/%s/%s/%s/%s", width, height, destWidth, destHeight, inSampleSize));
        return inSampleSize;
    }

    /**
     * android系统的压缩比算法
     */
    private static int calculateInSampleSize2(BitmapFactory.Options options, int ratio) {
        int width = options.outWidth;
        int height = options.outHeight;
        int destWidth = Math.round(width * (ratio / 100f));
        int destHeight = Math.round(height * (ratio / 100f));
        return computeSampleSize(options, Math.min(destWidth, destHeight), destWidth * destHeight);
    }

    /*
     * Compute the sample size as a function of minSideLength
     * and maxNumOfPixels.
     * minSideLength is used to specify that minimal width or height of a
     * bitmap.
     * maxNumOfPixels is used to specify the maximal size in pixels that is
     * tolerable in terms of memory usage.
     *
     * The function returns a sample size based on the constraints.
     * Both size and minSideLength can be passed in as IImage.UNCONSTRAINED,
     * which indicates no care of the corresponding constraint.
     * The functions prefers returning a sample size that
     * generates a smaller bitmap, unless minSideLength = IImage.UNCONSTRAINED.
     *
     * Also, the function rounds up the sample size to a power of 2 or multiple
     * of 8 because BitmapFactory only honors sample size this way.
     * For example, BitmapFactory downsamples an image by 2 even though the
     * request is 3. So we round up the sample size to avoid OOM.
     */
    private static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }
        Log.i(TAG, String.format("computeSampleSize %s", roundedSize));
        return roundedSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;

        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));
        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }

        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }
}