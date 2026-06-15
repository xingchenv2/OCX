package com.ocxworker.controller;

import com.ocxworker.model.params.UserParams;
import com.ocxworker.model.vo.ResponseData;
import com.ocxworker.service.DomainManagementService;
import com.ocxworker.service.UserManagementService;
import com.ocxworker.service.VerifyCodeService;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/oci/identity"})
public class UserController {
    @Resource
    private UserManagementService userManagementService;
    @Resource
    private DomainManagementService domainManagementService;
    @Resource
    private VerifyCodeService verifyCodeService;

    @PostMapping({"/list"})
    public ResponseData<?> listUsers(@RequestBody Map<String, String> params) {
        return ResponseData.ok(this.userManagementService.listUsers(params.get("tenantId")));
    }

    @PostMapping({"/domains"})
    public ResponseData<?> listIdentityDomains(@RequestBody Map<String, String> params) {
        return ResponseData.ok(this.domainManagementService.listIdentityDomains(params.get("tenantId")));
    }

    @PostMapping({"/groups"})
    public ResponseData<?> listGroups(@RequestBody Map<String, String> params) {
        return ResponseData.ok(this.userManagementService.listGroups(params.get("tenantId")));
    }

    @PostMapping({"/domainGroups"})
    public ResponseData<?> listDomainGroups(@RequestBody Map<String, String> params) {
        return ResponseData.ok(this.userManagementService.listDomainGroups(params.get("tenantId"), params.get("domainId")));
    }

    @PostMapping({"/create"})
    public ResponseData<?> createUser(@RequestBody Map<String, Object> params) {
        this.verifyCodeService.verifyCode("createUser", (String)params.get("verifyCode"));
        UserParams up = new UserParams();
        up.setTenantId((String)params.get("tenantId"));
        up.setUserName((String)params.get("userName"));
        up.setEmail((String)params.get("email"));
        up.setAddToAdminGroup(Boolean.TRUE.equals(params.get("addToAdminGroup")));
        up.setGroupIds(parseGroupIds(params.get("groupIds")));
        Object domainId = params.get("domainId");
        if (domainId != null) {
            up.setDomainId(String.valueOf(domainId));
        }

        return ResponseData.ok(this.userManagementService.createUser(up));
    }

    @PostMapping({"/resetPassword"})
    public ResponseData<?> resetPassword(@RequestBody UserParams params) {
        String newPassword = this.userManagementService.getResetPasswordResult(params);
        return ResponseData.ok(newPassword);
    }

    @PostMapping({"/clearMfa"})
    public ResponseData<?> clearMfa(@RequestBody Map<String, String> params) {
        this.verifyCodeService.verifyCode("clearMfa", params.get("verifyCode"));
        UserParams up = new UserParams();
        up.setTenantId(params.get("tenantId"));
        up.setUserId(params.get("userId"));
        this.userManagementService.clearMfa(up);
        return ResponseData.ok("MFA 已清除");
    }

    @PostMapping({"/addToAdmin"})
    public ResponseData<?> addToAdmin(@RequestBody UserParams params) {
        this.userManagementService.addUserToGroup(params);
        return ResponseData.ok("已加入管理员组");
    }

    @PostMapping({"/removeFromAdmin"})
    public ResponseData<?> removeFromAdmin(@RequestBody Map<String, String> params) {
        this.verifyCodeService.verifyCode("removeFromAdmin", params.get("verifyCode"));
        UserParams up = new UserParams();
        up.setTenantId(params.get("tenantId"));
        up.setUserId(params.get("userId"));
        this.userManagementService.removeUserFromGroup(up);
        return ResponseData.ok("已移出管理员组");
    }

    @PostMapping({"/userGroups"})
    public ResponseData<?> getUserGroups(@RequestBody Map<String, String> params) {
        return ResponseData.ok(this.userManagementService.getUserGroups(params.get("tenantId"), params.get("userId")));
    }

    @PostMapping({"/updateUser"})
    public ResponseData<?> updateUser(@RequestBody Map<String, Object> params) {
        this.verifyCodeService.verifyCode("updateUser", (String)params.get("verifyCode"));
        UserParams up = new UserParams();
        up.setTenantId((String)params.get("tenantId"));
        up.setUserId((String)params.get("userId"));
        up.setUserName((String)params.get("userName"));
        up.setEmail((String)params.get("email"));
        this.userManagementService.updateUser(up);
        if (params.containsKey("groupIds")) {
            this.userManagementService.syncUserGroups(up.getTenantId(), up.getUserId(), parseGroupIds(params.get("groupIds")));
        }

        return ResponseData.ok();
    }

    private static List<String> parseGroupIds(Object raw) {
        if (raw instanceof List<?> list) {
            ArrayList ids = new ArrayList();

            for (Object o : list) {
                if (o != null && !String.valueOf(o).isBlank()) {
                    ids.add(String.valueOf(o).trim());
                }
            }

            return ids;
        } else {
            return List.of();
        }
    }

    @PostMapping({"/updateUserState"})
    public ResponseData<?> updateUserState(@RequestBody Map<String, Object> params) {
        boolean blocked = Boolean.TRUE.equals(params.get("blocked"));
        if (blocked) {
            this.verifyCodeService.verifyCode("disableUser", (String)params.get("verifyCode"));
        }

        this.userManagementService.updateUserState((String)params.get("tenantId"), (String)params.get("userId"), blocked);
        return ResponseData.ok();
    }

    @PostMapping({"/listMfaDevices"})
    public ResponseData<?> listMfaDevices(@RequestBody Map<String, String> params) {
        return ResponseData.ok(this.userManagementService.listMfaDevices(params.get("tenantId"), params.get("userId")));
    }

    @PostMapping({"/userCapabilities"})
    public ResponseData<?> getUserCapabilities(@RequestBody Map<String, String> params) {
        return ResponseData.ok(this.userManagementService.getUserCapabilities(params.get("tenantId"), params.get("userId")));
    }

    @PostMapping({"/updateUserCapabilities"})
    public ResponseData<?> updateUserCapabilities(@RequestBody Map<String, Object> params) {
        this.verifyCodeService.verifyCode("updateUserCapabilities", (String)params.get("verifyCode"));
        UserParams up = new UserParams();
        up.setTenantId((String)params.get("tenantId"));
        up.setUserId((String)params.get("userId"));
        up.setCapabilities(UserManagementService.parseCapabilitiesMap(params.get("capabilities")));
        this.userManagementService.updateUserCapabilities(up);
        return ResponseData.ok();
    }
}
