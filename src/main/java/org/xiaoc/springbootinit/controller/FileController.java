package org.xiaoc.springbootinit.controller;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.xiaoc.springbootinit.common.BaseResponse;
import org.xiaoc.springbootinit.common.ErrorCode;
import org.xiaoc.springbootinit.common.ResultUtils;
import org.xiaoc.springbootinit.exception.BusinessException;
import org.xiaoc.springbootinit.exception.ThrowUtils;
import org.xiaoc.springbootinit.manager.CosManager;
import org.xiaoc.springbootinit.model.dto.file.UploadFileRequest;
import org.xiaoc.springbootinit.model.entity.User;
import org.xiaoc.springbootinit.model.vo.LoginUserVO;
import org.xiaoc.springbootinit.service.UserService;

import java.io.File;

@Slf4j
@RestController
@RequestMapping("/file")
public class FileController {

    @Resource
    private UserService userService;

    @Resource
    private CosManager cosManager;

    /**
     * 上传头像
     * @param multipartFile
     * @param request
     * @return
     */
    @PostMapping("/avatar/upload")
    public BaseResponse<String> upload(@RequestPart("file") MultipartFile multipartFile,
                                       HttpServletRequest request){
        //校验文件大小
        validFile(multipartFile);
        //获取用户
        LoginUserVO user = userService.getLoginUser(request);
        // 文件名 文件路劲
        String uuid = RandomStringUtils.randomAlphabetic(8);
        String fileName = uuid + "-" + multipartFile.getOriginalFilename();
        String filePath = String.format("/avatar/%s",fileName);
        // 文件上传
        File file = null;
        try {
            // 上传文件
            file = File.createTempFile(filePath, null);
            multipartFile.transferTo(file);
            cosManager.putObject(filePath, file);
            // 返回可访问地址
            String url = "https://web-object-1318943201.cos.ap-shanghai.myqcloud.com" + filePath;
            //保存到数据库
            userService.update(Wrappers.<User>lambdaUpdate().eq(User::getId,user.getId()).set(User::getUserAvatar,url));
            return ResultUtils.success("https://web-object-1318943201.cos.ap-shanghai.myqcloud.com" + filePath);
        } catch (Exception e) {
            log.error("file upload error, filepath = " + filePath, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传失败");
        } finally {
            if (file != null) {
                // 删除临时文件
                boolean delete = file.delete();
                if (!delete) {
                    log.error("file delete error, filepath = {}", filePath);
                }
            }
        }
    }

    /**
     * 校验文件大小
     * @param multipartFile
     */
    private void validFile(MultipartFile multipartFile) {
        long fileSize = multipartFile.getSize();
        // 最大50M
        long maxSize = 1024 * 1024 * 50L;
        ThrowUtils.throwIf(fileSize > maxSize, ErrorCode.SYSTEM_ERROR,"头像大小不能大于50MB");
    }

}
