package cn.ultronxr.framework.config;

import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.ErrorPageRegistrar;
import org.springframework.boot.web.server.ErrorPageRegistry;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * @author Ultronxr
 * @date 2023/05/03 18:10:26
 * @description 错误页面配置
 */
@Component
public class WebErrorPageConfig implements ErrorPageRegistrar {

    private static final String CONTROLLER_PREFIX = "/error/";

    @Override
    public void registerErrorPages(ErrorPageRegistry registry) {
        //ErrorPage error400 = new ErrorPage(HttpStatus.BAD_REQUEST, CONTROLLER_PREFIX + "400");
        //ErrorPage error401 = new ErrorPage(HttpStatus.UNAUTHORIZED, CONTROLLER_PREFIX + "401");
        ErrorPage error404 = new ErrorPage(HttpStatus.NOT_FOUND, CONTROLLER_PREFIX + "404");
        //ErrorPage error500 = new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, CONTROLLER_PREFIX + "500");
        //registry.addErrorPages(error400, error401, error404, error500);
        registry.addErrorPages(error404);
    }

}
