/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.ociworker.controller.LogController
 *  com.ociworker.model.vo.ResponseData
 *  com.ociworker.service.LogPersistService
 *  jakarta.annotation.Resource
 *  org.springframework.web.bind.annotation.PostMapping
 *  org.springframework.web.bind.annotation.RequestBody
 *  org.springframework.web.bind.annotation.RequestMapping
 *  org.springframework.web.bind.annotation.RestController
 */
package com.ociworker.controller;

import com.ociworker.model.vo.ResponseData;
import com.ociworker.service.LogPersistService;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value={"/api/log"})
public class LogController {
    @Resource
    private LogPersistService logPersistService;

    @PostMapping(value={"/search"})
    public ResponseData<?> search(@RequestBody Map<String, String> params) {
        String keyword = params.get("keyword");
        List all = this.logPersistService.readAllLines();
        if (keyword == null || keyword.isBlank()) {
            return ResponseData.ok((Object)all);
        }
        String lowerKey = keyword.toLowerCase();
        List<String> matched = all.stream().filter(line -> line.toLowerCase().contains(lowerKey)).toList();
        return ResponseData.ok(matched);
    }
}

