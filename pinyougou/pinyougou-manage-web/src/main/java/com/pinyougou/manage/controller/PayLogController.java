package com.pinyougou.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import com.pinyougou.common.util.PoiUtils;
import com.pinyougou.pay.service.PayLogService;
import com.pinyougou.pojo.TbPayLog;
import com.pinyougou.vo.Result;
import org.apache.poi.hpsf.DocumentSummaryInformation;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
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
        return PoiUtils.exportPayLogExcel(payLogService.getAllPayLog());
    }

    /**
     * 导出
     * @return 输出流
     */
    /*@RequestMapping(value = "/export")
    @ResponseBody
    public void exportExcel(HttpServletResponse response) {
        InputStream in = null;
        OutputStream out = null;
        HSSFWorkbook wb = null;
        try {
            //读取模板
            in = this.getClass().getResourceAsStream("/static/excel/test.xls");
            wb = new HSSFWorkbook(in);
            //设置表格内容样式
            HSSFCellStyle cellStyle = wb.createCellStyle();
            cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
            cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
            cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            //垂直、水平居中
            cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 水平居中
            cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER); // 上下居中
            //写入对象
            Sheet sheet = wb.getSheetAt(0);
            Row row = sheet.createRow(1);    //创建第一行
            row.setHeight((short) 500);
            Cell cell0 = row.createCell(0);
            cell0.setCellStyle(cellStyle);
            cell0.setCellValue("测试数据");
            //输出
            out = response.getOutputStream();
            wb.write(out);
        } catch (Exception e) {
            logger.error("exception",e);
        } finally {
            try {
                if(!ObjectUtils.isEmpty(in)){
                    in.close();
                }
                if(!ObjectUtils.isEmpty(out)){
                    out.close();
                }
                if(!ObjectUtils.isEmpty(wb)){
                    wb.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }*/


}
