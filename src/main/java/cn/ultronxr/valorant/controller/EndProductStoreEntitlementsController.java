package cn.ultronxr.valorant.controller;

import cn.ultronxr.common.bean.AjaxResponse;
import cn.ultronxr.common.util.AjaxResponseUtils;
import cn.ultronxr.framework.annotation.AdminAuthRequired;
import cn.ultronxr.valorant.bean.mybatis.bean.EndProductRiotAccount;
import cn.ultronxr.valorant.bean.mybatis.mapper.EndProductRiotAccountMapper;
import cn.ultronxr.valorant.service.EndProductStoreEntitlementsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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


    @AdminAuthRequired
    @GetMapping("/generateImg")
    @ResponseBody
    public AjaxResponse testGenerateImg(@RequestParam Long accountNo) {
        EndProductRiotAccount account = endProductRiotAccountMapper.selectById(accountNo);
        if(null != account) {
            String url = seService.generateSkinImg(account);
            return AjaxResponseUtils.success(url);
        }
        return AjaxResponseUtils.fail("账号不存在！");
    }

}
