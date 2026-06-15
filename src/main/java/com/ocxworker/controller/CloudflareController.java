package com.ocxworker.controller;

import com.ocxworker.exception.OciException;
import com.ocxworker.model.entity.CfCfg;
import com.ocxworker.model.vo.ResponseData;
import com.ocxworker.service.CloudflareService;
import com.ocxworker.service.VerifyCodeService;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/cf"})
public class CloudflareController {
    @Resource
    private CloudflareService cloudflareService;
    @Resource
    private VerifyCodeService verifyCodeService;

    @GetMapping({"/account/config"})
    public ResponseData<?> getAccountConfig() {
        return ResponseData.ok(this.cloudflareService.getAccountConfigForDisplay());
    }

    @PostMapping({"/account/config"})
    public ResponseData<?> saveAccountConfig(@RequestBody Map<String, String> params) {
        this.cloudflareService.saveAccountConfig(params.get("accountId"), params.get("apiToken"));
        return ResponseData.ok();
    }

    @PostMapping({"/account/test"})
    public ResponseData<?> testAccountConfig(@RequestBody Map<String, String> params) {
        String msg = this.cloudflareService.testAccountConfig(params.get("accountId"), params.get("apiToken"));
        return ResponseData.ok(msg);
    }

    @PostMapping({"/zones/list"})
    public ResponseData<?> listZones(@RequestBody Map<String, Object> params) {
        return ResponseData.ok(this.cloudflareService.listZones(parseInteger(params.get("page"), 1), parseInteger(params.get("perPage"), 50)));
    }

    @PostMapping({"/zones/listPage"})
    public ResponseData<?> listZonesPage(@RequestBody Map<String, Object> params) {
        return ResponseData.ok(this.cloudflareService.listZonesPage(parseInteger(params.get("page"), 1), parseInteger(params.get("perPage"), 50)));
    }

    @PostMapping({"/zones/detail"})
    public ResponseData<?> getZoneDetail(@RequestBody Map<String, String> params) {
        return ResponseData.ok(this.cloudflareService.getZoneDetail(params.get("zoneId")));
    }

    @PostMapping({"/zones/create"})
    public ResponseData<?> createZone(@RequestBody Map<String, String> params) {
        return ResponseData.ok(this.cloudflareService.createZone(params.get("name")));
    }

    @PostMapping({"/zones/delete"})
    public ResponseData<?> deleteZone(@RequestBody Map<String, String> params) {
        this.verifyCodeService.verifyCode("cfZoneDelete", params.get("verifyCode"));
        this.cloudflareService.deleteZone(params.get("zoneId"));
        return ResponseData.ok();
    }

    @PostMapping({"/zones/paused"})
    public ResponseData<?> setZonePaused(@RequestBody Map<String, Object> params) {
        this.verifyCodeService.verifyCode("cfZonePause", parseString(params.get("verifyCode")));
        return ResponseData.ok(this.cloudflareService.setZonePaused(parseString(params.get("zoneId")), parseBoolean(params.get("paused"), false)));
    }

    @PostMapping({"/tunnel/list"})
    public ResponseData<?> listTunnels() {
        return ResponseData.ok(this.cloudflareService.listTunnels());
    }

    @PostMapping({"/tunnel/create"})
    public ResponseData<?> createTunnel(@RequestBody Map<String, String> params) {
        return ResponseData.ok(this.cloudflareService.createTunnel(params.get("name")));
    }

    @PostMapping({"/tunnel/delete"})
    public ResponseData<?> deleteTunnel(@RequestBody Map<String, String> params) {
        this.verifyCodeService.verifyCode("cfTunnelDelete", params.get("verifyCode"));
        this.cloudflareService.deleteTunnel(params.get("tunnelId"));
        return ResponseData.ok();
    }

    @PostMapping({"/tunnel/token"})
    public ResponseData<?> getTunnelToken(@RequestBody Map<String, String> params) {
        return ResponseData.ok(this.cloudflareService.getTunnelRunToken(params.get("tunnelId")));
    }

