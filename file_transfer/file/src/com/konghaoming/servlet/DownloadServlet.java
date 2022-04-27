package com.konghaoming.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;

@WebServlet(name = "DownloadServlet")
public class DownloadServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 得到要下载的文件名
        String fileName = request.getParameter("filename");
        // 上传的文件都是保存在/WEB-INF/upload目录下的子目录当中
        String fileSaveRootPath = this.getServletContext().getRealPath("/WEB-INF/upload");
        // 得到要下载的文件
        File file = new File(fileSaveRootPath + "/" + fileName);
        // 处理文件名
        String realname = fileName.substring(fileName.indexOf("/") + 1);
        // 设置响应头，控制浏览器下载该文件
        response.setHeader("content-disposition","attachment;filename="+ URLEncoder.encode(realname,"UTF-8"));
        // 读取要下载的文件，保存到文件输入流
        FileInputStream fileInputStream= new FileInputStream(fileSaveRootPath + "/" + fileName);
        // 创建输出流
        OutputStream outputStream = response.getOutputStream();
        // 创建缓冲区
        byte buffer[] = new byte[1024*1024];
        int len = 0;
        // 循环将输入流中的内容读取到缓冲区当中
        while ((len=fileInputStream.read(buffer)) > 0) {
            // 输出缓冲区的内容到浏览器，实现文件下载
            outputStream.write(buffer, 0, len);
        }
        // 关闭文件输入流
        fileInputStream.close();
        // 关闭输出流
        outputStream.close();
}

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request,response);
    }
}
