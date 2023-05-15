package cn.ultronxr.valorant.controller;

import cn.ultronxr.common.bean.AjaxResponse;
import cn.ultronxr.common.util.AjaxResponseUtils;
import cn.ultronxr.distributed.datanode.DataNodeManager;
import cn.ultronxr.framework.annotation.AdminAuthRequired;
import cn.ultronxr.valorant.bean.DTO.BatchQueryBothDTO;
import cn.ultronxr.valorant.service.StoreFrontService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RTopic;
import org.redisson.api.listener.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
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
public class StoreFrontController implements ApplicationRunner {

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

    @Order(Ordered.LOWEST_PRECEDENCE)
    @Override
    public void run(ApplicationArguments args) throws Exception {
        if(dataNodeManager.isMainDataNode()) {
            return;
        }
        String missionName = "batchUpdateBoth";
        // 所有子数据节点订阅 redis topic channel ，如果收到主数据节点发布的更新任务，那么进行数据更新
        RTopic rtopic = dataNodeManager.getRTopic();
        rtopic.addListener(String.class, new MessageListener<String>() {
            @Override
            public void onMessage(CharSequence channel, String msg) {
                log.info("接收到主数据节点发布的任务，channel={}, msg={}", channel, msg);
                if(StringUtils.isNotEmpty(msg) && msg.equals(missionName)) {
                    log.info("子数据节点开始更新数据。");
                    sfService.batchUpdateBoth(true);
                }
            }
        });
        log.info("子数据节点订阅 redis topic channel - {} 完成。", rtopic.getChannelNames());
    }

    @PostMapping("/batchQueryBoth")
    @ResponseBody
    public AjaxResponse batchQueryBoth(@RequestBody BatchQueryBothDTO batchQueryBothDTO) {
        return AjaxResponseUtils.success(sfService.batchQueryBoth(batchQueryBothDTO));
    }

}
