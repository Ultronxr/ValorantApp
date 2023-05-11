package cn.ultronxr.valorant.service.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONObject;
import cn.ultronxr.distributed.datanode.DataNodeManager;
import cn.ultronxr.valorant.api.impl.StoreFrontAPI;
import cn.ultronxr.valorant.auth.RSO;
import cn.ultronxr.valorant.bean.DTO.BatchQueryBothDTO;
import cn.ultronxr.valorant.bean.VO.BatchBothStoreFrontVO;
import cn.ultronxr.valorant.bean.VO.StoreFrontVO;
import cn.ultronxr.valorant.bean.mybatis.bean.RiotAccount;
import cn.ultronxr.valorant.bean.mybatis.bean.StoreFront;
import cn.ultronxr.valorant.bean.mybatis.mapper.RiotAccountMapper;
import cn.ultronxr.valorant.bean.mybatis.mapper.StoreFrontMapper;
import cn.ultronxr.valorant.exception.APIUnauthorizedException;
import cn.ultronxr.valorant.service.RSOService;
import cn.ultronxr.valorant.service.StoreFrontService;
import cn.ultronxr.valorant.service.WeaponSkinService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.jeffreyning.mybatisplus.service.MppServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author Ultronxr
 * @date 2023/02/22 10:21
 * @description
 */
@Service
@Slf4j
public class StoreFrontServiceImpl extends MppServiceImpl<StoreFrontMapper, StoreFront> implements StoreFrontService {

    @Autowired
    private StoreFrontAPI sfAPI;

    @Autowired
    private StoreFrontMapper sfMapper;

    @Autowired
    private WeaponSkinService wsService;

    @Autowired
    private RSOService rsoService;

    @Autowired
    private RiotAccountMapper accountMapper;

    @Autowired
    private DataNodeManager dataNodeManager;

    /** 事先每个节点更新账号 access token 的数据量 */
    @Value("${valorant.storefront.pre-update-account-token.count-per-datanode}")
    private Long priorUpdateAccountTokenCountPerDataNode;


    @Override
    public List<StoreFront> singleItemOffers(String userId, String date) {
        log.info("获取每日商店数据：userId={} , date={}", userId, date);
        List<StoreFront> list = queryDB(userId, date, false);

        // 如果数据库中没有数据且过了今天8点，则请求API获取，并插入数据库
        // 注意：已经过去的日期是无法再通过接口获取数据的，如果当天没有存入数据库，那么只能返回空结果！
        if((null == list || list.isEmpty()) && isDateValid(date)) {
            log.info("数据库没有对应的每日商店数据，尝试请求API获取：userId={} , date={}", userId, date);
            RSO rso = rsoService.fromAccount(userId);
            JSONObject jObj = requestAPI(rso);
            if(null == jObj) {
                log.warn("StoreFront API 请求异常，跳过解析数据。userId={} , date={}", userId, date);
                return list;
            }
            list = sfAPI.getSingleItemOffers(jObj, userId);
            this.saveOrUpdateBatchByMultiId(list);

            // 既然请求API了，那么顺便更新一下夜市数据
            List<StoreFront> byTheWayList = sfAPI.getBonusOffers(jObj, userId);
            this.saveOrUpdateBatchByMultiId(byTheWayList);
        }

        return list;
    }

    @Override
    public void singleItemOffersWithSleep(@NotNull String userId, @NotNull String date, float sleepSeconds) {
        log.info("获取每日商店数据（WithSleep）：userId={} , date={}", userId, date);
        List<StoreFront> list = queryDB(userId, date, false);
        if(CollectionUtils.isNotEmpty(list) || !isDateValid(date)) {
            return;
        }

        log.info("数据库没有对应的每日商店数据，尝试请求API获取：userId={} , date={}", userId, date);

        RSO rso = rsoService.fromAccount(userId);
        boolean skipSleep = !rso.isAccessTokenExpired();

        JSONObject jObj = requestAPI(rso);
        if(null != jObj) {
            userId = jObj.getStr("userId");
            list = sfAPI.getSingleItemOffers(jObj, userId);
            this.saveOrUpdateBatchByMultiId(list);

            // 既然请求API了，那么顺便更新一下夜市数据
            List<StoreFront> bonusList = sfAPI.getBonusOffers(jObj, userId);
            this.saveOrUpdateBatchByMultiId(bonusList);
        } else {
            log.warn("StoreFront API 请求异常，跳过解析数据。userId={} , date={}", userId, date);
        }

        if(skipSleep) {
            return;
        }
        // 请求API之后等待时间
        try {
            log.info("请求API后等待时间：{} 秒", sleepSeconds);
            Thread.sleep((long) (sleepSeconds * 1000.0f));
        } catch (InterruptedException e) {
            log.warn("Thread.sleep 抛出异常！", e);
        }
    }

