package com.pinyougou.common.util;

import com.pinyougou.pojo.TbPayLog;
import org.apache.poi.hpsf.DocumentSummaryInformation;
import org.apache.poi.hssf.usermodel.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class PoiUtils {

    //这是把数据导出到本地保存为Excel文件的方法
    public static ResponseEntity<byte[]> exportPayLogExcel(List<TbPayLog> payLogs) throws IOException {
        HSSFWorkbook workbook = new HSSFWorkbook();//创建一个Excel文件

        //创建Excel文档属性，必不可少。
        // 少了的话，getDocumentSummaryInformation()方法就会返回null
        workbook.createInformationProperties();
        DocumentSummaryInformation info = workbook.getDocumentSummaryInformation();
        info.setCompany("PinYouGou Ltd.");//设置公司信息
        info.setManager("JBL");//设置管理者
        info.setCategory("支付日志列表");//设置文件名

        //设置文件中的日期格式
        HSSFCellStyle datecellStyle = workbook.createCellStyle();
        datecellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy"));//这个文件的日期格式和平时的不一样

        //创建表单
        HSSFSheet sheet = workbook.createSheet("百威美食尚职称表");
        HSSFRow r0 = sheet.createRow(0);//创建第一行
        HSSFCell c0 = r0.createCell(0);// 创建列
        HSSFCell c1 = r0.createCell(1);// 创建列
        HSSFCell c2 = r0.createCell(2);// 创建列
        HSSFCell c3 = r0.createCell(3);// 创建列
        HSSFCell c4 = r0.createCell(4);// 创建列
        HSSFCell c5 = r0.createCell(5);// 创建列

        c0.setCellValue("支付日志ID");
        c1.setCellValue("用户ID");
        c2.setCellValue("支付金额");
        c3.setCellValue("支付状态");
        c4.setCellValue("创建时间");
        c5.setCellValue("支付时间");

        for (int i = 0; i < payLogs.size(); i++) {
            TbPayLog payLog = payLogs.get(i);
            HSSFRow row = sheet.createRow(i + 1);
            HSSFCell cell0 = row.createCell(0);
            cell0.setCellValue(payLog.getOutTradeNo());
            HSSFCell cell1 = row.createCell(1);
            cell1.setCellValue(payLog.getUserId());
            HSSFCell cell2 = row.createCell(2);
            cell2.setCellValue(payLog.getTotalFee());
            HSSFCell cell3 = row.createCell(3);
            cell3.setCellValue(payLog.getTradeState()=="1"?"已支付":"未支付");
            HSSFCell cell4 = row.createCell(4);
            cell4.setCellStyle(datecellStyle);//让日期格式数据正确显示
            cell4.setCellValue(payLog.getCreateTime());
            HSSFCell cell5 = row.createCell(5);
            cell5.setCellStyle(datecellStyle);//让日期格式数据正确显示
            cell5.setCellValue(payLog.getPayTime());

        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("attachment",
                new String("支付日志列表.xls".getBytes("UTF-8"),"iso-8859-1"));
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        workbook.write(baos);

        ResponseEntity<byte[]> entity = new ResponseEntity<>(baos.toByteArray(), headers, HttpStatus.CREATED);

        return entity;
    }

    //这是解析上传的Excel文件为对象集合，从而批量添加数据的方法
    /*public static List<JObLevel> parseFile2List(MultipartFile file) throws IOException {
        List<JObLevel> jObLevels=new ArrayList<>();
        HSSFWorkbook workbook = new HSSFWorkbook(file.getInputStream());
        HSSFSheet sheet = workbook.getSheetAt(0);
        int physicalNumberOfRows = sheet.getPhysicalNumberOfRows();//获取表单所有的行
        for (int i = 1; i < physicalNumberOfRows; i++) {
            HSSFRow row = sheet.getRow(i);
            JObLevel j1=new JObLevel();

            HSSFCell c0 = row.getCell(0);
            j1.setId((int) c0.getNumericCellValue());

            HSSFCell c1 = row.getCell(1);
            j1.setName(c1.getStringCellValue());

            HSSFCell c2 = row.getCell(2);
            j1.setTitlelevel(c2.getStringCellValue());

            HSSFCell c3 = row.getCell(3);
            j1.setCreatedate(c3.getDateCellValue());

            HSSFCell c4 = row.getCell(4);
            j1.setEnabled(c4.getStringCellValue().equals("是"));

            jObLevels.add(j1);
        }

        return jObLevels;
    }*/
}
