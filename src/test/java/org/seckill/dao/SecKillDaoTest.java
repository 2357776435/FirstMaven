package org.seckill.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entity.SecKill;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * 配置spring和junit整合,junit启动时加载springIOC容器
 * spring-test,junit
 */
@RunWith(SpringJUnit4ClassRunner.class)
//告诉junit spring配置文件的路径,代表spring容器启动时加载哪些配置
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SecKillDaoTest {
    @Resource
    private SecKillDao secKillDao;

    @Test
    public void testQueryById() throws Exception{
        long id=1000;
        SecKill secKill=secKillDao.queryById(id);
        System.out.println(secKill.getName());
        System.out.println(secKill);
        /**
         * 打印出:
         * 1000元秒杀iphone8
         * SecKill{seckillId=1000,name='1000元秒杀iphone8', number=100,startTime=Thu Aug 08 00:00:00 CST 2019,endTime=Fri Aug 09 00:00:00 CST 2019,createTime=Wed Aug 07 15:01:49 CST 2019}
         */
    }

    @Test
    public void testQueryAll() throws Exception{
        /*从0开始查100条*/
        /**
         * 报错解决:
         * Parameter 'offset' not found. Available parameters are [arg1, arg0, param1, param2]
         *  List<SecKill> queryAll(int offset, int limit)方法中，
         *  因为JAVA没有保存形参的记录:queryAll(int offset, int limit)-->queryAll(arg0, arg1)
         */
        List<SecKill> list= secKillDao.queryAll(0,100);
        for(SecKill sk:list){
            System.out.println(sk);
        }
        /**
         * 打印出:
         * SecKill{seckillId=1003, name='200元秒杀老人机sgw', number=400, startTime=Thu Aug 08 00:00:00 CST 2019, endTime=Fri Aug 09 00:00:00 CST 2019, createTime=Wed Aug 07 15:02:43 CST 2019}
         * SecKill{seckillId=1002, name='500元秒杀小米6', number=300, startTime=Thu Aug 08 00:00:00 CST 2019, endTime=Fri Aug 09 00:00:00 CST 2019, createTime=Wed Aug 07 15:02:32 CST 2019}
         * SecKill{seckillId=1001, name='800元秒杀Nokia7x', number=200, startTime=Thu Aug 08 00:00:00 CST 2019, endTime=Fri Aug 09 00:00:00 CST 2019, createTime=Wed Aug 07 15:02:10 CST 2019}
         * SecKill{seckillId=1000, name='1000元秒杀iphone8', number=100, startTime=Thu Aug 08 00:00:00 CST 2019, endTime=Fri Aug 09 00:00:00 CST 2019, createTime=Wed Aug 07 15:01:49 CST 2019}
         */
    }

    @Test
    public void testReduceNumber() throws Exception{
        Date createTime=new Date();
        int updateCount=secKillDao.reduceNumber(1004L,createTime);
        System.out.println("减了"+updateCount+"商品数量");
        /**
         * 在规定时间内秒杀打印出:
         * 16:31:56.084 [main] DEBUG org.mybatis.spring.transaction.SpringManagedTransaction - JDBC Connection [com.mchange.v2.c3p0.impl.NewProxyConnection@110844f6 [wrapping: com.mysql.jdbc.JDBC4Connection@6f89f665]]
         * will not be managed by Spring
         * 16:31:56.115 [main] DEBUG org.seckill.dao.SecKillDao.reduceNumber - ==>  Preparing: 具体的sql语句*
         * update seckill set number = number - 1
         * where seckill_id = ?
         * and start_time <= ?
         * and end_time >= ?
         * and number >0;
         * 16:31:56.158 [main] DEBUG org.seckill.dao.SecKillDao.reduceNumber - ==> Parameters: 1003(Long),
         * 2019-08-08 16:31:55.749(Timestamp),
         * Updates: 1
         */
        /**
         * 不在规定时间内秒杀打印出:
         * JDBC Connection [com.mchange.v2.c3p0.impl.NewProxyConnection@726a17c4 [wrapping: com.mysql.jdbc.JDBC4Connection@5dc3fcb7]]
         * will not be managed by Spring
         * update seckill set number = number -1
         * where seckill_id= ?
         * and start_time <= ?
         * and end_time >= ?
         * and number >0;
         * 16:45:10.913 [main] DEBUG org.seckill.dao.SecKillDao.reduceNumber - ==> Parameters: 1004(Long),
         * 2019-08-08 16:45:10.334(Timestamp),
         * Updates: 0
         */
    }
}