package cn.ultronxr.valorant.controller;

import cn.ultronxr.common.bean.AjaxResponse;
import cn.ultronxr.common.util.AjaxResponseUtils;
import cn.ultronxr.distributed.datanode.DataNodeManager;
import cn.ultronxr.framework.annotation.AdminAuthRequired;
import cn.ultronxr.valorant.bean.DTO.BatchQueryBothDTO;
import cn.ultronxr.valorant.service.StoreFrontService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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

    @Autowired
    private DataNodeManager dataNodeManager;


    //@GetMapping("/singleItemOffers")
    //@ResponseBody
    //public AjaxResponse singleItemOffers(String userId, String date) {
    //    if(StringUtils.isEmpty(userId) || StringUtils.isEmpty(date)) {
    //        return AjaxResponseUtils.success();
    //    }
    //    return AjaxResponseUtils.success(sfService.toVO(sfService.singleItemOffers(userId, date)));
    //}
    //
    //@GetMapping("/bonusOffers")
    //@ResponseBody
    //public AjaxResponse bonusOffers(String userId, String date) {
    //    if(StringUtils.isEmpty(userId) || StringUtils.isEmpty(date)) {
    //        return AjaxResponseUtils.success();
    //    }
    //    return AjaxResponseUtils.success(sfService.toVO(sfService.bonusOffers(userId, date)));
    //}

    @AdminAuthRequired
    @PostMapping("/batchUpdateBoth")
    @ResponseBody
    public AjaxResponse batchUpdateBoth() {
        // 如果是主数据节点，那么同时向 redis topic 发布更新任务，触发子数据节点同时开始更新
        if(dataNodeManager.isMainDataNode()) {
            dataNodeManager.publishUpdateMission();
        }
        // 数据节点本身开始数据更新
        if(sfService.batchUpdateBoth(true)) {
            return AjaxResponseUtils.success();
        }
        return AjaxResponseUtils.fail();
    }

    @PostMapping("/batchQueryBoth")
    @ResponseBody
    public AjaxResponse batchQueryBoth(@RequestBody BatchQueryBothDTO batchQueryBothDTO) {
        return AjaxResponseUtils.success(sfService.batchQueryBoth(batchQueryBothDTO));
    }

}
