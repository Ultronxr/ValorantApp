package cn.ultronxr.framework.jwt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author Ultronxr
 * @date 2023/05/12 16:22:35
 * @description JJWT (JSON Web Token Support For The JVM) 配置
 */
@Configuration
public class JJWTConfig {

    /** JWT 的签发者 */
    @Value("${jjwt.issuer}")
    private String issuer;

    /** 签发 JWT 时的自定义加密串 */
    @Value("${jjwt.secret}")
    private String secret;

    /** 登录 token 的有效时长 */
    @Value("${jjwt.expireMinutes.authToken}")
    private long authTokenExpireMinutes;


    public String getIssuer() {
        return issuer;
    }

    public String getSecret() {
        return secret;
    }

    public long authTokenExpireMilliSeconds() {
        return getMilliSeconds(authTokenExpireMinutes);
    }

    public long authTokenExpireSeconds() {
        return getSeconds(authTokenExpireMinutes);
    }

    private static long getMilliSeconds(long minutes) {
        return getSeconds(minutes) * 1000L;
    }

    private static long getSeconds(long minutes) {
        return minutes * 60L;
    }

}
