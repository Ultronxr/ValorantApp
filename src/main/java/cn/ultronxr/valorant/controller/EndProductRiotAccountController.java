package cn.ultronxr.valorant.controller;

import cn.ultronxr.common.bean.AjaxResponse;
import cn.ultronxr.common.util.AjaxResponseUtils;
import cn.ultronxr.framework.annotation.AdminAuthRequired;
import cn.ultronxr.framework.bean.mybatis.bean.SystemAccount;
import cn.ultronxr.framework.service.SystemAccountService;
import cn.ultronxr.valorant.bean.DTO.EndProductRiotAccountDTO;
import cn.ultronxr.valorant.bean.enums.RiotAccountCreateState;
import cn.ultronxr.valorant.bean.mybatis.bean.EndProductRiotAccount;
import cn.ultronxr.valorant.service.EndProductRiotAccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * @author Ultronxr
 * @date 2023/07/05 14:49:26
 * @description 成品拳头账号 controller
 */
@Controller
@RequestMapping("/valorant/endProduct/account")
@Slf4j
public class EndProductRiotAccountController {

    @Autowired
    private EndProductRiotAccountService endProductRiotAccountService;

    @Autowired
    private SystemAccountService systemAccountService;


    @AdminAuthRequired
    @PostMapping("/management/create")
    @ResponseBody
    public AjaxResponse create(@RequestBody EndProductRiotAccount endProductRiotAccount) {
        RiotAccountCreateState state = endProductRiotAccountService.create(endProductRiotAccount);
        if(state == RiotAccountCreateState.OK) {
            return AjaxResponseUtils.success();
        }
        return AjaxResponseUtils.fail(state.getMsg());
    }

    @AdminAuthRequired
    @PostMapping("/management/update")
    @ResponseBody
    public AjaxResponse update(@RequestBody EndProductRiotAccount endProductRiotAccount) {
        if(endProductRiotAccountService.update(endProductRiotAccount)) {
            return AjaxResponseUtils.success();
        }
        return AjaxResponseUtils.fail();
    }

    @AdminAuthRequired
    @DeleteMapping("/management/delete")
    @ResponseBody
    public AjaxResponse delete(@RequestParam String[] accountNoList) {
        if(endProductRiotAccountService.removeByIds(Arrays.asList(accountNoList))) {
            return AjaxResponseUtils.success();
        }
        return AjaxResponseUtils.fail();
    }

    @AdminAuthRequired
    @PostMapping("/management/query")
    @ResponseBody
    public AjaxResponse managementQuery(@RequestBody EndProductRiotAccountDTO accountDTO) {
        return AjaxResponseUtils.success(
                endProductRiotAccountService.managementQueryAccount(accountDTO)
        );
    }

    @AdminAuthRequired
    @PostMapping("/management/redeem")
    @ResponseBody
    public AjaxResponse redeem(HttpServletRequest request, @RequestBody EndProductRiotAccountDTO accountDTO) {
        String authToken = null;
        // 从请求的 cookie 中取出 token
        if(null != request.getCookies() && request.getCookies().length > 0) {
            for(Cookie cookie : request.getCookies()) {
                if(cookie.getName().equals(systemAccountService.getCookieName())) {
                    authToken = cookie.getValue();
                    break;
                }
            }
        }
        SystemAccount systemAccount = systemAccountService.getSystemUserFromValidatedToken(authToken);
        if(systemAccount != null && systemAccount.getXsecret().equals(accountDTO.getXsecret())) {
            EndProductRiotAccount account = endProductRiotAccountService.redeem(accountDTO.getAccountNo());
            if(null != account) {
                return AjaxResponseUtils.success(account);
            }
            return AjaxResponseUtils.fail("无数据！");
        }

        return AjaxResponseUtils.fail("无权获取！");
    }

    @PostMapping("/query")
    @ResponseBody
    public AjaxResponse query(@RequestBody EndProductRiotAccountDTO accountDTO) {
        return AjaxResponseUtils.success(
                endProductRiotAccountService.queryAccount(accountDTO)
        );
    }

    @GetMapping("/getOne")
    @ResponseBody
    public AjaxResponse getOne(@RequestParam Long accountNo) {
        return AjaxResponseUtils.success(
                endProductRiotAccountService.getOne(accountNo)
        );
    }

}
