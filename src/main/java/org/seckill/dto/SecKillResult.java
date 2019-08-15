package org.seckill.dto;

//所以的ajax请求返回类型,封装到json结果中,<T>指泛型
public class SecKillResult <T>{
    private boolean success;//判断返回的json是否成功(true)或者失败(false)

    private T data;//成功后要拿到的数据

    private String error;//失败后输出error信息

    /**
     * 成功
     * @param success
     * @param data
     */
    public SecKillResult(boolean success, T data) {
        this.success = success;
        this.data = data;
    }

    /**
     * 失败
     * @param success
     * @param error
     */
    public SecKillResult(boolean success, String error) {
        this.success = success;
        this.error = error;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
