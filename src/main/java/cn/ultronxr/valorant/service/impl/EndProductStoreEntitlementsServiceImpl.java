package cn.ultronxr.valorant.service.impl;

import cn.hutool.core.img.ImgUtil;
import cn.hutool.json.JSONObject;
import cn.ultronxr.common.constant.ResBundle;
import cn.ultronxr.common.tentcentcloud.cos.COS;
import cn.ultronxr.valorant.api.impl.StoreEntitlementsAPI;
import cn.ultronxr.valorant.auth.RSO;
import cn.ultronxr.valorant.bean.VO.EndProductWeaponSkinVO;
import cn.ultronxr.valorant.bean.mybatis.bean.EndProductRiotAccount;
import cn.ultronxr.valorant.bean.mybatis.bean.EndProductStoreEntitlements;
import cn.ultronxr.valorant.bean.mybatis.mapper.EndProductStoreEntitlementsMapper;
import cn.ultronxr.valorant.exception.APIUnauthorizedException;
import cn.ultronxr.valorant.service.EndProductStoreEntitlementsService;
import cn.ultronxr.valorant.service.RSOService;
import cn.ultronxr.valorant.util.BeanUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
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

    @Autowired
    private COS cos;


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
    public List<EndProductStoreEntitlements> skins(@NotNull EndProductRiotAccount endProductRiotAccount) {
        RSO rso = rsoService.fromAccount(BeanUtils.transformToRiotAccountFromEndProduct(endProductRiotAccount));
        JSONObject jObj = requestAPI(rso);
        if(null != jObj) {
            List<EndProductStoreEntitlements> list = seAPI.getContent(jObj, endProductRiotAccount.getAccountNo());
            this.saveBatch(list);
            return list;
        }
        return null;
    }

    @Override
    public String generateSkinImg(@NotNull EndProductRiotAccount endProductRiotAccount) {
        List<EndProductWeaponSkinVO> weaponSkinVOList = this.getBaseMapper().selectSkinForOneAccount(endProductRiotAccount.getAccountNo());
        if(null == weaponSkinVOList || weaponSkinVOList.isEmpty()) {
            return null;
        }

        String filename = endProductRiotAccount.getAccountNo() + ".jpg";
        File resultFile = new File("cache/img/valorant/endproduct/" + filename);
        if(resultFile.exists()) {
            resultFile.delete();
            resultFile = new File("cache/img/valorant/endproduct/" + filename);
        }

        BufferedImage resultImg = null, skinImgBG = null;
        InputStream resultImgIS = null, skinImgBGIS = null;
        try {
            resultImgIS = EndProductStoreEntitlementsServiceImpl.class.getClassLoader().getResourceAsStream("img/valorant/endproduct/resultBG.jpg");
            skinImgBGIS = EndProductStoreEntitlementsServiceImpl.class.getClassLoader().getResourceAsStream("img/valorant/endproduct/skinBG.png");
            //resultImgIS = new FileInputStream("cache/img/valorant/endproduct/resultBG.jpg");
            //skinImgBGIS = new FileInputStream("cache/img/valorant/endproduct/skinBG.png");
            resultImg = ImgUtil.read(resultImgIS);
            skinImgBG = ImgUtil.read(skinImgBGIS);
        } catch (Exception e) {
            log.warn("读取图片失败！", e);
        } finally {
            if(resultImgIS != null) {
                try {
                    resultImgIS.close();
                } catch (IOException e) {
                    log.warn("输入流关闭失败！", e);
                }
            }
            if(skinImgBGIS != null) {
                try {
                    resultImgIS.close();
                } catch (IOException e) {
                    log.warn("输入流关闭失败！", e);
                }
            }
        }

        for(int i = 0; i < weaponSkinVOList.size(); ++i) {
            String[] displayNameList = weaponSkinVOList.get(i).getDisplayNameList(),
                    displayIconList = weaponSkinVOList.get(i).getDisplayIconList();

            for(int j = 0; j < displayNameList.length; ++j) {
                log.info("正在处理图片：{}", displayNameList[j]);

                String url = displayIconList[j];
                Image skinImg = null;
                try {
                    skinImg = ImgUtil.read(new URL(url));
                } catch (MalformedURLException e) {
                    log.warn("读取URL图片失败！", e);
                    throw new RuntimeException(e);
                }
                // 处理皮肤图片：填充背景、控制图片大小
                float scaleBefore = 1.0f;
                if(skinImg.getWidth(null) > 512 || skinImg.getHeight(null) > 120) {
                    scaleBefore = Math.min(512.0f / skinImg.getWidth(null), 120.0f / skinImg.getHeight(null));
                    skinImg = ImgUtil.scale(skinImg, scaleBefore);
                }
                skinImg = ImgUtil.pressImage(skinImgBG, skinImg, 0, 0, 1.0f);
                skinImg = ImgUtil.scale(skinImg, 0.75f);

                // 计算每一个皮肤图片在最终背景图片上的偏移量
                int xOffset = 0, yOffset = 0;
                xOffset = -(1920 - 192 - 384*j);
                yOffset = -(1080 - 60 - 120*i);
                resultImg = (BufferedImage) ImgUtil.pressImage(resultImg, skinImg, xOffset, yOffset, 1.0f);

                log.info("图片处理完成：{}", displayNameList[j]);
            }
        }
        ImgUtil.write(resultImg, resultFile);
        log.info("图片写入完成：{}", filename);

        cos.uploadFile(filename, "cache/img/valorant/endproduct/" + filename);
        return "https://ultronxr-cos-01-1253915264.cos.ap-shanghai.myqcloud.com/" + ResBundle.TENCENT_CLOUD.getString("object.key.path") + filename;
    }

    //public static void main(String[] args) {
    //    BufferedImage backgroundImg = ImgUtil.read("img/valorant/endproduct/resultBG.jpg");
    //
    //    File resultFile = new File("cache/img/endProduct/test.jpg");
    //    if(resultFile.exists()) {
    //        resultFile.delete();
    //        resultFile = new File("cache/img/endProduct/test.jpg");
    //    }
    //
    //    Image skinImg = null;
    //    try {
    //        String url = "https://media.valorant-api.com/weaponskinlevels/232f93cb-4c6e-512c-9961-db9b90929400/displayicon.png";
    //        //String url = "https://media.valorant-api.com/weaponskinlevels/ec7585ec-4569-3c28-bd82-709cfa26e95b/displayicon.png";
    //        //skinImg = ImgUtil.read(new URL(url));
    //        skinImg = ImgUtil.read("E:\\Downloads\\Chrome\\displayicon (2).png");
    //
    //        // 处理皮肤图片：填充背景、控制图片大小
    //        BufferedImage skinImgBG = ImgUtil.read("img/valorant/endproduct/skinBG.png");
    //        skinImg = ImgUtil.pressImage(skinImgBG, skinImg, 0, 0, 1.0f);
    //        skinImg = ImgUtil.scale(skinImg, 0.75f);
    //    } catch (Exception e) {
    //        throw new RuntimeException(e);
    //    }
    //    int xOffset = 0, yOffset = 0;
    //    xOffset = -(1920-192-384*5);
    //    yOffset = -(1080-62);
    //    Image resultImg = ImgUtil.pressImage(backgroundImg, skinImg, xOffset, yOffset, 1.0f);
    //
    //    //ImgUtil.write(skinImg, resultFile);
    //    ImgUtil.write(resultImg, resultFile);
    //}

}
