package cn.ultronxr.valorant.service;

import cn.ultronxr.valorant.bean.mybatis.bean.EndProductRiotAccount;
import cn.ultronxr.valorant.bean.mybatis.bean.EndProductStoreEntitlements;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author Ultronxr
 * @date 2023/07/05 10:04:57
 * @description 获取账号中已拥有的物品 service
 */
public interface EndProductStoreEntitlementsService extends IService<EndProductStoreEntitlements> {

    /**
     * 获取指定 成品拳头账号 的库存皮肤
     *
     * @param endProductRiotAccount {@link EndProductRiotAccount} 成品号拳头账号
     * @return
     */
    List<EndProductStoreEntitlements> skins(@NotNull EndProductRiotAccount endProductRiotAccount);

    /**
     * 为指定 成品拳头账号 生成库存皮肤预览截图
     *
     * @param endProductRiotAccount {@link EndProductRiotAccount} 成品号拳头账号
     * @return 一个 URL 链接，指向存储该图片的 OSS 地址<br/>
     *          如果图片生成失败，或其他异常情况，返回 null
     */
    String generateSkinImg(@NotNull EndProductRiotAccount endProductRiotAccount);

}