    @Override
    public List<StoreFront> bonusOffers(String userId, String date) {
        log.info("获取夜市数据：userId={} , date={}", userId, date);
        List<StoreFront> list = queryDB(userId, date, true);

        // 如果数据库中没有数据，则请求API获取，并插入数据库
        if((null == list || list.isEmpty()) && isDateValid(date)) {
            log.info("数据库没有对应的夜市数据，尝试请求API获取：userId={} , date={}", userId, date);
            RSO rso = rsoService.fromAccount(userId);
            JSONObject jObj = requestAPI(rso);
            list = sfAPI.getBonusOffers(jObj, userId);
            this.saveOrUpdateBatchByMultiId(list);

            List<StoreFront> byTheWayList = sfAPI.getSingleItemOffers(jObj, userId);
            this.saveOrUpdateBatchByMultiId(byTheWayList);
        }

        return list;
    }

    private JSONObject requestAPI(RSO rso) {
        JSONObject jObj = null;
        try {
            if(rso.isAccessTokenExpired()) {
                throw new APIUnauthorizedException();
            } else {
                log.info("StoreFront API 正在请求，userId={}", rso.getUserId());
                jObj = sfAPI.process(rso);
            }
        } catch (APIUnauthorizedException e1) {
            log.info("RSO token 已过期，尝试更新 token ，userId={}", rso.getUserId());
            rso = rsoService.updateRSO(rso.getUserId());
            try {
                if(rso != null) {
                    jObj = sfAPI.process(rso);
                } else {
                    log.warn("RSO token 尝试更新失败！中止请求API！");
                    return null;
                }
            } catch (APIUnauthorizedException e2) {
                log.warn("StoreFront API 账号验证失败！");
            }
        }
        log.info("StoreFront API 请求成功。");
        if(jObj != null) {
            jObj.set("userId", rso.getUserId());
        }
        return jObj;
    }

    @Override
    public List<StoreFrontVO> toVO(List<StoreFront> sfList) {
        List<StoreFrontVO> voList = new ArrayList<>(sfList.size());
        sfList.forEach(sf -> {
            StoreFrontVO sfv = new StoreFrontVO().toVO(sf);
            sfv.setWeaponSkin(wsService.getById(sf.getItemId()));
            // 注意这里的 itemId 并不是直接的 皮肤ID，而是 皮肤升级ID，要先查出其父皮肤ID
            String skinId = wsService.getParentSkinIdByLevelOrChromaId(sf.getItemId());
            sfv.setWeaponSkinLevels(wsService.getLevels(skinId));
            sfv.setWeaponSkinChromas(wsService.getChromas(skinId));
            voList.add(sfv);
        });
        return voList;
    }

    private List<StoreFront> queryDB(String userId, String date, boolean isBonus) {
        // 早上8点前查询，返回的是前一天的数据
        if(!isNowAfterToday8AM()) {
            date = addDays(date, -1);
        }
        LambdaQueryWrapper<StoreFront> wrapper = Wrappers.lambdaQuery();
        wrapper.orderByAsc(StoreFront::getOfferId)
                .eq(StoreFront::getUserId, userId)
                .eq(StoreFront::getIsBonus, isBonus)
                .eq(!isBonus, StoreFront::getDate, date)
                .gt(isBonus, StoreFront::getDate, date);
        return sfMapper.selectList(wrapper);
    }

    /**
     * 检查日期是否合法，合法的条件：<br/>
     *      先把 date ==> dateTime
     *      今天8点 < dateTime < 明天8点
     * @param date 前端传入的查询日期
     * @return 是否合法
     */
    private static boolean isDateValid(String date) {
        DateTime now = DateUtil.date();
        DateTime dateTime = DateUtil.parse(date)
                .setField(DateField.HOUR_OF_DAY, now.getField(DateField.HOUR_OF_DAY))
                .setField(DateField.MINUTE, now.getField(DateField.MINUTE))
                .setField(DateField.SECOND, now.getField(DateField.SECOND))
                .setField(DateField.MILLISECOND, now.getField(DateField.MILLISECOND));
        DateTime today8AM = DateUtil.date()
                .setField(DateField.HOUR_OF_DAY, 8)
                .setField(DateField.MINUTE, 0)
                .setField(DateField.SECOND, 0)
                .setField(DateField.MILLISECOND, 0);
        DateTime tomorrow8AM = DateUtil.date(
                DateUtils.addDays(DateUtil.date(today8AM).toJdkDate(), 1)
        );
        boolean valid = DateUtil.compare(today8AM, dateTime) < 0
                && DateUtil.compare(dateTime, tomorrow8AM) < 0;
        //log.info("dateTime={}, today8AM={}, tomorrow8AM={}", dateTime.toString(), today8AM.toString(), tomorrow8AM.toString());
        //log.info("valid={}", valid);
        return valid;
    }

