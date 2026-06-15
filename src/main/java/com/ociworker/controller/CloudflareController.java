/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.ociworker.controller.CloudflareController
 *  com.ociworker.exception.OciException
 *  com.ociworker.model.entity.CfCfg
 *  com.ociworker.model.vo.ResponseData
 *  com.ociworker.service.CloudflareService
 *  com.ociworker.service.VerifyCodeService
 *  jakarta.annotation.Resource
 *  org.springframework.web.bind.annotation.GetMapping
 *  org.springframework.web.bind.annotation.PostMapping
 *  org.springframework.web.bind.annotation.RequestBody
 *  org.springframework.web.bind.annotation.RequestMapping
 *  org.springframework.web.bind.annotation.RestController
 */
package com.ociworker.controller;

import com.ociworker.exception.OciException;
import com.ociworker.model.entity.CfCfg;
import com.ociworker.model.vo.ResponseData;
import com.ociworker.service.CloudflareService;
import com.ociworker.service.VerifyCodeService;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 * Exception performing whole class analysis ignored.
 */
@RestController
@RequestMapping(value={"/api/cf"})
public class CloudflareController {
    @Resource
    private CloudflareService cloudflareService;
    @Resource
    private VerifyCodeService verifyCodeService;

    @GetMapping(value={"/account/config"})
    public ResponseData<?> getAccountConfig() {
        return ResponseData.ok((Object)this.cloudflareService.getAccountConfigForDisplay());
    }

    @PostMapping(value={"/account/config"})
    public ResponseData<?> saveAccountConfig(@RequestBody Map<String, String> params) {
        this.cloudflareService.saveAccountConfig(params.get("accountId"), params.get("apiToken"));
        return ResponseData.ok();
    }

    @PostMapping(value={"/account/test"})
    public ResponseData<?> testAccountConfig(@RequestBody Map<String, String> params) {
        String msg = this.cloudflareService.testAccountConfig(params.get("accountId"), params.get("apiToken"));
        return ResponseData.ok((Object)msg);
    }

    @PostMapping(value={"/zones/list"})
    public ResponseData<?> listZones(@RequestBody Map<String, Object> params) {
        return ResponseData.ok((Object)this.cloudflareService.listZones(CloudflareController.parseInteger((Object)params.get("page"), (Integer)1).intValue(), CloudflareController.parseInteger((Object)params.get("perPage"), (Integer)50).intValue()));
    }

    @PostMapping(value={"/zones/listPage"})
    public ResponseData<?> listZonesPage(@RequestBody Map<String, Object> params) {
        return ResponseData.ok((Object)this.cloudflareService.listZonesPage(CloudflareController.parseInteger((Object)params.get("page"), (Integer)1).intValue(), CloudflareController.parseInteger((Object)params.get("perPage"), (Integer)50).intValue()));
    }

    @PostMapping(value={"/zones/detail"})
    public ResponseData<?> getZoneDetail(@RequestBody Map<String, String> params) {
        return ResponseData.ok((Object)this.cloudflareService.getZoneDetail(params.get("zoneId")));
    }

    @PostMapping(value={"/zones/create"})
    public ResponseData<?> createZone(@RequestBody Map<String, String> params) {
        return ResponseData.ok((Object)this.cloudflareService.createZone(params.get("name")));
    }

    @PostMapping(value={"/zones/delete"})
    public ResponseData<?> deleteZone(@RequestBody Map<String, String> params) {
        this.verifyCodeService.verifyCode("cfZoneDelete", params.get("verifyCode"));
        this.cloudflareService.deleteZone(params.get("zoneId"));
        return ResponseData.ok();
    }

    @PostMapping(value={"/zones/paused"})
    public ResponseData<?> setZonePaused(@RequestBody Map<String, Object> params) {
        this.verifyCodeService.verifyCode("cfZonePause", CloudflareController.parseString((Object)params.get("verifyCode")));
        return ResponseData.ok((Object)this.cloudflareService.setZonePaused(CloudflareController.parseString((Object)params.get("zoneId")), CloudflareController.parseBoolean((Object)params.get("paused"), (boolean)false)));
    }

