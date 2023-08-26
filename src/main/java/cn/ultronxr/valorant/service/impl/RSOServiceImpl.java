package cn.ultronxr.valorant.service.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import cn.hutool.http.cookie.GlobalCookieManager;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.ultronxr.valorant.auth.RSO;
import cn.ultronxr.valorant.bean.mybatis.bean.EndProductRiotAccount;
import cn.ultronxr.valorant.bean.mybatis.bean.RiotAccount;
import cn.ultronxr.valorant.bean.mybatis.mapper.EndProductRiotAccountMapper;
import cn.ultronxr.valorant.bean.mybatis.mapper.RiotAccountMapper;
import cn.ultronxr.valorant.exception.*;
import cn.ultronxr.valorant.service.RSOService;
import cn.ultronxr.valorant.util.RSOUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.net.CookieStore;
import java.net.HttpCookie;
import java.util.List;

/**
 * @author Ultronxr
 * @date 2023/03/04 12:30
 * @description
 */
@Service
@Slf4j
public class RSOServiceImpl implements RSOService {

    @Autowired
    private RiotAccountMapper accountMapper;

    @Autowired
    private EndProductRiotAccountMapper endProductRiotAccountMapper;

    /**
     * Cookie ReAuth Test
     */
    public static void main(String[] args) {
        CookieStore cookieStore = GlobalCookieManager.getCookieManager().getCookieStore();
        // 清空 cookie ，防止服务器记住账号登录
        List<HttpCookie> cookies = cookieStore.getCookies();
        if(cookies.size() > 0) {
            cookieStore.removeAll();
            log.info("清空cookie");
        }

        HttpRequest request = HttpUtil.createPost(RSOUtils.AUTH_URL);

        String cookie = "__cf_bm=I3SNpKc_aJZymOjeoxwjYwjtRSJ_BgQWjAUiyPSiRmU-1692778689-0-AR9DbCKKDcH9b6HrTOKQbxy39X7kXbV2CTvTtlvQo2LH+xWRvggShKGZUDu9Fpw0eMwjvS6e8fHerJNo+iTfmAY=; tdid=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjEzYTJiMmVlLTBkOTktNDAwMC05ODcyLTA0NzYxYTQ4MmRlZCIsIm5vbmNlIjoiL1VXOGhIS0Erakk9IiwiaWF0IjoxNjkyNzc5MTg1fQ.mvqtKV3eF5ocN3epOb-nAiAvdlKvOa42bA7fYh1Dsn8; clid=as1; csid=VSF08A64Z4lrUqMxwaL7Cw.pYuWMQLdoWNYIldSF5xXbw; ssid=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzc2lkIjoiVlNGMDhBNjRaNGxyVXFNeHdhTDdDdy5wWXVXTVFMZG9XTllJbGRTRjV4WGJ3Iiwic3ViIjoiNmUxZTBmNTQtMDQwZS01OGNkLTk1ZjQtNzdiODA2Y2VmOTA4IiwiaWF0IjoxNjkyNzc5MTg1fQ.DR__1SMTUXfBkn6Bki3vRJnemi1W3bJZtqh_kbNPtgs; sub=6e1e0f54-040e-58cd-95f4-77b806cef908";
        request.setUrl(RSOUtils.RE_AUTH_URL)
                .method(Method.GET)
                .headerMap(RSOUtils.getHeader(), true)
                .header("Cookie", cookie);
        HttpResponse responseReAuth = request.execute();
        if(responseReAuth.getStatus() == 303 && responseReAuth.headers().containsKey("location")) {
            // 解析 reauth 免密获取的token
            String location = responseReAuth.header("location");
            RSO rso = new RSO();
            RSOUtils.resolveAuthResUri(location, rso);
            log.info(location);
            log.info("{}", rso.getAccessToken());
        }
    }

    /**
     * 每天随机生成请求头 User-Agent
     */
    @Scheduled(cron = "0 0 7 1/1 * ? ")
    public void updateHeaderUA() {
        RSOUtils.generateRandomUA();
    }