    private static boolean isNowAfterToday8AM() {
        DateTime now = DateUtil.date();
        DateTime today8AM = DateUtil.date()
                .setField(DateField.HOUR_OF_DAY, 8)
                .setField(DateField.MINUTE, 0)
                .setField(DateField.SECOND, 0)
                .setField(DateField.MILLISECOND, 0);
        return DateUtil.compare(now, today8AM) >= 0;
    }

    private static String addDays(String date, int amount) {
        Date dateObj = DateUtil.parseDate(date);
        dateObj = DateUtils.addDays(dateObj, amount);
        return DateUtil.date(dateObj).toDateStr();
    }

    @Scheduled(cron = "${valorant.storefront.cron.pre-update-account-token}")
    @Override
    public void preUpdateAccountToken() {
        long dataNodeIndex = dataNodeManager.getIndex(),
                dataNodeTotal = dataNodeManager.getTotal();
        long sqlLimitIndex = dataNodeIndex * priorUpdateAccountTokenCountPerDataNode,
                sqlLimitCount = priorUpdateAccountTokenCountPerDataNode;
        log.info("开始预更新拳头账号的 RSO token 。数据节点总数：{}，当前数据节点index：{}，当前数据节点ID：{}", dataNodeTotal, dataNodeIndex, dataNodeManager.getDataNodeId());
        log.info("预更新数据量：SQL LIMIT {},{}", sqlLimitIndex, sqlLimitCount);

        // 获取未被删除，且上次RSO认证成功的所有账号ID
        List<Object> accountUserIdList = accountMapper.selectObjs(
                new LambdaQueryWrapper<RiotAccount>()
                        .select(RiotAccount::getUserId)
                        .eq(RiotAccount::getIsDel, false)
                        .eq(RiotAccount::getIsAuthFailure, false)
                        .orderByAsc(RiotAccount::getAccountNo)
                        .last("LIMIT " + sqlLimitIndex + "," + sqlLimitCount)
        );
        accountUserIdList.forEach(userId -> {
            RSO rso = rsoService.fromAccount((String) userId);
            if(rso.isAccessTokenExpired()) {
                rsoService.updateRSO((String) userId);
                try {
                    Thread.sleep(2100);
                } catch (InterruptedException e) {
                    log.warn("", e);
                }
            }
        });
        log.info("预更新拳头账号的RSO access token 完成。");
    }

    @Scheduled(cron = "${valorant.storefront.cron.batch-update-both}")
    protected void batchUpdateBothScheduled() {
        batchUpdateBoth(true);
    }

    public boolean batchUpdateBoth(boolean isDistributed) {
        long dataNodeIndex = 0 , dataNodeTotal = 1;
        if(isDistributed) {
            dataNodeIndex = dataNodeManager.getIndex();
            dataNodeTotal = dataNodeManager.getTotal();
        }
        // 预更新了RSO token的账号
        long preSqlLimitCount = priorUpdateAccountTokenCountPerDataNode,
                preSqlLimitIndex = dataNodeIndex*preSqlLimitCount;
        Date now1 = new Date();
        // 未预更新RSO token的账号
        long accountCount = accountMapper.selectCount(
                new LambdaQueryWrapper<RiotAccount>()
                        .eq(RiotAccount::getIsDel, false)
                        .eq(RiotAccount::getIsAuthFailure, false)
                        .and(condition -> condition
                                .le(RiotAccount::getAccessTokenExpireAt, now1)
                                .or().isNull(RiotAccount::getAccessTokenExpireAt)
                        )
        );
        long sqlLimitCount = accountCount / dataNodeTotal,
                sqlLimitIndex = dataNodeIndex * sqlLimitCount;

        log.info("开始批量更新每日商店+夜市数据。数据节点总数：{}，当前数据节点index：{}，当前数据节点ID：{}", dataNodeTotal, dataNodeIndex, dataNodeManager.getDataNodeId());
        log.info("数据量（预更新了RSO token的账号）：SQL LIMIT {},{}", preSqlLimitIndex, preSqlLimitCount);
        if(isDistributed) {
            log.info("数据量（未预更新RSO token的账号）：SQL LIMIT {},{}", sqlLimitIndex, sqlLimitCount);
        } else {
            log.info("数据量（未预更新RSO token的账号）：全量 {}。", accountCount);
        }

        String date = DateUtil.today();
        // 获取预更新了RSO token的账号，直接多线程并行查商店，不用考虑拳头账号登录API速率上限
        List<Object> preAccountUserIdList = accountMapper.selectObjs(
                new LambdaQueryWrapper<RiotAccount>()
                        .select(RiotAccount::getUserId)
                        .eq(RiotAccount::getIsDel, false)
                        .eq(RiotAccount::getIsAuthFailure, false)
                        .gt(RiotAccount::getAccessTokenExpireAt, now1)
                        .orderByAsc(RiotAccount::getAccountNo)
                        .last(isDistributed, "LIMIT " + preSqlLimitIndex + "," + preSqlLimitCount)
        );
        preAccountUserIdList.parallelStream().forEach(userId -> {
            singleItemOffersWithSleep((String) userId, date, 0);
        });

        // TODO 2023/05/11 09:55:02 设置了now2批量更新结束后是缺数据的，明天尝试使用now1更新
        //Date now2 = new Date();
        // 获取未预更新RSO token的账号
        List<Object> accountUserIdList = accountMapper.selectObjs(
                new LambdaQueryWrapper<RiotAccount>()
                        .select(RiotAccount::getUserId)
                        .eq(RiotAccount::getIsDel, false)
                        .eq(RiotAccount::getIsAuthFailure, false)
                        .and(condition -> condition
                                .le(RiotAccount::getAccessTokenExpireAt, now1)
                                .or().isNull(RiotAccount::getAccessTokenExpireAt)
                        )
                        .orderByAsc(RiotAccount::getAccountNo)
                        .last(isDistributed, "LIMIT " + sqlLimitIndex + "," + sqlLimitCount)
        );
        accountUserIdList.forEach(userId -> {
            // 拳头API速率限制：100 requests every 2 minutes
            // 处理一个账号数据需要请求4-6次API（包括RSO认证），2分钟的请求上限为20个账号，即6秒处理一个账号
            // 实测处理一个账号数据请求时间为1.5秒左右，添加 sleep
            singleItemOffersWithSleep((String) userId, date, 1.5f);
        });
        return true;
    }