    @PostMapping(value={"/tunnel/list"})
    public ResponseData<?> listTunnels() {
        return ResponseData.ok((Object)this.cloudflareService.listTunnels());
    }

    @PostMapping(value={"/tunnel/create"})
    public ResponseData<?> createTunnel(@RequestBody Map<String, String> params) {
        return ResponseData.ok((Object)this.cloudflareService.createTunnel(params.get("name")));
    }

    @PostMapping(value={"/tunnel/delete"})
    public ResponseData<?> deleteTunnel(@RequestBody Map<String, String> params) {
        this.verifyCodeService.verifyCode("cfTunnelDelete", params.get("verifyCode"));
        this.cloudflareService.deleteTunnel(params.get("tunnelId"));
        return ResponseData.ok();
    }

    @PostMapping(value={"/tunnel/token"})
    public ResponseData<?> getTunnelToken(@RequestBody Map<String, String> params) {
        return ResponseData.ok((Object)this.cloudflareService.getTunnelRunToken(params.get("tunnelId")));
    }

    @PostMapping(value={"/tunnel/connections"})
    public ResponseData<?> listTunnelConnections(@RequestBody Map<String, String> params) {
        return ResponseData.ok((Object)this.cloudflareService.listTunnelConnections(params.get("tunnelId")));
    }

    @PostMapping(value={"/tunnel/routes/list"})
    public ResponseData<?> listTunnelRoutes(@RequestBody Map<String, String> params) {
        return ResponseData.ok((Object)this.cloudflareService.listTunnelRoutes(params.get("tunnelId")));
    }

    @PostMapping(value={"/tunnel/routes/create"})
    public ResponseData<?> createTunnelRoute(@RequestBody Map<String, String> params) {
        return ResponseData.ok((Object)this.cloudflareService.addTunnelRoute(params.get("tunnelId"), params.get("zoneId"), params.get("subdomain"), params.get("service")));
    }

    @PostMapping(value={"/tunnel/routes/delete"})
    public ResponseData<?> deleteTunnelRoute(@RequestBody Map<String, String> params) {
        this.cloudflareService.deleteTunnelRoute(params.get("tunnelId"), params.get("hostname"));
        return ResponseData.ok();
    }

    @PostMapping(value={"/access-rules/list"})
    public ResponseData<?> listIpAccessRules() {
        return ResponseData.ok((Object)this.cloudflareService.listIpAccessRules());
    }

    @PostMapping(value={"/access-rules/create"})
    public ResponseData<?> createIpAccessRule(@RequestBody Map<String, String> params) {
        return ResponseData.ok((Object)this.cloudflareService.createIpAccessRule(params.get("target"), params.get("value"), params.get("mode"), params.get("notes")));
    }

    @PostMapping(value={"/access-rules/delete"})
    public ResponseData<?> deleteIpAccessRule(@RequestBody Map<String, String> params) {
        this.cloudflareService.deleteIpAccessRule(params.get("ruleId"));
        return ResponseData.ok();
    }

    @Deprecated
    @PostMapping(value={"/cfg/list"})
    public ResponseData<?> listCfg(@RequestBody Map<String, Integer> params) {
        return ResponseData.ok((Object)this.cloudflareService.listCfgPage(params.getOrDefault("current", 1).intValue(), params.getOrDefault("size", 10).intValue()));
    }

    @PostMapping(value={"/cfg/add"})
    public ResponseData<?> addCfg(@RequestBody CfCfg cfg) {
        this.cloudflareService.addCfg(cfg);
        return ResponseData.ok();
    }

