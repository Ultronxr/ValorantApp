package cn.ultronxr.framework.service.impl;

import cn.hutool.core.codec.Base64;
import cn.ultronxr.framework.bean.mybatis.bean.SystemAccount;
import cn.ultronxr.framework.bean.mybatis.mapper.SystemAccountMapper;
import cn.ultronxr.framework.service.SystemAccountService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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

    private static final String ADMIN_UUID = "ce7142302079b20a9e45851ec43cc21d";

    private static final Integer ADMIN_PASSWORD_LENGTH = 50;

    /** 登录成功后签发的 cookie name */
    private static final String COOKIE_NAME = "X-ADMIN-AUTH";

    private static final Integer COOKIE_EXPIRE_SECONDS = 3*24*60*60;


    @Override
    public boolean validatePassword(String password) {
        log.info("尝试校验登录密码：{}", password);
        if(StringUtils.isBlank(password) || password.trim().length() != ADMIN_PASSWORD_LENGTH) {
            return false;
        }
        String correctPwd = getBaseMapper().selectById(ADMIN_UUID).getPassword();
        return correctPwd.equals(password);
    }

    @Override
    public Cookie issueAuthCookie() {
        String token = getBaseMapper().selectById(ADMIN_UUID).getToken();
        Cookie cookie = new Cookie(COOKIE_NAME, Base64.encode(token));
        cookie.setPath("/");
        cookie.setMaxAge(COOKIE_EXPIRE_SECONDS);
        cookie.setHttpOnly(true);
        return cookie;
    }

    @Override
    public String getCookieName() {
        return COOKIE_NAME;
    }

    @Override
    public boolean validateToken(String token) {
        if(StringUtils.isBlank(token)) {
            return false;
        }
        String correctToken = Base64.encode(getBaseMapper().selectById(ADMIN_UUID).getToken());
        return correctToken.equals(token.trim());
    }

}
