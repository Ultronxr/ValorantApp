package cn.ultronxr.valorant.api.impl;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.ultronxr.valorant.api.BaseAPI;
import cn.ultronxr.valorant.api.ValorantDotComAPIEnum;
import cn.ultronxr.valorant.bean.mybatis.bean.Weapon;
import cn.ultronxr.valorant.bean.mybatis.bean.WeaponSkin;
import cn.ultronxr.valorant.service.WeaponService;
import cn.ultronxr.valorant.service.WeaponSkinService;
import cn.ultronxr.valorant.service.impl.WeaponSkinServiceImpl;
import com.github.houbb.opencc4j.util.ZhConverterUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ultronxr
 * @date 2023/02/20 18:21
 * @description <b>武器信息</b> 及 <b>武器皮肤（包含皮肤、升级、炫彩）</b>API
 */
@Component
@Slf4j
public class WeaponAndSkinAPI extends BaseAPI {

    private static final String WEAPON_API = ValorantDotComAPIEnum.Weapon.getUrl();

    @Autowired
    private WeaponService weaponService;

    @Autowired
    private WeaponSkinService weaponSkinService;

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;


    /**
     * 更新数据库中的武器、皮肤内容<br/>
     * 如果缓存了武器、皮肤信息，那么需要进行清理并更新！（这里默认会清理 redis 缓存）
     *
     * @return true-更新成功；false-更新失败
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean processAndUpdateDB() {
        log.info("正在更新武器皮肤数据库...");
        String responseBody = requestAPI(WEAPON_API, null);
        if(StringUtils.isEmpty(responseBody)) {
            return false;
        }

        JSONArray dataArray = parseData(responseBody);
        if(null == dataArray) {
            return false;
        }

        List<Weapon> weaponList = new ArrayList<>(25);
        List<WeaponSkin> skinList = new ArrayList<>(4000);
        parseWeapon(dataArray, weaponList, skinList);

        if(weaponService.saveOrUpdateBatch(weaponList)
                && weaponSkinService.saveOrUpdateBatch(skinList)) {
            // 清理 redis 缓存
            redisTemplate.opsForHash().delete(WeaponSkinServiceImpl.REDIS_CACHE_KEY, WeaponSkinServiceImpl.REDIS_CACHE_HASH_KEY);
            log.info("更新武器及皮肤数据库完成。武器（weapon）数据量={}，皮肤（skin/level/chroma）数据量={}", weaponList.size(), skinList.size());
            return true;
        }
        log.info("更新武器及皮肤数据库失败。");
        return false;
    }

    /**
     * 把请求 API 返回的数据的 json "data" 节点解析成 JSONArray
     */
    @Override
    public JSONArray parseData(String responseBody) {
        JSONObject rootObj = JSONUtil.parseObj(responseBody, false);
        if(!rootObj.get("status", String.class).equals("200")) {
            return null;
        }
        // 获取 "data" 节点
        return rootObj.getJSONArray("data");
    }

    /**
     * 解析武器信息数据
     */
    private void parseWeapon(JSONArray dataArray, @NotNull List<Weapon> weaponList, @NotNull List<WeaponSkin> skinList) {
        if(null == dataArray || dataArray.isEmpty()) {
            return;
        }

        dataArray.forEach(obj -> {
            JSONObject jObj = (JSONObject) obj;
            Weapon weapon = JSONUtil.toBean(jObj, Weapon.class);
            weapon.setDisplayName(ZhConverterUtil.toSimple(weapon.getDisplayName()));
            weaponList.add(weapon);

            JSONArray skinArray = jObj.getJSONArray("skins");
            parseWeaponSkin(skinArray, weapon.getUuid(), skinList);
        });
    }

    /**
     * 解析武器的皮肤数据
     */
    private void parseWeaponSkin(JSONArray skinArray, String parentWeaponID, @NotNull List<WeaponSkin> skinList) {
        if(null == skinArray || skinArray.isEmpty()) {
            return;
        }

        skinArray.forEach(obj -> {
            JSONObject jObj = (JSONObject) obj;
            WeaponSkin skin = JSONUtil.toBean(jObj, WeaponSkin.class);
            skin.setType("skin");
            skin.setParentWeaponUuid(parentWeaponID);
            skin.setDisplayName(ZhConverterUtil.toSimple(skin.getDisplayName()));
            skinList.add(skin);

            JSONArray levelArray = jObj.getJSONArray("levels"),
                    chromaArray = jObj.getJSONArray("chromas");
            parseWeaponSkinLevelOrChroma(levelArray, parentWeaponID, skin.getUuid(), "level", skin.getContentTierUuid(), skinList);
            parseWeaponSkinLevelOrChroma(chromaArray, parentWeaponID, skin.getUuid(), "chroma", skin.getContentTierUuid(), skinList);
        });
    }

    /**
     * 解析武器皮肤包含的升级、炫彩数据
     */
    private void parseWeaponSkinLevelOrChroma(JSONArray levelArray,
                                              String parentWeaponID, String parentSkinID, String type, String contentTierUuid,
                                              @NotNull List<WeaponSkin> skinList) {
        if(null == levelArray || levelArray.isEmpty()) {
            return;
        }

        levelArray.forEach(obj -> {
            WeaponSkin levelOrChroma = JSONUtil.toBean((JSONObject) obj, WeaponSkin.class);
            levelOrChroma.setType(type);
            levelOrChroma.setParentWeaponUuid(parentWeaponID);
            levelOrChroma.setParentSkinUuid(parentSkinID);
            levelOrChroma.setContentTierUuid(contentTierUuid);
            levelOrChroma.setDisplayName(ZhConverterUtil.toSimple(levelOrChroma.getDisplayName()));
            skinList.add(levelOrChroma);
        });
    }

}
