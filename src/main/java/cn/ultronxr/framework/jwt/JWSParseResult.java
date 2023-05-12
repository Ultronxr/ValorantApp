package cn.ultronxr.framework.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.Data;

/**
 * @author Ultronxr
 * @date 2023/05/12 16:24:58
 * @description JWS token 的验证与解析结果封装<br/>
 *              本对象是以下方法的产物： {@link JJWTService#validateToken(String)}
 */
@Data
public class JWSParseResult {

    /**
     * 验证状态，标记该 JWS token 有没有通过验证<br/>
     * {@code true} - 通过验证；{@code false} - 未通过验证
     */
    private boolean validation;

    /**
     * 如果 JWS token 未通过验证，那么这里是非空的验证信息（为何未通过验证）<br/>
     * 如果 通过验证，那么 {@code msg == null}
     */
    private String msg;

    /**
     * 如果 JWS token 未通过验证，那么 {@code jws == null} <br/>
     * 如果 通过验证，那么这里是非空的解析结果
     */
    private Jws<Claims> jws;

    /**
     * 前提：当 JWS token 通过验证后，<br/>
     *      获取解析结果中 "username" 对应的值
     *
     * @return token 解析结果中的 "username" 对应的 {@code String} 值<br/>
     *          如果 token 验证未通过，返回 {@code null}
     */
    public String getUsername() {
        //return null == jws ? null : jws.getBody().get("username", String.class);
        return null == jws ? null : jws.getBody().getSubject();
    }

}
