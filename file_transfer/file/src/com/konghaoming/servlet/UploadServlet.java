package com.konghaoming.servlet;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.ProgressListener;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.*;
import java.util.List;

@WebServlet(name = "UploadServlet")
public class UploadServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String method = request.getParameter("_m");
        if("poi_down".equals(method)){
            poi_down(request,response);
        }else if("poi_upload".equals(method)){
            poi_upload(request,response);
        }
    }

    protected void doGet(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {

    }

    //文件上传，如果是excel在控制台读取
    private void poi_upload(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //判断上传的是否为文件表单
        if(!ServletFileUpload.isMultipartContent(request)){
            return; //说明这不是一个文件表单，直接返回
        }

        //创建上传文件的保存地址
        String uploadPath=this.getServletContext().getRealPath("/WEB-INF/upload"); //存在WEB-INF路径下，用户无法直接访问上传的文件
        File uploadFile=new File(uploadPath);
        if (!uploadFile.exists()) {   //第一次上传文件时创建这个目录先
            uploadFile.mkdir();
        }

        //创建临时文件的保存地址，文件过大则转存为临时文件
        String tmpPath=this.getServletContext().getRealPath("/WEB-INF/tmp");
        File tmpFile=new File(tmpPath);
        if(!tmpFile.exists()){
            tmpFile.mkdir();
        }

        //处理上传的文件
        //1.处理文件上传路径和文件大小限制
        //创建磁盘文件项目工厂
        DiskFileItemFactory factory=new DiskFileItemFactory();
        //设置一个缓冲区，当文件大于这个缓冲区时就放入临时文件中
        factory.setSizeThreshold(1024);  //缓存区大小为1M
        factory.setRepository(tmpFile);

        //2.获取ServletFileUpload
        //实例话Servlet文件上传对象，把缓存区大小放入构造中
        ServletFileUpload upload=new ServletFileUpload(factory);
        //监听文件上传路径
        upload.setProgressListener(new ProgressListener() {
            public void update(long l, long l1, int i) {   //l是已经读取的文件大小 l1是文件大小
                System.out.println("总大小："+l1+"已上传："+l);
            }
        });
        //处理乱码问题
        upload.setHeaderEncoding("UTF-8");
        //设置单个文件最大值
        upload.setFileSizeMax(1024*1024*100);
        //设置总共能上传文件的大小
        upload.setSizeMax(1024*1024*100);

        //3.处理上传文件
        //把前端请求解析，封装成一个FileItem对象
        List<FileItem>fileItems= null;
        try {
            fileItems = upload.parseRequest(request);
        } catch (FileUploadException e) {
            e.printStackTrace();
        }
        for (FileItem fileItem:fileItems){
            if(fileItem.isFormField()){      //判断当前表单字段是否为普通文本字段，如果返回false，说明是文件字段；
                String name=fileItem.getFieldName(); //getFieldName()是前端表单控件的name
                String value=fileItem.getString("UTF-8");//getString("UTF-8");
                System.out.println(name+"："+value);
            }else{ //文件表单
                //=============处理文件=============//
                String uploadFileName=fileItem.getName();
                System.out.println("上传的文件名: " + uploadFileName);
                // 获得上传的文件名
                String fileName = uploadFileName.substring(uploadFileName.lastIndexOf("/") + 1);
                // 获得文件的后缀名
                String fileExtName = uploadFileName.substring(uploadFileName.lastIndexOf(".") + 1);
                System.out.println("文件信息[件名: " + fileName + " ---文件类型" + fileExtName + "]");
                //=============文件传输=============//
                // 获得文件输入流
                InputStream inputStream = fileItem.getInputStream();
                // 创建一个文件输出流
                FileOutputStream outputStream = new FileOutputStream(uploadPath + "/" + fileName);
                System.out.println("path:"+uploadPath + "/" + fileName);
                // 创建一个缓冲区
                byte[] buffer = new byte[1024*1024];
                // 判断是否读取完毕
                int len = 0;
                // 如果大于0说明还存在数据;
                while ((len = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, len);
                }
                //如果是excel表，读取表格内容
                System.out.println("\n---------------------------------------");
                if("xlsx".equals(fileExtName)){
                    XSSFWorkbook workbook=new XSSFWorkbook(uploadPath + "/" + fileName);   //realPath
                    XSSFSheet sheet=workbook.getSheetAt(0);
                    int rows = sheet.getPhysicalNumberOfRows();
                    System.out.print(rows);
                    for (int i = 0; i < rows; i++) {
                        XSSFRow row = sheet.getRow(i);
                        int columns = row.getPhysicalNumberOfCells();
                        for (int j = 0; j < columns; j++) {
                            XSSFCell cell = row.getCell(j);
                            String value = this.getCellStringValue(cell);
                            System.out.print(value + "|");
                        }
                        System.out.println("\n---------------------------------------");
                    }
                }else{
                    System.out.println("只有excel类型才能读取");
                }
                // 关闭文件输入流
                inputStream.close();
                //关闭文件输出流
                outputStream.close();
                System.out.println("文件上传成功");
                fileItem.delete(); // 上传成功,清除临时文件
            }
        }
    }

    //控制台写入excel文件
    private void poi_down(HttpServletRequest request, HttpServletResponse response) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("我的联系人");
        sheet.setColumnWidth(0, 2000);
        sheet.setColumnWidth(1, 5000);
        //创建表头(第一行)
        XSSFRow row = sheet.createRow(0);
        //列
        XSSFCell cell = row.createCell(0);
        cell.setCellValue("姓名");
        XSSFCell cell2 = row.createCell(1);
        cell2.setCellValue("电话");
        //创建数据行
        for(int i =1;i<=20;i++) {
            XSSFRow newrow = sheet.createRow(i);
            newrow.createCell(0).setCellValue("tom"+i);
            newrow.createCell(1).setCellValue("135816****"+i);
        }
        try {
            String fileName="data.xlsx";
            String uploadPath=this.getServletContext().getRealPath("/WEB-INF/upload");
            FileOutputStream fileOutputStream= new FileOutputStream(uploadPath+"/"+fileName);
            workbook.write(fileOutputStream);
            fileOutputStream.close();
            workbook.close();
            System.out.println("数据成功写入！");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //获取单元格内不同类型值
    public String getCellStringValue(Cell cell) {
        String cellValue = "";
        switch (cell.getCellType()) {
            case STRING:
                cellValue = cell.getStringCellValue();
                if(cellValue.trim().equals("")||cellValue.trim().length()<=0)
                    cellValue=" ";
                break;
            case NUMERIC:
                cellValue = String.valueOf(cell.getNumericCellValue());
                break;
            case FORMULA:
                cell.setCellType(CellType.NUMERIC);
                cellValue = String.valueOf(cell.getNumericCellValue());
                break;
            case BLANK:
                cellValue=" ";
                break;
            case BOOLEAN:
                break;
            case ERROR:
                break;
            default:
                break;
        }
        return cellValue;
    }


}