    @Override
    public RSO processRSO(HttpRequest request, String username, String password, String multiFactorCode, boolean needRequestEntitlementsToken) {
        if(null == request) {
            return null;
        }

        CookieStore cookieStore = GlobalCookieManager.getCookieManager().getCookieStore();
        // 清空 cookie ，防止服务器记住账号登录
        List<HttpCookie> cookies = cookieStore.getCookies();
        if(cookies.size() > 0) {
            cookieStore.removeAll();
            log.info("清空cookie");
        }

        // TODO 2023/05/13 16:50:59 入参添加 cookie 参数，检查cookie是否过期，如果没有过期优先使用cookie进行免密登录
        //String cookie = "__cf_bm=I3SNpKc_aJZymOjeoxwjYwjtRSJ_BgQWjAUiyPSiRmU-1692778689-0-AR9DbCKKDcH9b6HrTOKQbxy39X7kXbV2CTvTtlvQo2LH+xWRvggShKGZUDu9Fpw0eMwjvS6e8fHerJNo+iTfmAY=; tdid=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjEzYTJiMmVlLTBkOTktNDAwMC05ODcyLTA0NzYxYTQ4MmRlZCIsIm5vbmNlIjoiL1VXOGhIS0Erakk9IiwiaWF0IjoxNjkyNzc5MTg1fQ.mvqtKV3eF5ocN3epOb-nAiAvdlKvOa42bA7fYh1Dsn8; clid=as1; csid=VSF08A64Z4lrUqMxwaL7Cw.pYuWMQLdoWNYIldSF5xXbw; ssid=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzc2lkIjoiVlNGMDhBNjRaNGxyVXFNeHdhTDdDdy5wWXVXTVFMZG9XTllJbGRTRjV4WGJ3Iiwic3ViIjoiNmUxZTBmNTQtMDQwZS01OGNkLTk1ZjQtNzdiODA2Y2VmOTA4IiwiaWF0IjoxNjkyNzc5MTg1fQ.DR__1SMTUXfBkn6Bki3vRJnemi1W3bJZtqh_kbNPtgs; sub=6e1e0f54-040e-58cd-95f4-77b806cef908";
        //request.setUrl(RSOUtils.RE_AUTH_URL)
        //        .method(Method.GET)
        //        .headerMap(RSOUtils.getHeader(), true)
        //        .header("Cookie", cookie);
        //HttpResponse responseReAuth = request.execute();
        //if(responseReAuth.getStatus() == 303 && responseReAuth.headers().containsKey("location")) {
        //    // 解析reauth 免密获取的token
        //    String location = responseReAuth.header("location");
        //
        //}

        request.setUrl(RSOUtils.AUTH_URL)
                .method(Method.POST)
                .headerMap(RSOUtils.getHeader(), true)
                .body(RSOUtils.getPingBodyJsonStr());
        HttpResponse response = request.execute();
        log.info("RSO流程第一步：ping response = {}", StringUtils.substring(response.body(), 0, 100));

        JSONObject resObj = JSONUtil.parseObj(response.body());
        String resType = resObj.getStr("type", "null");
        if(!"response".equals(resType)) {
            request.method(Method.PUT)
                    .headerMap(RSOUtils.getHeader(), true)
                    .cookie(cookieStore.getCookies())
                    .body(RSOUtils.getAuthBodyJsonStr(username, password, true));
            response = request.execute();
            log.info("RSO流程第二步：auth response = {}", StringUtils.substring(response.body(), 0, 100));

            resObj = JSONUtil.parseObj(response.body());
            resType = resObj.getStr("type", "null");
            if("multifactor".equals(resType)) {
                // 两步验证
                request.method(Method.PUT)
                        .headerMap(RSOUtils.getHeader(), true)
                        .body(RSOUtils.getMultiFactorBodyJsonStr(multiFactorCode, true));
                response = request.execute();
                log.info("RSO流程第三步：multifactor response = {}", StringUtils.substring(response.body(), 0, 100));

                JSONObject multiFactorResObj  = JSONUtil.parseObj(response.body());
                if (multiFactorResObj.containsKey("error") && multiFactorResObj.getStr("error").equals("multifactor_attempt_failed")) {
                    throw new RSOMultiFactorAttemptFailedException();
                } else {
                    // 两步验证成功
                    resObj = multiFactorResObj;
                }
            } else if ("response".equals(resType)) {
                // 解析结果
            } else if ("auth".equals(resType)) {
                log.warn("RSO流程失败：response headers = {}", response.headers());
                response.close();
                // 认证错误
                String error = resObj.getStr("error");
                if(StringUtils.isNotEmpty(error)) {
                    switch (error) {
                        case "auth_failure":
                            throw new RSOAuthFailureException();
                        case "rate_limited":
                            int retryAfter = Integer.parseInt(response.header("Retry-After"));
                            throw new RSORateLimitedException(retryAfter);
                        default:
                            throw new RSOUnknownAuthErrorException();
                    }
                }
            } else {
                log.warn("RSO流程失败：response headers = {}", response.headers());
                response.close();
                // 其他未知情况
                throw new RSOUnknownResponseTypeException();
            }
        }

        // 解析结果
        RSO rso = new RSO();
        rso = RSOUtils.parseAccessToken(resObj, rso);
        rso.setCookie(response.getCookieStr());

        // 如果不需要获取 entitlements token ，那么在此中断，返回RSO
        if(!needRequestEntitlementsToken) {
            response.close();
            log.info("RSO流程完成（无需请求 entitlements token）：RSO = {}", rso.toString());
            return rso;
        }

        request.setUrl(RSOUtils.ENTITLEMENTS_URL)
                .method(Method.POST)
                .headerMap(RSOUtils.getHeader(), true)
                .header("Authorization", "Bearer " + rso.getAccessToken())
                .body("{}");
        response = request.execute();
        log.info("RSO流程第四步：entitlements response = {}", response.body());

        resObj = JSONUtil.parseObj(response.body());
        response.close();
        if(StringUtils.isNotEmpty(resObj.getStr("errorCode"))) {
            throw new RSOEntitlementsErrorException();
        }
        rso = RSOUtils.parseEntitlementsToken(resObj, rso);
        log.info("RSO流程完成：RSO = {}", rso.toString());
        return rso;
    }

