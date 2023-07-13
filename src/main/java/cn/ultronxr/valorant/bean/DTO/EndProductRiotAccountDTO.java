package cn.ultronxr.valorant.bean.DTO;

import lombok.Data;

/**
 * @author Ultronxr
 * @date 2023/07/12 14:40:33
 * @description
 */
@Data
public class EndProductRiotAccountDTO {

    private Long accountNo;

    private Integer region;

    private Integer status;

    private Float priceLow;

    private Float priceHigh;

    private String xxSecret;

    private Integer current = 1;

    private Integer size = 100;

}
