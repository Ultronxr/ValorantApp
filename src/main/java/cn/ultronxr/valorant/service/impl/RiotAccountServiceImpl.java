package cn.ultronxr.valorant.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import cn.ultronxr.valorant.auth.RSO;
import cn.ultronxr.valorant.bean.DTO.RiotAccountDTO;
import cn.ultronxr.valorant.bean.enums.RiotAccountCreateState;
import cn.ultronxr.valorant.bean.enums.RiotAccountRegion;
import cn.ultronxr.valorant.bean.mybatis.bean.RiotAccount;
import cn.ultronxr.valorant.bean.mybatis.mapper.RiotAccountMapper;
import cn.ultronxr.valorant.service.RSOService;
import cn.ultronxr.valorant.service.RiotAccountService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static cn.ultronxr.valorant.bean.enums.RiotAccountCreateState.*;

/**
 * @author Ultronxr
 * @date 2023/02/22 15:11
 * @description
 */
@Service
@Slf4j
public class RiotAccountServiceImpl extends ServiceImpl<RiotAccountMapper, RiotAccount> implements RiotAccountService {

    @Autowired
    private RiotAccountMapper accountMapper;

    @Autowired
    private RSOService rsoService;


    @Override
    public RiotAccountCreateState create(RiotAccount account) {
        if(StringUtils.isBlank(account.getUsername()) || StringUtils.isBlank(account.getPassword())) {
            log.info("拳头账号添加失败：{}", MISSING_REQUIRED_FIELD);
            return MISSING_REQUIRED_FIELD;
        }

        RiotAccount accountInDB = this.getOne(new LambdaQueryWrapper<RiotAccount>().eq(RiotAccount::getUsername, account.getUsername()));
        if(accountInDB != null) {
            log.info("拳头账号添加失败：{}", DUPLICATE);
            return DUPLICATE;
        }

        log.info("添加拳头账号：{}", account);
        // 手动设置是否带初邮
        account.setHasEmail(StringUtils.isNotEmpty(account.getEmail()));
        RSO rso = rsoService.requestRSOByAccount(account);
        if(null != rso) {
            account.setUserId(rso.getUserId());
            account.setAccessToken(rso.getAccessToken());
            account.setAccessTokenExpireAt(rso.getAccessTokenExpireAt());
            account.setEntitlementsToken(rso.getEntitlementsToken());
            account.setIsAuthFailure(false);
            if(this.save(account)) {
                log.info("拳头账号添加成功。username={}", account.getUsername());
                return OK;
            }
        }
        log.info("拳头账号添加失败：{}", AUTH_FAILURE);
        return AUTH_FAILURE;
    }

    @Override
    public RiotAccountCreateState createWithoutRSO(RiotAccount account) {
        if(StringUtils.isBlank(account.getUsername()) || StringUtils.isBlank(account.getPassword())) {
            log.info("拳头账号添加失败：{}", MISSING_REQUIRED_FIELD);
            return MISSING_REQUIRED_FIELD;
        }

        RiotAccount accountInDB = this.getOne(new LambdaQueryWrapper<RiotAccount>().eq(RiotAccount::getUsername, account.getUsername()));
        if(accountInDB != null) {
            log.info("拳头账号添加失败：{}", DUPLICATE);
            return DUPLICATE;
        }

        log.info("添加拳头账号：{}", account);
        // 手动设置是否带初邮
        account.setHasEmail(StringUtils.isNotEmpty(account.getEmail()));
        account.setUserId("temp" + UUID.randomUUID().toString());
        account.setIsAuthFailure(false);
        account.setIsDel(false);
        if(this.save(account)) {
            log.info("拳头账号添加成功。username={}", account.getUsername());
            return OK;
        }
        log.info("拳头账号添加失败：{}", AUTH_FAILURE);
        return AUTH_FAILURE;
    }

