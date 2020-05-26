package com.ypf.feijibookserver.util;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.List;

/**
 * FileUtils
 *
 * @author PengFei Yue
 * @date 2019/10/19
 * @description 文件处理工具类
 */
public class FileUtils {

    /**
     * 解析文件上传请求
     *
     * @param request 上传文件请求
     * @return 解析的请求的列表
     */
    public static List<FileItem> parseFileUpload(HttpServletRequest request) throws FileUploadException {
        // 创建文件项目工厂对象
        DiskFileItemFactory factory = new DiskFileItemFactory();
        // 获取系统默认的临时文件保存路径，该路径为Tomcat根目录下的temp文件夹
        String temp = System.getProperty("java.io.tmpdir");
        // 设置缓冲区大小为5M
        factory.setSizeThreshold(1024 * 1024 * 5);
        // 设置临时文件夹为temp
        factory.setRepository(new File(temp));
        // 用工厂实例化上传组件 ，ServletFileUpload用来解析文件上传请求
        ServletFileUpload servletFileUpload = new ServletFileUpload(factory);
        return servletFileUpload.parseRequest(request);
    }

    /**
     * 输入流转换为文件
     *
     * @param is      文件输入流
     * @param saveUrl 文件存储路径
     */
    public static void inputStreamToFile(InputStream is, String saveUrl) throws IOException {
        File file = createFile(saveUrl);
        OutputStream fileOutputStream = new FileOutputStream(file);
        int f;
        byte[] buf = new byte[100000000];
        if ((f = is.read(buf)) != -1) {
            fileOutputStream.write(buf, 0, f);
        }
        fileOutputStream.flush();
        fileOutputStream.close();
        is.close();
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
                file.delete();
            } else if (file.isDirectory()) {
                // 如果是文件夹，则递归依次删除其中的文件
                File[] files = file.listFiles();
                for (File f : files) {
                    deleteFile(f.getAbsolutePath());
                }
            }
        }
    }

    /**
     * 获取存储上传文件的父文件名
     *
     * @param account 账户名
     * @param type    上传的类型 : portrait \ photo \ video
     */
    public static String getFileName(String realPath, String account, String type, String fileName) {
        String newUrl = realPath + "\\data\\" + account + "\\";
        if (type.equals("portrait")) {
            return newUrl + fileName;
        } else if (type.equals("photo") || type.equals("video")) {
            return newUrl + type + "\\" + fileName;
        }
        return null;
    }

    /**
     * 创建文件
     */
    public static File createFile(String fileUrl) {
        File file = new File(fileUrl);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }
}