    @PostMapping(value={"/cfg/remove"})
    public ResponseData<?> removeCfg(@RequestBody Map<String, String> params) {
        this.cloudflareService.removeCfg(params.get("id"));
        return ResponseData.ok();
    }

    @PostMapping(value={"/dns/list"})
    public ResponseData<?> listDns(@RequestBody Map<String, Object> params) {
        String zoneId = (String)params.get("zoneId");
        if (zoneId != null && !zoneId.isBlank()) {
            return ResponseData.ok((Object)this.cloudflareService.listDnsRecords(zoneId, CloudflareController.parseInteger((Object)params.get("page"), (Integer)1).intValue(), CloudflareController.parseInteger((Object)params.get("perPage"), (Integer)50).intValue()));
        }
        return ResponseData.ok((Object)this.cloudflareService.listDnsRecordsByCfgId((String)params.get("cfgId"), CloudflareController.parseInteger((Object)params.get("page"), (Integer)1).intValue(), CloudflareController.parseInteger((Object)params.get("perPage"), (Integer)50).intValue()));
    }

    @PostMapping(value={"/dns/listPage"})
    public ResponseData<?> listDnsPage(@RequestBody Map<String, Object> params) {
        String zoneId = CloudflareController.parseString((Object)params.get("zoneId"));
        if (zoneId == null || zoneId.isBlank()) {
            throw new OciException("\u8bf7\u9009\u62e9 Zone");
        }
        return ResponseData.ok((Object)this.cloudflareService.listDnsRecordsPage(zoneId, CloudflareController.parseInteger((Object)params.get("page"), (Integer)1).intValue(), CloudflareController.parseInteger((Object)params.get("perPage"), (Integer)50).intValue(), CloudflareController.parseString((Object)params.get("search")), CloudflareController.parseString((Object)params.get("type"))));
    }

    @PostMapping(value={"/dns/add"})
    public ResponseData<?> addDns(@RequestBody Map<String, Object> params) {
        String zoneId = (String)params.get("zoneId");
        if (zoneId == null || zoneId.isBlank()) {
            throw new OciException("\u8bf7\u63d0\u4f9b zoneId");
        }
        this.cloudflareService.addDnsRecord(zoneId, (String)params.get("type"), (String)params.get("name"), (String)params.get("content"), params.containsKey("proxied") ? Boolean.valueOf(CloudflareController.parseBoolean((Object)params.get("proxied"), (boolean)false)) : null, CloudflareController.parseInteger((Object)params.get("ttl"), (Integer)1), CloudflareController.parseInteger((Object)params.get("priority"), null), (String)params.get("comment"));
        return ResponseData.ok();
    }

    @PostMapping(value={"/dns/update"})
    public ResponseData<?> updateDns(@RequestBody Map<String, Object> params) {
        this.cloudflareService.updateDnsRecord((String)params.get("zoneId"), (String)params.get("recordId"), (String)params.get("type"), (String)params.get("name"), (String)params.get("content"), params.containsKey("proxied") ? Boolean.valueOf(CloudflareController.parseBoolean((Object)params.get("proxied"), (boolean)false)) : null, CloudflareController.parseInteger((Object)params.get("ttl"), (Integer)1), CloudflareController.parseInteger((Object)params.get("priority"), null), (String)params.get("comment"));
        return ResponseData.ok();
    }

    @PostMapping(value={"/dns/delete"})
    public ResponseData<?> deleteDns(@RequestBody Map<String, String> params) {
        this.cloudflareService.deleteDnsRecord(params.get("zoneId"), params.get("recordId"));
        return ResponseData.ok();
    }

    @PostMapping(value={"/workers/domain/delete"})
    public ResponseData<?> deleteWorkerDomain(@RequestBody Map<String, String> params) {
        this.cloudflareService.deleteWorkerDomain(params.get("workerDomainId"));
        return ResponseData.ok();
    }

