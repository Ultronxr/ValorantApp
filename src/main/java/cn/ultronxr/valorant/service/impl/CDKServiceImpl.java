package cn.ultronxr.valorant.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.ultronxr.valorant.bean.DTO.CDKDTO;
import cn.ultronxr.valorant.bean.DTO.CDKHistoryDTO;
import cn.ultronxr.valorant.bean.enums.CDKRedeemState;
import cn.ultronxr.valorant.bean.mybatis.bean.CDK;
import cn.ultronxr.valorant.bean.mybatis.bean.CDKHistory;
import cn.ultronxr.valorant.bean.mybatis.bean.RiotAccount;
import cn.ultronxr.valorant.bean.mybatis.mapper.CDKHistoryMapper;
import cn.ultronxr.valorant.bean.mybatis.mapper.CDKMapper;
import cn.ultronxr.valorant.bean.mybatis.mapper.RiotAccountMapper;
import cn.ultronxr.valorant.service.CDKService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static cn.ultronxr.valorant.bean.enums.CDKRedeemState.*;

/**
 * @author Ultronxr
 * @date 2023/05/01 13:01:03
 * @description
 */
@Service
@Slf4j
public class CDKServiceImpl extends ServiceImpl<CDKMapper, CDK> implements CDKService {

    @Autowired
    private CDKMapper cdkMapper;

    @Autowired
    private CDKHistoryMapper cdkHistoryMapper;

    @Autowired
    private RiotAccountMapper riotAccountMapper;

    // 生成CDK使用的字符
    private static final String RANDOM_BASE_STRING = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    // CDK的默认长度
    private static final Integer DEFAULT_CDK_LENGTH = 50;

    // CDK是否带初邮邮箱、是否可重复使用 缺省值
    private static final Boolean DEFAULT_TYPE_HAS_EMAIL = false;
    private static final Boolean DEFAULT_TYPE_REUSABLE = false;

    // 可重复使用CDK的可重复使用次数缺省值
    private static final Integer DEFAULT_REUSE_REMAINING_TIMES = 1;


    @Override
    public int create(CDKDTO cdkDTO) {
        if(null == cdkDTO.getCreateCount() || cdkDTO.getCreateCount() <= 0) {
            return 0;
        }
        log.info("新增CDK，参数：createCount={}, typeHasEmail={}, typeReusable={}, reuseRemainingTimes={}",
                cdkDTO.getCreateCount(), cdkDTO.getTypeHasEmail(), cdkDTO.getTypeReusable(), cdkDTO.getReuseRemainingTimes());

        List<CDK> cdkList = new ArrayList<>(cdkDTO.getCreateCount());
        for(int i = 0; i < cdkDTO.getCreateCount(); i++) {
            CDK cdk = new CDK();
            cdk.setCdk(RandomUtil.randomString(RANDOM_BASE_STRING, DEFAULT_CDK_LENGTH));
            cdk.setCdkNo(null);
            cdk.setTypeHasEmail(cdkDTO.getTypeHasEmail() != null ? cdkDTO.getTypeHasEmail() : DEFAULT_TYPE_HAS_EMAIL);
            cdk.setTypeReusable(cdkDTO.getTypeReusable() != null ? cdkDTO.getTypeReusable() : DEFAULT_TYPE_REUSABLE);
            if(cdkDTO.getTypeReusable() && cdkDTO.getReuseRemainingTimes() != null) {
                cdk.setReuseRemainingTimes(cdkDTO.getReuseRemainingTimes());
            } else {
                cdk.setReuseRemainingTimes(DEFAULT_REUSE_REMAINING_TIMES);
            }
            cdk.setIsUsed(false);
            cdkList.add(cdk);
        }
        return this.saveBatch(cdkList) ? cdkList.size() : 0;
    }

    @Override
    public boolean updateReuseRemainingTimes(Long[] reusableCDKNoList, Integer reuseRemainingTimes) {
        if(null == reusableCDKNoList || reusableCDKNoList.length == 0 || reuseRemainingTimes <= 0) {
            return false;
        }
        List<CDK> toBeUpdatedList = this.list(
                new LambdaQueryWrapper<CDK>()
                        .in(CDK::getCdkNo, Arrays.asList(reusableCDKNoList))
                        .eq(CDK::getTypeReusable, true)
        );
        for(CDK cdk : toBeUpdatedList) {
            cdk.setReuseRemainingTimes(reuseRemainingTimes);
        }
        return this.updateBatchById(toBeUpdatedList);
    }

