package cn.ultronxr.valorant.bean.VO;

import cn.ultronxr.valorant.bean.enums.CDKRedeemState;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * @author Ultronxr
 * @date 2023/05/02 11:38:01
 * @description CDK兑换时返回给前端的确认信息
 */
@Data
public class CDKRedeemVerifyVO {

    /**
     * 兑换结果
     */
    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    CDKRedeemState state;

    /**
     * 详细信息（包括账号信息、每日商店/夜市信息、CDK信息、账号密码等）
     */
    String detail;


    public CDKRedeemVerifyVO() {
    }

    public CDKRedeemVerifyVO(CDKRedeemState state) {
        this.state = state;
    }

}
