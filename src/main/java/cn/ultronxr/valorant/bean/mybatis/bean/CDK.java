package cn.ultronxr.valorant.bean.mybatis.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author Ultronxr
 * @date 2023/04/29 16:58:57
 * @description
 */
@Data
@TableName("valorant_cdk")
public class CDK {

    @TableId(type = IdType.INPUT)
    private String cdk;

    private Long cdkNo;

    private Boolean typeHasEmail;

    private Boolean typeReusable;

    private Integer reuseRemainingTimes;

    private Boolean isUsed;

}
