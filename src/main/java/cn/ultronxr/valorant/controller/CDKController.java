package cn.ultronxr.valorant.controller;

import cn.hutool.json.JSONObject;
import cn.ultronxr.common.bean.AjaxResponse;
import cn.ultronxr.common.util.AjaxResponseUtils;
import cn.ultronxr.valorant.bean.DTO.CDKDTO;
import cn.ultronxr.valorant.bean.DTO.CDKHistoryDTO;
import cn.ultronxr.valorant.bean.enums.CDKRedeemState;
import cn.ultronxr.valorant.service.CDKService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @author Ultronxr
 * @date 2023/05/01 13:05:18
 * @description
 */
@Controller
@RequestMapping("/valorant/cdk")
@Slf4j
public class CDKController {

    @Autowired
    private CDKService cdkService;


    @PostMapping("/create")
    @ResponseBody
    public AjaxResponse create(@RequestBody CDKDTO cdkDTO) {
        return AjaxResponseUtils.success(cdkService.create(cdkDTO));
    }

    @PostMapping("/updateReuseRemainingTimes")
    @ResponseBody
    public AjaxResponse updateReuseRemainingTimes(@RequestBody JSONObject jsonObject) {
        Long[] reusableCDKNoList = jsonObject.get("reusableCDKNoList", Long[].class);
        Integer reuseRemainingTimes = jsonObject.getInt("reuseRemainingTimes");
        if(cdkService.updateReuseRemainingTimes(reusableCDKNoList, reuseRemainingTimes)) {
            return AjaxResponseUtils.success();
        }
        return AjaxResponseUtils.fail();
    }

    @PostMapping("/query")
    @ResponseBody
    public AjaxResponse query(@RequestBody CDKDTO cdkDTO) {
        return AjaxResponseUtils.success(cdkService.queryCDK(cdkDTO));
    }

    @DeleteMapping("/delete")
    @ResponseBody
    public AjaxResponse delete(@RequestParam Long[] cdkNoList) {
        if(cdkService.delete(cdkNoList)) {
            return AjaxResponseUtils.success();
        }
        return AjaxResponseUtils.fail();
    }

    @PostMapping("/redeem")
    @ResponseBody
    public AjaxResponse redeem(@RequestBody JSONObject jsonObject) {
        String cdk = jsonObject.getStr("cdk");
        Long accountNo = jsonObject.getLong("accountNo");
        CDKRedeemState state = cdkService.redeem(cdk, accountNo);
        if(state == CDKRedeemState.OK) {
            return AjaxResponseUtils.success(state.getMsg());
        }
        return AjaxResponseUtils.fail(state.getMsg());
    }

    @PostMapping("/history")
    @ResponseBody
    public AjaxResponse history(@RequestBody CDKHistoryDTO cdkHistoryDTO) {
        return AjaxResponseUtils.success(cdkService.queryCDKHistory(cdkHistoryDTO));
    }

}
