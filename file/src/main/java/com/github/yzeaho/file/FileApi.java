package com.github.yzeaho.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 文件相关的api集合
 *
 * @author y
 */
public class FileApi {

    /**
     * 检查文件是否存在
     *
     * @param f 被检查的文件
     */
    public static void checkFile(File f) throws IOException {
        if (!f.exists()) {
            throw new IOException("this file \"" + f.getAbsolutePath() + "\" does not exist.");
        }
    }

    /**
     * 文件夹如果不存在，先创建， 如果已经存在并且不是文件夹则删除再创建文件夹
     *
     * @param dir 文件夹
     */
    public static void checkDir(File dir) throws IOException {
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IOException("Create folder(" + dir.getAbsolutePath() + ") failed.");
        } else if (dir.isFile() && (!dir.delete() || !dir.mkdirs())) {
            throw new IOException("Failed to delete the file or create folder(" + dir.getAbsolutePath() + ").");
        }
    }

    /**
     * 删除文件夹及文件夹下面的所以文件和子文件夹
     *
     * @param dir 文件夹
     */
    public static void deleteDir(File dir) {
        if (dir == null || !dir.exists() || !dir.isDirectory()) {
            return;
        }
        File[] fs = dir.listFiles();
        if (fs != null) {
            for (File file : fs) {
                if (file.isFile()) {
                    //noinspection ResultOfMethodCallIgnored
                    file.delete();
                } else if (file.isDirectory()) {
                    deleteDir(file);
                }
            }
        }
        //noinspection ResultOfMethodCallIgnored
        dir.delete();
    }

    /**
     * 剪切文件
     *
     * @param sourceFile 源文件
     * @param toFile     目标文件
     */
    public static void cut(File sourceFile, File toFile) throws IOException {
        InputStream in = null;
        OutputStream out = null;
        boolean r = false;
        try {
            in = new FileInputStream(sourceFile);
            out = new FileOutputStream(toFile);
            byte[] buffer = new byte[1024];
            int len = -1;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            out.flush();
            r = true;
        } finally {
            Closeables.close(in);
            Closeables.close(out);
            if (r) {
                //noinspection ResultOfMethodCallIgnored
                sourceFile.delete();
            }
        }
    }

    public static void copy(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int len = -1;
        while ((len = in.read(buffer)) != -1) {
            out.write(buffer, 0, len);
        }
        out.flush();
    }
}