    @Override
    public boolean importFile(MultipartFile file, RiotAccountRegion region) {
        FileInputStream fileIS = null;
        try {
            String transferFilename = "拳头账号导入模板-" + DateUtil.now().replaceAll("[ :]", "-") + ".xlsx";
            File transferFile = new File("cache/files/", transferFilename);
            FileUtils.copyInputStreamToFile(file.getInputStream(), transferFile);
            //file.transferTo(transferFile);

            fileIS = new FileInputStream(transferFile);
            //fileIS = file.getInputStream();
            ExcelReader excelReader = ExcelUtil.getReader(fileIS, 0);
            List<List<Object>> data = excelReader.read(2);
            log.info("批量导入拳头账号，解析Excel数据成功。");

            File resultFile = new File("cache/files/", "拳头账号导入结果.xlsx");
            if(resultFile.exists()) {
                resultFile.delete();
                resultFile = new File("cache/files/", "拳头账号导入结果.xlsx");
            }
            ExcelWriter writer = ExcelUtil.getWriter(resultFile, "sheet1");
            try {
                writer.merge(0, 0, 0, 3, "在下方填写账号信息。请勿修改本Excel模板的任何格式！", true);
            } catch (IllegalStateException e) {
                log.warn("首行单元格合并失败：{}", e.getMessage());
            }
            writer.writeHeadRow(Arrays.asList("在下方填写账号信息。请勿修改本Excel模板的任何格式！"));
            List<String> headRow = Arrays.asList("拳头账号用户名", "拳头账号密码", "初始邮箱（若无留空）", "初始邮箱密码（若无留空）", "导入结果");
            writer.writeHeadRow(headRow);
            for (List<Object> row : data) {
                int size = getRowTrueSize(row);
                if(size == 0) {
                    continue;
                }
                RiotAccount account = new RiotAccount();
                account.setUsername(String.valueOf(row.get(0)));
                if(size > 1) {
                    account.setPassword(String.valueOf(row.get(1)));
                }
                if(size > 2) {
                    account.setEmail(String.valueOf(row.get(2)));
                }
                if(size > 3) {
                    account.setEmailPwd(String.valueOf(row.get(3)));
                }
                if(region != null) {
                    account.setRegion(region.getCode());
                }

                RiotAccountCreateState state = this.createWithoutRSO(account);
                if (state != OK) {
                    if(row.size() < 4) {
                        int addCount = 4-row.size();
                        for(int i = 1; i <= addCount; i++) {
                            row.add("");
                        }
                    }
                    if(row.size() > 4) {
                        int removeCount = row.size()-4;
                        for(int i = 1; i <= removeCount; i++) {
                            row.remove(row.size() - 1);
                        }
                    }
                    row.add(state.getMsg());
                    writer.writeRow(row);
                }
            }
            writer.autoSizeColumnAll();
            int failCount = writer.getRowCount() - 2;
            writer.close();
            log.info("写出拳头账号导入结果文件完成。导入总数据量={} ，导入成功数据量={} ，导入失败数量={}", data.size(), data.size()-failCount, failCount);
        } catch (IOException e) {
            log.warn("批量导入拳头账号时，处理Excel数据失败！", e);
            return false;
        } finally {
            try {
                if(fileIS != null) {
                    fileIS.close();
                }
            } catch (IOException e) {
                log.warn("关闭输入流失败！", e);
            }
        }
        return true;
    }

    private int getRowTrueSize(List<Object> row) {
        int res = 0;
        for (Object o : row) {
            if (StringUtils.isNotBlank(String.valueOf(o))) {
                res++;
            }
        }
        return res;
    }

    //@Override
    //public boolean update(RiotAccount account) {
    //    return false;
    //}

    @Override
    public Page<RiotAccount> queryAccount(RiotAccountDTO accountDTO) {
        Page<RiotAccount> page = Page.of(accountDTO.getCurrent(), accountDTO.getSize());

        LambdaQueryWrapper<RiotAccount> wrapper = Wrappers.lambdaQuery();
        wrapper.select(RiotAccount::getUserId, RiotAccount::getUsername, RiotAccount::getAccountNo, RiotAccount::getHasEmail,
                        RiotAccount::getEmail, RiotAccount::getIsAuthFailure, RiotAccount::getRegion)
                .eq(accountDTO.getAccountNo() != null, RiotAccount::getAccountNo, accountDTO.getAccountNo())
                .like(StringUtils.isNotEmpty(accountDTO.getUserId()), RiotAccount::getUserId, accountDTO.getUserId())
                .like(StringUtils.isNotEmpty(accountDTO.getUsername()), RiotAccount::getUsername, accountDTO.getUsername())
                .eq(accountDTO.getHasEmail() != null, RiotAccount::getHasEmail, accountDTO.getHasEmail())
                .eq(accountDTO.getIsAuthFailure() != null, RiotAccount::getIsAuthFailure, accountDTO.getIsAuthFailure())
                .eq(accountDTO.getRegion() != null, RiotAccount::getRegion, accountDTO.getRegion())
                // 如果使用账号编号/ID查找，是可以查出 isDel = true 的账号的
                .eq(accountDTO.getAccountNo() == null && StringUtils.isEmpty(accountDTO.getUserId()), RiotAccount::getIsDel, false)
                .orderByAsc(RiotAccount::getAccountNo);

        return accountMapper.selectPage(page, wrapper);
    }

}
