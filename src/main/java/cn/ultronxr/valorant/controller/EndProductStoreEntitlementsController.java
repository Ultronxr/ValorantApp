package cn.ultronxr.valorant.controller;

import cn.ultronxr.common.bean.AjaxResponse;
import cn.ultronxr.common.util.AjaxResponseUtils;
import cn.ultronxr.framework.annotation.AdminAuthRequired;
import cn.ultronxr.valorant.bean.mybatis.bean.EndProductRiotAccount;
import cn.ultronxr.valorant.bean.mybatis.bean.EndProductStoreEntitlements;
import cn.ultronxr.valorant.bean.mybatis.mapper.EndProductRiotAccountMapper;
import cn.ultronxr.valorant.bean.mybatis.mapper.EndProductStoreEntitlementsMapper;
import cn.ultronxr.valorant.service.EndProductStoreEntitlementsService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Ultronxr
 * @date 2023/07/05 10:37:13
 * @description 账号已拥有物品 相关的controller
 */
@Controller
@RequestMapping("/valorant/endProduct/storeEntitlements")
@Slf4j
public class EndProductStoreEntitlementsController {

    @Autowired
    private EndProductStoreEntitlementsService seService;

    @Autowired
    private EndProductRiotAccountMapper endProductRiotAccountMapper;

    @Autowired
    private EndProductStoreEntitlementsMapper endProductStoreEntitlementsMapper;


    @AdminAuthRequired
    @GetMapping("/generateSkinImg")
    @ResponseBody
    public AjaxResponse generateSkinImg(@RequestParam Long accountNo) {
        log.info("需要重新生成库存皮肤图片。accountNo={}", accountNo);
        EndProductRiotAccount account = endProductRiotAccountMapper.selectById(accountNo);
        // 检查账号编号是否合法
        if(null != account) {
            // 检查数据库中是否有该账号的库存皮肤记录
            if(!endProductStoreEntitlementsMapper.exists(
                    new LambdaQueryWrapper<EndProductStoreEntitlements>()
                            .eq(EndProductStoreEntitlements::getAccountNo, accountNo))
            ) {
                log.info("数据库中无库存皮肤记录，重新请求API。");
                List list = seService.getSkinByAPI(account);
                if(null == list || list.size() == 0) {
                    return AjaxResponseUtils.fail("账号库存皮肤获取失败！");
                }
            }

            log.info("正在重新生成库存皮肤图片。");
            String url = seService.generateSkinImg(account);
            if(StringUtils.isNotEmpty(url)) {
                account.setImg(url);
                endProductRiotAccountMapper.update(account,
                        new LambdaUpdateWrapper<EndProductRiotAccount>()
                                .eq(EndProductRiotAccount::getAccountNo, account.getAccountNo())
                                .set(EndProductRiotAccount::getImg, url));
                log.info("重新生成库存皮肤图片成功。accountNo={}", accountNo);
                return AjaxResponseUtils.success(url);
            }
            return AjaxResponseUtils.fail("生成失败！");
        }
        return AjaxResponseUtils.fail("账号不存在！");
    }

}
