package cn.ultronxr.valorant;

import com.github.jeffreyning.mybatisplus.conf.EnableMPP;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.ZoneId;
import java.util.TimeZone;

/**
 * @author Ultronxr
 * @date 2023/04/07 14:28:09
 * @description
 */
@SpringBootApplication(scanBasePackages = {
        "cn.ultronxr.common",
        "cn.ultronxr.framework",
        "cn.ultronxr.distributed",
        "cn.ultronxr.valorant",
})
@MapperScan(basePackages = {
        "cn.ultronxr.common.bean.mybatis.mapper",
        "cn.ultronxr.framework.bean.mybatis.mapper",
        "cn.ultronxr.distributed.bean.mybatis.mapper",
        "cn.ultronxr.valorant.bean.mybatis.mapper",
})
@EnableMPP
@EnableScheduling
public class ValorantApplication {

    public static void main(String[] args) {
        SpringApplication.run(ValorantApplication.class, args);
        TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of("GMT+8")));
    }

}
