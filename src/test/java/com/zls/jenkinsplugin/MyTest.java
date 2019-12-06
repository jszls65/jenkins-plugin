package com.zls.jenkinsplugin;

import lombok.Data;
import org.junit.Test;
import org.springframework.format.annotation.DateTimeFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

public class MyTest {
    @Test
    public void test01(){
        String dateStr = "2019-11-29T18:38:21.000+08:00";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        try {
            Date date =  sdf.parse(dateStr);
            SimpleDateFormat sdf2 = new SimpleDateFormat("dd号hh点mm分");
            System.out.println(sdf2.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

}
