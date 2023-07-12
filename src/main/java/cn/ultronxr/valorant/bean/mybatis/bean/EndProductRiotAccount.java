package cn.ultronxr.valorant.bean.mybatis.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Ultronxr
 * @date 2023/02/22 15:02
 * @description
 */
@Data
@TableName("valorant_end_product_riot_account")
public class EndProductRiotAccount {

    @TableId(type = IdType.AUTO)
    private Long accountNo;

    private String userId;

    private Integer region;

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

   private Integer status;

   private String title;

   private String note;

   private String img;

   private BigDecimal price;

}
