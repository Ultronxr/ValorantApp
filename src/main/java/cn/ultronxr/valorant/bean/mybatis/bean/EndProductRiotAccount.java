package cn.ultronxr.valorant.bean.mybatis.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
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

    /** 账号地区，0-缅甸、1-马来西亚、2-香港、3-泰国 */
    private Integer region;

    private String username;

    private String password;

    private Boolean hasEmail;

    private String email;

    private String emailPwd;

    @JsonIgnore
    private String accessToken;

    @JsonIgnore
    private Date accessTokenExpireAt;

    @JsonIgnore
    private String entitlementsToken;

    @JsonIgnore
    private String multiFactor;

    @JsonIgnore
    private String cookie;

    /** 状态标记：1-在售、2-出租、10-已售出 */
    private Integer status;

    private String title;

    private String note;

    private String img;

    private BigDecimal price;

}
