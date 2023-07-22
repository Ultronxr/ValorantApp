package cn.ultronxr.valorant.bean.mybatis.mapper;

import cn.ultronxr.valorant.bean.VO.WeaponSkinSelectVO;
import cn.ultronxr.valorant.bean.mybatis.bean.WeaponSkin;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * @author Ultronxr
 * @date 2023/02/20 18:35
 * @description
 */
public interface WeaponSkinMapper extends BaseMapper<WeaponSkin> {

    /**
     * 查询出每一种武器的所有皮肤名称、皮肤 ID
     * @return
     */
    List<WeaponSkinSelectVO> weaponSkinSelect();

}
