package org.seckill.exception;

/**
 * 异常分为:(编译期异常)和(运行期异常)
 * 重复秒杀异常(运行期异常 RuntimeException)
 * spring的声明事事务只接受运行期异常回滚策略
 */
public class RepeatKillException extends SecKillException{
    public RepeatKillException(String message) {
        super(message);
    }

    public RepeatKillException(String message, Throwable cause) {
        super(message, cause);
    }
}
