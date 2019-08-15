package org.seckill.entity;

import java.util.Date;

public class SuccessKilled {
    private long seckillId;//商品库存id
    private long userPhone;//用户手机号
    private short state;//状态标示:-1:无效 0:成功 1:已付款 2:已发货
    private Date createTime;//创建时间
    //多对一的复合属性：就是一个秒杀SuccessKilled的实体对应的是多个成功记录
    private SecKill seckill;

    public SecKill getSeckill() {
        return seckill;
    }

    public void setSeckill(SecKill seckill) {
        this.seckill = seckill;
    }

    public long getSeckillId() {
        return seckillId;
    }

    public void setSeckillId(long seckillId) {
        this.seckillId = seckillId;
    }

    public long getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(long userPhone) {
        this.userPhone = userPhone;
    }

    public short getState() {
        return state;
    }

    public void setState(short state) {
        this.state = state;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "SuccessKilled{" +
                "seckillId=" + seckillId +
                ", userPhone=" + userPhone +
                ", state=" + state +
                ", createTime=" + createTime +
                '}';
    }
}
