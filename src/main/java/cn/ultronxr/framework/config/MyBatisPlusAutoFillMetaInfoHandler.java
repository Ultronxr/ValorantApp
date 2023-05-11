package cn.ultronxr.framework.config;

import cn.hutool.core.date.CalendarUtil;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author Ultronxr
 * @date 2023/05/11 09:46:17
 * @description MyBatis-Plus 自动填充字段功能处理器<br/>
 *                           <a href="https://baomidou.com/pages/4c6bcf/">自动填充功能</a>
 */
@Slf4j
@Component
public class MyBatisPlusAutoFillMetaInfoHandler implements MetaObjectHandler {

    /**
     * 配置了 {@code @TableField(... fill = FieldFill.INSERT)} 注解的自动填充处理器
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        // 注意：这里的第二个入参 fieldName 填的是 Java Bean 的属性名，不是数据库字段名，下同
        //this.strictInsertFill(metaObject, "createTime", () -> LocalDateTime.now(), LocalDateTime.class); // 起始版本 3.3.3(推荐)
        this.strictInsertFill(metaObject, "createTime", () -> CalendarUtil.calendar().getTime(), Date.class);
        log.info("INSERT 自动填充字段：createTime");
    }

    /**
     * 配置了 {@code @TableField(... fill = FieldFill.UPDATE)} 注解的自动填充处理器
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        //this.strictUpdateFill(metaObject, "updateTime", () -> LocalDateTime.now(), LocalDateTime.class);
        this.strictUpdateFill(metaObject, "updateTime", () -> CalendarUtil.calendar().getTime(), Date.class);
        log.info("UPDATE 自动填充字段：updateTime");
    }

}
