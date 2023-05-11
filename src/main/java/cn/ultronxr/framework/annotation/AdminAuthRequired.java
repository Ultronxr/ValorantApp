package cn.ultronxr.framework.annotation;

import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
 * @author Ultronxr
 * @date 2023/05/11 08:04:56
 * @description 后台方法管理员认证注解，标注了这个注解，说明该方法（Controller）需要管理员权限才能访问
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AdminAuthRequired {

}
