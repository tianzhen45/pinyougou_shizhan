package com.pinyougou.task;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class SeckillTask {

    /**
     * 更新秒杀数据
     */
    @Scheduled(cron = "0/2 * * * * ?")
    public void refreshSeckillGoods(){
        System.out.println("--------------" + new Date());
    }
}