    @PostMapping({"/tunnel/connections"})
    public ResponseData<?> listTunnelConnections(@RequestBody Map<String, String> params) {
        return ResponseData.ok(this.cloudflareService.listTunnelConnections(params.get("tunnelId")));
    }

    @PostMapping({"/tunnel/routes/list"})
    public ResponseData<?> listTunnelRoutes(@RequestBody Map<String, String> params) {
        return ResponseData.ok(this.cloudflareService.listTunnelRoutes(params.get("tunnelId")));
    }

    @PostMapping({"/tunnel/routes/create"})
    public ResponseData<?> createTunnelRoute(@RequestBody Map<String, String> params) {
        return ResponseData.ok(
            this.cloudflareService.addTunnelRoute(params.get("tunnelId"), params.get("zoneId"), params.get("subdomain"), params.get("service"))
        );
    }

    @PostMapping({"/tunnel/routes/delete"})
    public ResponseData<?> deleteTunnelRoute(@RequestBody Map<String, String> params) {
        this.cloudflareService.deleteTunnelRoute(params.get("tunnelId"), params.get("hostname"));
        return ResponseData.ok();
    }

    @PostMapping({"/access-rules/list"})
    public ResponseData<?> listIpAccessRules() {
        return ResponseData.ok(this.cloudflareService.listIpAccessRules());
    }

    @PostMapping({"/access-rules/create"})
    public ResponseData<?> createIpAccessRule(@RequestBody Map<String, String> params) {
        return ResponseData.ok(this.cloudflareService.createIpAccessRule(params.get("target"), params.get("value"), params.get("mode"), params.get("notes")));
    }

    @PostMapping({"/access-rules/delete"})
    public ResponseData<?> deleteIpAccessRule(@RequestBody Map<String, String> params) {
        this.cloudflareService.deleteIpAccessRule(params.get("ruleId"));
        return ResponseData.ok();
    }

    @Deprecated
    @PostMapping({"/cfg/list"})
    public ResponseData<?> listCfg(@RequestBody Map<String, Integer> params) {
        return ResponseData.ok(this.cloudflareService.listCfgPage(params.getOrDefault("current", 1), params.getOrDefault("size", 10)));
    }

    @PostMapping({"/cfg/add"})
    public ResponseData<?> addCfg(@RequestBody CfCfg cfg) {
        this.cloudflareService.addCfg(cfg);
        return ResponseData.ok();
    }

    @PostMapping({"/cfg/remove"})
    public ResponseData<?> removeCfg(@RequestBody Map<String, String> params) {
        this.cloudflareService.removeCfg(params.get("id"));
        return ResponseData.ok();
    }

    @PostMapping({"/dns/list"})
    public ResponseData<?> listDns(@RequestBody Map<String, Object> params) {
        String zoneId = (String)params.get("zoneId");
        return zoneId != null && !zoneId.isBlank()
            ? ResponseData.ok(this.cloudflareService.listDnsRecords(zoneId, parseInteger(params.get("page"), 1), parseInteger(params.get("perPage"), 50)))
            : ResponseData.ok(
                this.cloudflareService
                    .listDnsRecordsByCfgId((String)params.get("cfgId"), parseInteger(params.get("page"), 1), parseInteger(params.get("perPage"), 50))
            );
    }

    @PostMapping({"/dns/listPage"})
    public ResponseData<?> listDnsPage(@RequestBody Map<String, Object> params) {
        String zoneId = parseString(params.get("zoneId"));
        if (zoneId != null && !zoneId.isBlank()) {
            return ResponseData.ok(
                this.cloudflareService
                    .listDnsRecordsPage(
                        zoneId,
                        parseInteger(params.get("page"), 1),
                        parseInteger(params.get("perPage"), 50),
                        parseString(params.get("search")),
                        parseString(params.get("type"))
                    )
            );
        } else {
            throw new OciException("请选择 Zone");
        }
    }

