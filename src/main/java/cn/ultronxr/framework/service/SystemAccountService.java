package cn.ultronxr.framework.service;

import cn.ultronxr.framework.bean.mybatis.bean.SystemAccount;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.Cookie;

/**
 * @author Ultronxr
 * @date 2023/05/11 08:13:41
 * @description 系统账号登录认证、权限校验 service
 */
public interface SystemAccountService extends IService<SystemAccount> {

    /**
     * 校验登录请求的密码是否合法
     * @param username 待校验的 登录用户名
     * @param password 待校验的 登录密码
     * @return true - 合法；false - 不合法
     */
    boolean validateSystemAccount(String username, String password);

    /**
     * 登录成功签发 cookie
     * @param username 登录成功的用户名
     */
    Cookie issueAuthCookie(String username);

    /**
     * 获取 cookie name
     */
    String getCookieName();

    /**
     * 校验传入的 token 是否合法
     * @param token 待校验的 token
     * @return true - 合法；false - 不合法
     */
    boolean validateToken(String token);

}
