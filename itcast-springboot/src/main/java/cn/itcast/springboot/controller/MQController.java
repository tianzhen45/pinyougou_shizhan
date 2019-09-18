package cn.itcast.springboot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("/mq")
@RestController
public class MQController {

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;

    @GetMapping("/sendMsg")
    public String sendMsg(){
        Map<String, Object> map = new HashMap<>();
        map.put("id", 123L);
        map.put("name", "黑马");
        //参数1：队列或者主题的名称
        //参数2：发送的数据
        jmsMessagingTemplate.convertAndSend("spring.boot.queue", map);
        return "已发送消息到 spring.boot.queue 队列中...";
    }
}