    @PostMapping({"/dns/add"})
    public ResponseData<?> addDns(@RequestBody Map<String, Object> params) {
        String zoneId = (String)params.get("zoneId");
        if (zoneId != null && !zoneId.isBlank()) {
            this.cloudflareService
                .addDnsRecord(
                    zoneId,
                    (String)params.get("type"),
                    (String)params.get("name"),
                    (String)params.get("content"),
                    params.containsKey("proxied") ? parseBoolean(params.get("proxied"), false) : null,
                    parseInteger(params.get("ttl"), 1),
                    parseInteger(params.get("priority"), null),
                    (String)params.get("comment")
                );
            return ResponseData.ok();
        } else {
            throw new OciException("请提供 zoneId");
        }
    }

    @PostMapping({"/dns/update"})
    public ResponseData<?> updateDns(@RequestBody Map<String, Object> params) {
        this.cloudflareService
            .updateDnsRecord(
                (String)params.get("zoneId"),
                (String)params.get("recordId"),
                (String)params.get("type"),
                (String)params.get("name"),
                (String)params.get("content"),
                params.containsKey("proxied") ? parseBoolean(params.get("proxied"), false) : null,
                parseInteger(params.get("ttl"), 1),
                parseInteger(params.get("priority"), null),
                (String)params.get("comment")
            );
        return ResponseData.ok();
    }

    @PostMapping({"/dns/delete"})
    public ResponseData<?> deleteDns(@RequestBody Map<String, String> params) {
        this.cloudflareService.deleteDnsRecord(params.get("zoneId"), params.get("recordId"));
        return ResponseData.ok();
    }

    @PostMapping({"/workers/domain/delete"})
    public ResponseData<?> deleteWorkerDomain(@RequestBody Map<String, String> params) {
        this.cloudflareService.deleteWorkerDomain(params.get("workerDomainId"));
        return ResponseData.ok();
    }

    @PostMapping({"/dns/export"})
    public ResponseData<?> exportDns(@RequestBody Map<String, String> params) {
        return ResponseData.ok(this.cloudflareService.exportDnsRecords(params.get("zoneId")));
    }

    @PostMapping({"/dns/import"})
    public ResponseData<?> importDns(@RequestBody Map<String, Object> params) {
        this.cloudflareService
            .importDnsRecords(
                (String)params.get("zoneId"),
                (String)params.get("bindContent"),
                params.containsKey("proxied") ? parseBoolean(params.get("proxied"), false) : null
            );
        return ResponseData.ok();
    }

    @PostMapping({"/dns/dnssec/get"})
    public ResponseData<?> getDnssec(@RequestBody Map<String, String> params) {
        return ResponseData.ok(this.cloudflareService.getDnssec(params.get("zoneId")));
    }

    @PostMapping({"/dns/dnssec/set"})
    public ResponseData<?> setDnssec(@RequestBody Map<String, String> params) {
        return ResponseData.ok(this.cloudflareService.setDnssec(params.get("zoneId"), params.get("status")));
    }

    @PostMapping({"/email/settings"})
    public ResponseData<?> emailSettings(@RequestBody Map<String, String> params) {
        return ResponseData.ok(this.cloudflareService.getEmailRoutingSettings(params.get("zoneId")));
    }

    @PostMapping({"/email/enable"})
    public ResponseData<?> emailEnable(@RequestBody Map<String, String> params) {
        return ResponseData.ok(this.cloudflareService.enableEmailRouting(params.get("zoneId")));
    }

    @PostMapping({"/email/disable"})
    public ResponseData<?> emailDisable(@RequestBody Map<String, String> params) {
        this.verifyCodeService.verifyCode("cfEmailRoutingDisable", params.get("verifyCode"));
        this.cloudflareService.disableEmailRouting(params.get("zoneId"));
        return ResponseData.ok();
    }

    @PostMapping({"/email/dns/get"})
    public ResponseData<?> emailDnsGet(@RequestBody Map<String, String> params) {
        return ResponseData.ok(this.cloudflareService.getEmailRoutingDns(params.get("zoneId")));
    }

