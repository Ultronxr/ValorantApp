package cn.ultronxr.framework.bean.mybatis.bean;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * @author Ultronxr
 * @date 2023/05/11 09:37:12
 * @description
 */
@Data
@TableName("system_account")
public class SystemAccount {

    @TableId(type = IdType.ASSIGN_UUID)
    private String uuid;

    private String username;

    private String password;

    private String token;

    /** 前两个字母都小写，避免字段命名的坑 */
    @TableField(value = "x_secret")
    private String xsecret;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    private Boolean isDel;

}
