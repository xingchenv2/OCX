package com.ocxworker.controller;

import com.ocxworker.model.params.IdListParams;
import com.ocxworker.model.params.IdParams;
import com.ocxworker.model.params.PageParams;
import com.ocxworker.model.params.TenantBatchMoveGroupParams;
import com.ocxworker.model.params.TenantParams;
import com.ocxworker.model.vo.ResponseData;
import com.ocxworker.service.AnnouncementService;
import com.ocxworker.service.CompartmentService;
import com.ocxworker.service.DomainManagementService;
import com.ocxworker.service.IamPolicyService;
import com.ocxworker.service.TenantService;
import com.ocxworker.service.VerifyCodeService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping({"/api/oci/user"})
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

    @PostMapping({"/list"})
    public ResponseData<?> list(@RequestBody PageParams params) {
        return ResponseData.ok(this.tenantService.list(params));
    }

    @PostMapping({"/add"})
    public ResponseData<?> add(@RequestBody @Valid TenantParams params) {
        this.tenantService.add(params);
        return ResponseData.ok();
    }

    @PostMapping({"/update"})
    public ResponseData<?> update(@RequestBody @Valid TenantParams params) {
        this.tenantService.update(params);
        return ResponseData.ok();
    }

    @PostMapping({"/remove"})
    public ResponseData<?> remove(@RequestBody @Valid IdListParams params) {
        this.tenantService.remove(params);
        return ResponseData.ok();
    }

    @PostMapping({"/batchMoveGroup"})
    public ResponseData<?> batchMoveGroup(@RequestBody @Valid TenantBatchMoveGroupParams params) {
        this.tenantService.batchMoveGroup(params);
        return ResponseData.ok();
    }

    @PostMapping({"/details"})
    public ResponseData<?> details(@RequestBody @Valid IdParams params) {
        return ResponseData.ok(this.tenantService.getById(params.getId()));
    }

    @PostMapping({"/fullInfo"})
    public ResponseData<?> fullInfo(@RequestBody Map<String, String> params) {
        return ResponseData.ok(this.tenantService.getTenantFullInfo(params.get("id")));
    }

    @PostMapping({"/refreshPlanType"})
    public ResponseData<?> refreshPlanType(@RequestBody Map<String, String> params) {
        this.tenantService.refreshPlanType(params.get("id"));
        return ResponseData.ok();
    }

    @PostMapping({"/billingSummary"})
    public ResponseData<?> billingSummary(@RequestBody Map<String, Object> params) {
        String id = params == null ? null : String.valueOf(params.get("id"));
        Object limits = params == null ? null : params.get("limits");
        return ResponseData.ok(this.tenantService.getTenantBillingSummary(id, limits));
    }

    @PostMapping({"/invoicePdf"})
    public ResponseEntity<byte[]> invoicePdf(@RequestBody Map<String, String> params) {
        String id = params == null ? null : params.get("id");
        String invoiceId = params == null ? null : params.get("invoiceId");
        String fileName = params == null ? null : params.get("fileName");
        byte[] pdf = this.tenantService.downloadInvoicePdf(id, invoiceId);
        String safeName = fileName != null && !fileName.isBlank() ? fileName : "invoice-" + (invoiceId == null ? "unknown" : invoiceId) + ".pdf";
        String encoded = URLEncoder.encode(safeName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        return ((BodyBuilder)ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header("Content-Disposition", new String[]{"attachment; filename*=UTF-8''" + encoded}))
            .body(pdf);
    }

    @PostMapping({"/uploadKey"})
    public ResponseData<?> uploadKey(@RequestParam("file") MultipartFile file) throws Exception {
        return ResponseData.ok(this.tenantService.uploadKey(file));
    }

    @PostMapping({"/domainSettings"})
    public ResponseData<?> domainSettings(@RequestBody Map<String, String> params) {
        return ResponseData.ok(this.domainManagementService.getDomainSettings(params.get("id")));
    }

    @PostMapping({"/updateMfa"})
    public ResponseData<?> updateMfa(@RequestBody Map<String, Object> params) {
        this.domainManagementService.updateMfaSetting((String)params.get("id"), (String)params.get("domainId"), Boolean.TRUE.equals(params.get("enabled")));
        return ResponseData.ok();
    }

    @PostMapping({"/updatePasswordExpiry"})
    public ResponseData<?> updatePasswordExpiry(@RequestBody Map<String, Object> params) {
        Object daysRaw = params == null ? null : params.get("days");
        if (daysRaw == null) {
            return ResponseData.error("days 不能为空");
        } else {
            int days;
            if (daysRaw instanceof Number n) {
                days = n.intValue();
            } else {
                try {
                    days = Integer.parseInt(String.valueOf(daysRaw));
                } catch (NumberFormatException var6) {
                    return ResponseData.error("days 格式非法");
                }
            }

            this.domainManagementService.updatePasswordExpiry((String)params.get("id"), (String)params.get("domainId"), days);
            return ResponseData.ok();
        }
    }

    @PostMapping({"/auditLogs"})
    public ResponseData<?> auditLogs(@RequestBody Map<String, Object> params) {
        int days = 7;
        Object raw = params == null ? null : params.get("days");
        if (raw instanceof Number n) {
            days = n.intValue();
        } else if (raw != null) {
            try {
                days = Integer.parseInt(String.valueOf(raw));
            } catch (Exception var6) {
            }
        }

        String id = params == null ? null : String.valueOf(params.get("id"));
        return ResponseData.ok(this.domainManagementService.getAuditLogs(id, days));
    }

    @PostMapping({"/quotas"})
    public ResponseData<?> quotas(@RequestBody Map<String, String> params) {
        return ResponseData.ok(this.domainManagementService.getServiceQuotas(params.get("id")));
    }

    @PostMapping({"/iamPolicies"})
    public ResponseData<?> iamPolicies(@RequestBody Map<String, String> params) {
        return ResponseData.ok(this.iamPolicyService.listPolicies(params.get("id")));
    }

    @PostMapping({"/iamPolicy"})
    public ResponseData<?> iamPolicy(@RequestBody Map<String, String> params) {
        return ResponseData.ok(this.iamPolicyService.getPolicy(params.get("id"), params.get("policyId")));
    }

    @PostMapping({"/compartments"})
    public ResponseData<?> compartments(@RequestBody Map<String, String> params) {
        return ResponseData.ok(this.compartmentService.listCompartments(params.get("id"), params.get("parentId"), params.get("keyword")));
    }

    @PostMapping({"/compartmentPicker"})
    public ResponseData<?> compartmentPicker(@RequestBody Map<String, String> params) {
        return ResponseData.ok(this.compartmentService.listCompartmentsPicker(params.get("id")));
    }

    @PostMapping({"/compartmentDetail"})
    public ResponseData<?> compartmentDetail(@RequestBody Map<String, String> params) {
        return ResponseData.ok(this.compartmentService.getCompartment(params.get("id"), params.get("compartmentId")));
    }

    @PostMapping({"/compartmentCreate"})
    public ResponseData<?> compartmentCreate(@RequestBody Map<String, String> params) {
        return ResponseData.ok(
            this.compartmentService.createCompartment(params.get("id"), params.get("parentId"), params.get("name"), params.get("description"))
        );
    }

    @PostMapping({"/compartmentUpdate"})
    public ResponseData<?> compartmentUpdate(@RequestBody Map<String, String> params) {
        this.verifyCodeService.verifyCode("updateCompartment", params == null ? null : params.get("verifyCode"));
        return ResponseData.ok(
            this.compartmentService.updateCompartment(params.get("id"), params.get("compartmentId"), params.get("name"), params.get("description"))
        );
    }

    @PostMapping({"/compartmentDelete"})
    public ResponseData<?> compartmentDelete(@RequestBody Map<String, String> params) {
        this.verifyCodeService.verifyCode("deleteCompartment", params == null ? null : params.get("verifyCode"));
        this.compartmentService.deleteCompartment(params.get("id"), params.get("compartmentId"));
        return ResponseData.ok();
    }

    @PostMapping({"/compartmentMove"})
    public ResponseData<?> compartmentMove(@RequestBody Map<String, String> params) {
        return ResponseData.ok(this.compartmentService.moveCompartment(params.get("id"), params.get("compartmentId"), params.get("newParentId")));
    }

    @PostMapping({"/compartmentResources"})
    public ResponseData<?> compartmentResources(@RequestBody Map<String, Object> params) {
        String id = params == null ? null : String.valueOf(params.get("id"));
        String compartmentId = params == null ? null : String.valueOf(params.get("compartmentId"));
        String pageToken = params == null ? null : (params.get("pageToken") == null ? null : String.valueOf(params.get("pageToken")));
        Integer limit = null;
        Object lim = params == null ? null : params.get("limit");
        if (lim instanceof Number n) {
            limit = n.intValue();
        } else if (lim != null) {
            try {
                limit = Integer.parseInt(String.valueOf(lim));
            } catch (Exception var9) {
            }
        }

        return ResponseData.ok(this.compartmentService.listResources(id, compartmentId, pageToken, limit));
    }

    @PostMapping({"/compartmentMoveResource"})
    public ResponseData<?> compartmentMoveResource(@RequestBody Map<String, String> params) {
        this.verifyCodeService.verifyCode("moveCompartmentResource", params == null ? null : params.get("verifyCode"));
        this.compartmentService.moveResource(params.get("id"), params.get("resourceId"), params.get("resourceType"), params.get("targetCompartmentId"));
        return ResponseData.ok();
    }

    @PostMapping({"/announcements"})
    public ResponseData<?> announcements(@RequestBody Map<String, String> params) {
        return ResponseData.ok(this.announcementService.listAnnouncements(params.get("id")));
    }

    @PostMapping({"/announcement"})
    public ResponseData<?> announcement(@RequestBody Map<String, String> params) {
        return ResponseData.ok(this.announcementService.getAnnouncementDetail(params.get("id"), params.get("announcementId")));
    }

    @PostMapping({"/authFactorsUnlock"})
    public ResponseData<?> authFactorsUnlock(@RequestBody Map<String, String> params) {
        String code = params == null ? null : params.get("verifyCode");
        String token = this.domainManagementService.unlockAuthFactors(code);
        return ResponseData.ok(Map.of("accessToken", token));
    }

    @PostMapping({"/authFactors"})
    public ResponseData<?> authFactors(@RequestBody Map<String, String> params) {
        return ResponseData.ok(this.domainManagementService.listAuthFactorSettings(params.get("id"), params.get("accessToken")));
    }

    @PostMapping({"/updateAuthFactors"})
    public ResponseData<?> updateAuthFactors(@RequestBody Map<String, Object> params) {
        return ResponseData.ok(
            this.domainManagementService
                .updateAuthFactorSettings(
                    (String)params.get("id"),
                    (String)params.get("domainId"),
                    (String)params.get("accessToken"),
                    (Map<String, Object>)params.get("factors"),
                    (Map<String, Object>)params.get("limits"),
                    (Map<String, Object>)params.get("trustedDevice")
                )
        );
    }

    @GetMapping({"/groups"})
    public ResponseData<?> groups() {
        return ResponseData.ok(this.tenantService.getDistinctGroups());
    }

    @PostMapping({"/saveGroupOrder"})
    public ResponseData<?> saveGroupOrder(@RequestBody Map<String, Object> params) {
        Object raw = params == null ? null : params.get("order");
        List<String> order = new ArrayList<>();
        if (raw instanceof List) {
            for (Object o : (List)raw) {
                if (o != null) {
                    order.add(String.valueOf(o));
                }
            }
        }

        this.tenantService.saveGroupOrder(order);
        return ResponseData.ok();
    }

    @PostMapping({"/createGroup"})
    public ResponseData<?> createGroup(@RequestBody Map<String, String> params) {
        this.tenantService.createGroup(params.get("name"), params.get("level"), params.get("parent"));
        return ResponseData.ok();
    }

    @PostMapping({"/renameGroup"})
    public ResponseData<?> renameGroup(@RequestBody Map<String, String> params) {
        this.tenantService.renameGroup(params.get("oldName"), params.get("newName"), params.get("level"));
        return ResponseData.ok();
    }

    @PostMapping({"/deleteGroup"})
    public ResponseData<?> deleteGroup(@RequestBody Map<String, String> params) {
        this.tenantService.deleteGroup(params.get("name"), params.get("level"));
        return ResponseData.ok();
    }
}
