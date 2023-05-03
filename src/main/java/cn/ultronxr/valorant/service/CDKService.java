package cn.ultronxr.valorant.service;

import cn.ultronxr.valorant.bean.DTO.CDKDTO;
import cn.ultronxr.valorant.bean.DTO.CDKHistoryDTO;
import cn.ultronxr.valorant.bean.VO.CDKRedeemVerifyVO;
import cn.ultronxr.valorant.bean.enums.CDKRedeemState;
import cn.ultronxr.valorant.bean.mybatis.bean.CDK;
import cn.ultronxr.valorant.bean.mybatis.bean.CDKHistory;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Ultronxr
 * @date 2023/05/01 13:00:57
 * @description
 */
public interface CDKService extends IService<CDK> {

    /**
     * 新增CDK
     * @param cdkDTO 传入新增CDK的参数，
     * @return 实际创建的CDK数量
     */
    int create(CDKDTO cdkDTO);

    /**
     * 更新CDK的可重复使用次数（注：仅有可重复使用CDK 才能 修改可重复使用次数！）
     * @param reusableCDKNoList 可重复使用的CDK编号列表
     * @param reuseRemainingTimes 修改之后的可重复使用次数
     * @return 修改操作是否成功
     */
    boolean updateReuseRemainingTimes(Long[] reusableCDKNoList, Integer reuseRemainingTimes);

    /**
     * 查询CDK
     * @param cdkDTO 查询参数
     * @return 分页后的CDK列表
     */
    Page<CDK> queryCDK(CDKDTO cdkDTO);

    /**
     * 删除CDK
     * @param cdkNoList CDK编号列表
     * @return 删除操作是否成功
     */
    boolean delete(Long[] cdkNoList);

    /**
     * 兑换CDK之前进行信息确认
     * @param cdk       CDK 内容
     * @param accountNo 拳头账号编号
     * @return 确认信息
     */
    CDKRedeemVerifyVO redeemVerify(String cdk, Long accountNo);

    /**
     * 兑换CDK
     * @param cdk       CDK内容
     * @param accountNo 拳头账号编号
     * @return 兑换操作结果
     */
    @Transactional(rollbackFor = Exception.class)
    CDKRedeemVerifyVO redeem(String cdk, Long accountNo);

    /**
     * 查询CDK兑换历史记录
     * @param cdkHistoryDTO 查询参数
     * @return 分页后的CDK兑换历史记录
     */
    Page<CDKHistory> queryCDKHistory(CDKHistoryDTO cdkHistoryDTO);

}
