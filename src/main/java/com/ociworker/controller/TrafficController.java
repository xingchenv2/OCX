/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.ociworker.controller.TrafficController
 *  com.ociworker.model.vo.ResponseData
 *  com.ociworker.service.TrafficService
 *  jakarta.annotation.Resource
 *  org.springframework.web.bind.annotation.PostMapping
 *  org.springframework.web.bind.annotation.RequestBody
 *  org.springframework.web.bind.annotation.RequestMapping
 *  org.springframework.web.bind.annotation.RestController
 */
package com.ociworker.controller;

import com.ociworker.model.vo.ResponseData;
import com.ociworker.service.TrafficService;
import jakarta.annotation.Resource;
import java.util.Map;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value={"/api/oci/traffic"})
public class TrafficController {
    @Resource
    private TrafficService trafficService;

    @PostMapping(value={"/data"})
    public ResponseData<?> getData(@RequestBody Map<String, Object> params) {
        Object minutesRaw = params == null ? null : params.get("minutes");
        int minutes = 60;
        if (minutesRaw instanceof Number) {
            Number n = (Number)minutesRaw;
            minutes = n.intValue();
        } else if (minutesRaw != null) {
            try {
                minutes = Integer.parseInt(String.valueOf(minutesRaw));
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
        }
        String reg = null;
        if (params != null && params.get("region") != null && (reg = String.valueOf(params.get("region")).trim()).isEmpty()) {
            reg = null;
        }
        return ResponseData.ok((Object)this.trafficService.getTrafficData(params == null ? null : (String)params.get("id"), params == null ? null : (String)params.get("instanceId"), minutes, reg));
    }
}

