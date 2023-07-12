package cn.ultronxr.valorant.service;

import cn.ultronxr.valorant.bean.mybatis.bean.EndProductStoreEntitlements;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author Ultronxr
 * @date 2023/07/05 10:04:57
 * @description 获取账号中已拥有的物品 service
 */
public interface EndProductStoreEntitlementsService extends IService<EndProductStoreEntitlements> {

    List<EndProductStoreEntitlements> skins(String userId);

}
