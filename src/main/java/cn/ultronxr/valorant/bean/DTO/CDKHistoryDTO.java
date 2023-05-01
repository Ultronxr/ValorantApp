package cn.ultronxr.valorant.bean.DTO;

import lombok.Data;

/**
 * @author Ultronxr
 * @date 2023/05/01 13:16:19
 * @description
 */
@Data
public class CDKHistoryDTO {

    private String cdk;

    private Long accountNo;

    private Integer current = 1;

    private Integer size = 100;

}
