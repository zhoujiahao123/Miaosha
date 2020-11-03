package com.example.secondKill.result;

public class CodeMsg {
    private int code;
    private String msg;

    //通用异常
    public static CodeMsg SUCCESS = new CodeMsg(0,"success");
    public static CodeMsg SERVER_ERROR = new CodeMsg(500100,"服务端异常");
    public static CodeMsg BIND_ERROR = new CodeMsg(500101, "参数校验异常：%s");
    //登录模块5002XX
    public static CodeMsg PASSWORD_ERROR = new CodeMsg(500200,"密码不正确");
    public static CodeMsg USER_NOTFOUND = new CodeMsg(500202,"用户不存在");
    public static CodeMsg MOBILE_INVALID = new CodeMsg(500201,"手机号码格式不正确");
    //商品模块5003xx
    //订单模块5004xx
    //秒杀模块5005xx

    private CodeMsg(int i, String success) {
        this.code = i;
        this.msg = success;
    }
    public CodeMsg fillArgs(Object... args) {
        int code = this.code;
        String message = String.format(this.msg, args);
        return new CodeMsg(code, message);
    }
    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

}
