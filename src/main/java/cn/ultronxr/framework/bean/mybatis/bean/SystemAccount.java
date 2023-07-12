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

    private String xSecret;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    private Boolean isDel;

}
