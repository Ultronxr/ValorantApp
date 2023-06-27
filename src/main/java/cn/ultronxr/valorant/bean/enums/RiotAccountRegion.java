package cn.ultronxr.valorant.bean.enums;

import cn.hutool.core.util.EnumUtil;
import lombok.Getter;

/**
 * @author Ultronxr
 * @date 2023/06/27 13:00:09
 * @description 拳头账号地区枚举
 */
@Getter
public enum RiotAccountRegion {
    Myanmar(0, "缅甸")
    ,Malaysia(1, "马来西亚")
    ;

    private int code;
    private String region;

    RiotAccountRegion(int code, String region) {
        this.code = code;
        this.region = region;
    }

    public static String getRegionStrByCode(int code) {
        RiotAccountRegion result = EnumUtil.getBy(RiotAccountRegion.class, (e)->e.code == code);
        return result != null ? result.getRegion() : null;
    }

}
