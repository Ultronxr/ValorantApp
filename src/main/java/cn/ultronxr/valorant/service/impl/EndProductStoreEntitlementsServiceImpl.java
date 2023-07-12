package cn.ultronxr.valorant.service.impl;

import cn.hutool.json.JSONObject;
import cn.ultronxr.valorant.api.impl.StoreEntitlementsAPI;
import cn.ultronxr.valorant.auth.RSO;
import cn.ultronxr.valorant.bean.mybatis.bean.EndProductStoreEntitlements;
import cn.ultronxr.valorant.bean.mybatis.mapper.EndProductStoreEntitlementsMapper;
import cn.ultronxr.valorant.exception.APIUnauthorizedException;
import cn.ultronxr.valorant.service.EndProductStoreEntitlementsService;
import cn.ultronxr.valorant.service.RSOService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Ultronxr
 * @date 2023/07/05 10:05:55
 * @description
 */
@Service
@Slf4j
public class EndProductStoreEntitlementsServiceImpl extends ServiceImpl<EndProductStoreEntitlementsMapper, EndProductStoreEntitlements> implements EndProductStoreEntitlementsService {

    @Autowired
    private StoreEntitlementsAPI seAPI;

    @Autowired
    private RSOService rsoService;


    private JSONObject requestAPI(RSO rso) {
        JSONObject jObj = null;
        try {
            if(rso.isAccessTokenExpired()) {
                throw new APIUnauthorizedException();
            } else {
                log.info("StoreEntitlements API 正在请求，userId={}", rso.getUserId());
                jObj = seAPI.processSkins(rso);
            }
        } catch (APIUnauthorizedException e1) {
            log.info("RSO token 已过期，尝试更新 token ，userId={}", rso.getUserId());
            rso = rsoService.updateRSO(rso.getUserId());
            try {
                if(rso != null) {
                    jObj = seAPI.processSkins(rso);
                } else {
                    log.warn("RSO token 尝试更新失败！中止请求API！");
                    return null;
                }
            } catch (APIUnauthorizedException e2) {
                log.warn("StoreEntitlements API 账号验证失败！");
            }
        }
        log.info("StoreEntitlements API 请求成功。");
        if(jObj != null) {
            jObj.set("userId", rso.getUserId());
        }
        return jObj;
    }

    @Override
    public List<EndProductStoreEntitlements> skins(String userId) {
        return null;
    }

}
