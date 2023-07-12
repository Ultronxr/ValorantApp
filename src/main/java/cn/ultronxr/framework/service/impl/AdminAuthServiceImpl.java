package cn.ultronxr.framework.service.impl;

import cn.ultronxr.framework.bean.mybatis.bean.SystemAccount;
import cn.ultronxr.framework.bean.mybatis.mapper.SystemAccountMapper;
import cn.ultronxr.framework.jwt.JJWTConfig;
import cn.ultronxr.framework.jwt.JJWTService;
import cn.ultronxr.framework.jwt.JWSParseResult;
import cn.ultronxr.framework.service.SystemAccountService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;

/**
 * @author Ultronxr
 * @date 2023/05/11 08:13:49
 * @description 管理员登录认证、权限校验service
 */
@Service
@Slf4j
public class AdminAuthServiceImpl extends ServiceImpl<SystemAccountMapper, SystemAccount> implements SystemAccountService {

    /** 登录成功后签发的 cookie name */
    @Value("${adminAuth.cookieName}")
    private String cookieName;

    @Value("${adminAuth.adminPasswordLength}")
    private Integer adminPasswordLength;

    @Autowired
    private JJWTConfig jjwtConfig;

    @Autowired
    private JJWTService jjwtService;


    @Override
    public boolean validateSystemAccount(String username, String password) {
        username = username.trim();
        password = password.trim();
        if(StringUtils.isBlank(username) || StringUtils.isBlank(password) || password.length() != adminPasswordLength) {
            return false;
        }
        SystemAccount account = getBaseMapper().selectOne(
                new LambdaQueryWrapper<SystemAccount>()
                        .eq(SystemAccount::getUsername, username)
        );
        boolean result = null != account
                && account.getUsername().equals(username)
                && account.getPassword().equals(password);
        log.info("尝试校验登录账号：result={}, username={}, password={}", result, username, password);
        return result;
    }

    @Override
    public Cookie issueAuthCookie(String username) {
        String token = jjwtService.generate(username);
        Cookie cookie = new Cookie(cookieName, token);
        cookie.setPath("/");
        cookie.setMaxAge((int) jjwtConfig.authTokenExpireSeconds());
        cookie.setHttpOnly(true);
        return cookie;
    }

    @Override
    public String getCookieName() {
        return cookieName;
    }

    @Override
    public boolean validateToken(String token) {
        if(StringUtils.isBlank(token)) {
            return false;
        }
        JWSParseResult result = jjwtService.validateToken(token);
        if(!result.isValidation()) {
            log.info("JWS token username = {} | 验证结果 = {} | 信息 = {}", result.getUsername() ,result.isValidation(), result.getMsg());
            return false;
        }
        boolean userExists = this.getBaseMapper().exists(
                new LambdaQueryWrapper<SystemAccount>()
                        .eq(SystemAccount::getUsername, result.getUsername())
                        .eq(SystemAccount::getIsDel, false)
        );
        if(!userExists) {
            log.warn("JWS token 验证通过，但其中的用户不存在或已删除！subject/username={}", result.getUsername());
        }
        return userExists;
    }

    @Override
    public SystemAccount getSystemUserFromValidatedToken(String token) {
        if(StringUtils.isBlank(token)) {
            return null;
        }
        JWSParseResult result = jjwtService.validateToken(token);
        if(!result.isValidation()) {
            log.info("JWS token username = {} | 验证结果 = {} | 信息 = {}", result.getUsername() ,result.isValidation(), result.getMsg());
            return null;
        }
        return this.getOne(
            new LambdaQueryWrapper<SystemAccount>()
                    .eq(SystemAccount::getUsername, result.getUsername())
                    .eq(SystemAccount::getIsDel, false)
        );
    }

}
