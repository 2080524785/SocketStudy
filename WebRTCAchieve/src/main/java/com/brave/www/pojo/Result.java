package com.brave.www.pojo;

public class Result {
    private Boolean status;
    private Object data;
    private String message;
    private Integer code;

    public Result(Boolean status, Object data, String message, Integer code) {
        this.status = status;
        this.data = data;
        this.message = message;
        this.code = code;
    }
    public static Result success(Object data,String message){
        return new Result(true,data,message,200);
    }

    public static Result fail(Object data,String message,int code){
        return new Result(false,data,message,code);
    }
    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
