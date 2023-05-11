package cn.ultronxr.valorant.bean.mybatis.mapper;

import cn.ultronxr.valorant.bean.mybatis.bean.RiotAccount;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.Date;
import java.util.List;

/**
 * @author Ultronxr
 * @date 2023/02/22 15:10
 * @description
 */
public interface RiotAccountMapper extends BaseMapper<RiotAccount> {

    /**
     * 计数 - 预更新了 access token 的拳头账号数量（且未被删除、登录验证成功的）<br/>
     * “预更新了”的成立条件：token 过期时间 > 当前时间
     * @param now 当前时间
     * @return
     */
    default Long selectCountForPreUpdatedTokenAccount(final Date now) {
        return this.selectCount(
                new LambdaQueryWrapper<RiotAccount>()
                        .select(RiotAccount::getUserId)
                        .eq(RiotAccount::getIsDel, false)
                        .eq(RiotAccount::getIsAuthFailure, false)
                        .gt(RiotAccount::getAccessTokenExpireAt, now)
        );
    }

    /**
     * 计数 - 未预更新 access token 的拳头账号数量（且未被删除、登录验证成功的）<br/>
     * “未预更新”的成立条件：token 过期时间 <= 当前时间 || token 过期时间为空
     * @param now 当前时间
     * @return
     */
    default Long selectCountForNotPreUpdatedTokenAccount(final Date now) {
        return this.selectCount(
                new LambdaQueryWrapper<RiotAccount>()
                        .eq(RiotAccount::getIsDel, false)
                        .eq(RiotAccount::getIsAuthFailure, false)
                        .and(condition -> condition
                                .le(RiotAccount::getAccessTokenExpireAt, now)
                                .or().isNull(RiotAccount::getAccessTokenExpireAt)
                        )
        );
    }

    /**
     * 获取 ID List - 预更新了 access token 的拳头账号 ID （且未被删除、登录验证成功的）<br/>
     * “预更新了”的成立条件：token 过期时间 > 当前时间
     *
     * @param now           当前时间
     * @param isDistributed 是否分布式（是分布式，则使用 SQL LIMIT 只获取部分数据；不是分布式，则直接获取全量数据）
     * @param sqlLimitIndex SQL LIMIT 起始下标
     * @param sqlLimitCount SQL LIMIT 数量
     * @return
     */
    default List<Object> selectIdListForPreUpdatedTokenAccount(final Date now, final boolean isDistributed, final long sqlLimitIndex, final long sqlLimitCount) {
        return this.selectObjs(
                new LambdaQueryWrapper<RiotAccount>()
                        .select(RiotAccount::getUserId)
                        .eq(RiotAccount::getIsDel, false)
                        .eq(RiotAccount::getIsAuthFailure, false)
                        .gt(RiotAccount::getAccessTokenExpireAt, now)
                        .orderByAsc(RiotAccount::getAccountNo)
                        .last(isDistributed, "LIMIT " + sqlLimitIndex + "," + sqlLimitCount)
        );
    }

    /**
     * 获取 ID List - 未预更新 access token 的拳头账号 ID （且未被删除、登录验证成功的）<br/>
     * “未预更新”的成立条件：token 过期时间 <= 当前时间 || token 过期时间为空
     *
     * @param now           当前时间
     * @param isDistributed 是否分布式（是分布式，则使用 SQL LIMIT 只获取部分数据；不是分布式，则直接获取全量数据）
     * @param sqlLimitIndex SQL LIMIT 起始下标
     * @param sqlLimitCount SQL LIMIT 数量
     * @return
     */
    default List<Object> selectIdListForNotPreUpdatedTokenAccount(final Date now, final boolean isDistributed, final long sqlLimitIndex, final long sqlLimitCount) {
        return this.selectObjs(
                new LambdaQueryWrapper<RiotAccount>()
                        .select(RiotAccount::getUserId)
                        .eq(RiotAccount::getIsDel, false)
                        .eq(RiotAccount::getIsAuthFailure, false)
                        .and(condition -> condition
                                .le(RiotAccount::getAccessTokenExpireAt, now)
                                .or().isNull(RiotAccount::getAccessTokenExpireAt)
                        )
                        .orderByAsc(RiotAccount::getAccountNo)
                        .last(isDistributed, "LIMIT " + sqlLimitIndex + "," + sqlLimitCount)
        );
    }

}
