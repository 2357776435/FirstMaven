package org.seckill.service;

import org.seckill.dto.Exposer;
import org.seckill.dto.SecKillExecution;
import org.seckill.entity.SecKill;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SecKillCloseException;
import org.seckill.exception.SecKillException;

import java.util.List;

/**
 * 业务接口:站在"使用者"角度取设计接口,而不是"实现者"的角度设计接口
 * 例如:秒杀一个商品，"使用者"就要一个执行秒杀的接口就行，而"实现者"要设计如何减库存，怎么添加用户的购买行为;
 * 三个方面:方法定义的粒度(明确),参数(越简便越直接),返回类型(return 类型/异常)
 */
public interface SecKillService {
    /**
     * 查询所以秒杀记录
     *
     * @return
     */
    List<SecKill> getSecKillList();

    /**
     * 查询单个秒杀记录
     *
     * @param seckillId
     * @return
     */
    SecKill getById(long seckillId);

    /**
     * 秒杀开启时输出秒杀接口的地址,否则输出系统时间和秒杀时间
     */
    Exposer exportSecKillUrl(long seckillId);

    /**
     * 执行秒杀操作
     *
     * @param seckillId
     * @param userPhone
     * @param md5
     */
    SecKillExecution executeSecKill(long seckillId, long userPhone, String md5) throws SecKillException, RepeatKillException, SecKillCloseException;

}
