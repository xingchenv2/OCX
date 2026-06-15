package com.ocxworker.controller;

import com.ocxworker.model.vo.ResponseData;
import com.ocxworker.service.TrafficService;
import jakarta.annotation.Resource;
import java.util.Map;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/oci/traffic"})
public class TrafficController {
    @Resource
    private TrafficService trafficService;

    @PostMapping({"/data"})
    public ResponseData<?> getData(@RequestBody Map<String, Object> params) {
        Object minutesRaw = params == null ? null : params.get("minutes");
        int minutes = 60;
        if (minutesRaw instanceof Number n) {
            minutes = n.intValue();
        } else if (minutesRaw != null) {
            try {
                minutes = Integer.parseInt(String.valueOf(minutesRaw));
            } catch (NumberFormatException var6) {
            }
        }

        String reg = null;
        if (params != null && params.get("region") != null) {
            reg = String.valueOf(params.get("region")).trim();
            if (reg.isEmpty()) {
                reg = null;
            }
        }

        return ResponseData.ok(
            this.trafficService
                .getTrafficData(params == null ? null : (String)params.get("id"), params == null ? null : (String)params.get("instanceId"), minutes, reg)
        );
    }
}
