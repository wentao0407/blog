package com.hu.blog.controller.admin;

import cn.hutool.core.io.FileUtil;
import com.hu.blog.common.Result;
import com.hu.blog.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * 管理端-图片上传控制器
 */
@RestController
@RequestMapping("/api/admin/upload")
public class AdminUploadController {

    @Value("${upload.path:./uploads}")
    private String uploadPath;

    @Value("${upload.url-prefix:http://localhost:8080/uploads}")
    private String urlPrefix;

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "gif", "webp");

    /**
     * 上传图片
     *
     * @param file 图片文件
     * @return 图片访问URL
     */
    @PostMapping("/image")
    public Result<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException("文件不能为空");
        }
        String originalName = file.getOriginalFilename();
        String ext = FileUtil.extName(originalName);
        if (!ALLOWED_EXTENSIONS.contains(ext.toLowerCase())) {
            throw new BusinessException("仅支持图片格式");
        }
        String fileName = UUID.randomUUID().toString().replace("-", "") + "." + ext;
        File dest = new File(uploadPath, fileName);
        try {
            dest.getParentFile().mkdirs();
            file.transferTo(dest);
        } catch (IOException e) {
            throw new BusinessException("上传失败");
        }
        Map<String, String> result = new HashMap<>();
        result.put("url", urlPrefix + "/" + fileName);
        return Result.success(result);
    }
}
