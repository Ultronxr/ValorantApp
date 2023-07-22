package cn.ultronxr.valorant.bean.DTO;

import lombok.Data;

import java.util.List;

/**
 * @author Ultronxr
 * @date 2023/07/12 14:40:33
 * @description
 */
@Data
public class EndProductRiotAccountDTO {

    /** 用于搜索皮肤的 皮肤ID 数组 */
    private List<String> skinSearchArray;

    private Long accountNo;

    /** 账号地区：0-缅甸、1-马来西亚、2-香港、3-泰国 */
    private Integer region;

    /** 账号状态：1-在售、2-出租、10-已售出 */
    private Integer status;

    private Float priceLow;

    private Float priceHigh;

    /** 价格排序：0-升序、1-降序 */
    private Integer priceOrder;

    /** 前两个字母都小写，避免字段命名的坑 */
    private String xsecret;

    private Integer current = 1;

    private Integer size = 100;

}