    @PostMapping(value={"/dns/export"})
    public ResponseData<?> exportDns(@RequestBody Map<String, String> params) {
        return ResponseData.ok((Object)this.cloudflareService.exportDnsRecords(params.get("zoneId")));
    }

    @PostMapping(value={"/dns/import"})
    public ResponseData<?> importDns(@RequestBody Map<String, Object> params) {
        this.cloudflareService.importDnsRecords((String)params.get("zoneId"), (String)params.get("bindContent"), params.containsKey("proxied") ? Boolean.valueOf(CloudflareController.parseBoolean((Object)params.get("proxied"), (boolean)false)) : null);
        return ResponseData.ok();
    }

    @PostMapping(value={"/dns/dnssec/get"})
    public ResponseData<?> getDnssec(@RequestBody Map<String, String> params) {
        return ResponseData.ok((Object)this.cloudflareService.getDnssec(params.get("zoneId")));
    }

    @PostMapping(value={"/dns/dnssec/set"})
    public ResponseData<?> setDnssec(@RequestBody Map<String, String> params) {
        return ResponseData.ok((Object)this.cloudflareService.setDnssec(params.get("zoneId"), params.get("status")));
    }

    @PostMapping(value={"/email/settings"})
    public ResponseData<?> emailSettings(@RequestBody Map<String, String> params) {
        return ResponseData.ok((Object)this.cloudflareService.getEmailRoutingSettings(params.get("zoneId")));
    }

    @PostMapping(value={"/email/enable"})
    public ResponseData<?> emailEnable(@RequestBody Map<String, String> params) {
        return ResponseData.ok((Object)this.cloudflareService.enableEmailRouting(params.get("zoneId")));
    }

    @PostMapping(value={"/email/disable"})
    public ResponseData<?> emailDisable(@RequestBody Map<String, String> params) {
        this.verifyCodeService.verifyCode("cfEmailRoutingDisable", params.get("verifyCode"));
        this.cloudflareService.disableEmailRouting(params.get("zoneId"));
        return ResponseData.ok();
    }

    @PostMapping(value={"/email/dns/get"})
    public ResponseData<?> emailDnsGet(@RequestBody Map<String, String> params) {
        return ResponseData.ok((Object)this.cloudflareService.getEmailRoutingDns(params.get("zoneId")));
    }

    @PostMapping(value={"/email/dns/lock"})
    public ResponseData<?> emailDnsLock(@RequestBody Map<String, String> params) {
        this.verifyCodeService.verifyCode("cfEmailDnsLock", params.get("verifyCode"));
        this.cloudflareService.lockEmailDns(params.get("zoneId"));
        return ResponseData.ok();
    }

    @PostMapping(value={"/email/dns/unlock"})
    public ResponseData<?> emailDnsUnlock(@RequestBody Map<String, String> params) {
        this.verifyCodeService.verifyCode("cfEmailDnsUnlock", params.get("verifyCode"));
        this.cloudflareService.unlockEmailDns(params.get("zoneId"));
        return ResponseData.ok();
    }

    @PostMapping(value={"/email/rules/list"})
    public ResponseData<?> emailRulesList(@RequestBody Map<String, String> params) {
        return ResponseData.ok((Object)this.cloudflareService.listEmailRoutingRules(params.get("zoneId")));
    }

    @PostMapping(value={"/email/rules/create"})
    public ResponseData<?> emailRulesCreate(@RequestBody Map<String, Object> params) {
        Boolean enabled;
        String zoneId = (String)params.get("zoneId");
        String customAddress = (String)params.get("customAddress");
        String name = (String)params.get("name");
        Boolean bl = enabled = params.get("enabled") == null ? null : Boolean.valueOf(CloudflareController.parseBoolean((Object)params.get("enabled"), (boolean)true));
        if (params.containsKey("actionType") || params.containsKey("destinations") || params.containsKey("workerName") || params.containsKey("priority")) {
            return ResponseData.ok((Object)this.cloudflareService.createEmailRoutingRule(zoneId, name, customAddress, (String)params.get("actionType"), CloudflareController.parseStringList((Object)params.get("destinations")), (String)params.get("workerName"), CloudflareController.parseInteger((Object)params.get("priority"), null), enabled));
        }
        return ResponseData.ok((Object)this.cloudflareService.createEmailRoutingRule(zoneId, name, customAddress, (String)params.get("destination"), enabled == null || enabled != false));
    }

