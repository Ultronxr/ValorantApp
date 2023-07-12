package cn.ultronxr.valorant.bean.mybatis.bean;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author Ultronxr
 * @date 2023/07/05 10:45:02
 * @description
 */
@Data
@TableName("valorant_end_product_store_entitlements")
public class EndProductStoreEntitlements {

    private Long accountNo;

    private String itemId;

    private String typeId;

}
