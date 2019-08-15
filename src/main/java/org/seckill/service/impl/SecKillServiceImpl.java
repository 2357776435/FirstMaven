package org.seckill.service.impl;

import org.seckill.dao.SecKillDao;
import org.seckill.dao.SuccessKilledDao;
import org.seckill.dto.Exposer;
import org.seckill.dto.SecKillExecution;
import org.seckill.entity.SecKill;
import org.seckill.entity.SuccessKilled;
import org.seckill.enums.SecKillStateEnum;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SecKillCloseException;
import org.seckill.exception.SecKillException;
import org.seckill.service.SecKillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.List;

/**
 * 实现SecKillService接口的方法
 */
//@Component(能注解所有的组件)   @Service  @Dao  @Controller
@Service
public class SecKillServiceImpl implements SecKillService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    //注入Service依赖
    @Autowired //@Resource,@Inject
    private SecKillDao secKillDao;
    @Autowired
    private SuccessKilledDao successKilledDao;
    //md5的研制字符串,用于混淆MD5，越复杂越好
    private final String slat = "js!#^&da,klf.jklsda*^$&*<?><?:~!_sgw";

    @Override
    public List<SecKill> getSecKillList() {
        return secKillDao.queryAll(0, 10);
    }

    @Override
    public SecKill getById(long seckillId) {
        return secKillDao.queryById(seckillId);
    }

    /**
     * 用来展示秒杀接口地址
     *
     * @param seckillId
     * @return
     */
    @Override
    public Exposer exportSecKillUrl(long seckillId) {
        // SecKill secKill=getById(seckillId);== SecKill secKill=secKillDao.queryById(seckillId);
        SecKill secKill = secKillDao.queryById(seckillId);
        if (secKill == null) {
            return new Exposer(false, seckillId);
        }
        //秒杀开启时间
        Date startTime = secKill.getStartTime();
        //秒杀结束时间
        Date endTime = secKill.getEndTime();
        //系统当前时间,
        Date nowTime = new Date();
        //.getTime()是时间的毫秒,如果系统时间小于秒杀开启时间,或者系统时间大于秒杀结束时间,就表示秒杀还没开始或秒杀已经结束
        if (nowTime.getTime() < startTime.getTime() || nowTime.getTime() > endTime.getTime()) {
            return new Exposer(false, seckillId, nowTime.getTime(), startTime.getTime(), endTime.getTime());
        }
        //md5转换特定字符串的过程,不可逆-->TODO (加一个记号，表示之后完成或修改)
        String md5 = getMD5(seckillId);
        return new Exposer(true, md5, seckillId);
    }

    /**
     * 加密方法MD5
     *
     * @param seckillId
     * @return
     */
    private String getMD5(long seckillId) {
        String base = seckillId + "/" + slat;
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }

    /**
     * 执行秒杀操作
     *
     * @param seckillId
     * @param userPhone
     * @param md5
     * @return
     * @throws SecKillException
     * @throws RepeatKillException
     * @throws SecKillCloseException
     */
    @Override
    @Transactional
    /**
     * 使用注解控制事务方法的有点:
     * 1:开发团队达成一致约定,明确标注事务方法的编程风格。
     * 2:保证事务方法的执行时间尽可能短,不要穿插其他网络操作RPC/HTTP请求或者剥离到事务方法外部。
     * 3:不是所有的方法都需要事务,如只有一条修改操作(例:增删改查),只读操作不需要事务控制。
     */
    public SecKillExecution executeSecKill(long seckillId, long userPhone, String md5) throws SecKillException, RepeatKillException, SecKillCloseException {
        //如果md5为空，或者md5的地址值不同，抛出异常
        if (md5 == null || !md5.equals(getMD5(seckillId))) {
            throw new SecKillException("秒杀的数据被重写了,seckill data rewrite");
        }
        //执行秒杀逻辑: 减库存 + 添加购买行为记录
        Date nowTime = new Date();//系统时间
        try {
            int updateCount = secKillDao.reduceNumber(seckillId, nowTime);
            if (updateCount <= 0) {
                throw new SecKillCloseException("秒杀结束了 seckill is closed");
            } else {
                //添加购买行为记录 状态标示:-1:无效 0:成功 1:已付款 2:已发货
                int insertCount = successKilledDao.insertSuccessKilled(seckillId, userPhone);
                if (insertCount <= 0) {
                    //重复秒杀
                    throw new RepeatKillException("重复秒杀 seckill repeated");
                } else {
                    //秒杀成功
                    SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
                    return new SecKillExecution(seckillId, SecKillStateEnum.SUCCESS, successKilled);
                }
            }
        } catch (SecKillCloseException e1) {//秒杀关闭异常
            throw e1;
        } catch (RepeatKillException e2) {//重复秒杀异常
            throw e2;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            //把所以的Exception编译期异常转化为运行期异常SecKillException
            throw new SecKillException("seckill  inner error" + e.getMessage());
        }
    }
}
