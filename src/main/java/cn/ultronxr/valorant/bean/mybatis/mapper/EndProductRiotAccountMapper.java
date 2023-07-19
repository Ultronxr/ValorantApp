package cn.ultronxr.valorant.bean.mybatis.mapper;

import cn.ultronxr.valorant.bean.DTO.EndProductRiotAccountDTO;
import cn.ultronxr.valorant.bean.mybatis.bean.EndProductRiotAccount;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

/**
 * @author Ultronxr
 * @date 2023/07/05 14:59:26
 * @description
 */
public interface EndProductRiotAccountMapper extends BaseMapper<EndProductRiotAccount> {

    /**
     * 用户侧 查询成品号列表，不包括特殊信息字段（账号状态、备注等），不包括敏感信息字段
     */
    Page<EndProductRiotAccount> queryAccount(@Param("page") Page<EndProductRiotAccount> page,
                                             @Param("endProductRiotAccountDTO") EndProductRiotAccountDTO endProductRiotAccountDTO);

}
