package cn.ultronxr.valorant.api.impl;

import cn.hutool.json.JSONException;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.ultronxr.valorant.api.BaseAPI;
import cn.ultronxr.valorant.api.InGameAPIEnum;
import cn.ultronxr.valorant.auth.RSO;
import cn.ultronxr.valorant.exception.APIUnauthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author Ultronxr
 * @date 2023/07/05 15:07:25
 * @description <b>获取账号已拥有物品</b>API<br/>
 */
@Component
@Slf4j
public class StoreEntitlementsAPI extends BaseAPI {

    private static final String API = InGameAPIEnum.StoreEntitlements.getUrl();

    private static final String ITEM_TYPE_SKINS = "e7c63390-eda7-46e0-bb7a-a6abdacd2433";

    private static final String ITEM_TYPE_SKINS_CHROMA = "3ad1b2b2-acdb-4524-852f-954a76ddae0a";


    public JSONObject processSkins(RSO rso) throws APIUnauthorizedException {
        return process(rso, ITEM_TYPE_SKINS);
    }

    public JSONObject processSkinsChroma(RSO rso) throws APIUnauthorizedException {
        return process(rso, ITEM_TYPE_SKINS_CHROMA);
    }

    public JSONObject process(RSO rso, String itemType) throws APIUnauthorizedException {
        String response = requestAPI(
                API.replace("{userId}", rso.getUserId())
                        .replace("{itemType}", itemType),
                rso.getRequestHeaderMap()
        );
        return parseData(response);
    }

    @Override
    public JSONObject parseData(String responseBody) throws APIUnauthorizedException {
        JSONObject obj = null;
        try {
            obj = JSONUtil.parseObj(responseBody, false);
            if(obj.getInt("httpStatus", 200) == 400 && obj.getStr("errorCode", "OK").equals("BAD_CLAIMS")) {
                throw new APIUnauthorizedException();
            }
        } catch (JSONException e) {
            log.warn("API请求响应体json解析失败！", e);
            log.warn("如下是本次请求的响应体：\n{}", responseBody);
        }
        return obj;
    }

}
