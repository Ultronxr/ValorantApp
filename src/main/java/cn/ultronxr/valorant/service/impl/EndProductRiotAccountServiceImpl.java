package cn.ultronxr.valorant.service.impl;

import cn.ultronxr.framework.bean.mybatis.mapper.SystemAccountMapper;
import cn.ultronxr.valorant.auth.RSO;
import cn.ultronxr.valorant.bean.DTO.EndProductRiotAccountDTO;
import cn.ultronxr.valorant.bean.enums.RiotAccountCreateState;
import cn.ultronxr.valorant.bean.mybatis.bean.EndProductRiotAccount;
import cn.ultronxr.valorant.bean.mybatis.bean.RiotAccount;
import cn.ultronxr.valorant.bean.mybatis.mapper.EndProductRiotAccountMapper;
import cn.ultronxr.valorant.service.EndProductRiotAccountService;
import cn.ultronxr.valorant.service.RSOService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static cn.ultronxr.valorant.bean.enums.RiotAccountCreateState.*;

/**
 * @author Ultronxr
 * @date 2023/07/05 14:57:16
 * @description
 */
@Service
@Slf4j
public class EndProductRiotAccountServiceImpl extends ServiceImpl<EndProductRiotAccountMapper, EndProductRiotAccount> implements EndProductRiotAccountService {

    @Autowired
    private RSOService rsoService;


    @Override
    public RiotAccountCreateState create(EndProductRiotAccount account) {
        if(StringUtils.isBlank(account.getUsername()) || StringUtils.isBlank(account.getPassword())) {
            log.info("成品拳头账号添加失败：{}", MISSING_REQUIRED_FIELD);
            return MISSING_REQUIRED_FIELD;
        }

        EndProductRiotAccount accountInDB = this.getOne(new LambdaQueryWrapper<EndProductRiotAccount>().eq(EndProductRiotAccount::getUsername, account.getUsername()));
        if(accountInDB != null) {
            log.info("成品拳头账号添加失败：{}", DUPLICATE);
            return DUPLICATE;
        }

        log.info("添加成品拳头账号：{}", account);
        // 手动设置是否带初邮
        account.setHasEmail(StringUtils.isNotEmpty(account.getEmail()));
        RiotAccount tempAccount = new RiotAccount();
        BeanUtils.copyProperties(account, tempAccount);
        RSO rso = rsoService.requestRSOByAccount(tempAccount);
        if(null != rso) {
            account.setUserId(rso.getUserId());
            account.setAccessToken(rso.getAccessToken());
            account.setAccessTokenExpireAt(rso.getAccessTokenExpireAt());
            account.setEntitlementsToken(rso.getEntitlementsToken());
            account.setStatus(1);
            if(this.save(account)) {
                log.info("成品拳头账号添加成功。username={}", account.getUsername());
                return OK;
            }
        }
        log.info("成品拳头账号添加失败：{}", AUTH_FAILURE);
        return AUTH_FAILURE;
    }

    @Override
    public boolean update(EndProductRiotAccount account) {
        return updateById(account);
    }

    @Override
    public Page<EndProductRiotAccount> managementQueryAccount(EndProductRiotAccountDTO accountDTO) {
        Page<EndProductRiotAccount> page = Page.of(accountDTO.getCurrent(), accountDTO.getSize());

        LambdaQueryWrapper<EndProductRiotAccount> wrapper = Wrappers.lambdaQuery();
        wrapper.select(EndProductRiotAccount::getAccountNo, EndProductRiotAccount::getUserId, EndProductRiotAccount::getUsername,
                        EndProductRiotAccount::getRegion, EndProductRiotAccount::getStatus, EndProductRiotAccount::getTitle,
                        EndProductRiotAccount::getNote, EndProductRiotAccount::getImg, EndProductRiotAccount::getPrice)
                .eq(accountDTO.getAccountNo() != null, EndProductRiotAccount::getAccountNo, accountDTO.getAccountNo())
                .eq(accountDTO.getRegion() != null, EndProductRiotAccount::getRegion, accountDTO.getRegion())
                .eq(accountDTO.getStatus() != null, EndProductRiotAccount::getStatus, accountDTO.getStatus())
                // 默认不显示已售出状态的数据
                .ne(accountDTO.getStatus() == null, EndProductRiotAccount::getStatus, 10)
                .gt(accountDTO.getPriceLow() != null, EndProductRiotAccount::getPrice, accountDTO.getPriceLow())
                .lt(accountDTO.getPriceHigh() != null, EndProductRiotAccount::getPrice, accountDTO.getPriceHigh())
                .orderByAsc(EndProductRiotAccount::getStatus)
                .orderByAsc(EndProductRiotAccount::getAccountNo)
                .orderByAsc(EndProductRiotAccount::getPrice);

        return this.getBaseMapper().selectPage(page, wrapper);
    }

    @Override
    public EndProductRiotAccount redeem(Long accountNo) {
        return this.getById(accountNo);
    }

    @Override
    public Page<EndProductRiotAccount> queryAccount(EndProductRiotAccountDTO accountDTO) {
        Page<EndProductRiotAccount> page = Page.of(accountDTO.getCurrent(), accountDTO.getSize());

        LambdaQueryWrapper<EndProductRiotAccount> wrapper = Wrappers.lambdaQuery();
        wrapper.select(EndProductRiotAccount::getAccountNo, EndProductRiotAccount::getUserId, EndProductRiotAccount::getRegion,
                        EndProductRiotAccount::getTitle, EndProductRiotAccount::getImg, EndProductRiotAccount::getPrice)
                .eq(accountDTO.getAccountNo() != null, EndProductRiotAccount::getAccountNo, accountDTO.getAccountNo())
                .eq(accountDTO.getRegion() != null, EndProductRiotAccount::getRegion, accountDTO.getRegion())
                // 只筛选正在出售的数据
                .eq(EndProductRiotAccount::getStatus, 1)
                .gt(accountDTO.getPriceLow() != null, EndProductRiotAccount::getPrice, accountDTO.getPriceLow())
                .lt(accountDTO.getPriceHigh() != null, EndProductRiotAccount::getPrice, accountDTO.getPriceHigh())
                .orderByAsc(EndProductRiotAccount::getAccountNo)
                .orderByAsc(EndProductRiotAccount::getPrice);

        return this.getBaseMapper().selectPage(page, wrapper);
    }

    @Override
    public EndProductRiotAccount getOne(Long accountNo) {
        return null;
    }

}