    @PostMapping(value={"/email/rules/delete"})
    public ResponseData<?> emailRulesDelete(@RequestBody Map<String, String> params) {
        this.cloudflareService.deleteEmailRoutingRule(params.get("zoneId"), params.get("ruleId"));
        return ResponseData.ok();
    }

    @PostMapping(value={"/email/rules/update"})
    public ResponseData<?> emailRulesUpdate(@RequestBody Map<String, Object> params) {
        Boolean enabled;
        Boolean bl = enabled = params.get("enabled") == null ? null : Boolean.valueOf(CloudflareController.parseBoolean((Object)params.get("enabled"), (boolean)true));
        if (params.containsKey("actionType") || params.containsKey("destinations") || params.containsKey("workerName") || params.containsKey("customAddress") || params.containsKey("name") || params.containsKey("priority")) {
            return ResponseData.ok((Object)this.cloudflareService.updateEmailRoutingRule((String)params.get("zoneId"), (String)params.get("ruleId"), (String)params.get("name"), (String)params.get("customAddress"), (String)params.get("actionType"), CloudflareController.parseStringList((Object)params.get("destinations")), (String)params.get("workerName"), enabled, CloudflareController.parseInteger((Object)params.get("priority"), null)));
        }
        return ResponseData.ok((Object)this.cloudflareService.updateEmailRoutingRule((String)params.get("zoneId"), (String)params.get("ruleId"), null, null, null, null, null, enabled, null));
    }

    @PostMapping(value={"/email/rules/catch-all/get"})
    public ResponseData<?> emailCatchAllGet(@RequestBody Map<String, String> params) {
        return ResponseData.ok((Object)this.cloudflareService.getCatchAllRule(params.get("zoneId")));
    }

    @PostMapping(value={"/email/rules/catch-all/update"})
    public ResponseData<?> emailCatchAllUpdate(@RequestBody Map<String, Object> params) {
        Boolean enabled = params.get("enabled") == null ? null : Boolean.valueOf(CloudflareController.parseBoolean((Object)params.get("enabled"), (boolean)true));
        return ResponseData.ok((Object)this.cloudflareService.updateCatchAllRule((String)params.get("zoneId"), (String)params.get("actionType"), CloudflareController.parseStringList((Object)params.get("destinations")), (String)params.get("workerName"), enabled));
    }

    @PostMapping(value={"/email/destinations/list"})
    public ResponseData<?> emailDestinationsList() {
        return ResponseData.ok((Object)this.cloudflareService.listEmailDestinations());
    }

    @PostMapping(value={"/email/destinations/create"})
    public ResponseData<?> emailDestinationsCreate(@RequestBody Map<String, String> params) {
        return ResponseData.ok((Object)this.cloudflareService.createEmailDestination(params.get("email")));
    }

    @PostMapping(value={"/email/destinations/resend"})
    public ResponseData<?> emailDestinationsResend(@RequestBody Map<String, String> params) {
        return ResponseData.ok((Object)this.cloudflareService.resendEmailDestination(params.get("email")));
    }

    @PostMapping(value={"/email/destinations/delete"})
    public ResponseData<?> emailDestinationsDelete(@RequestBody Map<String, String> params) {
        this.verifyCodeService.verifyCode("cfEmailDestinationDelete", params.get("verifyCode"));
        this.cloudflareService.deleteEmailDestination(params.get("destinationId"));
        return ResponseData.ok();
    }

