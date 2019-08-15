package org.seckill.dao;

import org.apache.ibatis.annotations.Param;
import org.seckill.entity.SecKill;

import java.util.Date;
import java.util.List;

public interface SecKillDao {
    /**
     * 减库存
     *
     * @param seckillId
     * @param createTime
     * @return 返回的int 指如果影响行数>1,表示更新的记录行数
     */
    int reduceNumber(@Param("seckillId") long seckillId, @Param("createTime") Date createTime);

    /**
     * 根据id查询秒杀对象
     *
     * @param seckillId
     * @return
     */
    SecKill queryById(long seckillId);

    /**
     * 根据偏移量查询秒杀商品列表
     *
     * @param offset 偏移量
     * @param limit  偏移量后要取多少条记录
     * @return
     */
    List<SecKill> queryAll(@Param("offset") int offset, @Param("limit") int limit);
}
