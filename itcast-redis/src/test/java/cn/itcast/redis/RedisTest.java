package cn.itcast.redis;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/spring-redis.xml")
public class RedisTest {

    @Autowired
    private RedisTemplate redisTemplate;

    //字符串
    @Test
    public void testString() {
        redisTemplate.boundValueOps("string_key").set("itcast黑马");
        Object obj = redisTemplate.boundValueOps("string_key").get();
        System.out.println(obj);
    }

    //哈希 散列
    @Test
    public void testHash() {
        //参数1 域名
        //参数2 域值
        redisTemplate.boundHashOps("h_key").put("f1", "a");
        redisTemplate.boundHashOps("h_key").put("f2", "b");
        redisTemplate.boundHashOps("h_key").put("f3", "c");
        //获取该散列key对应的所有域的集合
        //Object obj = redisTemplate.boundHashOps("h_key").keys();
        //获取该散列key对应的所有域值的集合
        Object obj = redisTemplate.boundHashOps("h_key").values();
        System.out.println(obj);
    }

    //list
    @Test
    public void testList() {
        redisTemplate.boundListOps("l_key").rightPush("b");
        redisTemplate.boundListOps("l_key").leftPush("a");
        redisTemplate.boundListOps("l_key").rightPush("c");
        //取列表中的所有元素；-1表示最后一个元素的索引号
        Object obj = redisTemplate.boundListOps("l_key").range(0, -1);
        System.out.println(obj);
    }

    //集合
    @Test
    public void testSet() {
        redisTemplate.boundSetOps("set_key").add("a", "b", "c");
        Object obj = redisTemplate.boundSetOps("set_key").members();
        System.out.println(obj);
    }

    //有序集合 sorted set 根据每个元素对应分值排序（升序）
    @Test
    public void testZSet() {
        //参数1：元素值，参数2：元素对应的分值
        redisTemplate.boundZSetOps("z_key").add("a", 20);
        redisTemplate.boundZSetOps("z_key").add("c", 5);
        redisTemplate.boundZSetOps("z_key").add("b", 10);
        Object obj = redisTemplate.boundZSetOps("z_key").range(0, -1);
        System.out.println(obj);
    }
}