    @PostMapping(value={"/email/workers/list"})
    public ResponseData<?> emailWorkersList() {
        return ResponseData.ok((Object)this.cloudflareService.listWorkers());
    }

    @PostMapping(value={"/workers/scripts/list"})
    public ResponseData<?> workerScriptsList() {
        return ResponseData.ok((Object)this.cloudflareService.listWorkerScripts());
    }

    @PostMapping(value={"/workers/pages/usage"})
    public ResponseData<?> workersPagesUsage() {
        return ResponseData.ok((Object)this.cloudflareService.getWorkersUsageSummary());
    }

    @PostMapping(value={"/workers/pages/applications/list"})
    public ResponseData<?> workersPagesApplicationsList() {
        return ResponseData.ok((Object)this.cloudflareService.listWorkersAndPagesApplications());
    }

    @PostMapping(value={"/workers/pages/templates/list"})
    public ResponseData<?> workersPagesTemplatesList() {
        return ResponseData.ok((Object)this.cloudflareService.listWorkerTemplates());
    }

    @PostMapping(value={"/workers/subdomain/info"})
    public ResponseData<?> workersSubdomainInfo() {
        return ResponseData.ok((Object)this.cloudflareService.getWorkersSubdomainInfo());
    }

    @PostMapping(value={"/workers/pages/templates/preview"})
    public ResponseData<?> workersPagesTemplatePreview(@RequestBody Map<String, String> params) {
        return ResponseData.ok((Object)this.cloudflareService.getWorkersPagesTemplatePreview(params.get("templateId")));
    }

    @PostMapping(value={"/workers/deploy"})
    public ResponseData<?> workerDeploy(@RequestBody Map<String, String> params) {
        return ResponseData.ok((Object)this.cloudflareService.deployWorker(params.get("name"), params.get("script")));
    }

    @PostMapping(value={"/workers/script/get"})
    public ResponseData<?> workerScriptGet(@RequestBody Map<String, String> params) {
        return ResponseData.ok((Object)this.cloudflareService.getWorkerScriptContent(params.get("name")));
    }

    @PostMapping(value={"/workers/script/update"})
    public ResponseData<?> workerScriptUpdate(@RequestBody Map<String, String> params) {
        return ResponseData.ok((Object)this.cloudflareService.updateWorkerScript(params.get("name"), params.get("script")));
    }

    @PostMapping(value={"/workers/rename"})
    public ResponseData<?> workerRename(@RequestBody Map<String, String> params) {
        return ResponseData.ok((Object)this.cloudflareService.renameWorkerScript(params.get("name"), params.get("newName")));
    }

    @PostMapping(value={"/workers/delete"})
    public ResponseData<?> workerDelete(@RequestBody Map<String, String> params) {
        this.verifyCodeService.verifyCode("cfWorkerDelete", params.get("verifyCode"));
        this.cloudflareService.deleteWorkerScript(params.get("name"));
        return ResponseData.ok();
    }

    @PostMapping(value={"/workers/create/hello-world"})
    public ResponseData<?> workerCreateHelloWorld(@RequestBody Map<String, String> params) {
        return ResponseData.ok((Object)this.cloudflareService.createWorkerHelloWorld(params.get("name"), params.get("script")));
    }

    @PostMapping(value={"/workers/create/template"})
    public ResponseData<?> workerCreateTemplate(@RequestBody Map<String, String> params) {
        return ResponseData.ok((Object)this.cloudflareService.createWorkerFromTemplate(params.get("name"), params.get("templateId"), params.get("script")));
    }

    @PostMapping(value={"/pages/create/template"})
    public ResponseData<?> pagesCreateTemplate(@RequestBody Map<String, String> params) {
        return ResponseData.ok((Object)this.cloudflareService.createPagesFromTemplate(params.get("name"), params.get("templateId")));
    }

