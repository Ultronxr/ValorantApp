package cn.ultronxr.distributed.datanode;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
public class DataNodeManager {

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    /** redis zset key */
    @Value("${valorant.datanode.redis-key}")
    private String REDIS_KEY;

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
     * 清空 datanode （早上6点由 main datanode 执行）
     */
    @Scheduled(cron = "${valorant.datanode.cron.clear-datanode}")
    protected void clearDataNode() {
        if(dataNodeId.equals(mainDataNodeId)) {
            Boolean res = redisTemplate.delete(REDIS_KEY);
            if(null != res && res) {
                log.info("清空 DataNode ZSet 完成。");
            }
        }
    }

    /**
     * 插入 nodeId （早上7点统一写入ID）
     */
    @Scheduled(cron = "${valorant.datanode.cron.add-datanode}")
    protected void addDataNode() {
        Boolean res = redisTemplate.opsForZSet().add(REDIS_KEY, dataNodeId, 1);
        if(null != res && res) {
            log.info("向 ZSet 中添加 dataNodeId={}", dataNodeId);
        }
    }

    /**
     * 获取总节点数量
     */
    public Long getTotal() {
        return redisTemplate.opsForZSet().size(REDIS_KEY);
    }

    /**
     * 获取当前节点下标/排名（下标从0开始）
     */
    public Long getIndex() {
        return redisTemplate.opsForZSet().rank(REDIS_KEY, dataNodeId);
    }

    public String getDataNodeId() {
        return dataNodeId;
    }

}
