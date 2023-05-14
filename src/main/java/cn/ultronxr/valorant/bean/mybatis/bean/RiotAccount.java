package cn.ultronxr.valorant.bean.mybatis.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author Ultronxr
 * @date 2023/02/22 15:02
 * @description
 */
@Data
@TableName("valorant_riot_account")
public class RiotAccount {

    @TableId(type = IdType.INPUT)
    private String userId;

    private Long accountNo;

    private String username;

    private String password;

    private Boolean hasEmail;

    private String email;

    private String emailPwd;

    private String accessToken;

    private Date accessTokenExpireAt;

    private String entitlementsToken;

    private String multiFactor;

    private String cookie;

    private Boolean isVerified;

    private Boolean isAuthFailure;

    private Boolean isDel;

}
