package cn.ultronxr.valorant.service;

import cn.ultronxr.valorant.bean.DTO.EndProductRiotAccountDTO;
import cn.ultronxr.valorant.bean.enums.RiotAccountCreateState;
import cn.ultronxr.valorant.bean.mybatis.bean.EndProductRiotAccount;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author Ultronxr
 * @date 2023/07/05 14:52:06
 * @description 成品拳头账号 service
 */
public interface EndProductRiotAccountService extends IService<EndProductRiotAccount> {

    RiotAccountCreateState create(EndProductRiotAccount account);

    boolean update(EndProductRiotAccount account);

    Page<EndProductRiotAccount> managementQueryAccount(EndProductRiotAccountDTO accountDTO);

    EndProductRiotAccount redeem(Long accountNo);

    Page<EndProductRiotAccount> queryAccount(EndProductRiotAccountDTO accountDTO);

    EndProductRiotAccount getOne(Long accountNo);

}
