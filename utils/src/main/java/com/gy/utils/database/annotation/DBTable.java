package com.gy.utils.database.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by sam_gan on 2016/6/21.
 *
 * 创建简单表，满足应用正常使用就好，
 * 需要用到外键什么的，用到时候再加吧
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DBTable {
    String tableName() default "";
    String[] primaryKey() default {};
}
