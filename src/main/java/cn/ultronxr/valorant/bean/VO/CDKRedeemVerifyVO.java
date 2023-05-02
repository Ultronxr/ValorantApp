package cn.ultronxr.valorant.bean.VO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 * @author Ultronxr
 * @date 2023/05/02 11:38:01
 * @description CDK兑换时返回给前端的确认信息（包括账号信息、每日商店/夜市信息、CDK信息）VO
 */
@Data
public class CDKRedeemVerifyVO {

    String msg;

    String detail;

    @JsonIgnore
    Long accountNo;

    @JsonIgnore
    String accountType;

    @JsonIgnore
    String cdkType;

    @JsonIgnore
    BatchBothStoreFrontVO storeFront;

}
