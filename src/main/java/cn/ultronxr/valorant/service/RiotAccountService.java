package cn.ultronxr.valorant.service;

import cn.ultronxr.valorant.bean.DTO.RiotAccountDTO;
import cn.ultronxr.valorant.bean.enums.RiotAccountCreateState;
import cn.ultronxr.valorant.bean.enums.RiotAccountRegion;
import cn.ultronxr.valorant.bean.mybatis.bean.RiotAccount;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Ultronxr
 * @date 2023/02/22 15:10
 * @description
 */
public interface RiotAccountService extends IService<RiotAccount> {

    RiotAccountCreateState create(RiotAccount account);

    /**
     * 创建拳头账号时不进行登录验证（用于Excel导入的情况）
     * @param account
     * @return
     */
    RiotAccountCreateState createWithoutRSO(RiotAccount account);

    /**
     * 使用 Excel 批量导入拳头账号
     * @param file   Excel 文件
     * @param region 拳头账号地区枚举
     * @return
     */
    boolean importFile(MultipartFile file, RiotAccountRegion region);

    //boolean update(RiotAccount account);

    /**
     * 查询拳头账户列表
     * @param accountDTO 查询条件
     * @return 返回的账户列表中不包含敏感信息（密码、token等）
     */
    Page<RiotAccount> queryAccount(RiotAccountDTO accountDTO);

}