    @Override
    public RSO initRSO(String username, String password, String multiFactorCode) {
        RSO rso = null;
        try {
            HttpRequest request = HttpUtil.createPost(RSOUtils.AUTH_URL);
            rso = this.processRSO(request, username, password, multiFactorCode, true);
        } catch (Exception e) {
            log.warn("RSO验证失败：username = {}, exception = {}", username, e.getMessage());
            return null;
        }
        return rso;
    }

    @Override
    public RSO updateRSO(String userId) {
        RSO rso = null;
        RiotAccount account = accountMapper.selectById(userId);
        boolean needRequestEntitlementsToken = StringUtils.isEmpty(account.getEntitlementsToken());
        try {
            HttpRequest request = HttpUtil.createPost(RSOUtils.AUTH_URL);
            rso = this.processRSO(request, account.getUsername(), account.getPassword(), account.getMultiFactor(), needRequestEntitlementsToken);
            // 如果不需要请求 entitlementsToken，那么需要手动填充 RSO 对象字段
            if(!needRequestEntitlementsToken) {
                rso.setEntitlementsToken(account.getEntitlementsToken());
            }
        } catch (RSOAuthFailureException authFailureEx) {
            log.warn("RSO验证失败：exception={}, userId={}, username={}, 更新RiotAccount数据库字段 is_auth_failure=1",
                    authFailureEx.getMessage(), account.getUserId(), account.getUsername());
            account.setIsAuthFailure(true);
            accountMapper.updateById(account);
            return null;
        } catch (RSORateLimitedException rateLimitedException) {
            log.warn("RSO验证失败：exception={}, Retry-After={}秒（{}分钟）",
                    rateLimitedException.getMessage(), rateLimitedException.getRetryAfter(), rateLimitedException.getRetryAfter() / 60.0f);
            try {
                long sleepMillis = (long) ((rateLimitedException.getRetryAfter() + 10) * 1000.0f);
                log.info("触发retry-after，开始休眠，休眠时长sleepMillis={}", sleepMillis);
                Thread.sleep(sleepMillis);
                log.info("触发retry-after，休眠结束，休眠时长sleepMillis={}", sleepMillis);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return null;
        } catch (Exception e) {
            log.warn("RSO验证失败：exception={}, userId={}, username={}, ", e.getMessage(), account.getUserId(), account.getUsername());
            return null;
        }
        // 如果userId不一致，说明是excel导入未验证的账号，需要更新userId
        if(!rso.getUserId().equals(account.getUserId())) {
            account.setUserId(rso.getUserId());
        }
        account.setAccessToken(rso.getAccessToken());
        account.setAccessTokenExpireAt(rso.getAccessTokenExpireAt());
        if(needRequestEntitlementsToken) {
            account.setEntitlementsToken(rso.getEntitlementsToken());
        }
        accountMapper.update(account,
                new LambdaUpdateWrapper<RiotAccount>()
                        .eq(RiotAccount::getAccountNo, account.getAccountNo())
                        .set(RiotAccount::getUserId, account.getUserId())
                        .set(RiotAccount::getAccessToken, account.getAccessToken())
                        .set(RiotAccount::getAccessTokenExpireAt, account.getAccessTokenExpireAt())
                        .set(needRequestEntitlementsToken, RiotAccount::getEntitlementsToken, account.getEntitlementsToken())
        );
        return rso;
    }

    @Override
    public RSO updateRSOForEndProduct(String userId) {
        RSO rso = null;
        EndProductRiotAccount account = endProductRiotAccountMapper.selectOne(
                new LambdaQueryWrapper<EndProductRiotAccount>()
                        .eq(EndProductRiotAccount::getUserId, userId)
        );
        boolean needRequestEntitlementsToken = StringUtils.isEmpty(account.getEntitlementsToken());
        try {
            HttpRequest request = HttpUtil.createPost(RSOUtils.AUTH_URL);
            rso = this.processRSO(request, account.getUsername(), account.getPassword(), account.getMultiFactor(), needRequestEntitlementsToken);
            // 如果不需要请求 entitlementsToken，那么需要手动填充 RSO 对象字段
            if(!needRequestEntitlementsToken) {
                rso.setEntitlementsToken(account.getEntitlementsToken());
            }
        } catch (Exception e) {
            log.warn("RSO验证失败：exception={}, userId={}, username={}, ", e.getMessage(), account.getUserId(), account.getUsername());
            return null;
        }
        account.setAccessToken(rso.getAccessToken());
        account.setAccessTokenExpireAt(rso.getAccessTokenExpireAt());
        if(needRequestEntitlementsToken) {
            account.setEntitlementsToken(rso.getEntitlementsToken());
        }
        endProductRiotAccountMapper.update(account,
                new LambdaUpdateWrapper<EndProductRiotAccount>()
                        .eq(EndProductRiotAccount::getAccountNo, account.getAccountNo())
                        .set(EndProductRiotAccount::getAccessToken, account.getAccessToken())
                        .set(EndProductRiotAccount::getAccessTokenExpireAt, account.getAccessTokenExpireAt())
                        .set(needRequestEntitlementsToken, EndProductRiotAccount::getEntitlementsToken, account.getEntitlementsToken())
        );
        return rso;
    }

    @Override
    public RSO requestRSOByAccount(RiotAccount account) {
        RSO rso;
        if(StringUtils.isNotEmpty(account.getUserId())) {
            rso = updateRSO(account.getUserId());
        } else {
            rso = initRSO(account.getUsername(), account.getPassword(), account.getMultiFactor());
        }
        return rso;
    }

    @Override
    public RSO fromAccount(RiotAccount account) {
        RSO rso = new RSO();
        rso.updateToken(account);
        return rso;
    }

    @Override
    public RSO fromAccount(String userId) {
        RiotAccount account = accountMapper.selectById(userId);
        return null == account ? null : fromAccount(account);
    }

}
