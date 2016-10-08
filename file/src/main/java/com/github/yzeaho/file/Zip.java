package com.github.yzeaho.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Zip {

    /**
     * 解压缩zip包
     *
     * @param zipFile zip包文件
     * @param toDir   解压缩后的目录
     */
    public static void unzip(File zipFile, File toDir) throws IOException {
        ZipInputStream in = null;
        try {
            FileApi.checkDir(toDir);
            in = new ZipInputStream(new FileInputStream(zipFile));
            ZipEntry entry;
            File file;
            byte[] buffer = new byte[1024];
            while ((entry = in.getNextEntry()) != null) {
                file = new File(toDir, entry.getName());
                if (entry.isDirectory()) {
                    // 文件夹不存在就创建
                    if (!file.exists()) {
                        //noinspection ResultOfMethodCallIgnored
                        file.mkdir();
                    } else if (file.isFile()) {
                        //noinspection ResultOfMethodCallIgnored
                        file.delete();
                        //noinspection ResultOfMethodCallIgnored
                        file.mkdir();
                    }
                } else {
                    if (!file.exists()) {
                        //noinspection ResultOfMethodCallIgnored
                        file.getParentFile().mkdirs();
                    } else if (file.isDirectory()) {
                        FileApi.deleteDir(file);
                    }
                    write2file(in, file, buffer);
                }
            }
        } finally {
            Closeables.close(in);
        }
    }

    /**
     * 解压缩zip包
     *
     * @param zipFile zip包文件
     * @param to      解压缩后的目录
     */
    public static void unzip(String zipFile, String to) throws IOException {
        unzip(new File(zipFile), new File(to));
    }

    /**
     * 打包文件夹
     *
     * @param sourceDir 需要打包的文件夹
     * @param zipFile   打包后的文件
     */
    public static void zip(String sourceDir, String zipFile) throws IOException {
        ZipOutputStream out = null;
        try {
            byte[] buffs = new byte[1024];
            File dir = new File(sourceDir);
            out = new ZipOutputStream(new FileOutputStream(zipFile, false));
            eachItem(null, dir, out, buffs);
        } finally {
            Closeables.close(out);
        }
    }

    private static void eachItem(String baseDir, File dir, ZipOutputStream out, byte[] bufs) throws IOException {
        File[] files = dir.listFiles();
        String prex = "";
        if (baseDir != null) {
            prex = baseDir + "/";
        }
        if (files == null || files.length == 0) {
            out.putNextEntry(new ZipEntry(prex));
            return;
        }

        String name;
        ZipEntry entry;
        for (File file : files) {
            name = prex + file.getName();
            if (!file.isFile()) {
                eachItem(name, file, out, bufs);
            } else {
                entry = new ZipEntry(name);
                entry.setTime(file.lastModified());
                out.putNextEntry(entry);
                write2zip(out, file, bufs);
            }
        }
    }

    private static void write2zip(ZipOutputStream out, File file, byte[] buffs) throws IOException {
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            int len;
            while ((len = in.read(buffs)) != -1) {
                out.write(buffs, 0, len);
            }
        } finally {
            Closeables.close(in);
        }
    }

    private static void write2file(ZipInputStream in, File file, byte[] buffer) throws IOException {
        FileOutputStream out = null;
        try {
            int count;
            out = new FileOutputStream(file, false);
            while ((count = in.read(buffer)) != -1) {
                out.write(buffer, 0, count);
            }
        } finally {
            Closeables.close(out);
        }
    }
}