    @Override
    public Page<CDK> queryCDK(CDKDTO cdkDTO) {
        Page<CDK> page = Page.of(cdkDTO.getCurrent(), cdkDTO.getSize());
        LambdaQueryWrapper<CDK> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(StringUtils.isNotEmpty(cdkDTO.getCdk()), CDK::getCdk, cdkDTO.getCdk())
                .eq(cdkDTO.getCdkNo() != null, CDK::getCdkNo, cdkDTO.getCdkNo())
                .eq(cdkDTO.getTypeHasEmail() != null, CDK::getTypeHasEmail, cdkDTO.getTypeHasEmail())
                .eq(cdkDTO.getTypeReusable() != null, CDK::getTypeReusable, cdkDTO.getTypeReusable())
                .eq(cdkDTO.getIsUsed() == null, CDK::getIsUsed, false)
                .eq(cdkDTO.getIsUsed() != null, CDK::getIsUsed, cdkDTO.getIsUsed())
                .orderByDesc(CDK::getCdkNo);

        return this.page(page, wrapper);
    }

    @Override
    public boolean delete(Long[] cdkNoList) {
        List<Long> list = Arrays.asList(cdkNoList);
        log.info("删除CDK，cdkNoList={}", list);

        LambdaQueryWrapper<CDK> wrapper = Wrappers.lambdaQuery();
        wrapper.in(CDK::getCdkNo, list);
        return this.remove(wrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public CDKRedeemState redeem(String cdk, Long accountNo) {
        log.info("尝试兑换CDK，cdk={}, 拳头账号编号accountNo={}", cdk, accountNo);
        CDK cdkObj = this.getById(cdk);
        RiotAccount account = riotAccountMapper.selectOne(new LambdaQueryWrapper<RiotAccount>().eq(RiotAccount::getAccountNo, accountNo));
        if(null == cdkObj) {
            return CDK_NOT_EXIST;
        }
        if(null == account) {
            return ACCOUNT_NOT_EXIST;
        }
        if(account.getIsDel()) {
            return ACCOUNT_ALREADY_REDEEMED;
        }
        if(cdkObj.getTypeHasEmail() != account.getHasEmail()) {
            return CDK_VERSION_ERROR;
        }
        if(cdkObj.getIsUsed()) {
            return CDK_USED;
        }
        if(cdkObj.getTypeReusable() && cdkObj.getReuseRemainingTimes() <= 0) {
            return CDK_REUSE_REMAINING_TIMES_EXHAUSTED;
        }

        if(!cdkObj.getTypeReusable()) {
            cdkObj.setReuseRemainingTimes(0);
            cdkObj.setIsUsed(true);
        } else {
            cdkObj.setReuseRemainingTimes(cdkObj.getReuseRemainingTimes() - 1);
        }
        cdkMapper.updateById(cdkObj);

        account.setIsDel(true);
        riotAccountMapper.updateById(account);

        CDKHistory history = new CDKHistory();
        history.setCdk(cdkObj.getCdk());
        history.setAccountNo(accountNo);
        history.setRedeemTime(new Date());
        cdkHistoryMapper.insert(history);

        return OK;
    }

    @Override
    public Page<CDKHistory> queryCDKHistory(CDKHistoryDTO cdkHistoryDTO) {
        Page<CDKHistory> page = Page.of(cdkHistoryDTO.getCurrent(), cdkHistoryDTO.getSize());
        LambdaQueryWrapper<CDKHistory> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(cdkHistoryDTO.getCdk() != null, CDKHistory::getCdk, cdkHistoryDTO.getCdk())
                .eq(cdkHistoryDTO.getAccountNo() != null, CDKHistory::getAccountNo, cdkHistoryDTO.getAccountNo())
                .orderByDesc(CDKHistory::getRedeemTime);

        return cdkHistoryMapper.selectPage(page, wrapper);
    }

}
