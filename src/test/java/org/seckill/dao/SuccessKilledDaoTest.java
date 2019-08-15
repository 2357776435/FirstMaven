package org.seckill.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entity.SuccessKilled;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
//告诉junit spring配置文件的路径,代表spring容器启动时加载哪些配置
@ContextConfiguration("classpath:spring/spring-dao.xml")
public class SuccessKilledDaoTest {
    @Resource
    private SuccessKilledDao successKilledDao;

    @Test
    public void testInsertSuccessKilled() {
        Long id=1000L;
        Long phone=17726118690L;
        int insertCount=successKilledDao.insertSuccessKilled(id,phone);
        System.out.println("秒杀记录:"+insertCount+"个");
        /**
         * 在规定时间内秒杀打印出:
         * insert ignore into success_seckilled(seckill_id,user_phone) values (?,?)
         * 16:58:42.005 [main] DEBUG org.seckill.dao.SuccessKilledDao.insertSuccessKilled - ==>
         * Parameters: 1002(Long),
         * 18208533574(Long)
         * Updates: 1
         * 第一次:秒杀记录:1个
         * 在此秒杀,重复秒杀同一个商品失败
         * 第二次:秒杀记录:0个
         */
    }

    @Test
    public void testQueryByIdWithSeckill() {
        Long id=1000L;
        Long phone=17726118690L;
        SuccessKilled sk=successKilledDao.queryByIdWithSeckill(id,phone);
        System.out.println(sk);
        System.out.println(sk.getSeckill());
        /**
         * 打印出:
         * 17:20:39.307 [main] DEBUG org.seckill.dao.SuccessKilledDao.queryByIdWithSeckill - ==>
         * Parameters: 1000(Long), 18208533574(Long)
         * Total: 1
         * SuccessKilled (秒杀成功记录)
         * {seckillId=1000,
         * userPhone=18208533574,
         * state=-1,
         * createTime=Thu Aug 08 17:01:33 CST 2019}
         *
         * SecKill(秒杀详情)
         * {seckillId=1000,
         * name='1000元秒杀iphone8',
         * number=100,
         * startTime=Thu Aug 08 00:00:00 CST 2019,
         * endTime=Fri Aug 09 00:00:00 CST 2019,
         * createTime=Wed Aug 07 15:01:49 CST 2019}
         */
    }
}