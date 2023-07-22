package cn.ultronxr.valorant.bean.VO;

import lombok.Data;

/**
 * @author Ultronxr
 * @date 2023/07/22 10:37:40
 * @description 用于前端查询每种武器的所有皮肤的 VO
 */
@Data
public class WeaponSkinSelectVO {

    /** 武器 */
    private String weapon;

    /** 武器 ID */
    private String weaponUuid;

    /** 这种武器的所有皮肤 */
    private String[] skinList;

    /** 这种武器的所有皮肤 ID */
    private String[] skinUuidList;

}
