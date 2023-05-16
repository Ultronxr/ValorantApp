package cn.ultronxr.distributed.datanode;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author Ultronxr
 * @date 2023/05/08 16:32:44
 * @description 分布式数据节点管理（分布式批量更新每日商店+夜市，以突破拳头账号认证的API速率限制）
 */
@Component
@Slf4j
public class DataNodeManager implements ApplicationRunner {

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    /** zset 的 redis key ，该 zset 用于维护所有在线的数据节点ID */
    @Value("${valorant.datanode.redis-zset-key}")
    private String redisZsetKey;

    /** redis topic 的名称，该 topic 用于主数据节点发布数据更新任务、子数据节点订阅数据更新任务 */
    @Value("${valorant.datanode.redis-topic-channel}")
    private String redisTopicChannel;

    /** 主数据节点ID */
    @Value("${valorant.datanode.main-datanode-id}")
    private String mainDataNodeId;

    /** 当前数据节点ID */
    private String dataNodeId;


    /**
     * 从启动参数中取值
     */
    @PostConstruct
    protected void initSystemProperty() {
        dataNodeId = System.getProperty("dataNodeId");
        log.info("从启动参数中取值：dataNodeId={}", dataNodeId);
    }

    /**
     * 清空 datanode redis zset （定时由 main datanode 执行）
     */
    @Scheduled(cron = "${valorant.datanode.cron.clear-datanode}")
    public void clearDataNode() {
        if(isMainDataNode()) {
            Boolean res = redisTemplate.delete(redisZsetKey);
            if(null != res && res) {
                log.info("清空 DataNode ZSet 完成。");
            }
        }
    }

    /**
     * 向 redis zset 中插入 nodeId （默认定时统一写入ID，或者由数据节点主动调用）
     */
    @Scheduled(cron = "${valorant.datanode.cron.add-datanode}")
    public void addDataNode() {
        Boolean res = redisTemplate.opsForZSet().add(redisZsetKey, dataNodeId, 1);
        if(null != res && res) {
            log.info("向 ZSet 中添加 dataNodeId={}", dataNodeId);
        }
    }

    /**
     * 从 redis zset 中获取总节点数量
     */
    public Long getTotal() {
        return redisTemplate.opsForZSet().size(redisZsetKey);
    }

    /**
     * 从 redis zset 中获取当前数据节点下标/排名（下标从0开始）
     */
    public Long getIndex() {
        return redisTemplate.opsForZSet().rank(redisZsetKey, dataNodeId);
    }

    /**
     * 检查本数据节点自身是否是主数据节点
     */
    public boolean isMainDataNode() {
        return mainDataNodeId.equals(getDataNodeId());
    }

    /**
     * 获取本数据节点的ID
     */
    public String getDataNodeId() {
        return dataNodeId;
    }

    @Order(Ordered.LOWEST_PRECEDENCE)
    @Override
    public void run(ApplicationArguments args) throws Exception {
        addDataNode();
        log.info("项目启动时自动注册 datanode 完成。");
    }

    public RTopic getRTopic() {
        // 请注意：这里的 codec 需要与 topic channel 中发送的消息类型对应，否则会报无法解码数据异常！
        return redissonClient.getTopic(redisTopicChannel, new StringCodec());
    }

    /**
     * 主数据节点向 redis topic channel 中发布更新任务（所有子数据节点订阅获取到这个任务之后，进行数据更新）（1对多）<br/>
     * 有关子数据节点的订阅，请查看 {@link cn.ultronxr.valorant.controller.StoreFrontController#run(ApplicationArguments)}
     */
    public void publishUpdateMission() {
        String missionName = "batchUpdateBoth";
        RTopic rtopic = getRTopic();
        long clientsNumber = rtopic.publish(missionName);
        log.info("主数据节点向 redis topic channel 发布更新任务，channel={}, msg={}, clientsNumber={}", rtopic.getChannelNames(), missionName, clientsNumber);
    }

}
