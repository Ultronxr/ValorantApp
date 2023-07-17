package cn.ultronxr.valorant.bean.mybatis.mapper;

import cn.ultronxr.valorant.bean.VO.EndProductWeaponSkinVO;
import cn.ultronxr.valorant.bean.mybatis.bean.EndProductStoreEntitlements;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * @author Ultronxr
 * @date 2023/07/05 15:00:22
 * @description
 */
public interface EndProductStoreEntitlementsMapper extends BaseMapper<EndProductStoreEntitlements> {

    /**
     * 查询指定成品拳头账号的所有库存武器皮肤
     *
     * @param accountNo 成品拳头账号 编号
     * @return 该账号拥有的全部武器的所有库存皮肤 {@link EndProductWeaponSkinVO} 列表
     */
    List<EndProductWeaponSkinVO> selectSkinForOneAccount(Long accountNo);

}
