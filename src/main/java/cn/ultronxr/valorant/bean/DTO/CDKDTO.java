package cn.ultronxr.valorant.bean.DTO;

import lombok.Data;

/**
 * @author Ultronxr
 * @date 2023/05/01 13:16:19
 * @description
 */
@Data
public class CDKDTO {

    private String cdk;

    private Long cdkNo;

    private Boolean typeHasEmail;

    private Boolean typeReusable;

    private Integer reuseRemainingTimes;

    private Boolean isUsed;

    private Integer createCount;

    private Integer current = 1;

    private Integer size = 100;

}
