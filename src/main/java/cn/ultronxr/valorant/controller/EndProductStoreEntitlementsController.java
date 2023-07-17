package cn.ultronxr.valorant.controller;

import cn.ultronxr.common.bean.AjaxResponse;
import cn.ultronxr.common.util.AjaxResponseUtils;
import cn.ultronxr.valorant.bean.mybatis.bean.EndProductRiotAccount;
import cn.ultronxr.valorant.bean.mybatis.mapper.EndProductRiotAccountMapper;
import cn.ultronxr.valorant.service.EndProductStoreEntitlementsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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

    //@Autowired
    //private EndProductRiotAccountMapper endProductRiotAccountMapper;
    //
    //
    //@RequestMapping("/generateImg")
    //@ResponseBody
    //public AjaxResponse testGenerateImg() {
    //    EndProductRiotAccount account = endProductRiotAccountMapper.selectById(4);
    //    String url = seService.generateSkinImg(account);
    //    return AjaxResponseUtils.success(url);
    //}


}
