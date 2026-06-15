package com.ocxworker.controller;

import com.ocxworker.model.vo.ResponseData;
import com.ocxworker.service.LogPersistService;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/log"})
public class LogController {
    @Resource
    private LogPersistService logPersistService;

    @PostMapping({"/search"})
    public ResponseData<?> search(@RequestBody Map<String, String> params) {
        String keyword = params.get("keyword");
        List<String> all = this.logPersistService.readAllLines();
        if (keyword != null && !keyword.isBlank()) {
            String lowerKey = keyword.toLowerCase();
            List<String> matched = all.stream().filter(line -> line.toLowerCase().contains(lowerKey)).toList();
            return ResponseData.ok(matched);
        } else {
            return ResponseData.ok(all);
        }
    }
}
