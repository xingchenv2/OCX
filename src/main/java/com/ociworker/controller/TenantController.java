/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.ociworker.controller.TenantController
 *  com.ociworker.model.params.IdListParams
 *  com.ociworker.model.params.IdParams
 *  com.ociworker.model.params.PageParams
 *  com.ociworker.model.params.TenantBatchMoveGroupParams
 *  com.ociworker.model.params.TenantParams
 *  com.ociworker.model.vo.ResponseData
 *  com.ociworker.service.AnnouncementService
 *  com.ociworker.service.CompartmentService
 *  com.ociworker.service.DomainManagementService
 *  com.ociworker.service.IamPolicyService
 *  com.ociworker.service.TenantService
 *  com.ociworker.service.VerifyCodeService
 *  jakarta.annotation.Resource
 *  jakarta.validation.Valid
 *  org.springframework.http.MediaType
 *  org.springframework.http.ResponseEntity
 *  org.springframework.http.ResponseEntity$BodyBuilder
 *  org.springframework.web.bind.annotation.GetMapping
 *  org.springframework.web.bind.annotation.PostMapping
 *  org.springframework.web.bind.annotation.RequestBody
 *  org.springframework.web.bind.annotation.RequestMapping
 *  org.springframework.web.bind.annotation.RequestParam
 *  org.springframework.web.bind.annotation.RestController
 *  org.springframework.web.multipart.MultipartFile
 */
package com.ociworker.controller;

import com.ociworker.model.params.IdListParams;
import com.ociworker.model.params.IdParams;
import com.ociworker.model.params.PageParams;
import com.ociworker.model.params.TenantBatchMoveGroupParams;
import com.ociworker.model.params.TenantParams;
import com.ociworker.model.vo.ResponseData;
import com.ociworker.service.AnnouncementService;
import com.ociworker.service.CompartmentService;
import com.ociworker.service.DomainManagementService;
import com.ociworker.service.IamPolicyService;
import com.ociworker.service.TenantService;
import com.ociworker.service.VerifyCodeService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(value={"/api/oci/user"})
public class TenantController {
    @Resource
    private TenantService tenantService;
    @Resource
    private DomainManagementService domainManagementService;
    @Resource
    private IamPolicyService iamPolicyService;
    @Resource
    private CompartmentService compartmentService;
    @Resource
    private AnnouncementService announcementService;
    @Resource
    private VerifyCodeService verifyCodeService;

    @PostMapping(value={"/list"})
    public ResponseData<?> list(@RequestBody PageParams params) {
        return ResponseData.ok((Object)this.tenantService.list(params));
    }

    @PostMapping(value={"/add"})
    public ResponseData<?> add(@RequestBody @Valid TenantParams params) {
        this.tenantService.add(params);
        return ResponseData.ok();
    }

    @PostMapping(value={"/update"})
    public ResponseData<?> update(@RequestBody @Valid TenantParams params) {
        this.tenantService.update(params);
        return ResponseData.ok();
    }

    @PostMapping(value={"/remove"})
    public ResponseData<?> remove(@RequestBody @Valid IdListParams params) {
        this.tenantService.remove(params);
        return ResponseData.ok();
    }

    @PostMapping(value={"/batchMoveGroup"})
    public ResponseData<?> batchMoveGroup(@RequestBody @Valid TenantBatchMoveGroupParams params) {
        this.tenantService.batchMoveGroup(params);
        return ResponseData.ok();
    }

    @PostMapping(value={"/details"})
    public ResponseData<?> details(@RequestBody @Valid IdParams params) {
        return ResponseData.ok((Object)this.tenantService.getById(params.getId()));
    }

    @PostMapping(value={"/fullInfo"})
    public ResponseData<?> fullInfo(@RequestBody Map<String, String> params) {
        return ResponseData.ok((Object)this.tenantService.getTenantFullInfo(params.get("id")));
    }

    @PostMapping(value={"/refreshPlanType"})
    public ResponseData<?> refreshPlanType(@RequestBody Map<String, String> params) {
        this.tenantService.refreshPlanType(params.get("id"));
        return ResponseData.ok();
    }

    @PostMapping(value={"/billingSummary"})
    public ResponseData<?> billingSummary(@RequestBody Map<String, Object> params) {
        String id = params == null ? null : String.valueOf(params.get("id"));
        Object limits = params == null ? null : params.get("limits");
        return ResponseData.ok((Object)this.tenantService.getTenantBillingSummary(id, limits));
    }

    @PostMapping(value={"/invoicePdf"})
    public ResponseEntity<byte[]> invoicePdf(@RequestBody Map<String, String> params) {
        String id = params == null ? null : params.get("id");
        String invoiceId = params == null ? null : params.get("invoiceId");
        String fileName = params == null ? null : params.get("fileName");
        byte[] pdf = this.tenantService.downloadInvoicePdf(id, invoiceId);
        Object safeName = fileName == null || fileName.isBlank() ? "invoice-" + (invoiceId == null ? "unknown" : invoiceId) + ".pdf" : fileName;
        String encoded = URLEncoder.encode((String)safeName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        return ((ResponseEntity.BodyBuilder)ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).header("Content-Disposition", new String[]{"attachment; filename*=UTF-8''" + encoded})).body((Object)pdf);
    }

