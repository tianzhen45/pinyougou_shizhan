package com.pinyougou.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import com.pinyougou.service.impl.BaseServiceImpl;
import com.pinyougou.user.service.UserService;
import com.pinyougou.vo.SeckillOrderVo;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import tk.mybatis.mapper.entity.Example;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl extends BaseServiceImpl<TbUser> implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private ActiveMQQueue itcastSmsQueue;

    @Autowired
    private SeckillOrderMapper seckillOrderMapper;

    @Autowired
    private SellerMapper sellerMapper;

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private ItemMapper itemMapper;

    @Value("${signName}")
    private String signName;
    @Value("${templateCode}")
    private String templateCode;

    @Override
    public PageInfo<TbUser> search(Integer pageNum, Integer pageSize, TbUser user) {
        //设置分页
        PageHelper.startPage(pageNum, pageSize);
        //创建查询对象
        Example example = new Example(TbUser.class);

        //创建查询条件对象
        Example.Criteria criteria = example.createCriteria();

        //模糊查询
        /**if (StringUtils.isNotBlank(user.getProperty())) {
         criteria.andLike("property", "%" + user.getProperty() + "%");
         }*/

        List<TbUser> list = userMapper.selectByExample(example);
        return new PageInfo<>(list);
    }

    @Override
    public void sendSmsCode(String phone) {
        //- 生成随机的6为数值字符串 TODO 如果出现0.012345 -->12345
        String code = (long)(Math.random()*1000000) + "";
        System.out.println("-----验证码：" + code);
        //- 将用户手机号和验证码存入redis并设置过期时间5分钟
        redisTemplate.boundValueOps(phone).set(code);
        redisTemplate.boundValueOps(phone).expire(5, TimeUnit.MINUTES);
        //- 将信息（4个）发送到MQ队列 itcast_sms_queue
        jmsTemplate.send(itcastSmsQueue, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                MapMessage mapMessage = session.createMapMessage();
                //手机号
                mapMessage.setString("mobile", phone);
                //签名
                mapMessage.setString("signName", signName);
                //模版编号
                mapMessage.setString("templateCode", templateCode);
                //模版参数 {"code":123456}
                mapMessage.setString("templateParam", "{\"code\": "+code+"}");
                return mapMessage;
            }
        });
    }

    @Override
    public Boolean checkSmsCode(String phone, String smsCode) {
        String code = (String) redisTemplate.boundValueOps(phone).get();
        if (smsCode.equals(code)) {
            //返回之前将redis中的验证码删除
            redisTemplate.delete(phone);

            return true;
        }
        return false;
    }

    @Override
    public Map<String,Object> findUserSeckillOrder(Integer pageNum,Integer pageSize, String userId) {
        Map<String, Object> resultMap = new HashMap<>();
        //select * from tb_seckill_order where user_id = 'heima' ORDER BY create_time DESC
        //设置分页信息
        PageHelper.startPage(pageNum, pageSize);
        Example example = new Example(TbSeckillOrder.class);
        example.createCriteria().andEqualTo("userId", userId);
        //设置排序
        example.orderBy("createTime").desc();
        List<TbSeckillOrder> seckillOrderList = seckillOrderMapper.selectByExample(example);
        if (seckillOrderList == null || seckillOrderList.size() == 0) {
            return null;
        }
        PageInfo<TbSeckillOrder> pageInfo = PageInfo.of(seckillOrderList);

        List<SeckillOrderVo> list = new ArrayList<>();
        for (TbSeckillOrder tbSeckillOrder : seckillOrderList) {

            SeckillOrderVo seckillOrderVo = new SeckillOrderVo();

            seckillOrderVo.setSeckillOrder(tbSeckillOrder);
            //1.根据商家id查询商家店铺名称
            TbSeller seller = sellerMapper.selectByPrimaryKey(tbSeckillOrder.getSellerId());
            if (seller == null) {
                return null;
            }
            seckillOrderVo.setSeller(seller);
            //2.根据秒杀商品id查询秒杀商品
            TbSeckillGoods seckillGoods = seckillGoodsMapper.selectByPrimaryKey(tbSeckillOrder.getSeckillId());
            if (seckillGoods == null) {
                return null;
            }
            seckillOrderVo.setTbSeckillGoods(seckillGoods);
            //3.根据sku id查询商品详情的规格spec
            TbItem item = itemMapper.selectByPrimaryKey(seckillGoods.getItemId());
            if (item == null) {
                return null;
            }
            String spec = item.getSpec().toString();
            String specStr = spec.replaceAll("\\{", "")
                    .replaceAll("}", "")
                    .replaceAll(",", " ")
                    .replace("\"", "");
            seckillOrderVo.setSpecStr(specStr);
            list.add(seckillOrderVo);
        }

        //vo列表
        resultMap.put("seckillOrderList", list);
        //总页数
        resultMap.put("pageNum", pageInfo.getPageNum());
        //当前页
        resultMap.put("pages", pageInfo.getPages());
        return resultMap;
    }

    @Override
    public void deleteSeckillOrder(Long[] ids) {
        if (ids != null && ids.length > 0) {
            for (Long id : ids) {
                seckillOrderMapper.deleteByPrimaryKey(id.toString());
            }
        }

    }


}