    @PostMapping({"/email/dns/lock"})
    public ResponseData<?> emailDnsLock(@RequestBody Map<String, String> params) {
        this.verifyCodeService.verifyCode("cfEmailDnsLock", params.get("verifyCode"));
        this.cloudflareService.lockEmailDns(params.get("zoneId"));
        return ResponseData.ok();
    }

    @PostMapping({"/email/dns/unlock"})
    public ResponseData<?> emailDnsUnlock(@RequestBody Map<String, String> params) {
        this.verifyCodeService.verifyCode("cfEmailDnsUnlock", params.get("verifyCode"));
        this.cloudflareService.unlockEmailDns(params.get("zoneId"));
        return ResponseData.ok();
    }

    @PostMapping({"/email/rules/list"})
    public ResponseData<?> emailRulesList(@RequestBody Map<String, String> params) {
        return ResponseData.ok(this.cloudflareService.listEmailRoutingRules(params.get("zoneId")));
    }

    @PostMapping({"/email/rules/create"})
    public ResponseData<?> emailRulesCreate(@RequestBody Map<String, Object> params) {
        String zoneId = (String)params.get("zoneId");
        String customAddress = (String)params.get("customAddress");
        String name = (String)params.get("name");
        Boolean enabled = params.get("enabled") == null ? null : parseBoolean(params.get("enabled"), true);
        return !params.containsKey("actionType") && !params.containsKey("destinations") && !params.containsKey("workerName") && !params.containsKey("priority")
            ? ResponseData.ok(
                this.cloudflareService.createEmailRoutingRule(zoneId, name, customAddress, (String)params.get("destination"), enabled == null || enabled)
            )
            : ResponseData.ok(
                this.cloudflareService
                    .createEmailRoutingRule(
                        zoneId,
                        name,
                        customAddress,
                        (String)params.get("actionType"),
                        parseStringList(params.get("destinations")),
                        (String)params.get("workerName"),
                        parseInteger(params.get("priority"), null),
                        enabled
                    )
            );
    }

    @PostMapping({"/email/rules/delete"})
    public ResponseData<?> emailRulesDelete(@RequestBody Map<String, String> params) {
        this.cloudflareService.deleteEmailRoutingRule(params.get("zoneId"), params.get("ruleId"));
        return ResponseData.ok();
    }

    @PostMapping({"/email/rules/update"})
    public ResponseData<?> emailRulesUpdate(@RequestBody Map<String, Object> params) {
        Boolean enabled = params.get("enabled") == null ? null : parseBoolean(params.get("enabled"), true);
        return !params.containsKey("actionType")
                && !params.containsKey("destinations")
                && !params.containsKey("workerName")
                && !params.containsKey("customAddress")
                && !params.containsKey("name")
                && !params.containsKey("priority")
            ? ResponseData.ok(
                this.cloudflareService
                    .updateEmailRoutingRule((String)params.get("zoneId"), (String)params.get("ruleId"), null, null, null, null, null, enabled, null)
            )
            : ResponseData.ok(
                this.cloudflareService
                    .updateEmailRoutingRule(
                        (String)params.get("zoneId"),
                        (String)params.get("ruleId"),
                        (String)params.get("name"),
                        (String)params.get("customAddress"),
                        (String)params.get("actionType"),
                        parseStringList(params.get("destinations")),
                        (String)params.get("workerName"),
                        enabled,
                        parseInteger(params.get("priority"), null)
                    )
            );
    }

    @PostMapping({"/email/rules/catch-all/get"})
    public ResponseData<?> emailCatchAllGet(@RequestBody Map<String, String> params) {
        return ResponseData.ok(this.cloudflareService.getCatchAllRule(params.get("zoneId")));
    }

    @PostMapping({"/email/rules/catch-all/update"})
    public ResponseData<?> emailCatchAllUpdate(@RequestBody Map<String, Object> params) {
        Boolean enabled = params.get("enabled") == null ? null : parseBoolean(params.get("enabled"), true);
        return ResponseData.ok(
            this.cloudflareService
                .updateCatchAllRule(
                    (String)params.get("zoneId"),
                    (String)params.get("actionType"),
                    parseStringList(params.get("destinations")),
                    (String)params.get("workerName"),
                    enabled
                )
        );
    }

