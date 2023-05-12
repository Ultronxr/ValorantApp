package cn.ultronxr.framework.interceptor;

import cn.ultronxr.common.util.AjaxResponseUtils;
import cn.ultronxr.framework.annotation.AdminAuthRequired;
import cn.ultronxr.framework.service.SystemAccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * @author Ultronxr
 * @date 2023/05/11 08:08:26
 * @description 后台方法系统账号权限校验拦截器，对于标注了 {@link AdminAuthRequired} 注解的controller进行 cookie 权限校验
 */
@Component
@Slf4j
public class AdminAuthInterceptor implements HandlerInterceptor {

    @Autowired
    private SystemAccountService systemAccountService;


    /**
     * 在请求处理之前进行调用（Controller方法调用之前）
     * 若返回true请求将会继续执行
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 如果不是映射到方法不拦截 直接通过
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        // 检查请求方法是否包含 AdminAuthRequired 注解
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Object result = AnnotationUtils.findAnnotation(handlerMethod.getMethod(), AdminAuthRequired.class);
        if(null == result) {
            // 不需要权限的方法，直接放行
            return true;
        }

        String authToken = null;
        // 从请求的 cookie 中取出 token
        if(null != request.getCookies() && request.getCookies().length > 0) {
            for(Cookie cookie : request.getCookies()) {
                if(cookie.getName().equals(systemAccountService.getCookieName())) {
                    authToken = cookie.getValue();
                    break;
                }
            }
        }
        if(systemAccountService.validateToken(authToken)) {
            // token正确放行
            return true;
        }
        log.warn("请求需要管理员权限的方法但被拒绝：{}#{}，来自客户端：{}",
                handlerMethod.getMethod().getDeclaringClass().getName(), handlerMethod.getMethod().getName(), request.getRemoteAddr());
        return needAdminAuthResponse(response);
    }

    private boolean needAdminAuthResponse(HttpServletResponse response) throws Exception {
        response.setStatus(401);
        //response.sendError(401, "需要管理员权限！");
        //response.setCharacterEncoding("UTF-8");
        //response.setContentType("application/json; charset=utf-8");
        //PrintWriter pw = response.getWriter();
        //pw.write(AjaxResponseUtils.HTTP.unauthorized("需要管理员权限！").toString());

        return false;
    }

    /***
     * 请求处理之后进行调用，但是在视图被渲染之前（Controller方法调用之后）
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    /***
     * 整个请求结束之后被调用，也就是在DispatchServlet渲染了对应的视图之后执行（主要用于进行资源清理工作）
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }

}