    @PostMapping(value={"/uploadKey"})
    public ResponseData<?> uploadKey(@RequestParam(value="file") MultipartFile file) throws Exception {
        return ResponseData.ok((Object)this.tenantService.uploadKey(file));
    }

    @PostMapping(value={"/domainSettings"})
    public ResponseData<?> domainSettings(@RequestBody Map<String, String> params) {
        return ResponseData.ok((Object)this.domainManagementService.getDomainSettings(params.get("id")));
    }

    @PostMapping(value={"/updateMfa"})
    public ResponseData<?> updateMfa(@RequestBody Map<String, Object> params) {
        this.domainManagementService.updateMfaSetting((String)params.get("id"), (String)params.get("domainId"), Boolean.TRUE.equals(params.get("enabled")));
        return ResponseData.ok();
    }

    @PostMapping(value={"/updatePasswordExpiry"})
    public ResponseData<?> updatePasswordExpiry(@RequestBody Map<String, Object> params) {
        int days;
        Object daysRaw;
        Object object = daysRaw = params == null ? null : params.get("days");
        if (daysRaw == null) {
            return ResponseData.error((String)"days \u4e0d\u80fd\u4e3a\u7a7a");
        }
        if (daysRaw instanceof Number) {
            Number n = (Number)daysRaw;
            days = n.intValue();
        } else {
            try {
                days = Integer.parseInt(String.valueOf(daysRaw));
            }
            catch (NumberFormatException e) {
                return ResponseData.error((String)"days \u683c\u5f0f\u975e\u6cd5");
            }
        }
        this.domainManagementService.updatePasswordExpiry((String)params.get("id"), (String)params.get("domainId"), days);
        return ResponseData.ok();
    }

