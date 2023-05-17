package cn.ultronxr.valorant.util;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.codec.Base64Decoder;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.ultronxr.valorant.auth.RSO;
import cn.ultronxr.valorant.auth.RSOAuthResUri;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Ultronxr
 * @date 2023/03/06 22:45
 * @description 拳头用户登录验证服务（Riot Sign On）的流程 方法库
 */
@Slf4j
public class RSOUtils {

    public static final String AUTH_URL = "https://auth.riotgames.com/api/v1/authorization";

    public static final String RE_AUTH_URL = "https://auth.riotgames.com/authorize?redirect_uri=http://localhost/redirect&client_id=riot-client&response_type=token%20id_token&nonce=1";

    public static final String ENTITLEMENTS_URL = "https://entitlements.auth.riotgames.com/api/token/v1";

    private static final Map<String, String> HEADER = new HashMap<>() {{
        put("Accept-Encoding", "deflate, gzip, zstd");
        put("User-Agent", RSO.DEFAULT_RIOT_CLIENT_AGENT);
        put("Cache-Control", "no-cache");
        put("Accept", "application/json");
        put("Content-Type", "application/json");
    }};

    private static final Map<String, Object> PING_BODY = new HashMap<>() {{
        put("acr_values", "");
        put("claims", "");
        put("client_id", "riot-client");
        put("code_challenge", "");
        put("code_challenge_method", "");
        put("nonce", "S357dE8QSuE"); // python token_urlsafe(16)
        put("redirect_uri", "http://localhost/redirect");
        put("response_type", "token id_token");
        put("scope", "openid link ban lol_region account");
    }};

    private static final Map<String, Object> AUTH_BODY = new HashMap<>() {{
        put("type", "auth");
        put("language", "en_US");
        put("region", null);
        put("remember", true);
        put("username", "{username}");
        put("password", "{password}");
    }};

    private static final Map<String, Object> MULTI_FACTOR_BODY = new HashMap<>() {{
        put("type", "multifactor");
        put("rememberDevice", false);
        put("code", "{multiFactorCode}");
    }};


    public static Map<String, String> getHeader() {
        return HEADER;
    }

    public static String getHeaderJsonStr() {
        return JSONUtil.toJsonStr(getHeader());
    }

    public static Map<String, Object> getPingBody() {
        // 在RSO ping步骤的请求头中修改随机因子nonce，防止服务器记住账号的登录状态，进而导致获取到其他账号的脏数据
        // 返回安全的 URL 随机文本字符串，包含 n bytes 个随机字节，文本用 Base64 编码
        byte[] bytes = RandomUtil.randomBytes(16);
        String nonce = Base64.encode(bytes);
        PING_BODY.put("nonce", nonce);
        //log.info("RSO ping步骤 请求头 生成随机因子 nonce = {}", nonce);
        return PING_BODY;
    }

    public static String getPingBodyJsonStr() {
        return JSONUtil.toJsonStr(getPingBody());
    }

    public static Map<String, Object> getAuthBody(String username, String password, boolean remember) {
        AUTH_BODY.put("remember", remember);
        AUTH_BODY.put("username", username);
        AUTH_BODY.put("password", password);
        return AUTH_BODY;
    }

    public static String getAuthBodyJsonStr(String username, String password, boolean remember) {
        return JSONUtil.toJsonStr(getAuthBody(username, password, remember));
    }

    public static Map<String, Object> getMultiFactorBody(String multiFactorCode, boolean rememberDevice) {
        MULTI_FACTOR_BODY.put("code", multiFactorCode);
        MULTI_FACTOR_BODY.put("rememberDevice", rememberDevice);
        return MULTI_FACTOR_BODY;
    }

    public static String getMultiFactorBodyJsonStr(String multiFactorCode, boolean rememberDevice) {
        return JSONUtil.toJsonStr(getMultiFactorBody(multiFactorCode, rememberDevice));
    }

    /**
     * 从RSO验证流程的 第2/3步 响应结果中解析出 accessToken
     *
     * @param authResObj 第2/3步 响应结果的 JSON 对象
     * @param rso        待更新的 RSO 对象
     * @return 更新了 accessToken 与 userId 的 RSO 对象
     */
    public static RSO parseAccessToken(JSONObject authResObj, RSO rso) {
        if(null == authResObj || authResObj.isEmpty()) {
            return rso;
        }
        String uri = authResObj.getByPath("response.parameters.uri", String.class);
        if(StringUtils.isEmpty(uri)) {
            return rso;
        }
        if(null == rso) {
            rso = new RSO();
        }
        RSOAuthResUri rsoARU = RSOAuthResUri.of(uri);
        JSONObject JWTObj = decodeJWT(rsoARU.getAccessToken());
        rso.setAccessToken(rsoARU.getAccessToken());
        rso.setAccessTokenExpireAt(getExpireAtTimeFromAccessTokenJsonObj(JWTObj));
        rso.setUserId(getUserIdFromAccessTokenJsonObj(JWTObj));
        return rso;
    }

    /**
     * 对 accessToken JWT 进行解码，并包装成 Json Object
     * @param base64CodedAccessToken 经过base64编码的 accessToken JWT
     * @return JSONObject
     */
    public static JSONObject decodeJWT(String base64CodedAccessToken) {
        if(StringUtils.isEmpty(base64CodedAccessToken)) {
            return null;
        }
        String payload = base64CodedAccessToken.split("\\.")[1];
        String decodedJWT = Base64Decoder.decodeStr(payload, StandardCharsets.UTF_8);
        return JSONUtil.parseObj(decodedJWT);
    }

    /**
     * 从 accessToken JWT 解码之后的 Json Object 对象中获取 userId
     * @param decodedJWTJsonObj accessToken JWT 解码之后的 Json Object
     * @return {@code String} userId
     */
    public static String getUserIdFromAccessTokenJsonObj(JSONObject decodedJWTJsonObj) {
        return decodedJWTJsonObj == null ? null : decodedJWTJsonObj.getStr("sub", null);
    }

    /**
     * 从 accessToken JWT 解码之后的 Json Object 对象中获取 expireAt
     * @param decodedJWTJsonObj accessToken JWT 解码之后的 Json Object
     * @return {@code Date} expireAt
     */
    public static Date getExpireAtTimeFromAccessTokenJsonObj(JSONObject decodedJWTJsonObj) {
        return decodedJWTJsonObj == null ? null : new Date(1000L * decodedJWTJsonObj.getLong("exp", null));
    }

    /**
     * 从RSO验证流程的 第4步 响应结果中解析出 entitlementsToken
     * @param entitlementsResObj 第4步 响应结果的 JSON 对象
     * @param rso                待更新的 RSO 对象
     * @return 更新了 entitlementsToken 的 RSO 对象
     */
    public static RSO parseEntitlementsToken(JSONObject entitlementsResObj, RSO rso) {
        if(null == entitlementsResObj || entitlementsResObj.isEmpty()) {
            return rso;
        }
        String entitlementsToken = entitlementsResObj.getByPath("entitlements_token", String.class);
        if(StringUtils.isEmpty(entitlementsToken)) {
            return rso;
        }
        if(null == rso) {
            rso = new RSO();
        }
        rso.setEntitlementsToken(entitlementsToken);
        return rso;
    }

}
