package com.pinyougou.vo;

import java.io.Serializable;

public class Result implements Serializable {
    //操作标识符
    private Boolean success;
    //操作信息
    private String message;

    public static Result ok(String message){
        return new Result(true, message);
    }

    public static Result fail(String message){
        return new Result(false, message);
    }

    public Result(Boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
