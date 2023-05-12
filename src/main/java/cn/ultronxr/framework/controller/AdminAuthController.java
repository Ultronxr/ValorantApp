package cn.ultronxr.framework.controller;

import cn.ultronxr.common.bean.AjaxResponse;
import cn.ultronxr.common.util.AjaxResponseUtils;
import cn.ultronxr.framework.bean.SystemAccountDto;
import cn.ultronxr.framework.service.SystemAccountService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Ultronxr
 * @date 2023/05/11 08:48:34
 * @description
 */
@Controller
@RequestMapping("/system")
@Slf4j
public class AdminAuthController {

    @Autowired
    private SystemAccountService systemAccountService;


    @PostMapping("/adminAuth")
    @ResponseBody
    public AjaxResponse adminAuth(@RequestBody SystemAccountDto systemAccountDto, HttpServletRequest request, HttpServletResponse response) {
        if(systemAccountService.validateSystemAccount(systemAccountDto.getUsername(), systemAccountDto.getPassword())) {
            Cookie cookie = systemAccountService.issueAuthCookie();
            response.addCookie(cookie);
            return AjaxResponseUtils.success("管理员登录成功。");
        }
        return AjaxResponseUtils.fail("管理员登录验证失败！");
    }

}