    @PostMapping({"/email/destinations/list"})
    public ResponseData<?> emailDestinationsList() {
        return ResponseData.ok(this.cloudflareService.listEmailDestinations());
    }

    @PostMapping({"/email/destinations/create"})
    public ResponseData<?> emailDestinationsCreate(@RequestBody Map<String, String> params) {
        return ResponseData.ok(this.cloudflareService.createEmailDestination(params.get("email")));
    }

    @PostMapping({"/email/destinations/resend"})
    public ResponseData<?> emailDestinationsResend(@RequestBody Map<String, String> params) {
        return ResponseData.ok(this.cloudflareService.resendEmailDestination(params.get("email")));
    }

    @PostMapping({"/email/destinations/delete"})
    public ResponseData<?> emailDestinationsDelete(@RequestBody Map<String, String> params) {
        this.verifyCodeService.verifyCode("cfEmailDestinationDelete", params.get("verifyCode"));
        this.cloudflareService.deleteEmailDestination(params.get("destinationId"));
        return ResponseData.ok();
    }

    @PostMapping({"/email/workers/list"})
    public ResponseData<?> emailWorkersList() {
        return ResponseData.ok(this.cloudflareService.listWorkers());
    }

    @PostMapping({"/workers/scripts/list"})
    public ResponseData<?> workerScriptsList() {
        return ResponseData.ok(this.cloudflareService.listWorkerScripts());
    }

    @PostMapping({"/workers/pages/usage"})
    public ResponseData<?> workersPagesUsage() {
        return ResponseData.ok(this.cloudflareService.getWorkersUsageSummary());
    }

    @PostMapping({"/workers/pages/applications/list"})
    public ResponseData<?> workersPagesApplicationsList() {
        return ResponseData.ok(this.cloudflareService.listWorkersAndPagesApplications());
    }

    @PostMapping({"/workers/pages/templates/list"})
    public ResponseData<?> workersPagesTemplatesList() {
        return ResponseData.ok(this.cloudflareService.listWorkerTemplates());
    }

    @PostMapping({"/workers/subdomain/info"})
    public ResponseData<?> workersSubdomainInfo() {
        return ResponseData.ok(this.cloudflareService.getWorkersSubdomainInfo());
    }

    @PostMapping({"/workers/pages/templates/preview"})
    public ResponseData<?> workersPagesTemplatePreview(@RequestBody Map<String, String> params) {
        return ResponseData.ok(this.cloudflareService.getWorkersPagesTemplatePreview(params.get("templateId")));
    }

    @PostMapping({"/workers/deploy"})
    public ResponseData<?> workerDeploy(@RequestBody Map<String, String> params) {
        return ResponseData.ok(this.cloudflareService.deployWorker(params.get("name"), params.get("script")));
    }

    @PostMapping({"/workers/script/get"})
    public ResponseData<?> workerScriptGet(@RequestBody Map<String, String> params) {
        return ResponseData.ok(this.cloudflareService.getWorkerScriptContent(params.get("name")));
    }

    @PostMapping({"/workers/script/update"})
    public ResponseData<?> workerScriptUpdate(@RequestBody Map<String, String> params) {
        return ResponseData.ok(this.cloudflareService.updateWorkerScript(params.get("name"), params.get("script")));
    }

    @PostMapping({"/workers/rename"})
    public ResponseData<?> workerRename(@RequestBody Map<String, String> params) {
        return ResponseData.ok(this.cloudflareService.renameWorkerScript(params.get("name"), params.get("newName")));
    }

    @PostMapping({"/workers/delete"})
    public ResponseData<?> workerDelete(@RequestBody Map<String, String> params) {
        this.verifyCodeService.verifyCode("cfWorkerDelete", params.get("verifyCode"));
        this.cloudflareService.deleteWorkerScript(params.get("name"));
        return ResponseData.ok();
    }

    @PostMapping({"/workers/create/hello-world"})
    public ResponseData<?> workerCreateHelloWorld(@RequestBody Map<String, String> params) {
        return ResponseData.ok(this.cloudflareService.createWorkerHelloWorld(params.get("name"), params.get("script")));
    }

