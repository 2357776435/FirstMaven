package org.seckill.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dto.Exposer;
import org.seckill.dto.SecKillExecution;
import org.seckill.entity.SecKill;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SecKillCloseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.*;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({
        "classpath:spring/spring-dao.xml",
        "classpath:spring/spring-service.xml"})
public class SecKillServiceTest {

    private final Logger logger= LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SecKillService secKillService;

    /**
     * 查询所以的数据测试
     * @throws Exception
     */
    @Test
    public void getSecKillList() throws Exception{
        List<SecKill> list=secKillService.getSecKillList();
        logger.info("list={}",list);
        /**
         * 打印出:
         * Closing non transactional SqlSession()不是在事务控制下
         */
    }

    /**
     * 查询单个的数据测试
     * @throws Exception
     */
    @Test
    public void getById() throws Exception{
        long id = 1000L;
        SecKill secKill=secKillService.getById(id);
        logger.info("secKill={}",secKill);
    }

    /**
     * 执行返回秒杀地址测试并执行秒杀操作
     * 集成测试代码完整逻辑,注意可重复执行
     * @throws Exception
     */
    @Test
    public void testSecKillLogic() throws Exception{
        long id=1003L;
        Exposer exposer=secKillService.exportSecKillUrl(id);
        //秒杀开启时
        if(exposer.isExposed()){
            logger.info("exposer={}",exposer);
            long phone=18208533574L;
            String md5=exposer.getMd5();
            try{
                SecKillExecution execution=secKillService.executeSecKill(id,phone,md5);
                logger.info("result={}",execution);
            }catch (RepeatKillException e){
                logger.error(e.getMessage());
            }catch (SecKillCloseException e){
                logger.error(e.getMessage());
            }
        }else{
            //warn警告:秒杀未开启
            logger.warn("exposer={}",exposer);
        }
    }
}
        /**
         * 在秒杀开启时间内:
         * 打印出:
         * exposer=Exposer{
         * exposed=true,
         * md5='0ee85c2b3ff5d667f91cdd2680735f97',
         * seckillId=1000,
         * now=0,
         * start=0,
         * end=0}
         * 不在秒杀时间内:
         * Exposer{
         * exposed=false,
         * md5='null',
         * seckillId=1002,
         * now=1565490463223,
         * start=1565539200000,
         * end=1565625600000}
         */
        /**
         *  执行秒杀测试
         * 打印出:
         *  result=SecKillExecution{
         *  seckillId=1000,
         *  state=1,
         *  stateInfo='秒杀成功',
         *  successKilled=SuccessKilled{
         *  seckillId=1000,
         *  userPhone=17726118690,
         *  state=0,
         *  createTime=Sun Aug 11 10:42:59 CST 2019}}
         */
