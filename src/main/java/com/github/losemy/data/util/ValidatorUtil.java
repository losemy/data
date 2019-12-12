package com.github.losemy.data.util;

import cn.hutool.core.date.DateUtil;
import lombok.Data;
import org.hibernate.validator.HibernateValidator;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

/**
 * @author lose
 * @date 2019-09-06
 **/
public class ValidatorUtil {

    /**
     * 开启快速结束模式 failFast (true)
     */
    private static Validator validator = Validation.byProvider(HibernateValidator.class).configure().failFast(true).buildValidatorFactory().getValidator();
    /**
     * 校验对象
     *
     * @param t bean
     * @param groups 校验组
     * @return ValidResult
     */
    public static <T> ValidResult validate(T t,Class<?>...groups) {
        ValidResult result = new ValidResult();
        Set<ConstraintViolation<T>> violationSet = validator.validate(t,groups);
        boolean isError = violationSet != null && violationSet.size() > 0;
        result.setError(isError);
        if (isError) {
            //直接取第一个数据 非快速失败 需要组装数据
            result.setMsg(violationSet.iterator().next().getMessage());
        }
        return result;
    }

    @Data
    public static class ValidResult{
        private boolean isError;
        private String msg;
    }


    public static void main(String[] args) {
        System.out.println(DateUtil.parseDateTime("2019-12-10 23:22:27").getTime());
    }
}
