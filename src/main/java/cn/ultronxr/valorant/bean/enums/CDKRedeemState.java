package cn.ultronxr.valorant.bean.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Ultronxr
 * @date 2023/05/01 21:15:24
 * @description 使用CDK兑换拳头账号时的状态码
 */
@Getter
public enum CDKRedeemState {
    OK(1, "兑换成功。")
    ,CDK_INVALID(9, "CDK不合法（格式错误）！")
    ,CDK_NOT_EXIST(10, "CDK不存在！")
    ,ACCOUNT_NOT_EXIST(11, "拳头账号不存在！")
    ,ACCOUNT_ALREADY_REDEEMED(12, "拳头账号已被兑换！")
    ,CDK_VERSION_ERROR(20, "CDK版本错误！")
    ,CDK_USED(30, "一次性CDK已使用！")
    ,CDK_REUSE_REMAINING_TIMES_EXHAUSTED(40, "可重复使用CDK剩余可用次数耗尽！")
    ,NO_STOREFRONT_RECORD(50, "没有每日商店或夜市数据！")
    ;

    private final int code;
    private final String msg;

    CDKRedeemState(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @JsonValue
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>(4);
        map.put("code", getCode());
        map.put("msg", getMsg());
        return map;
    }

}
