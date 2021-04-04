package com.mobai.utils;

public class OutDto {
    private String code;
    private String msg;
    private Object body;

    public static OutDto success(Object obj) {
        OutDto outDto = new OutDto();
        outDto.setCode("200");
        outDto.setMsg("成功");
        outDto.setBody(obj);
        return outDto;
    }

    public static OutDto success() {
        OutDto outDto = new OutDto();
        outDto.setCode("200");
        outDto.setMsg("成功");
        return outDto;
    }

    public static OutDto error(String code, String msg) {
        OutDto outDto = new OutDto();
        outDto.setCode(code);
        outDto.setMsg(msg);
        return outDto;
    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }
}