    @Override
    public IPage<BatchBothStoreFrontVO> batchQueryBoth(BatchQueryBothDTO batchQueryBothDTO) {
        log.info("批量查询每日商店+夜市数据，batchQueryBothDTO={}", batchQueryBothDTO);
        if(StringUtils.isEmpty(batchQueryBothDTO.getDate())) {
            batchQueryBothDTO.setDate(DateUtil.today());
        }
        if(!isNowAfterToday8AM()) {
            batchQueryBothDTO.setDate(addDays(batchQueryBothDTO.getDate(), -1));
        }

        boolean isNightShopClosed = sfMapper.isNightShopClosed(batchQueryBothDTO.getDate());
        if(isNightShopClosed) {
            // 如果夜市关闭，那么使用对应方法进行查询，该方法忽略了有关夜市皮肤的查询条件
            log.info("夜市关闭，忽略夜市皮肤的查询条件。");
            return sfMapper.batchQueryBothWhileNightShopClosed(Page.of(batchQueryBothDTO.getCurrent(), batchQueryBothDTO.getSize()), batchQueryBothDTO);
        }

        // 如果夜市开放，那么需要对查询结果进行处理
        log.info("夜市开放。");
        // 结果集会删去一半，所以size需要事先乘2
        IPage<BatchBothStoreFrontVO> result = sfMapper.batchQueryBoth(Page.of(batchQueryBothDTO.getCurrent(), batchQueryBothDTO.getSize()*2), batchQueryBothDTO);
        if(CollectionUtils.isNotEmpty(result.getRecords())) {
            // 把夜市的数据合并到每日商店数据中（两条数据合并为一条）
            // SQL查询出来的总是 同一个账号的两条数据相连，且每日商店数据在夜市数据前面
            List<BatchBothStoreFrontVO> list = result.getRecords();
            for (int i = 0; i < list.size() - 1; i++) {
                if(!list.get(i).getIsBonus() && list.get(i+1).getIsBonus()
                        && list.get(i).getAccountNo().equals(list.get(i+1).getAccountNo())) {
                    list.get(i).setBonusOffer(list.get(i+1));
                }
            }
            // 删除夜市的数据条目
            list.removeIf(BatchBothStoreFrontVO::getIsBonus);
            result.setRecords(list);
            log.info("每日商店+夜市 查询结果处理完毕。");
        }
        // 总数据量除2
        result.setTotal(result.getTotal() / 2);
        return result;
    }

    @Override
    public BatchBothStoreFrontVO queryBothByAccountId(String userId, Long accountNo) {
        String date = DateUtil.today();
        if(!isNowAfterToday8AM()) {
            date = addDays(date, -1);
        }
        List<BatchBothStoreFrontVO> list = sfMapper.queryBothByAccountId(userId, accountNo, date);
        // 把每日商店和夜市合并为一条
        if(list != null && list.size() > 1){
            list.get(0).setBonusOffer(list.get(1));
        }
        return list != null && list.size() > 0 ? list.get(0) : null;
    }

}
