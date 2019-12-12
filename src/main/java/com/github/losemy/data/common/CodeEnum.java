package com.github.losemy.data.common;

import cn.hutool.core.util.StrUtil;

/**
 * @author lose
 * @date 2019-11-18
 **/
public enum CodeEnum {
    SUCCESS("000000","成功:%s"),
    UNKNOWN_ERROR("400999","未知异常:%s"),
    PARAM_VALID_ERROR("400001","参数输入有误:%s"),
    ;

    private String code;
    private String msg;

    CodeEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return StrUtil.replaceIgnoreCase(this.msg,":%s","");
    }

    /**
     * 避免没有传入
     * 需要保证足够安全
     * @param supple
     * @return
     */
    public String getMsg(String... supple){
        if(supple != null && supple.length > 0) {
            return String.format(this.msg, supple);
        }else{
            return getMsg();
        }
    }

}