    @PostMapping({"/workers/create/template"})
    public ResponseData<?> workerCreateTemplate(@RequestBody Map<String, String> params) {
        return ResponseData.ok(this.cloudflareService.createWorkerFromTemplate(params.get("name"), params.get("templateId"), params.get("script")));
    }

    @PostMapping({"/pages/create/template"})
    public ResponseData<?> pagesCreateTemplate(@RequestBody Map<String, String> params) {
        return ResponseData.ok(this.cloudflareService.createPagesFromTemplate(params.get("name"), params.get("templateId")));
    }

    @PostMapping({"/pages/deploy/static"})
    public ResponseData<?> pagesDeployStatic(@RequestBody Map<String, Object> params) {
        List<Map<String, String>> encoded = (List<Map<String, String>>)params.get("files");
        return ResponseData.ok(this.cloudflareService.deployPagesStaticFromUpload((String)params.get("name"), encoded));
    }

    @PostMapping({"/ssl/get"})
    public ResponseData<?> sslGet(@RequestBody Map<String, String> params) {
        return ResponseData.ok(this.cloudflareService.getSslSettings(params.get("zoneId")));
    }

    @PostMapping({"/ssl/set"})
    public ResponseData<?> sslSet(@RequestBody Map<String, Object> params) {
        return ResponseData.ok(this.cloudflareService.updateSslSetting((String)params.get("zoneId"), (String)params.get("settingId"), params.get("value")));
    }

    @PostMapping({"/cache/get"})
    public ResponseData<?> cacheGet(@RequestBody Map<String, String> params) {
        return ResponseData.ok(this.cloudflareService.getCacheSettings(params.get("zoneId")));
    }

    @PostMapping({"/cache/set"})
    public ResponseData<?> cacheSet(@RequestBody Map<String, Object> params) {
        return ResponseData.ok(this.cloudflareService.updateCacheSetting((String)params.get("zoneId"), (String)params.get("settingId"), params.get("value")));
    }

    @PostMapping({"/cache/purge"})
    public ResponseData<?> cachePurge(@RequestBody Map<String, Object> params) {
        this.cloudflareService
            .purgeZoneCache((String)params.get("zoneId"), parseBoolean(params.get("purgeEverything"), false), parseStringList(params.get("files")));
        return ResponseData.ok();
    }

    @PostMapping({"/security/firewall/list"})
    public ResponseData<?> firewallList(@RequestBody Map<String, String> params) {
        return ResponseData.ok(this.cloudflareService.listFirewallRules(params.get("zoneId")));
    }

    @PostMapping({"/security/firewall/create"})
    public ResponseData<?> firewallCreate(@RequestBody Map<String, Object> params) {
        return ResponseData.ok(
            this.cloudflareService
                .createFirewallRule(
                    parseString(params.get("zoneId")),
                    parseString(params.get("action")),
                    parseString(params.get("expression")),
                    parseString(params.get("description")),
                    parseBoolean(params.get("paused"), false)
                )
        );
    }

    @PostMapping({"/security/firewall/paused"})
    public ResponseData<?> firewallPaused(@RequestBody Map<String, Object> params) {
        return ResponseData.ok(
            this.cloudflareService
                .setFirewallRulePaused(
                    parseString(params.get("zoneId")),
                    parseString(params.get("rulesetId")),
                    parseString(params.get("ruleId")),
                    parseBoolean(params.get("paused"), false)
                )
        );
    }

    @PostMapping({"/security/firewall/update"})
    public ResponseData<?> firewallUpdate(@RequestBody Map<String, Object> params) {
        Boolean paused = params.containsKey("paused") ? parseBoolean(params.get("paused"), false) : null;
        return ResponseData.ok(
            this.cloudflareService
                .updateFirewallRule(
                    parseString(params.get("zoneId")),
                    parseString(params.get("rulesetId")),
                    parseString(params.get("ruleId")),
                    parseString(params.get("action")),
                    params.containsKey("description") ? parseString(params.get("description")) : null,
                    parseString(params.get("expression")),
                    paused
                )
        );
    }

