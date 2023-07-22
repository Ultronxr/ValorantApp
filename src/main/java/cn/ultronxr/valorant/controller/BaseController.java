package cn.ultronxr.valorant.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @author Ultronxr
 * @date 2023/05/03 18:04:46
 * @description
 */
@Controller
@Slf4j
public class BaseController {

    /**
     * 处理 HTTP错误代码 的 controller，跳转到对应的错误页面<br/>
     * {@link cn.ultronxr.framework.config.WebErrorPageConfig}
     */
    @GetMapping("error/{errorCode}")
    public String errorPage(@PathVariable Integer errorCode) {
        log.warn("web 请求错误，错误代码：{}", errorCode);
        switch (errorCode) {
            case 400:
                return "error/400";
            case 401:
                return "error/401";
            case 404:
                return "error/404";
            case 500:
                return "error/500";
            default:
                return "error/other";
        }
    }

    @GetMapping("/")
    public String fakeIndex() {
        return "index";
    }

    /** 后台管理网页 */
    @GetMapping("/magnt/mgemidx/XLlcGAAlOX4fBak8")
    public String management() {
        return "misc/fakeIndex";
    }

    /** 每日商店+夜市查询网页 */
    @GetMapping("/wwdw/sf")
    public String storefront() {
        return "valorant/storefront/batch/both";
    }

    /** 每日商店+夜市查询（带侧边栏）网页 */
    @GetMapping("/wwdw/sf2")
    public String sf2() {
        return "misc/userIndex";
    }

    /** 每日商店+夜市查询（带侧边栏）网页 */
    @GetMapping("/wwdw/sf3")
    public String sf3() {
        return "misc/userIndex2";
    }

    /** 每日商店+夜市查询（合作）网页 */
    @GetMapping("/wwdw/xh")
    public String storefront2() {
        return "valorant/storefront/batch/both2";
    }

    /** 每日商店+夜市 CDK兑换（提号）网页 */
    @GetMapping("/wwdw/cdk")
    public String cdkRedeem() {
        return "valorant/cdk/redeem/redeem";
    }

    /** 成品号选号网页 */
    @GetMapping("/wwdw/cph")
    public String endProduct() {
        return "valorant/endproduct/account/client/list";
    }

}
