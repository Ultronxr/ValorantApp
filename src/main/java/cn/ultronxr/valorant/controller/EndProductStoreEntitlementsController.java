package cn.ultronxr.valorant.controller;

import cn.ultronxr.valorant.service.EndProductStoreEntitlementsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

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



}
