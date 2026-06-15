/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.ocxworker.controller.AliDNSController
 *  com.ocxworker.model.vo.ResponseData
 *  com.ocxworker.service.AliDNSService
 *  jakarta.annotation.Resource
 *  org.springframework.web.bind.annotation.GetMapping
 *  org.springframework.web.bind.annotation.PostMapping
 *  org.springframework.web.bind.annotation.RequestBody
 *  org.springframework.web.bind.annotation.RequestMapping
 *  org.springframework.web.bind.annotation.RestController
 */
package com.ocxworker.controller;

import com.ocxworker.model.vo.ResponseData;
import com.ocxworker.service.AliDNSService;
import jakarta.annotation.Resource;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value={"/api/alidns"})
public class AliDNSController {
    @Resource
    private AliDNSService aliDNSService;

    @GetMapping(value={"/account/config"})
    public ResponseData<?> getAccountConfig() {
        return ResponseData.ok((Object)this.aliDNSService.getAccountConfigForDisplay());
    }

    @PostMapping(value={"/account/config"})
    public ResponseData<?> saveAccountConfig(@RequestBody Map<String, String> params) {
        this.aliDNSService.saveAccountConfig(params.get("accessKeyId"), params.get("accessKeySecret"));
        return ResponseData.ok();
    }

    @PostMapping(value={"/account/test"})
    public ResponseData<?> testAccountConfig(@RequestBody Map<String, String> params) {
        String msg = this.aliDNSService.testAccountConfig(params.get("accessKeyId"), params.get("accessKeySecret"));
        return ResponseData.ok((Object)msg);
    }

    @PostMapping(value={"/domains/list"})
    public ResponseData<?> listDomains(@RequestBody Map<String, Object> params) {
        int page = params.get("page") != null ? ((Number)params.get("page")).intValue() : 1;
        int perPage = params.get("perPage") != null ? ((Number)params.get("perPage")).intValue() : 20;
        return ResponseData.ok((Object)this.aliDNSService.listDomains(page, perPage));
    }

    @PostMapping(value={"/domains/dns-servers"})
    public ResponseData<?> listDomainDnsServers(@RequestBody Map<String, Object> params) {
        return ResponseData.ok((Object)this.aliDNSService.listDomainDnsServers(this.parseString(params.get("domainName"))));
    }

    @PostMapping(value={"/records/list"})
    public ResponseData<?> listRecords(@RequestBody Map<String, Object> params) {
        return ResponseData.ok((Object)this.aliDNSService.listRecords(this.parseString(params.get("domainName")), this.parseString(params.get("rrKeyWord")), this.parseString(params.get("typeKeyWord")), this.parseString(params.get("valueKeyWord")), this.parseString(params.get("line")), this.parseInteger(params.get("page"), 1), this.parseInteger(params.get("perPage"), 50)));
    }

    @PostMapping(value={"/records/add"})
    public ResponseData<?> addRecord(@RequestBody Map<String, Object> params) {
        return ResponseData.ok((Object)this.aliDNSService.addRecord(params));
    }

    @PostMapping(value={"/records/update"})
    public ResponseData<?> updateRecord(@RequestBody Map<String, Object> params) {
        return ResponseData.ok((Object)this.aliDNSService.updateRecord(params));
    }

    @PostMapping(value={"/records/delete"})
    public ResponseData<?> deleteRecord(@RequestBody Map<String, Object> params) {
        this.aliDNSService.deleteRecord(this.parseString(params.get("recordId")));
        return ResponseData.ok();
    }

    @PostMapping(value={"/records/status"})
    public ResponseData<?> setRecordStatus(@RequestBody Map<String, Object> params) {
        return ResponseData.ok((Object)this.aliDNSService.setRecordStatus(this.parseString(params.get("recordId")), this.parseString(params.get("status"))));
    }

    @PostMapping(value={"/lines/list"})
    public ResponseData<?> listSupportLines(@RequestBody Map<String, Object> params) {
        return ResponseData.ok((Object)this.aliDNSService.listSupportLines(this.parseString(params.get("domainName")), this.parseString(params.get("domainType"))));
    }

    private String parseString(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private int parseInteger(Object value, int def) {
        if (value == null) {
            return def;
        }
        if (value instanceof Number) {
            Number n = (Number)value;
            return n.intValue();
        }
        try {
            return Integer.parseInt(String.valueOf(value));
        }
        catch (Exception e) {
            return def;
        }
    }
}

