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

    @GetMapping("/magnt/mgemidx/XLlcGAAlOX4fBak8")
    public String management() {
        return "misc/fakeIndex";
    }

    @GetMapping("/wwdw/sf")
    public String storefront() {
        return "valorant/storefront/batch/both";
    }

    @GetMapping("/wwdw/xh")
    public String storefront2() {
        return "valorant/storefront/batch/both2";
    }

    @GetMapping("/wwdw/cdk")
    public String cdkRedeem() {
        return "valorant/cdk/redeem/redeem";
    }

}
