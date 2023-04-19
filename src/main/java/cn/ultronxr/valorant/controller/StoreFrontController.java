package cn.ultronxr.valorant.controller;

import cn.ultronxr.common.bean.AjaxResponse;
import cn.ultronxr.common.util.AjaxResponseUtils;
import cn.ultronxr.valorant.service.StoreFrontService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Ultronxr
 * @date 2023/02/22 11:11
 * @description
 */
@Controller
@RequestMapping("/valorant/storefront")
@Slf4j
public class StoreFrontController {

    @Autowired
    private StoreFrontService sfService;


    @GetMapping("/singleItemOffers")
    @ResponseBody
    public AjaxResponse singleItemOffers(String userId, String date) {
        if(StringUtils.isEmpty(userId) || StringUtils.isEmpty(date)) {
            return AjaxResponseUtils.success();
        }
        return AjaxResponseUtils.success(sfService.toVO(sfService.singleItemOffers(userId, date)));
    }

    @GetMapping("/bonusOffers")
    @ResponseBody
    public AjaxResponse bonusOffers(String userId, String date) {
        if(StringUtils.isEmpty(userId) || StringUtils.isEmpty(date)) {
            return AjaxResponseUtils.success();
        }
        return AjaxResponseUtils.success(sfService.toVO(sfService.bonusOffers(userId, date)));
    }

    @Deprecated
    @PostMapping("/batchUpdateSingle")
    @ResponseBody
    public AjaxResponse batchUpdateSingle() {
        if(sfService.batchUpdateSingle()) {
            return AjaxResponseUtils.success();
        }
        return AjaxResponseUtils.fail();
    }

    @Deprecated
    @PostMapping("/batchUpdateBonus")
    @ResponseBody
    public AjaxResponse batchUpdateBonus() {
        if(sfService.batchUpdateBonus()) {
            return AjaxResponseUtils.success();
        }
        return AjaxResponseUtils.fail();
    }

    @PostMapping("/batchUpdateBoth")
    @ResponseBody
    public AjaxResponse batchUpdateBoth() {
        if(sfService.batchUpdateBoth()) {
            return AjaxResponseUtils.success();
        }
        return AjaxResponseUtils.fail();
    }

    @Deprecated
    @GetMapping("/batchQuerySingle")
    @ResponseBody
    public AjaxResponse batchQuerySingle(String displayName) {
        return AjaxResponseUtils.success(sfService.batchQuerySingle(null, displayName));
    }

    @Deprecated
    @GetMapping("/batchQueryBonus")
    @ResponseBody
    public AjaxResponse batchQueryBonus(String displayName) {
        return AjaxResponseUtils.success(sfService.batchQueryBonus(null, displayName));
    }

    @GetMapping("/batchQueryBoth")
    @ResponseBody
    public AjaxResponse batchQueryBoth(String skin1, String skin2, String skin3, String skin4,
                                       String bonusSkin1, String bonusSkin2, String bonusSkin3) {
        return AjaxResponseUtils.success(sfService.batchQueryBoth(null, skin1, skin2, skin3, skin4, bonusSkin1, bonusSkin2, bonusSkin3));
    }

}
