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

    /**
     * 后台管理界面 新增成品号
     * @param account
     * @return
     */
    RiotAccountCreateState create(EndProductRiotAccount account);

    /**
     * 后台管理界面 更新成品号信息
     * @param account
     * @return
     */
    boolean update(EndProductRiotAccount account);

    /**
     * 后台管理界面 查询成品号列表，包括特殊信息字段（账号状态、备注等），不包括敏感信息字段
     * @param accountDTO
     * @return
     */
    Page<EndProductRiotAccount> managementQueryAccount(EndProductRiotAccountDTO accountDTO);

    /**
     * 后台管理界面 提号，只包括敏感信息字段（账号、密码、邮箱等）
     * @param accountNo
     * @return
     */
    EndProductRiotAccount redeem(Long accountNo);

    /**
     * 用户侧 查询成品号列表，不包括特殊信息字段（账号状态、备注等），不包括敏感信息字段
     * @param accountDTO
     * @return
     */
    Page<EndProductRiotAccount> queryAccount(EndProductRiotAccountDTO accountDTO);

    /**
     * 用户侧 获取一条成品号详细信息，不包括特殊信息字段（账号状态、备注等），不包括敏感信息字段
     * @param accountNo
     * @return
     */
    EndProductRiotAccount getOne(Long accountNo);

}
