package com.konghaoming.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "ListFileServlet")
public class ListFileServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 获取上传文件的目录
        String uploadFilePath = this.getServletContext().getRealPath("/WEB-INF/upload");
        // 存储要下载的文件名
        Map<String, String> fileNameMap = new HashMap<String, String>();
        // 递归遍历目录下的所有文件和目录，将文件的文件名存储到map集合中
        listFile(new File(uploadFilePath), fileNameMap);// File既可以代表一个文件也可以代表一个目录
        // 将Map集合发送到listFile.jsp页面进行显示
        request.setAttribute("fileNameMap", fileNameMap);
        request.getRequestDispatcher("/listFile.jsp").forward(request, response);  //服务器跳转，在执行当前文件的过程中转向执行目标文件
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request,response);
    }

    public void listFile(File file, Map<String, String> map) {
        if(file == null) return;
        // 如果file代表的不是一个文件，而是一个目录
        if (!file.isFile()) {
        // 列出该目录下的所有文件和目录
        File files[] = file.listFiles();
        if(files == null) return;
        // 遍历files[]数组
        for (File f : files) {
        // 递归
        listFile(f, map);
         }
        } else {
            String realName = file.getName().substring(file.getName().indexOf("/") + 1);
            // file.getName()得到的是文件的原始名称，这个名称是唯一的，因此可以作为key，realName是处理过后的名称，有可能会重复
            map.put(file.getName(), realName);
        }
    }



}
