package cn.ultronxr.valorant.service;

import cn.ultronxr.valorant.bean.VO.WeaponSkinSelectVO;
import cn.ultronxr.valorant.bean.mybatis.bean.WeaponSkin;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author Ultronxr
 * @date 2023/02/21 17:37
 * @description
 */
public interface WeaponSkinService extends IService<WeaponSkin> {

    List<WeaponSkin> getSkins(String weaponId);

    List<WeaponSkin> getLevels(String skinId);

    List<WeaponSkin> getChromas(String skinId);

    /**
     * 通过 皮肤升级ID或炫彩ID 查找对应的 父皮肤ID <br/>
     * （对应关系为：武器ID ==> 皮肤ID ==> 升级ID/炫彩ID）<br/>
     *                            ↑             |      <br/>
     *                            --------------       <br/>
     *
     * @param levelOrChromaId 皮肤升级ID或炫彩ID
     * @return 父皮肤ID
     */
    String getParentSkinIdByLevelOrChromaId(String levelOrChromaId);

    /**
     * 通过 皮肤ID 查找对应的 父武器ID <br/>
     * （对应关系为：武器ID ==> 皮肤ID ==> 升级ID/炫彩ID）<br/>
     *                ↑          |             |       <br/>
     *                --------------------------       <br/>
     *
     * @param skinId 皮肤ID（或者 升级ID/炫彩ID 也是可以的）
     * @return 父武器ID
     */
    String getParentWeaponIdBySkinId(String skinId);

    /**
     * 查询出每一种武器的所有皮肤名称、皮肤 ID，前端由此（加载select下拉框内容）在所有皮肤中选择某几款皮肤，再进行后续查询<br/>
     * 由于该方法会被频繁调用，所以请对数据库查询结果进行缓存！（这里默认采用 redis 缓存方式）<br/>
     * 如果更新了数据库中的武器、皮肤数据，也要清理/更新缓存！
     *
     * @return
     */
    List<WeaponSkinSelectVO> weaponSkinSelectAllSkin();

}
