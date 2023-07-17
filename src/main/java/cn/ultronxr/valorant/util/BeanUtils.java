package cn.ultronxr.valorant.util;

import cn.ultronxr.valorant.bean.mybatis.bean.EndProductRiotAccount;
import cn.ultronxr.valorant.bean.mybatis.bean.RiotAccount;
import org.jetbrains.annotations.NotNull;

/**
 * @author Ultronxr
 * @date 2023/07/17 10:50:49
 * @description
 */
public class BeanUtils {

    /**
     * 把 成品拳头账号 对象转换成 普通拳头账号 对象 （此方法主要用于 RSO 功能）
     *
     * @param endProductRiotAccount {@link EndProductRiotAccount} 成品拳头账号 对象
     * @return {@link RiotAccount} 普通拳头账号 对象
     */
    public static @NotNull RiotAccount transformToRiotAccountFromEndProduct(@NotNull EndProductRiotAccount endProductRiotAccount) {
        RiotAccount riotAccount = new RiotAccount();
        org.springframework.beans.BeanUtils.copyProperties(endProductRiotAccount, riotAccount);
        return riotAccount;
    }

}