    @PostMapping(value={"/pages/deploy/static"})
    public ResponseData<?> pagesDeployStatic(@RequestBody Map<String, Object> params) {
        List encoded = (List)params.get("files");
        return ResponseData.ok((Object)this.cloudflareService.deployPagesStaticFromUpload((String)params.get("name"), encoded));
    }

    @PostMapping(value={"/ssl/get"})
    public ResponseData<?> sslGet(@RequestBody Map<String, String> params) {
        return ResponseData.ok((Object)this.cloudflareService.getSslSettings(params.get("zoneId")));
    }

    @PostMapping(value={"/ssl/set"})
    public ResponseData<?> sslSet(@RequestBody Map<String, Object> params) {
        return ResponseData.ok((Object)this.cloudflareService.updateSslSetting((String)params.get("zoneId"), (String)params.get("settingId"), params.get("value")));
    }

    @PostMapping(value={"/cache/get"})
    public ResponseData<?> cacheGet(@RequestBody Map<String, String> params) {
        return ResponseData.ok((Object)this.cloudflareService.getCacheSettings(params.get("zoneId")));
    }

    @PostMapping(value={"/cache/set"})
    public ResponseData<?> cacheSet(@RequestBody Map<String, Object> params) {
        return ResponseData.ok((Object)this.cloudflareService.updateCacheSetting((String)params.get("zoneId"), (String)params.get("settingId"), params.get("value")));
    }

    @PostMapping(value={"/cache/purge"})
    public ResponseData<?> cachePurge(@RequestBody Map<String, Object> params) {
        this.cloudflareService.purgeZoneCache((String)params.get("zoneId"), CloudflareController.parseBoolean((Object)params.get("purgeEverything"), (boolean)false), CloudflareController.parseStringList((Object)params.get("files")));
        return ResponseData.ok();
    }

    @PostMapping(value={"/security/firewall/list"})
    public ResponseData<?> firewallList(@RequestBody Map<String, String> params) {
        return ResponseData.ok((Object)this.cloudflareService.listFirewallRules(params.get("zoneId")));
    }

    @PostMapping(value={"/security/firewall/create"})
    public ResponseData<?> firewallCreate(@RequestBody Map<String, Object> params) {
        return ResponseData.ok((Object)this.cloudflareService.createFirewallRule(CloudflareController.parseString((Object)params.get("zoneId")), CloudflareController.parseString((Object)params.get("action")), CloudflareController.parseString((Object)params.get("expression")), CloudflareController.parseString((Object)params.get("description")), CloudflareController.parseBoolean((Object)params.get("paused"), (boolean)false)));
    }

    @PostMapping(value={"/security/firewall/paused"})
    public ResponseData<?> firewallPaused(@RequestBody Map<String, Object> params) {
        return ResponseData.ok((Object)this.cloudflareService.setFirewallRulePaused(CloudflareController.parseString((Object)params.get("zoneId")), CloudflareController.parseString((Object)params.get("rulesetId")), CloudflareController.parseString((Object)params.get("ruleId")), CloudflareController.parseBoolean((Object)params.get("paused"), (boolean)false)));
    }

    @PostMapping(value={"/security/firewall/update"})
    public ResponseData<?> firewallUpdate(@RequestBody Map<String, Object> params) {
        Boolean paused = params.containsKey("paused") ? Boolean.valueOf(CloudflareController.parseBoolean((Object)params.get("paused"), (boolean)false)) : null;
        return ResponseData.ok((Object)this.cloudflareService.updateFirewallRule(CloudflareController.parseString((Object)params.get("zoneId")), CloudflareController.parseString((Object)params.get("rulesetId")), CloudflareController.parseString((Object)params.get("ruleId")), CloudflareController.parseString((Object)params.get("action")), params.containsKey("description") ? CloudflareController.parseString((Object)params.get("description")) : null, CloudflareController.parseString((Object)params.get("expression")), paused));
    }

