package cn.ultronxr.valorant.service;

import cn.ultronxr.valorant.bean.DTO.BatchQueryBothDTO;
import cn.ultronxr.valorant.bean.VO.BatchBothStoreFrontVO;
import cn.ultronxr.valorant.bean.VO.StoreFrontVO;
import cn.ultronxr.valorant.bean.mybatis.bean.StoreFront;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.jeffreyning.mybatisplus.service.IMppService;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author Ultronxr
 * @date 2023/02/22 10:20
 * @description
 */
public interface StoreFrontService extends IMppService<StoreFront> {

    /**
     * 获取每日刷新的商店
     * @param userId 拳头账户ID
     * @param date   日期
     * @return 商品列表
     */
    List<StoreFront> singleItemOffers(String userId, String date);

    /**
     * 获取每日刷新的商店，如果请求了API，加入 Thread.sleep 等待时间，限制API请求速率
     * @param userId       拳头账户ID
     * @param date         日期
     * @param sleepSeconds 请求API之后的等待时间（秒）
     * @return 商品列表
     */
    void singleItemOffersWithSleep(@NotNull String userId, @NotNull String date, float sleepSeconds);

    /**
     * 获取夜市商品
     * @param userId 拳头账户ID
     * @param date   日期
     * @return 商品列表
     */
    List<StoreFront> bonusOffers(String userId, String date);

    /**
     * 查询出每个商品对应的 武器皮肤、升级、炫彩 列表，并包装成 VO 对象返回给前端做数据展示
     * @param storeFrontList 商品列表
     * @return 包装成 VO 的商品列表
     */
    List<StoreFrontVO> toVO(List<StoreFront> storeFrontList);

    /**
     * 预更新账号的 RSO token（access token 与 entitlements token），token有效期一小时
     */
    void preUpdateAccountToken();

    /**
     * 批量更新所有账号的每日商店+夜市
     * @param isDistributed 是否分布式更新<br/>
     *                      true - 开启分布式更新（SQL获取拳头账号记录时会进行 LIMIT 处理）；<br/>
     *                      false - 非分布式更新（SQL获取拳头账号记录时全量获取）
     */
    boolean batchUpdateBoth(boolean isDistributed);

    /**
     * 查询所有账号的每日商店+夜市
     * @return 商品列表
     */
    IPage<BatchBothStoreFrontVO> batchQueryBoth(BatchQueryBothDTO batchQueryBothDTO);

    /**
     * 使用指定的 账号ID或账号编号 查询每日商店+夜市
     * @param userId    账号ID
     * @param accountNo 账号编号
     * @return 该账号的每日商店+夜市
     */
    BatchBothStoreFrontVO queryBothByAccountId(String userId, Long accountNo);

}
