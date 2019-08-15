package org.seckill.dto;

import org.seckill.entity.SuccessKilled;
import org.seckill.enums.SecKillStateEnum;

/**
 * 封装秒杀执行后结果
 */
public class SecKillExecution {
    //产品Id
    private long seckillId;
    //秒杀执行结果状态
    private int state;
    //秒杀标识
    private String stateInfo;
    //秒杀成功对象
    private SuccessKilled successKilled;

    /**
     * 成功返回的参数,加入了枚举SecKillStateEnum
     *
     * @param seckillId
     * @param stateEnum
     * @param successKilled
     */
    public SecKillExecution(long seckillId, SecKillStateEnum stateEnum, SuccessKilled successKilled) {
        this.seckillId = seckillId;
        this.state = stateEnum.getState();
        this.stateInfo = stateEnum.getStateInfo();
        this.successKilled = successKilled;
    }

    /**
     * 失败返回的参数,加入了枚举SecKillStateEnum
     *
     * @param seckillId
     * @param stateEnum
     */
    public SecKillExecution(long seckillId, SecKillStateEnum stateEnum) {
        this.seckillId = seckillId;
        this.state = stateEnum.getState();
        this.stateInfo = stateEnum.getStateInfo();
    }

    public long getSeckillId() {
        return seckillId;
    }

    public void setSeckillId(long seckillId) {
        this.seckillId = seckillId;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getStateInfo() {
        return stateInfo;
    }

    public void setStateInfo(String stateInfo) {
        this.stateInfo = stateInfo;
    }

    public SuccessKilled getSuccessKilled() {
        return successKilled;
    }

    public void setSuccessKilled(SuccessKilled successKilled) {
        this.successKilled = successKilled;
    }

    @Override
    public String toString() {
        return "SecKillExecution{" +
                "seckillId=" + seckillId +
                ", state=" + state +
                ", stateInfo='" + stateInfo + '\'' +
                ", successKilled=" + successKilled +
                '}';
    }
}
