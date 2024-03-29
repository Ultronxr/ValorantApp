package cn.ultronxr.valorant.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import cn.ultronxr.common.util.ArrayUtils;
import cn.ultronxr.valorant.bean.DTO.CDKDTO;
import cn.ultronxr.valorant.bean.DTO.CDKHistoryDTO;
import cn.ultronxr.valorant.bean.VO.BatchBothStoreFrontVO;
import cn.ultronxr.valorant.bean.VO.CDKHistoryAndMoreCDKInfoVO;
import cn.ultronxr.valorant.bean.VO.CDKRedeemVerifyVO;
import cn.ultronxr.valorant.bean.enums.RiotAccountRegion;
import cn.ultronxr.valorant.bean.mybatis.bean.CDK;
import cn.ultronxr.valorant.bean.mybatis.bean.CDKHistory;
import cn.ultronxr.valorant.bean.mybatis.bean.RiotAccount;
import cn.ultronxr.valorant.bean.mybatis.mapper.CDKHistoryMapper;
import cn.ultronxr.valorant.bean.mybatis.mapper.CDKMapper;
import cn.ultronxr.valorant.bean.mybatis.mapper.RiotAccountMapper;
import cn.ultronxr.valorant.service.CDKService;
import cn.ultronxr.valorant.service.StoreFrontService;
import cn.ultronxr.valorant.util.ValorantDateUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
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

    @Autowired
    private StoreFrontService sfService;

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

    public void exportCDK() {
        File resultFile = new File("cache/files/", "CDK导出.xlsx");
        if(resultFile.exists()) {
            resultFile.delete();
            resultFile = new File("cache/files/", "CDK导出.xlsx");
        }
        ExcelWriter writer = ExcelUtil.getWriter(resultFile, "sheet1");
        List<String> headRow = Arrays.asList("CDK编号", "CDK", "是否带初邮", "是否可重复使用", "剩余可使用次数");
        writer.writeHeadRow(headRow);

        CDKDTO dto = new CDKDTO();
        dto.setCurrent(1);
        dto.setSize(9999999);
        List<CDK> list = queryCDK(dto, false).getRecords();

        for (CDK cdk : list) {
            List<String> row = new ArrayList<>(5);
            row.add(String.valueOf(cdk.getCdkNo()));
            row.add(cdk.getCdk());
            row.add(cdk.getTypeHasEmail() ? "带初邮CDK（完全版）" : "不带初邮CDK（黄金版）");
            row.add(cdk.getTypeReusable() ? "可重复使用CDK" : "一次性CDK");
            row.add(String.valueOf(cdk.getReuseRemainingTimes()));
            writer.writeRow(row);
        }
        writer.autoSizeColumnAll();
        writer.flush();
        writer.close();
    }

    @Override
    public Page<CDK> queryCDK(CDKDTO cdkDTO, boolean isDesensitization) {
        Page<CDK> page = Page.of(cdkDTO.getCurrent(), cdkDTO.getSize());
        LambdaQueryWrapper<CDK> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(StringUtils.isNotEmpty(cdkDTO.getCdk()), CDK::getCdk, cdkDTO.getCdk())
                .eq(cdkDTO.getCdkNo() != null, CDK::getCdkNo, cdkDTO.getCdkNo())
                .eq(cdkDTO.getTypeHasEmail() != null, CDK::getTypeHasEmail, cdkDTO.getTypeHasEmail())
                .eq(cdkDTO.getTypeReusable() != null, CDK::getTypeReusable, cdkDTO.getTypeReusable())
                // 如果直接用CDK编号 或 CDK内容进行查询，那么即使 isUsed=true 的CDK也能被查出来
                .eq(cdkDTO.getIsUsed() == null && StringUtils.isEmpty(cdkDTO.getCdk()) && cdkDTO.getCdkNo() == null, CDK::getIsUsed, false)
                .eq(cdkDTO.getIsUsed() != null, CDK::getIsUsed, cdkDTO.getIsUsed())
                .orderByDesc(CDK::getTypeReusable)
                .orderByDesc(CDK::getCdkNo);

        Page<CDK> result = this.page(page, wrapper);
        // 脱敏
        if(isDesensitization) {
            if(null != result.getRecords() && result.getRecords().size() > 0) {
                for(CDK record : result.getRecords()) {
                    String cdk = record.getCdk();
                    record.setCdk(cdk.substring(0, cdk.length()/3) + "******");
                }
            }
        }
        return result;
    }

    @Override
    public boolean delete(Long[] cdkNoList) {
        List<Long> list = Arrays.asList(cdkNoList);
        log.info("删除CDK，cdkNoList={}", list);

        LambdaQueryWrapper<CDK> wrapper = Wrappers.lambdaQuery();
        wrapper.in(CDK::getCdkNo, list);
        return this.remove(wrapper);
    }

    @Override
    public CDKRedeemVerifyVO redeemVerify(String cdk, Long accountNo) {
        CDKRedeemVerifyVO verify = new CDKRedeemVerifyVO();
        if(!validateCDK(cdk)) {
            verify.setState(CDK_INVALID);
            return verify;
        }
        CDK cdkObj = this.getById(cdk);
        RiotAccount account = riotAccountMapper.selectOne(new LambdaQueryWrapper<RiotAccount>().eq(RiotAccount::getAccountNo, accountNo));
        if(null == cdkObj) {
            verify.setState(CDK_NOT_EXIST);
            return verify;
        }
        if(null == account) {
            verify.setState(ACCOUNT_NOT_EXIST);
            return verify;
        }

        String verifyDetail = getCDKRedeemVerifyDetail(cdkObj, account, false);
        if(StringUtils.isEmpty(verifyDetail)) {
            verify.setState(NO_STOREFRONT_RECORD);
            return verify;
        }
        verify.setState(OK);
        verify.setDetail(verifyDetail);
        return verify;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public CDKRedeemVerifyVO redeem(String cdk, Long accountNo) {
        if(!validateCDK(cdk)) {
            return new CDKRedeemVerifyVO(CDK_INVALID);
        }
        CDK cdkObj = this.getById(cdk);
        RiotAccount account = riotAccountMapper.selectOne(new LambdaQueryWrapper<RiotAccount>().eq(RiotAccount::getAccountNo, accountNo));
        if(null == cdkObj) {
            log.info("尝试兑换CDK，兑换结果={}, cdk={}, 拳头账号编号accountNo={}", CDK_NOT_EXIST, cdk, accountNo);
            return new CDKRedeemVerifyVO(CDK_NOT_EXIST);
        }
        if(null == account) {
            log.info("尝试兑换CDK，兑换结果={}, cdk={}, 拳头账号编号accountNo={}", ACCOUNT_NOT_EXIST, cdk, accountNo);
            return new CDKRedeemVerifyVO(ACCOUNT_NOT_EXIST);
        }
        if(account.getIsDel()) {
            log.info("尝试兑换CDK，兑换结果={}, cdk={}, 拳头账号编号accountNo={}", ACCOUNT_ALREADY_REDEEMED, cdk, accountNo);
            return new CDKRedeemVerifyVO(ACCOUNT_ALREADY_REDEEMED);
        }
        if(cdkObj.getTypeHasEmail() != account.getHasEmail()) {
            log.info("尝试兑换CDK，兑换结果={}, cdk={}, 拳头账号编号accountNo={}", CDK_VERSION_ERROR, cdk, accountNo);
            return new CDKRedeemVerifyVO(CDK_VERSION_ERROR);
        }
        if(cdkObj.getIsUsed()) {
            log.info("尝试兑换CDK，兑换结果={}, cdk={}, 拳头账号编号accountNo={}", CDK_USED, cdk, accountNo);
            return new CDKRedeemVerifyVO(CDK_USED);
        }
        if(cdkObj.getTypeReusable() && cdkObj.getReuseRemainingTimes() <= 0) {
            log.info("尝试兑换CDK，兑换结果={}, cdk={}, 拳头账号编号accountNo={}", CDK_REUSE_REMAINING_TIMES_EXHAUSTED, cdk, accountNo);
            return new CDKRedeemVerifyVO(CDK_REUSE_REMAINING_TIMES_EXHAUSTED);
        }
        String verifyDetail = getCDKRedeemVerifyDetail(cdkObj, account, true);
        if(StringUtils.isEmpty(verifyDetail)) {
            log.info("尝试兑换CDK，兑换结果={}, cdk={}, 拳头账号编号accountNo={}", NO_STOREFRONT_RECORD, cdk, accountNo);
            return new CDKRedeemVerifyVO(NO_STOREFRONT_RECORD);
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
        history.setDetail(verifyDetail);
        cdkHistoryMapper.insert(history);

        log.info("尝试兑换CDK，兑换结果={}, cdk={}, 拳头账号编号accountNo={}", OK, cdk, accountNo);
        CDKRedeemVerifyVO verify = new CDKRedeemVerifyVO(OK);
        verify.setDetail(history.getDetail());
        return verify;
    }

    /**
     * 拼接 CDK 兑换前的确认信息
     * @param cdk     CDK对象
     * @param account 拳头账号对象
     * @param withSecret 是否包含敏感信息（账号名、密码、初邮、初邮密码）
     * @return 确认信息
     */
    private String getCDKRedeemVerifyDetail(CDK cdk, RiotAccount account, boolean withSecret) {
        BatchBothStoreFrontVO storeFront = sfService.queryBothByAccountId(null, account.getAccountNo());
        if(null == storeFront) {
            return null;
        }

        StringBuilder bonusOfferString = new StringBuilder();
        if(null == storeFront.getBonusOffer()) {
            bonusOfferString = new StringBuilder("夜市未开放");
        } else {
            BatchBothStoreFrontVO bonusOffer = storeFront.getBonusOffer();
            for(int i = 0; i < bonusOffer.getDisplayNameList().length; i++) {
                bonusOfferString.append(bonusOffer.getDisplayNameList()[i]).append(": ").append(bonusOffer.getDiscountPercentList()[i]);
                if(i < bonusOffer.getDisplayNameList().length - 1) {
                    bonusOfferString.append(", ");
                }
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("账号编号：").append(account.getAccountNo()).append("<br/>\n")
                .append("账号地区：").append(RiotAccountRegion.getRegionStrByCode(account.getRegion())).append("<br/>\n")
                .append("每日商店：").append(ArrayUtils.toString(storeFront.getDisplayNameList())).append("<br/>\n")
                .append("夜市商店：").append(bonusOfferString).append("<br/>\n")
                .append("账号版本：").append(account.getHasEmail() ? "完全版（带初邮）" : "黄金版（不带初邮）").append("<br/>\n")
                .append("CDK版本：").append(cdk.getTypeHasEmail() ? "完全版（带初邮）" : "黄金版（不带初邮）").append("<br/>\n");
        if(withSecret) {
            sb.append("拳头账号：").append(account.getUsername()).append("<br/>\n")
                    .append("拳头账号密码：").append(account.getPassword()).append("<br/>\n")
                    .append("初始邮箱：").append(account.getHasEmail() ? account.getEmail() : "不带初邮").append("<br/>\n")
                    .append("初始邮箱密码：").append(account.getHasEmail() ? account.getEmailPwd() : "不带初邮").append("<br/>\n");
        }

        return sb.toString();
    }

    @Override
    public Page<CDKHistory> queryCDKHistory(CDKHistoryDTO cdkHistoryDTO, boolean isDesensitization) {
        Date[] dates = new Date[2];
        if(StringUtils.isNotBlank(cdkHistoryDTO.getDate())) {
            dates = ValorantDateUtils.getDateTimeBaseOn8AM(cdkHistoryDTO.getDate(), false);
        }

        Page<CDKHistory> page = Page.of(cdkHistoryDTO.getCurrent(), cdkHistoryDTO.getSize());
        LambdaQueryWrapper<CDKHistory> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(StringUtils.isNotEmpty(cdkHistoryDTO.getCdk()), CDKHistory::getCdk, cdkHistoryDTO.getCdk())
                .eq(cdkHistoryDTO.getAccountNo() != null, CDKHistory::getAccountNo, cdkHistoryDTO.getAccountNo())
                .ge(StringUtils.isNotBlank(cdkHistoryDTO.getDate()), CDKHistory::getRedeemTime, dates[0])
                .lt(StringUtils.isNotBlank(cdkHistoryDTO.getDate()), CDKHistory::getRedeemTime, dates[1])
                .orderByDesc(CDKHistory::getRedeemTime);
        Page<CDKHistory> result = cdkHistoryMapper.selectPage(page, wrapper);
        // 脱敏
        if(isDesensitization) {
            if(null != result.getRecords() && result.getRecords().size() > 0) {
                for(CDKHistory record : result.getRecords()) {
                    String cdk = record.getCdk();
                    record.setCdk(cdk.substring(0, cdk.length()/3) + "******");
                    String detail = record.getDetail();
                    detail = detail.replaceAll("拳头账号密码：.*<br/>\n初始邮箱：", "拳头账号密码：******<br/>\n初始邮箱：");
                    detail = detail.replaceAll("初始邮箱密码：.*<br/>\n", "初始邮箱密码：******<br/>\n");
                    record.setDetail(detail);

                }
            }
        }
        return result;
    }

    @Override
    public CDKHistoryAndMoreCDKInfoVO queryCDKHistoryAndMoreCDKInfo(CDKHistoryDTO cdkHistoryDTO) {
        CDKHistoryAndMoreCDKInfoVO vo = new CDKHistoryAndMoreCDKInfoVO();
        if(!validateCDK(cdkHistoryDTO.getCdk())) {
            vo.setHistory(null);
            return vo;
        }
        vo.setHistory(queryCDKHistory(cdkHistoryDTO, false));
        CDK cdk = cdkMapper.selectById(cdkHistoryDTO.getCdk());
        if(null != cdk) {
            vo.setReuseRemainingTimes(cdk.getReuseRemainingTimes());
        }
        return vo;
    }

    private boolean validateCDK(String cdk) {
        return StringUtils.isNotBlank(cdk) && cdk.trim().length() == DEFAULT_CDK_LENGTH;
    }

}