    @PostMapping(value={"/auditLogs"})
    public ResponseData<?> auditLogs(@RequestBody Map<String, Object> params) {
        Object raw;
        int days = 7;
        Object object = raw = params == null ? null : params.get("days");
        if (raw instanceof Number) {
            Number n = (Number)raw;
            days = n.intValue();
        } else if (raw != null) {
            try {
                days = Integer.parseInt(String.valueOf(raw));
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        String id = params == null ? null : String.valueOf(params.get("id"));
        return ResponseData.ok((Object)this.domainManagementService.getAuditLogs(id, days));
    }

    @PostMapping(value={"/quotas"})
    public ResponseData<?> quotas(@RequestBody Map<String, String> params) {
        return ResponseData.ok((Object)this.domainManagementService.getServiceQuotas(params.get("id")));
    }

    @PostMapping(value={"/iamPolicies"})
    public ResponseData<?> iamPolicies(@RequestBody Map<String, String> params) {
        return ResponseData.ok((Object)this.iamPolicyService.listPolicies(params.get("id")));
    }

    @PostMapping(value={"/iamPolicy"})
    public ResponseData<?> iamPolicy(@RequestBody Map<String, String> params) {
        return ResponseData.ok((Object)this.iamPolicyService.getPolicy(params.get("id"), params.get("policyId")));
    }

    @PostMapping(value={"/compartments"})
    public ResponseData<?> compartments(@RequestBody Map<String, String> params) {
        return ResponseData.ok((Object)this.compartmentService.listCompartments(params.get("id"), params.get("parentId"), params.get("keyword")));
    }

    @PostMapping(value={"/compartmentPicker"})
    public ResponseData<?> compartmentPicker(@RequestBody Map<String, String> params) {
        return ResponseData.ok((Object)this.compartmentService.listCompartmentsPicker(params.get("id")));
    }

    @PostMapping(value={"/compartmentDetail"})
    public ResponseData<?> compartmentDetail(@RequestBody Map<String, String> params) {
        return ResponseData.ok((Object)this.compartmentService.getCompartment(params.get("id"), params.get("compartmentId")));
    }

    @PostMapping(value={"/compartmentCreate"})
    public ResponseData<?> compartmentCreate(@RequestBody Map<String, String> params) {
        return ResponseData.ok((Object)this.compartmentService.createCompartment(params.get("id"), params.get("parentId"), params.get("name"), params.get("description")));
    }

    @PostMapping(value={"/compartmentUpdate"})
    public ResponseData<?> compartmentUpdate(@RequestBody Map<String, String> params) {
        this.verifyCodeService.verifyCode("updateCompartment", params == null ? null : params.get("verifyCode"));
        return ResponseData.ok((Object)this.compartmentService.updateCompartment(params.get("id"), params.get("compartmentId"), params.get("name"), params.get("description")));
    }

    @PostMapping(value={"/compartmentDelete"})
    public ResponseData<?> compartmentDelete(@RequestBody Map<String, String> params) {
        this.verifyCodeService.verifyCode("deleteCompartment", params == null ? null : params.get("verifyCode"));
        this.compartmentService.deleteCompartment(params.get("id"), params.get("compartmentId"));
        return ResponseData.ok();
    }

    @PostMapping(value={"/compartmentMove"})
    public ResponseData<?> compartmentMove(@RequestBody Map<String, String> params) {
        return ResponseData.ok((Object)this.compartmentService.moveCompartment(params.get("id"), params.get("compartmentId"), params.get("newParentId")));
    }

    @PostMapping(value={"/compartmentResources"})
    public ResponseData<?> compartmentResources(@RequestBody Map<String, Object> params) {
        Object lim;
        String compartmentId;
        String id = params == null ? null : String.valueOf(params.get("id"));
        String string = compartmentId = params == null ? null : String.valueOf(params.get("compartmentId"));
        String pageToken = params == null ? null : (params.get("pageToken") == null ? null : String.valueOf(params.get("pageToken")));
        Integer limit = null;
        Object object = lim = params == null ? null : params.get("limit");
        if (lim instanceof Number) {
            Number n = (Number)lim;
            limit = n.intValue();
        } else if (lim != null) {
            try {
                limit = Integer.parseInt(String.valueOf(lim));
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        return ResponseData.ok((Object)this.compartmentService.listResources(id, compartmentId, pageToken, limit));
    }

    @PostMapping(value={"/compartmentMoveResource"})
    public ResponseData<?> compartmentMoveResource(@RequestBody Map<String, String> params) {
        this.verifyCodeService.verifyCode("moveCompartmentResource", params == null ? null : params.get("verifyCode"));
        this.compartmentService.moveResource(params.get("id"), params.get("resourceId"), params.get("resourceType"), params.get("targetCompartmentId"));
        return ResponseData.ok();
    }

    @PostMapping(value={"/announcements"})
    public ResponseData<?> announcements(@RequestBody Map<String, String> params) {
        return ResponseData.ok((Object)this.announcementService.listAnnouncements(params.get("id")));
    }

    @PostMapping(value={"/announcement"})
    public ResponseData<?> announcement(@RequestBody Map<String, String> params) {
        return ResponseData.ok((Object)this.announcementService.getAnnouncementDetail(params.get("id"), params.get("announcementId")));
    }

    @PostMapping(value={"/authFactorsUnlock"})
    public ResponseData<?> authFactorsUnlock(@RequestBody Map<String, String> params) {
        String code = params == null ? null : params.get("verifyCode");
        String token = this.domainManagementService.unlockAuthFactors(code);
        return ResponseData.ok(Map.of("accessToken", token));
    }

    @PostMapping(value={"/authFactors"})
    public ResponseData<?> authFactors(@RequestBody Map<String, String> params) {
        return ResponseData.ok((Object)this.domainManagementService.listAuthFactorSettings(params.get("id"), params.get("accessToken")));
    }

    @PostMapping(value={"/updateAuthFactors"})
    public ResponseData<?> updateAuthFactors(@RequestBody Map<String, Object> params) {
        return ResponseData.ok((Object)this.domainManagementService.updateAuthFactorSettings((String)params.get("id"), (String)params.get("domainId"), (String)params.get("accessToken"), (Map)params.get("factors"), (Map)params.get("limits"), (Map)params.get("trustedDevice")));
    }

    @GetMapping(value={"/groups"})
    public ResponseData<?> groups() {
        return ResponseData.ok((Object)this.tenantService.getDistinctGroups());
    }

    @PostMapping(value={"/saveGroupOrder"})
    public ResponseData<?> saveGroupOrder(@RequestBody Map<String, Object> params) {
        Object raw = params == null ? null : params.get("order");
        ArrayList<String> order = new ArrayList<String>();
        if (raw instanceof List) {
            List list = (List)raw;
            for (Object o : list) {
                if (o == null) continue;
                order.add(String.valueOf(o));
            }
        }
        this.tenantService.saveGroupOrder(order);
        return ResponseData.ok();
    }

    @PostMapping(value={"/createGroup"})
    public ResponseData<?> createGroup(@RequestBody Map<String, String> params) {
        this.tenantService.createGroup(params.get("name"), params.get("level"), params.get("parent"));
        return ResponseData.ok();
    }

    @PostMapping(value={"/renameGroup"})
    public ResponseData<?> renameGroup(@RequestBody Map<String, String> params) {
        this.tenantService.renameGroup(params.get("oldName"), params.get("newName"), params.get("level"));
        return ResponseData.ok();
    }

    @PostMapping(value={"/deleteGroup"})
    public ResponseData<?> deleteGroup(@RequestBody Map<String, String> params) {
        this.tenantService.deleteGroup(params.get("name"), params.get("level"));
        return ResponseData.ok();
    }
}

