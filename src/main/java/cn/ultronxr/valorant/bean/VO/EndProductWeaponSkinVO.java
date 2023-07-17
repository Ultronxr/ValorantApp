package cn.ultronxr.valorant.bean.VO;

import lombok.Data;

/**
 * @author Ultronxr
 * @date 2023/07/17 12:26:11
 * @description 瓦罗兰特成品拳头账号的库存 武器-皮肤 对象（一把武器的所有已拥有的皮肤）
 */
@Data
public class EndProductWeaponSkinVO {

    /** 武器 ID （所有皮肤都是这把武器的） */
    private String parentWeaponUuid;

    /** 皮肤名称列表 */
    private String[] displayNameList;

    /** 皮肤图片列表 */
    private String[] displayIconList;

    /** 排序（数字越小越靠前） */
    private Integer sortEndProduct;

}