    @PostMapping(value={"/security/firewall/delete"})
    public ResponseData<?> firewallDelete(@RequestBody Map<String, String> params) {
        this.cloudflareService.deleteFirewallRule(params.get("zoneId"), params.get("rulesetId"), params.get("ruleId"));
        return ResponseData.ok();
    }

    @PostMapping(value={"/security/firewall/reorder"})
    public ResponseData<?> firewallReorder(@RequestBody Map<String, String> params) {
        String before = params.containsKey("beforeRuleId") ? params.get("beforeRuleId") : null;
        String after = params.get("afterRuleId");
        return ResponseData.ok((Object)this.cloudflareService.reorderFirewallRule(params.get("zoneId"), params.get("rulesetId"), params.get("ruleId"), before, after));
    }

    @PostMapping(value={"/security/protection/get"})
    public ResponseData<?> securityProtectionGet(@RequestBody Map<String, String> params) {
        return ResponseData.ok((Object)this.cloudflareService.getSecuritySettings(params.get("zoneId")));
    }

    @PostMapping(value={"/security/protection/set"})
    public ResponseData<?> securityProtectionSet(@RequestBody Map<String, Object> params) {
        return ResponseData.ok((Object)this.cloudflareService.updateSecuritySetting(CloudflareController.parseString((Object)params.get("zoneId")), CloudflareController.parseString((Object)params.get("settingId")), params.get("value")));
    }

    @PostMapping(value={"/workers/routes/list"})
    public ResponseData<?> workersRoutesList(@RequestBody Map<String, String> params) {
        return ResponseData.ok((Object)this.cloudflareService.listWorkersRoutes(params.get("zoneId")));
    }

    @PostMapping(value={"/workers/routes/create"})
    public ResponseData<?> workersRoutesCreate(@RequestBody Map<String, String> params) {
        return ResponseData.ok((Object)this.cloudflareService.createWorkersRoute(params.get("zoneId"), params.get("pattern"), params.get("script")));
    }

    @PostMapping(value={"/workers/routes/delete"})
    public ResponseData<?> workersRoutesDelete(@RequestBody Map<String, String> params) {
        this.cloudflareService.deleteWorkersRoute(params.get("zoneId"), params.get("routeId"));
        return ResponseData.ok();
    }

    @PostMapping(value={"/rules/list"})
    public ResponseData<?> rulesList(@RequestBody Map<String, String> params) {
        return ResponseData.ok((Object)this.cloudflareService.listZoneRules(params.get("zoneId")));
    }

    @Deprecated
    @PostMapping(value={"/rules/pagerules/list"})
    public ResponseData<?> pageRulesList(@RequestBody Map<String, String> params) {
        return ResponseData.ok((Object)this.cloudflareService.listPageRules(params.get("zoneId")));
    }

    @PostMapping(value={"/rules/pagerules/delete"})
    public ResponseData<?> pageRulesDelete(@RequestBody Map<String, String> params) {
        this.cloudflareService.deletePageRule(params.get("zoneId"), params.get("ruleId"));
        return ResponseData.ok();
    }

    private static boolean parseBoolean(Object value, boolean defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof Boolean) {
            Boolean b = (Boolean)value;
            return b;
        }
        return Boolean.parseBoolean(String.valueOf(value));
    }

    private static Integer parseInteger(Object value, Integer defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof Number) {
            Number n = (Number)value;
            return n.intValue();
        }
        try {
            return Integer.parseInt(String.valueOf(value));
        }
        catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private static String parseString(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof String) {
            String s = (String)value;
            return s;
        }
        return null;
    }

    private static List<String> parseStringList(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof List) {
            List list = (List)value;
            ArrayList<String> out = new ArrayList<String>();
            for (Object item : list) {
                if (item == null) continue;
                out.add(String.valueOf(item));
            }
            return out;
        }
        return List.of(String.valueOf(value));
    }
}

