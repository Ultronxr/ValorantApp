package cn.ultronxr.distributed.datanode;

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
import org.springframework.stereotype.Component;

/**
 * @author Ultronxr
 * @date 2023/05/17 16:24:26
 * @description 子数据节点订阅 redis topic channel
 */
@Component
@Slf4j
public class TopicChannelListener implements ApplicationRunner {

    @Autowired
    private StoreFrontService sfService;

    @Autowired
    private DataNodeManager dataNodeManager;


    @Order(Ordered.LOWEST_PRECEDENCE)
    @Override
    public void run(ApplicationArguments args) throws Exception {
        dataNodeManager.addDataNode();
        log.info("项目启动时自动注册 datanode 完成。");

        if(dataNodeManager.isMainDataNode()) {
            return;
        }
        // 所有子数据节点订阅 redis topic channel ，如果收到主数据节点发布的更新任务，那么进行数据更新
        RTopic rtopic = dataNodeManager.getRTopic();
        rtopic.addListener(String.class, new MessageListener<String>() {
            @Override
            public void onMessage(CharSequence channel, String msg) {
                msg = StringUtils.isNotEmpty(msg) ? msg : "";
                log.info("接收到主数据节点发布的任务，channel={}, msg={}", channel, msg);
                switch (msg) {
                    case "batchUpdateBoth" : {
                        log.info("子数据节点开始更新每日商店+夜市数据。");
                        sfService.batchUpdateBoth(true);
                        break;
                    }
                    default: {
                        log.info("未事先约定的任务，不予响应！");
                        break;
                    }
                }
            }
        });
        log.info("子数据节点订阅 redis topic channel - {} 完成。", rtopic.getChannelNames());
    }

}
