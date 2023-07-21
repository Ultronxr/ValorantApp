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
     * 请求API，获取指定 成品拳头账号 的库存皮肤
     *
     * @param endProductRiotAccount {@link EndProductRiotAccount} 成品号拳头账号
     * @return
     */
    List<EndProductStoreEntitlements> getSkinByAPI(@NotNull EndProductRiotAccount endProductRiotAccount);

    /**
     * 为指定 成品拳头账号 生成库存皮肤预览截图
     *
     * @param endProductRiotAccount {@link EndProductRiotAccount} 成品号拳头账号
     * @return （在一个字符串中并由 , 分隔的）两个 URL 链接，指向存储图片的 COS 云对象存储地址<br/>
     *          第一个链接是选号（list）界面的皮肤预览图，第二个链接是某一个成品号详细信息（getOne）界面的皮肤详细信息图<br/>
     *          如果图片生成失败，或其他异常情况，返回 null
     */
    String generateSkinImg(@NotNull EndProductRiotAccount endProductRiotAccount);

}
