package cn.ultronxr.valorant.service.impl;

import cn.hutool.core.img.FontUtil;
import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.json.JSONObject;
import cn.ultronxr.common.constant.ResBundle;
import cn.ultronxr.common.tentcentcloud.cos.COS;
import cn.ultronxr.valorant.api.impl.StoreEntitlementsAPI;
import cn.ultronxr.valorant.auth.RSO;
import cn.ultronxr.valorant.bean.VO.EndProductWeaponSkinVO;
import cn.ultronxr.valorant.bean.enums.RiotAccountRegion;
import cn.ultronxr.valorant.bean.mybatis.bean.EndProductRiotAccount;
import cn.ultronxr.valorant.bean.mybatis.bean.EndProductStoreEntitlements;
import cn.ultronxr.valorant.bean.mybatis.mapper.EndProductStoreEntitlementsMapper;
import cn.ultronxr.valorant.exception.APIUnauthorizedException;
import cn.ultronxr.valorant.service.EndProductStoreEntitlementsService;
import cn.ultronxr.valorant.service.RSOService;
import cn.ultronxr.valorant.util.BeanUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
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

    private static final String FONT_PATH = "font/simhei.ttf";

    // 图片缓存路径
    private static final String CACHE_IMG_PATH_PREFIX = "cache/img/valorant/endProduct/";

    // 图片背景路径
    private static final String IMG_BACKGROUND_PREVIEW = "img/valorant/endProduct/backgroundPreview.png";
    private static final String IMG_BACKGROUND_DETAIL  = "img/valorant/endProduct/backgroundDetail.png";

    // 图片子标题路径
    private static final String IMG_TITLE_MELEE   = "img/valorant/endProduct/titleMelee.png";
    private static final String IMG_TITLE_RIFLE   = "img/valorant/endProduct/titleRifle.png";
    private static final String IMG_TITLE_SNIPER  = "img/valorant/endProduct/titleSniper.png";
    private static final String IMG_TITLE_SMG     = "img/valorant/endProduct/titleSMG.png";
    private static final String IMG_TITLE_SIDEARM = "img/valorant/endProduct/titleSidearm.png";
    private static final String IMG_TITLE_HEAVY   = "img/valorant/endProduct/titleHeavy.png";
    private static final String IMG_TITLE_SHOTGUN = "img/valorant/endProduct/titleShotgun.png";

    // 数组下标对应 sort_end_product 字段排序
    private static final String[] IMG_TITLE_ARRAY =
            new String[] {IMG_TITLE_MELEE, IMG_TITLE_RIFLE, IMG_TITLE_SNIPER, IMG_TITLE_SMG, IMG_TITLE_SIDEARM, IMG_TITLE_HEAVY, IMG_TITLE_SHOTGUN};

    // 背景图片的宽高（宽是X、高是Y）
    private static final int IMG_PIXEL_X = 2000,
                             IMG_PIXEL_Y = 1500;

    // 背景图片的四个角的修正值（相当于可填充内容的部分外面，有一圈 margin）
    private static final int IMG_PIXEL_OFFSET_X_LEFT = 80,
                             IMG_PIXEL_OFFSET_X_RIGHT = 108;
    private static final int IMG_PIXEL_OFFSET_Y_TOP = 206,
                             IMG_PIXEL_OFFSET_Y_BOTTOM = 194;

    // 标题图片的原始宽高、缩放后的宽高
    private static final int IMG_WIDTH_ORIGIN_TITLE = 1199,
                             IMG_HEIGHT_ORIGIN_TITLE = 320;
    private static final int IMG_WIDTH_SCALED_TITLE = 154,
                             IMG_HEIGHT_SCALED_TITLE = 41;

    // 皮肤图片的原始宽高、缩放后的宽高
    private static final int IMG_WIDTH_ORIGIN_SKIN = 512,
                             IMG_HEIGHT_ORIGIN_SKIN = 164;
    private static final int IMG_WIDTH_SCALED_SKIN = 181,
                             IMG_HEIGHT_SCALED_SKIN = 58;

    // 标题图片、皮肤图片的缩放值
    private static final float IMG_SCALE_TITLE = (float) IMG_WIDTH_SCALED_TITLE / (float) IMG_WIDTH_ORIGIN_TITLE,
                               IMG_SCALE_SKIN = (float) IMG_WIDTH_SCALED_SKIN / (float) IMG_WIDTH_ORIGIN_SKIN;

    // 背景图的每行可以填充的皮肤图片数量
    private static final int IMG_COUNT_EACH_ROW = 10;


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
            rso = rsoService.updateRSOForEndProduct(rso.getUserId());
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
    public List<EndProductStoreEntitlements> getSkinByAPI(@NotNull EndProductRiotAccount endProductRiotAccount) {
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

        // 创建本地缓存图片，这两个图片文件就是最终结果，要上传到COS的
        String filenameImgPreview = endProductRiotAccount.getAccountNo() + "_preview.png",
                filenameImgDetail = endProductRiotAccount.getAccountNo() + "_detail.png";
        File localCacheImgPreview = createLocalCacheFile(filenameImgPreview),
                localCacheImgDetail = createLocalCacheFile(filenameImgDetail);

        // 使用输入流读取图片文件资源
        BufferedImage previewImg = readImgFromResourceFile(IMG_BACKGROUND_PREVIEW),
                detailImg = readImgFromResourceFile(IMG_BACKGROUND_DETAIL);

        // 在背景图上填充信息： 编号、服务器地区、初邮
        Color fontColor = new Color(255, 58, 71);
        InputStream fontIS = EndProductStoreEntitlementsServiceImpl.class.getClassLoader().getResourceAsStream(FONT_PATH);
        Font font = FontUtil.createFont(fontIS);
        IoUtil.close(fontIS);
        Font infoFont = font.deriveFont(Font.PLAIN, 30), skinFont = font.deriveFont(Font.PLAIN, 15);
        previewImg = (BufferedImage) ImgUtil.pressText(previewImg, String.valueOf(endProductRiotAccount.getAccountNo()), fontColor, infoFont, 185-IMG_PIXEL_X/2, 135-IMG_PIXEL_Y/2, 1.0f);
        previewImg = (BufferedImage) ImgUtil.pressText(previewImg, RiotAccountRegion.getRegionStrByCode(endProductRiotAccount.getRegion()), fontColor, infoFont, 395-IMG_PIXEL_X/2, 135-IMG_PIXEL_Y/2, 1.0f);
        previewImg = (BufferedImage) ImgUtil.pressText(previewImg, endProductRiotAccount.getHasEmail() ? "带初邮" : "不带初邮", fontColor, infoFont, 645-IMG_PIXEL_X/2, 135-IMG_PIXEL_Y/2, 1.0f);

        // 维护一个当前在背景图片上填充图片的坐标（背景图正中心为原点，向右为 x 轴正方向，向下为 y 轴正方向）
        int x = 0, y = -IMG_PIXEL_Y/2 + IMG_PIXEL_OFFSET_Y_TOP - 70;
        int x1 = 0, y1 = 0;

        int lastTitleImgIndex = -1;

        // 遍历每一种武器
        for(int i = 0; i < weaponSkinVOList.size(); ++i) {
            String[] displayNameList = weaponSkinVOList.get(i).getDisplayNameList(),
                    displayIconList = weaponSkinVOList.get(i).getDisplayIconList();

            int titleImgIndex = weaponSkinVOList.get(i).getSortEndProduct() / 10;
            // 需要添加标题图片的情况
            if(lastTitleImgIndex != titleImgIndex) {
                // 在背景图片上填充标题图片
                x = 0;
                y += IMG_HEIGHT_SCALED_TITLE + 20;

                // 使用 sort_end_product 计算出数组下标，读取子标题图片资源
                Image titleImg = readImgFromResourceFile(IMG_TITLE_ARRAY[titleImgIndex]);
                titleImg = ImgUtil.scale(titleImg, IMG_SCALE_TITLE);
                previewImg = (BufferedImage) ImgUtil.pressImage(previewImg, titleImg, x, y, 1.0f);

                y += IMG_HEIGHT_SCALED_SKIN/2;
                lastTitleImgIndex = titleImgIndex;
            }

            // 遍历这种武器的所有皮肤，并填充在背景图片上
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
                float scaleSkin = Math.min(IMG_SCALE_SKIN,
                        Math.min((float) IMG_HEIGHT_SCALED_SKIN / skinImg.getHeight(null), (float) IMG_WIDTH_SCALED_SKIN / skinImg.getWidth(null)));
                skinImg = ImgUtil.scale(skinImg, scaleSkin);

                // 超出一行，手动换行
                if(x > IMG_PIXEL_X/2 - IMG_PIXEL_OFFSET_X_RIGHT - IMG_WIDTH_SCALED_SKIN/2) {
                    x = -IMG_PIXEL_X/2 + IMG_PIXEL_OFFSET_X_LEFT + IMG_WIDTH_SCALED_SKIN/2;
                    y += IMG_HEIGHT_SCALED_SKIN;
                    y += 25;
                } else if(x == 0) {
                    x = -IMG_PIXEL_X/2 + IMG_PIXEL_OFFSET_X_LEFT + IMG_WIDTH_SCALED_SKIN/2;
                    y += IMG_HEIGHT_SCALED_SKIN/2;
                }

                previewImg = (BufferedImage) ImgUtil.pressImage(previewImg, skinImg, x, y, 1.0f);
                previewImg = (BufferedImage) ImgUtil.pressText(previewImg, displayNameList[j], Color.white, skinFont, x, y + IMG_HEIGHT_SCALED_SKIN/2 + 15, 1.0f);
                x += IMG_WIDTH_SCALED_SKIN;
            }
        }
        ImgUtil.write(previewImg, localCacheImgPreview);
        log.info("图片写入完成：{}", filenameImgPreview);

        cos.uploadFile(filenameImgPreview, "cache/img/valorant/endProduct/" + filenameImgPreview);

        return ResBundle.TENCENT_CLOUD.getString("object.url.prefix") + ResBundle.TENCENT_CLOUD.getString("object.key.path") + filenameImgPreview;
    }

    /**
     * 创建本地图片文件缓存
     *
     * @param filename 图片文件名（文件路径是固定的，不需要传入）
     * @return 创建完成的文件对象<br/>
     *          如果入参为空，返回 {@code null}
     */
    private File createLocalCacheFile(String filename) {
        if(StringUtils.isBlank(filename)) {
            return null;
        }
        File resultFile = new File(CACHE_IMG_PATH_PREFIX + filename);
        if(resultFile.exists()) {
            resultFile.delete();
            resultFile = new File(CACHE_IMG_PATH_PREFIX + filename);
        }
        return resultFile;
    }

    /**
     * 读取资源图片文件（使用输入流实现功能）
     *
     * @param resourcePath 图片资源文件路径
     * @return {@code BufferedImage} 图片对象<br/>
     *          如果入参为空，返回 {@code null}
     */
    private BufferedImage readImgFromResourceFile(String resourcePath) {
        if(StringUtils.isBlank(resourcePath)) {
            return null;
        }
        InputStream is = null;
        BufferedImage img = null;
        try {
            is = EndProductStoreEntitlementsServiceImpl.class.getClassLoader().getResourceAsStream(resourcePath);
            img = ImgUtil.read(is);
        } catch (Exception e) {
            log.warn("读取图片资源失败！", e);
            IoUtil.close(is);
            return null;
        } finally {
            IoUtil.close(is);
        }
        return img;
    }

}
