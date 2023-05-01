package cn.ultronxr.valorant.bean.mybatis.bean;

import com.baomidou.mybatisplus.annotation.TableName;
import com.github.jeffreyning.mybatisplus.anno.MppMultiId;
import lombok.Data;

import java.util.Date;

/**
 * @author Ultronxr
 * @date 2023/05/01 13:02:08
 * @description
 */
@Data
@TableName("valorant_cdk_history")
public class CDKHistory {

    @MppMultiId
    private String cdk;

    @MppMultiId
    private Long accountNo;

    private Date redeemTime;

    private String detail;

}