    @PostMapping({"/security/firewall/delete"})
    public ResponseData<?> firewallDelete(@RequestBody Map<String, String> params) {
        this.cloudflareService.deleteFirewallRule(params.get("zoneId"), params.get("rulesetId"), params.get("ruleId"));
        return ResponseData.ok();
    }

    @PostMapping({"/security/firewall/reorder"})
    public ResponseData<?> firewallReorder(@RequestBody Map<String, String> params) {
        String before = params.containsKey("beforeRuleId") ? params.get("beforeRuleId") : null;
        String after = params.get("afterRuleId");
        return ResponseData.ok(this.cloudflareService.reorderFirewallRule(params.get("zoneId"), params.get("rulesetId"), params.get("ruleId"), before, after));
    }

    @PostMapping({"/security/protection/get"})
    public ResponseData<?> securityProtectionGet(@RequestBody Map<String, String> params) {
        return ResponseData.ok(this.cloudflareService.getSecuritySettings(params.get("zoneId")));
    }

    @PostMapping({"/security/protection/set"})
    public ResponseData<?> securityProtectionSet(@RequestBody Map<String, Object> params) {
        return ResponseData.ok(
            this.cloudflareService.updateSecuritySetting(parseString(params.get("zoneId")), parseString(params.get("settingId")), params.get("value"))
        );
    }

    @PostMapping({"/workers/routes/list"})
    public ResponseData<?> workersRoutesList(@RequestBody Map<String, String> params) {
        return ResponseData.ok(this.cloudflareService.listWorkersRoutes(params.get("zoneId")));
    }

    @PostMapping({"/workers/routes/create"})
    public ResponseData<?> workersRoutesCreate(@RequestBody Map<String, String> params) {
        return ResponseData.ok(this.cloudflareService.createWorkersRoute(params.get("zoneId"), params.get("pattern"), params.get("script")));
    }

    @PostMapping({"/workers/routes/delete"})
    public ResponseData<?> workersRoutesDelete(@RequestBody Map<String, String> params) {
        this.cloudflareService.deleteWorkersRoute(params.get("zoneId"), params.get("routeId"));
        return ResponseData.ok();
    }

    @PostMapping({"/rules/list"})
    public ResponseData<?> rulesList(@RequestBody Map<String, String> params) {
        return ResponseData.ok(this.cloudflareService.listZoneRules(params.get("zoneId")));
    }

    @Deprecated
    @PostMapping({"/rules/pagerules/list"})
    public ResponseData<?> pageRulesList(@RequestBody Map<String, String> params) {
        return ResponseData.ok(this.cloudflareService.listPageRules(params.get("zoneId")));
    }

    @PostMapping({"/rules/pagerules/delete"})
    public ResponseData<?> pageRulesDelete(@RequestBody Map<String, String> params) {
        this.cloudflareService.deletePageRule(params.get("zoneId"), params.get("ruleId"));
        return ResponseData.ok();
    }

    private static boolean parseBoolean(Object value, boolean defaultValue) {
        if (value == null) {
            return defaultValue;
        } else {
            return value instanceof Boolean b ? b : Boolean.parseBoolean(String.valueOf(value));
        }
    }

    private static Integer parseInteger(Object value, Integer defaultValue) {
        if (value == null) {
            return defaultValue;
        } else if (value instanceof Number n) {
            return n.intValue();
        } else {
            try {
                return Integer.parseInt(String.valueOf(value));
            } catch (NumberFormatException var3) {
                return defaultValue;
            }
        }
    }

    private static String parseString(Object value) {
        if (value == null) {
            return null;
        } else {
            return value instanceof String ? (String)value : null;
        }
    }

    private static List<String> parseStringList(Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof List<?> list) {
            List<String> out = new ArrayList<>();

            for (Object item : list) {
                if (item != null) {
                    out.add(String.valueOf(item));
                }
            }

            return out;
        } else {
            return List.of(String.valueOf(value));
        }
    }
}
