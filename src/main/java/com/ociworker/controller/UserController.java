/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.ociworker.controller.UserController
 *  com.ociworker.model.params.UserParams
 *  com.ociworker.model.vo.ResponseData
 *  com.ociworker.service.DomainManagementService
 *  com.ociworker.service.UserManagementService
 *  com.ociworker.service.VerifyCodeService
 *  jakarta.annotation.Resource
 *  org.springframework.web.bind.annotation.PostMapping
 *  org.springframework.web.bind.annotation.RequestBody
 *  org.springframework.web.bind.annotation.RequestMapping
 *  org.springframework.web.bind.annotation.RestController
 */
package com.ociworker.controller;

import com.ociworker.model.params.UserParams;
import com.ociworker.model.vo.ResponseData;
import com.ociworker.service.DomainManagementService;
import com.ociworker.service.UserManagementService;
import com.ociworker.service.VerifyCodeService;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 * Exception performing whole class analysis ignored.
 */
@RestController
@RequestMapping(value={"/api/oci/identity"})
public class UserController {
    @Resource
    private UserManagementService userManagementService;
    @Resource
    private DomainManagementService domainManagementService;
    @Resource
    private VerifyCodeService verifyCodeService;

    @PostMapping(value={"/list"})
    public ResponseData<?> listUsers(@RequestBody Map<String, String> params) {
        return ResponseData.ok((Object)this.userManagementService.listUsers(params.get("tenantId")));
    }

    @PostMapping(value={"/domains"})
    public ResponseData<?> listIdentityDomains(@RequestBody Map<String, String> params) {
        return ResponseData.ok((Object)this.domainManagementService.listIdentityDomains(params.get("tenantId")));
    }

    @PostMapping(value={"/groups"})
    public ResponseData<?> listGroups(@RequestBody Map<String, String> params) {
        return ResponseData.ok((Object)this.userManagementService.listGroups(params.get("tenantId")));
    }

    @PostMapping(value={"/domainGroups"})
    public ResponseData<?> listDomainGroups(@RequestBody Map<String, String> params) {
        return ResponseData.ok((Object)this.userManagementService.listDomainGroups(params.get("tenantId"), params.get("domainId")));
    }

    @PostMapping(value={"/create"})
    public ResponseData<?> createUser(@RequestBody Map<String, Object> params) {
        this.verifyCodeService.verifyCode("createUser", (String)params.get("verifyCode"));
        UserParams up = new UserParams();
        up.setTenantId((String)params.get("tenantId"));
        up.setUserName((String)params.get("userName"));
        up.setEmail((String)params.get("email"));
        up.setAddToAdminGroup(Boolean.valueOf(Boolean.TRUE.equals(params.get("addToAdminGroup"))));
        up.setGroupIds(UserController.parseGroupIds((Object)params.get("groupIds")));
        Object domainId = params.get("domainId");
        if (domainId != null) {
            up.setDomainId(String.valueOf(domainId));
        }
        return ResponseData.ok((Object)this.userManagementService.createUser(up));
    }

    @PostMapping(value={"/resetPassword"})
    public ResponseData<?> resetPassword(@RequestBody UserParams params) {
        String newPassword = this.userManagementService.getResetPasswordResult(params);
        return ResponseData.ok((Object)newPassword);
    }

    @PostMapping(value={"/clearMfa"})
    public ResponseData<?> clearMfa(@RequestBody Map<String, String> params) {
        this.verifyCodeService.verifyCode("clearMfa", params.get("verifyCode"));
        UserParams up = new UserParams();
        up.setTenantId(params.get("tenantId"));
        up.setUserId(params.get("userId"));
        this.userManagementService.clearMfa(up);
        return ResponseData.ok((Object)"MFA \u5df2\u6e05\u9664");
    }

    @PostMapping(value={"/addToAdmin"})
    public ResponseData<?> addToAdmin(@RequestBody UserParams params) {
        this.userManagementService.addUserToGroup(params);
        return ResponseData.ok((Object)"\u5df2\u52a0\u5165\u7ba1\u7406\u5458\u7ec4");
    }

    @PostMapping(value={"/removeFromAdmin"})
    public ResponseData<?> removeFromAdmin(@RequestBody Map<String, String> params) {
        this.verifyCodeService.verifyCode("removeFromAdmin", params.get("verifyCode"));
        UserParams up = new UserParams();
        up.setTenantId(params.get("tenantId"));
        up.setUserId(params.get("userId"));
        this.userManagementService.removeUserFromGroup(up);
        return ResponseData.ok((Object)"\u5df2\u79fb\u51fa\u7ba1\u7406\u5458\u7ec4");
    }

    @PostMapping(value={"/userGroups"})
    public ResponseData<?> getUserGroups(@RequestBody Map<String, String> params) {
        return ResponseData.ok((Object)this.userManagementService.getUserGroups(params.get("tenantId"), params.get("userId")));
    }

    @PostMapping(value={"/updateUser"})
    public ResponseData<?> updateUser(@RequestBody Map<String, Object> params) {
        this.verifyCodeService.verifyCode("updateUser", (String)params.get("verifyCode"));
        UserParams up = new UserParams();
        up.setTenantId((String)params.get("tenantId"));
        up.setUserId((String)params.get("userId"));
        up.setUserName((String)params.get("userName"));
        up.setEmail((String)params.get("email"));
        this.userManagementService.updateUser(up);
        if (params.containsKey("groupIds")) {
            this.userManagementService.syncUserGroups(up.getTenantId(), up.getUserId(), UserController.parseGroupIds((Object)params.get("groupIds")));
        }
        return ResponseData.ok();
    }

    private static List<String> parseGroupIds(Object raw) {
        if (!(raw instanceof List)) {
            return List.of();
        }
        List list = (List)raw;
        ArrayList<String> ids = new ArrayList<String>();
        for (Object o : list) {
            if (o == null || String.valueOf(o).isBlank()) continue;
            ids.add(String.valueOf(o).trim());
        }
        return ids;
    }

    @PostMapping(value={"/updateUserState"})
    public ResponseData<?> updateUserState(@RequestBody Map<String, Object> params) {
        boolean blocked = Boolean.TRUE.equals(params.get("blocked"));
        if (blocked) {
            this.verifyCodeService.verifyCode("disableUser", (String)params.get("verifyCode"));
        }
        this.userManagementService.updateUserState((String)params.get("tenantId"), (String)params.get("userId"), blocked);
        return ResponseData.ok();
    }

    @PostMapping(value={"/listMfaDevices"})
    public ResponseData<?> listMfaDevices(@RequestBody Map<String, String> params) {
        return ResponseData.ok((Object)this.userManagementService.listMfaDevices(params.get("tenantId"), params.get("userId")));
    }

    @PostMapping(value={"/userCapabilities"})
    public ResponseData<?> getUserCapabilities(@RequestBody Map<String, String> params) {
        return ResponseData.ok((Object)this.userManagementService.getUserCapabilities(params.get("tenantId"), params.get("userId")));
    }

    @PostMapping(value={"/updateUserCapabilities"})
    public ResponseData<?> updateUserCapabilities(@RequestBody Map<String, Object> params) {
        this.verifyCodeService.verifyCode("updateUserCapabilities", (String)params.get("verifyCode"));
        UserParams up = new UserParams();
        up.setTenantId((String)params.get("tenantId"));
        up.setUserId((String)params.get("userId"));
        up.setCapabilities(UserManagementService.parseCapabilitiesMap((Object)params.get("capabilities")));
        this.userManagementService.updateUserCapabilities(up);
        return ResponseData.ok();
    }
}

