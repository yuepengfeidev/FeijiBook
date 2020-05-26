package com.example.feijibook.util;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;

/**
 * FileUtils
 *
 * @author PengFei Yue
 * @date 2019/9/15
 * @description 文件处理工具类
 */
public class FileUtils {
    private static String TAG = "FileUtils";

    /**
     * 用于保存临时文件到指定位置
     *
     * @param resPath 临时文件路径
     * @param desPath 将要保存的路径
     */
    public static void copyToFile(String resPath, String desPath) {
        createFile(desPath);

        File resFile = new File(resPath);
        try {
            InputStream inputStream = new FileInputStream(resFile);
            OutputStream outputStream = new FileOutputStream(desPath);
            byte[] bytes = new byte[1024];
            int read;
            // 复制
            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
            inputStream.close();
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存文件输入流都文件
     *
     * @param fileUrl 文件路径
     * @param is      文件输入流
     */
    public static void saveFile(String fileUrl, InputStream is) {
        byte[] buf = new byte[2048];
        int len;
        FileOutputStream fos = null;
        try {
            /*final long total = responseBody.contentLength();
            long sum = 0;*/

            File file = FileUtils.createFile(fileUrl);
            fos = new FileOutputStream(file);
            while ((len = is.read(buf)) != -1) {
                /*sum += len;*/
                fos.write(buf, 0, len);
               /* final long finalSum = sum;
                //这里就是对进度的监听回调
                onProgress((int) (finalSum * 100 / total), total);*/
            }
            fos.flush();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 删除文件
     *
     * @param filePath 删除的文件路径
     */
    public static void deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            if (file.isFile()) {
                // 如果是文件则直接删除
                boolean result = file.delete();
                if (result) {
                    Log.d(TAG, "deleteFile:  删除文件失败！");
                } else {
                    Log.d(TAG, "deleteFile: 删除文件成功！");
                }
            } else if (file.isDirectory()) {
                // 如果是文件夹，则递归依次删除其中的文件
                File[] files = file.listFiles();
                for (File f : files) {
                    deleteFile(f.getAbsolutePath());
                }
            }
        }
    }

    public static File createFile(String fileUrl) {
        File file = new File(fileUrl);
        if (!file.getParentFile().exists()) {
            boolean result = file.getParentFile().mkdirs();
            if (result) {
                Log.d(TAG, "createFile: 目标文件父文件创建成功！");
            } else {
                Log.d(TAG, "createFile: 目标文件父文件创建失败！");
            }
        }
        if (!file.exists()) {
            try {
                boolean result = file.createNewFile();
                if (result) {
                    Log.d(TAG, "createFile: 目标文件创建成功！");
                } else {
                    Log.d(TAG, "createFile: 目标文件创建失败！");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

}
