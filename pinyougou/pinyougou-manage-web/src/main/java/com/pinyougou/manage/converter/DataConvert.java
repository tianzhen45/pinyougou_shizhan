package com.pinyougou.manage.converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;

public class DataConvert implements  Converter<String, Date> {

    private static final Logger LOGGER= LoggerFactory.getLogger(DataConvert.class);
    public String  datePattern="yyyy-MM-dd HH:mm:ss";

    public void setDatePattern(String datePattern) {
        this.datePattern = datePattern;
    }

    @Override
    public Date convert(String source) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(datePattern);
        try {
            return simpleDateFormat.parse(source);
        } catch (ParseException e) {
            LOGGER.error("对日期进行格式转换异常！期望的格式：{}",datePattern,e);
        }

        return null;
    }

}