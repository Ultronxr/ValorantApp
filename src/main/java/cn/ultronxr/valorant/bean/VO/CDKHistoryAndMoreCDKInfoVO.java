package cn.ultronxr.valorant.bean.VO;

import cn.ultronxr.valorant.bean.mybatis.bean.CDKHistory;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

/**
 * @author Ultronxr
 * @date 2023/05/10 12:03:40
 * @description CDK兑换历史记录查询VO
 */
@Data
public class CDKHistoryAndMoreCDKInfoVO {

    Page<CDKHistory> history;

    Integer reuseRemainingTimes;

}
