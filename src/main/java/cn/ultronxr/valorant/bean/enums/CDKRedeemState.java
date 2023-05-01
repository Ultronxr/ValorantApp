package cn.ultronxr.valorant.bean.enums;

import lombok.Getter;

/**
 * @author Ultronxr
 * @date 2023/05/01 21:15:24
 * @description 使用CDK兑换拳头账号时的状态码
 */
@Getter
public enum CDKRedeemState {
    OK(1, "兑换成功。")
    ,CDK_NOT_EXIST(10, "CDK不存在！")
    ,ACCOUNT_NOT_EXIST(11, "拳头账号不存在！")
    ,ACCOUNT_ALREADY_REDEEMED(12, "拳头账号已被兑换！")
    ,CDK_VERSION_ERROR(20, "CDK版本错误！")
    ,CDK_USED(30, "一次性CDK已使用！")
    ,CDK_REUSE_REMAINING_TIMES_EXHAUSTED(40, "可重复使用CDK剩余可用次数耗尽！")
    ;

    private final int code;
    private final String msg;

    CDKRedeemState(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
