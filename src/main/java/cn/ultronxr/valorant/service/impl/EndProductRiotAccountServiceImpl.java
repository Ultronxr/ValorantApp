package cn.ultronxr.valorant.service.impl;

import cn.ultronxr.valorant.auth.RSO;
import cn.ultronxr.valorant.bean.DTO.EndProductRiotAccountDTO;
import cn.ultronxr.valorant.bean.VO.EndProductWeaponSkinVO;
import cn.ultronxr.valorant.bean.enums.RiotAccountCreateState;
import cn.ultronxr.valorant.bean.mybatis.bean.EndProductRiotAccount;
import cn.ultronxr.valorant.bean.mybatis.mapper.EndProductRiotAccountMapper;
import cn.ultronxr.valorant.bean.mybatis.mapper.EndProductStoreEntitlementsMapper;
import cn.ultronxr.valorant.service.EndProductRiotAccountService;
import cn.ultronxr.valorant.service.EndProductStoreEntitlementsService;
import cn.ultronxr.valorant.service.RSOService;
import cn.ultronxr.valorant.util.BeanUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

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

    @Autowired
    private EndProductStoreEntitlementsService storeEntitlementsService;

    @Autowired
    private EndProductStoreEntitlementsMapper storeEntitlementsMapper;


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
        account.setHasEmail(StringUtils.isNotBlank(account.getEmail()));
        RSO rso = rsoService.requestRSOByAccount(BeanUtils.transformToRiotAccountFromEndProduct(account));
        if(null != rso) {
            account.setUserId(rso.getUserId());
            account.setAccessToken(rso.getAccessToken());
            account.setAccessTokenExpireAt(rso.getAccessTokenExpireAt());
            account.setEntitlementsToken(rso.getEntitlementsToken());
            account.setStatus(1);
            if(this.save(account)) {
                log.info("成品拳头账号添加成功。username={}", account.getUsername());
                // 请求API获取该账号的库存皮肤信息
                // （accountNo 是数据库自动填充的，所以这里要从数据库取一次）
                account = this.getOne(new LambdaQueryWrapper<EndProductRiotAccount>()
                                        .eq(EndProductRiotAccount::getUserId, account.getUserId()));
                storeEntitlementsService.skins(account);
                // 生成库存皮肤截图
                String imgUrl = storeEntitlementsService.generateSkinImg(account);
                if(StringUtils.isNotEmpty(imgUrl)) {
                    account.setImg(imgUrl);
                    this.update(new LambdaUpdateWrapper<EndProductRiotAccount>()
                                    .eq(EndProductRiotAccount::getAccountNo, account.getAccountNo())
                                    .set(EndProductRiotAccount::getImg, imgUrl));
                }
                return OK;
            }
        }
        log.info("成品拳头账号添加失败：{}", AUTH_FAILURE);
        return AUTH_FAILURE;
    }

    @Override
    public boolean update(EndProductRiotAccount account) {
        LambdaUpdateWrapper<EndProductRiotAccount> wrapper = Wrappers.lambdaUpdate();
        wrapper.eq(EndProductRiotAccount::getAccountNo, account.getAccountNo())
                .set(account.getRegion() != null, EndProductRiotAccount::getRegion, account.getRegion())
                .set(account.getPrice() != null, EndProductRiotAccount::getPrice, account.getPrice())
                .set(account.getStatus() != null, EndProductRiotAccount::getStatus, account.getStatus())
                .set(StringUtils.isNotBlank(account.getTitle()), EndProductRiotAccount::getTitle, account.getTitle())
                .set(StringUtils.isNotBlank(account.getNote()), EndProductRiotAccount::getNote, account.getNote());
        return this.update(wrapper);
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
                .ge(accountDTO.getPriceLow() != null, EndProductRiotAccount::getPrice, accountDTO.getPriceLow())
                .le(accountDTO.getPriceHigh() != null, EndProductRiotAccount::getPrice, accountDTO.getPriceHigh())
                .orderByAsc(accountDTO.getPriceOrder() != null && accountDTO.getPriceOrder() == 0, EndProductRiotAccount::getPrice)
                .orderByDesc(accountDTO.getPriceOrder() != null && accountDTO.getPriceOrder() == 1, EndProductRiotAccount::getPrice)
                .orderByAsc(EndProductRiotAccount::getStatus)
                .orderByAsc(EndProductRiotAccount::getAccountNo);

        return this.page(page, wrapper);
    }

    @Override
    public EndProductRiotAccount redeem(Long accountNo) {
        LambdaQueryWrapper<EndProductRiotAccount> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(EndProductRiotAccount::getAccountNo, accountNo)
                .select(EndProductRiotAccount::getRegion, EndProductRiotAccount::getPrice, EndProductRiotAccount::getAccountNo,
                        EndProductRiotAccount::getUsername, EndProductRiotAccount::getPassword,
                        EndProductRiotAccount::getEmail, EndProductRiotAccount::getEmailPwd);
        return this.getOne(wrapper);
    }

    @Override
    public Page<EndProductRiotAccount> queryAccount(EndProductRiotAccountDTO accountDTO) {
        Page<EndProductRiotAccount> page = Page.of(accountDTO.getCurrent(), accountDTO.getSize());

        //LambdaQueryWrapper<EndProductRiotAccount> wrapper = Wrappers.lambdaQuery();
        //wrapper.select(EndProductRiotAccount::getAccountNo, EndProductRiotAccount::getRegion,
        //                EndProductRiotAccount::getTitle, EndProductRiotAccount::getImg, EndProductRiotAccount::getPrice)
        //        .eq(accountDTO.getAccountNo() != null, EndProductRiotAccount::getAccountNo, accountDTO.getAccountNo())
        //        .eq(accountDTO.getRegion() != null, EndProductRiotAccount::getRegion, accountDTO.getRegion())
        //        // 只筛选正在出售的数据
        //        .eq(EndProductRiotAccount::getStatus, 1)
        //        .ge(accountDTO.getPriceLow() != null, EndProductRiotAccount::getPrice, accountDTO.getPriceLow())
        //        .le(accountDTO.getPriceHigh() != null, EndProductRiotAccount::getPrice, accountDTO.getPriceHigh())
        //        .orderByAsc(accountDTO.getPriceOrder() != null && accountDTO.getPriceOrder() == 0, EndProductRiotAccount::getPrice)
        //        .orderByDesc(accountDTO.getPriceOrder() != null && accountDTO.getPriceOrder() == 1, EndProductRiotAccount::getPrice)
        //        .orderByAsc(EndProductRiotAccount::getAccountNo);
        //
        //return this.page(page, wrapper);
        return this.getBaseMapper().queryAccount(page, accountDTO);
    }

    @Override
    public EndProductRiotAccount getOne(Long accountNo) {
        LambdaQueryWrapper<EndProductRiotAccount> wrapper = Wrappers.lambdaQuery();
        wrapper.select(EndProductRiotAccount::getAccountNo, EndProductRiotAccount::getRegion,
                        EndProductRiotAccount::getTitle, EndProductRiotAccount::getImg, EndProductRiotAccount::getPrice)
                .eq(EndProductRiotAccount::getAccountNo, accountNo);
        EndProductRiotAccount account = this.getOne(wrapper);

        List<EndProductWeaponSkinVO> skinVOList = storeEntitlementsMapper.selectSkinForOneAccount(accountNo);
        StringBuilder sb = new StringBuilder();
        for(EndProductWeaponSkinVO skinVO : skinVOList) {
            String skins = Arrays.toString(skinVO.getDisplayNameList());
            sb.append(skins.substring(1, skins.length()-1));
            sb.append("<br/>\n");
        }
        account.setNote(sb.toString());

        return account;
    }

}
