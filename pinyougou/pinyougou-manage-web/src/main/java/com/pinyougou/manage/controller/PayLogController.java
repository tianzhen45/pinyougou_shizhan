package com.pinyougou.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import com.pinyougou.common.util.PoiUtils;
import com.pinyougou.pay.service.PayLogService;
import com.pinyougou.pojo.TbPayLog;
import com.pinyougou.vo.Result;
import org.apache.poi.hpsf.DocumentSummaryInformation;
import org.apache.poi.hssf.usermodel.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

@RestController
@RequestMapping("/payLog")
public class PayLogController {

    @Reference(retries = 0, timeout = 100000)
    private PayLogService payLogService;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");


    /**
     * 查询
     * @param pageNum 页号
     * @param pageSize 页面大小
     * @param payLog 搜索条件
     * @return 分页信息
     */
    @PostMapping("/search")
    public PageInfo<TbPayLog> search(@RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
                                     @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
                                     @RequestBody TbPayLog payLog) {

        return payLogService.search(pageNum, pageSize, payLog);
    }

    @PostMapping("/export")
    //ResponseEntity里面装了所有响应的数据
    public ResponseEntity<byte[]> exportExcel() throws IOException {
        return PoiUtils.exportPayLogExcel(payLogService.findAll());
    }

    /*@PostMapping("/export")
    //ResponseEntity里面装了所有响应的数据
    public Result exportExcel(@RequestParam List<TbPayLog> payLogs) {
        try {

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
            HSSFSheet sheet = workbook.createSheet("支付日志表");
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
            workbook.close();

            return Result.ok("日志导出成功！");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Result.fail("日志导出失败！");
    }*/


}
