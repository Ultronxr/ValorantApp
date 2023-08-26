package cn.ultronxr.valorant.exception;

/**
 * @author Ultronxr
 * @date 2023/03/06 22:28
 * @description RSO 异常 —— 触发拳头账号登录认证 API 速率上限<br/>
 *              如下是触发速率上限的响应头举例：
 *              {@code {null=[HTTP/1.1 429 Too Many Requests], CF-RAY=[7fcda606cdee1001-LAX], Server=[cloudflare], Retry-After=[3600], X-RiotGames-CDN=[Cloudflare], Connection=[keep-alive], Vary=[Accept-Encoding], Content-Length=[73], Date=[Sat, 26 Aug 2023 17:03:30 GMT], Content-Type=[application/json]}}
 */
public class RSORateLimitedException extends RuntimeException {

    private String msg = "RSO流程-2-[auth]：认证速率受限！";

    /** 触发速率上限后的响应头中的 Retry-After 等待时间，单位秒 */
    private Integer retryAfter;


    public RSORateLimitedException() {
    }

    public RSORateLimitedException(String msg) {
        this.msg = msg;
    }

    public RSORateLimitedException(int retryAfter) {
        this.retryAfter = retryAfter;
    }

    @Override
    public String getMessage() {
        return this.msg;
    }

    public Integer getRetryAfter() {
        return retryAfter;
    }

}
