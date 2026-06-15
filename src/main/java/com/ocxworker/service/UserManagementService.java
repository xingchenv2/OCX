/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cn.hutool.core.text.CharSequenceUtil
 *  cn.hutool.core.util.StrUtil
 *  com.ocxworker.exception.OciException
 *  com.ocxworker.mapper.OciUserMapper
 *  com.ocxworker.model.entity.OciUser
 *  com.ocxworker.model.params.UserParams
 *  com.ocxworker.service.DomainManagementService
 *  com.ocxworker.service.OciClientService
 *  com.ocxworker.service.UserManagementService
 *  com.oracle.bmc.Region
 *  com.oracle.bmc.auth.AbstractAuthenticationDetailsProvider
 *  com.oracle.bmc.auth.SimpleAuthenticationDetailsProvider
 *  com.oracle.bmc.identity.IdentityClient
 *  com.oracle.bmc.identity.model.AddUserToGroupDetails
 *  com.oracle.bmc.identity.model.CreateUserDetails
 *  com.oracle.bmc.identity.model.CreateUserDetails$Builder
 *  com.oracle.bmc.identity.model.Group
 *  com.oracle.bmc.identity.model.MfaTotpDeviceSummary
 *  com.oracle.bmc.identity.model.UpdateStateDetails
 *  com.oracle.bmc.identity.model.UpdateUserCapabilitiesDetails
 *  com.oracle.bmc.identity.model.UpdateUserCapabilitiesDetails$Builder
 *  com.oracle.bmc.identity.model.UpdateUserDetails
 *  com.oracle.bmc.identity.model.UpdateUserDetails$Builder
 *  com.oracle.bmc.identitydomains.model.User
 *  com.oracle.bmc.identitydomains.model.UserCapabilities
 *  com.oracle.bmc.identitydomains.model.UserGroupMembership
 *  com.oracle.bmc.identity.requests.AddUserToGroupRequest
 *  com.oracle.bmc.identity.requests.CreateOrResetUIPasswordRequest
 *  com.oracle.bmc.identity.requests.CreateUserRequest
 *  com.oracle.bmc.identity.requests.DeleteMfaTotpDeviceRequest
 *  com.oracle.bmc.identity.requests.GetGroupRequest
 *  com.oracle.bmc.identity.requests.GetUserRequest
 *  com.oracle.bmc.identity.requests.ListGroupsRequest
 *  com.oracle.bmc.identity.requests.ListMfaTotpDevicesRequest
 *  com.oracle.bmc.identity.requests.ListUserGroupMembershipsRequest
 *  com.oracle.bmc.identity.requests.ListUsersRequest
 *  com.oracle.bmc.identity.requests.RemoveUserFromGroupRequest
 *  com.oracle.bmc.identity.requests.UpdateUserCapabilitiesRequest
 *  com.oracle.bmc.identity.requests.UpdateUserRequest
 *  com.oracle.bmc.identity.requests.UpdateUserStateRequest
 *  com.oracle.bmc.identity.responses.CreateOrResetUIPasswordResponse
 *  com.oracle.bmc.identity.responses.CreateUserResponse
 *  com.oracle.bmc.identity.responses.ListGroupsResponse
 *  com.oracle.bmc.identity.responses.ListMfaTotpDevicesResponse
 *  com.oracle.bmc.identity.responses.ListUserGroupMembershipsResponse
 *  com.oracle.bmc.identity.responses.ListUsersResponse
 *  com.oracle.bmc.identitydomains.IdentityDomainsClient
 *  com.oracle.bmc.identitydomains.model.Group
 *  com.oracle.bmc.identitydomains.model.Groups
 *  com.oracle.bmc.identitydomains.model.Operations
 *  com.oracle.bmc.identitydomains.model.Operations$Op
 *  com.oracle.bmc.identitydomains.model.PatchOp
 *  com.oracle.bmc.identitydomains.model.User
 *  com.oracle.bmc.identitydomains.model.User$Builder
 *  com.oracle.bmc.identitydomains.model.UserEmails
 *  com.oracle.bmc.identitydomains.model.UserEmails$Type
 *  com.oracle.bmc.identitydomains.model.UserName
 *  com.oracle.bmc.identitydomains.model.UserPasswordChanger
 *  com.oracle.bmc.identitydomains.model.Users
 *  com.oracle.bmc.identitydomains.requests.CreateUserRequest
 *  com.oracle.bmc.identitydomains.requests.GetGroupRequest
 *  com.oracle.bmc.identitydomains.requests.ListGroupsRequest
 *  com.oracle.bmc.identitydomains.requests.ListUsersRequest
 *  com.oracle.bmc.identitydomains.requests.PatchGroupRequest
 *  com.oracle.bmc.identitydomains.requests.PatchGroupRequest$Builder
 *  com.oracle.bmc.identitydomains.requests.PutUserPasswordChangerRequest
 *  com.oracle.bmc.identitydomains.responses.CreateUserResponse
 *  com.oracle.bmc.identitydomains.responses.GetGroupResponse
 *  com.oracle.bmc.identitydomains.responses.ListGroupsResponse
 *  com.oracle.bmc.identitydomains.responses.ListUsersResponse
 *  com.oracle.bmc.model.BmcException
 *  com.oracle.bmc.responses.BmcResponse
 *  jakarta.annotation.Resource
 *  lombok.Generated
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.stereotype.Service
 */
package com.ocxworker.service;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import com.ocxworker.exception.OciException;
import com.ocxworker.mapper.OciUserMapper;
import com.ocxworker.model.entity.OciUser;
import com.ocxworker.model.params.UserParams;
import com.ocxworker.service.DomainManagementService;
import com.ocxworker.service.OciClientService;
import com.oracle.bmc.Region;
import com.oracle.bmc.auth.AbstractAuthenticationDetailsProvider;
import com.oracle.bmc.auth.SimpleAuthenticationDetailsProvider;
import com.oracle.bmc.identity.IdentityClient;
import com.oracle.bmc.identity.model.AddUserToGroupDetails;
import com.oracle.bmc.identity.model.CreateUserDetails;
import com.oracle.bmc.identity.model.Group;
import com.oracle.bmc.identity.model.MfaTotpDeviceSummary;
import com.oracle.bmc.identity.model.UpdateStateDetails;
import com.oracle.bmc.identity.model.UpdateUserCapabilitiesDetails;
import com.oracle.bmc.identity.model.UpdateUserDetails;
import com.oracle.bmc.identitydomains.model.UserCapabilities;
import com.oracle.bmc.identitydomains.model.UserGroupMembership;
import com.oracle.bmc.identity.requests.AddUserToGroupRequest;
import com.oracle.bmc.identity.requests.CreateOrResetUIPasswordRequest;
import com.oracle.bmc.identity.requests.DeleteMfaTotpDeviceRequest;
import com.oracle.bmc.identity.requests.GetGroupRequest;
import com.oracle.bmc.identity.requests.GetUserRequest;
import com.oracle.bmc.identity.requests.ListGroupsRequest;
import com.oracle.bmc.identity.requests.ListMfaTotpDevicesRequest;
import com.oracle.bmc.identity.requests.ListUserGroupMembershipsRequest;
import com.oracle.bmc.identity.requests.ListUsersRequest;
import com.oracle.bmc.identity.requests.RemoveUserFromGroupRequest;
import com.oracle.bmc.identity.requests.UpdateUserCapabilitiesRequest;
import com.oracle.bmc.identity.requests.UpdateUserRequest;
import com.oracle.bmc.identity.requests.UpdateUserStateRequest;
import com.oracle.bmc.identity.responses.CreateOrResetUIPasswordResponse;
import com.oracle.bmc.identity.responses.ListMfaTotpDevicesResponse;
import com.oracle.bmc.identity.responses.ListUserGroupMembershipsResponse;
import com.oracle.bmc.identity.responses.ListUsersResponse;
import com.oracle.bmc.identitydomains.IdentityDomainsClient;
import com.oracle.bmc.identitydomains.model.Groups;
import com.oracle.bmc.identitydomains.model.Operations;
import com.oracle.bmc.identitydomains.model.PatchOp;
import com.oracle.bmc.identitydomains.model.User;
import com.oracle.bmc.identitydomains.model.UserEmails;
import com.oracle.bmc.identitydomains.model.UserName;
import com.oracle.bmc.identitydomains.model.UserPasswordChanger;
import com.oracle.bmc.identitydomains.model.Users;
import com.oracle.bmc.identitydomains.requests.CreateUserRequest;
import com.oracle.bmc.identitydomains.requests.PatchGroupRequest;
import com.oracle.bmc.identitydomains.requests.PutUserPasswordChangerRequest;
import com.oracle.bmc.identitydomains.responses.CreateUserResponse;
import com.oracle.bmc.identitydomains.responses.GetGroupResponse;
import com.oracle.bmc.identitydomains.responses.ListGroupsResponse;
import com.oracle.bmc.model.BmcException;
import com.oracle.bmc.responses.BmcResponse;
import jakarta.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/*
 * Exception performing whole class analysis ignored.
 */
@Service
public class UserManagementService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(UserManagementService.class);
    private static final String SCIM_SCHEMA_USER = "urn:ietf:params:scim:schemas:core:2.0:User";
    private static final String SCIM_SCHEMA_PATCH_OP = "urn:ietf:params:scim:api:messages:2.0:PatchOp";
    private static final String SCHEMA_USER_PASSWORD_CHANGER = "urn:ietf:params:scim:schemas:oracle:idcs:UserPasswordChanger";
    public static final List<String> CAPABILITY_KEYS = List.of("canUseConsolePassword", "canUseApiKeys", "canUseAuthTokens", "canUseSmtpCredentials", "canUseDbCredentials", "canUseCustomerSecretKeys", "canUseOAuth2ClientCredentials");
    @Resource
    private OciUserMapper userMapper;
    @Resource
    private DomainManagementService domainManagementService;

    private IdentityClient buildClient(OciUser tenant) {
        SimpleAuthenticationDetailsProvider provider = SimpleAuthenticationDetailsProvider.builder().tenantId(tenant.getOciTenantId()).userId(tenant.getOciUserId()).fingerprint(tenant.getOciFingerprint()).privateKeySupplier(() -> {
            try (FileInputStream fis = new FileInputStream(tenant.getOciKeyPath());){
                ByteArrayInputStream byteArrayInputStream;
                try (ByteArrayOutputStream baos = new ByteArrayOutputStream();){
                    int bytesRead;
                    byte[] buffer = new byte[1024];
                    while ((bytesRead = fis.read(buffer)) != -1) {
                        baos.write(buffer, 0, bytesRead);
                    }
                    byteArrayInputStream = new ByteArrayInputStream(baos.toByteArray());
                }
                return byteArrayInputStream;
            }
            catch (Exception e) {
                throw new RuntimeException("Failed to read private key: " + e.getMessage());
            }
        }).region(Region.valueOf((String)tenant.getOciRegion())).build();
        return IdentityClient.builder().build((AbstractAuthenticationDetailsProvider)provider);
    }

    private OciUser getTenant(String tenantId) {
        OciUser tenant = (OciUser)this.userMapper.selectById((Serializable)((Object)tenantId));
        if (tenant == null) {
            throw new OciException("\u79df\u6237\u4e0d\u5b58\u5728");
        }
        return tenant;
    }

    public List<Map<String, Object>> listUsers(String tenantId) {
        OciUser tenant = this.getTenant(tenantId);
        try (IdentityClient client = this.buildClient(tenant);){
            ListUsersResponse response = client.listUsers(ListUsersRequest.builder().compartmentId(tenant.getOciTenantId()).build());
            ArrayList result = new ArrayList();
            for (com.oracle.bmc.identitydomains.model.User user : response.getItems()) {
                LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
                map.put("id", user.getId());
                map.put("name", user.getName());
                map.put("email", user.getEmail());
                map.put("description", user.getDescription());
                map.put("state", user.getLifecycleState().getValue());
                map.put("timeCreated", user.getTimeCreated() != null ? user.getTimeCreated().toString() : null);
                boolean hasMfa = false;
                try {
                    ListMfaTotpDevicesResponse mfaResp = client.listMfaTotpDevices(ListMfaTotpDevicesRequest.builder().userId(user.getId()).build());
                    hasMfa = mfaResp.getItems() != null && !mfaResp.getItems().isEmpty();
                }
                catch (Exception exception) {
                    // empty catch block
                }
                map.put("isMfaActivated", hasMfa);
                result.add(map);
            }
            ArrayList arrayList = result;
            return arrayList;
        }
    }

    public Map<String, Object> createUser(UserParams params) {
        OciUser tenant = this.getTenant(params.getTenantId());
        String domainId = StrUtil.trimToNull((CharSequence)params.getDomainId());
        if (domainId == null) {
            return this.createUserViaIamApi(tenant, params);
        }
        try (OciClientService oci = this.domainManagementService.openOciClient(tenant.getId());){
            Object object;
            List domains = this.domainManagementService.listDomains(oci, false);
            Map selected = null;
            for (Map d : domains) {
                if (!domainId.equals(d.get("id"))) continue;
                selected = d;
                break;
            }
            if (selected == null) {
                throw new OciException("\u672a\u627e\u5230\u6307\u5b9a\u7684 Identity Domain\uff0c\u8bf7\u5237\u65b0\u57df\u5217\u8868\u540e\u91cd\u8bd5");
            }
            if (UserManagementService.isDefaultDomainForClassicIam(selected)) {
                object = this.createUserViaIamApi(tenant, params);
                return object;
            }
            object = this.createUserViaIdentityDomainsApi(oci, selected, params, tenant);
            return object;
        }
    }

    private Map<String, Object> createUserViaIamApi(OciUser tenant, UserParams params) {
        try (IdentityClient client = this.buildClient(tenant);){
            CreateUserDetails.Builder builder = CreateUserDetails.builder().compartmentId(tenant.getOciTenantId()).name(params.getUserName()).description(params.getEmail() != null ? params.getEmail() : params.getUserName()).email(params.getEmail());
            com.oracle.bmc.identity.responses.CreateUserResponse response = client.createUser(com.oracle.bmc.identity.requests.CreateUserRequest.builder().createUserDetails(builder.build()).build());
            User created = response.getUser();
            log.info("Created user (IAM API): {} in tenant: {}", (Object)created.getName(), (Object)tenant.getUsername());
            if (Boolean.TRUE.equals(params.getAddToAdminGroup())) {
                this.addToAdminGroup(client, tenant.getOciTenantId(), created.getId());
            }
            LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>();
            result.put("id", created.getId());
            result.put("name", created.getName());
            result.put("email", created.getEmail());
            result.put("state", created.getLifecycleState().getValue());
            LinkedHashMap<String, Object> linkedHashMap = result;
            return linkedHashMap;
        }
    }

    private static boolean isDefaultDomainForClassicIam(Map<String, Object> domain) {
        return "Default".equals(String.valueOf(domain.get("displayName")));
    }

    private Map<String, Object> createUserViaIdentityDomainsApi(OciClientService oci, Map<String, Object> domain, UserParams params, OciUser tenant) {
        String url = (String)domain.get("url");
        if (StrUtil.isBlank((CharSequence)url)) {
            throw new OciException("\u8be5 Identity Domain \u7f3a\u5c11 URL\uff0c\u65e0\u6cd5\u521b\u5efa\u7528\u6237");
        }
        String loginName = StrUtil.blankToDefault((CharSequence)StrUtil.trim((CharSequence)params.getUserName()), (String)"User");
        UserName scimName = UserName.builder().formatted(loginName).givenName(loginName).familyName(loginName).build();
        User.Builder ub = com.oracle.bmc.identitydomains.model.User.builder().schemas(List.of("urn:ietf:params:scim:schemas:core:2.0:User")).userName(loginName).name(scimName).active(Boolean.TRUE).description(StrUtil.isNotBlank((CharSequence)params.getEmail()) ? params.getEmail() : loginName);
        if (StrUtil.isNotBlank((CharSequence)params.getEmail())) {
            ub.emails(List.of(UserEmails.builder().value(params.getEmail()).type(UserEmails.Type.Work).primary(Boolean.valueOf(true)).build()));
        }
        com.oracle.bmc.identitydomains.model.User scimUser = ub.build();
        try (IdentityDomainsClient dc = IdentityDomainsClient.builder().build((AbstractAuthenticationDetailsProvider)oci.getProvider());){
            dc.setEndpoint(url);
            CreateUserResponse response = dc.createUser(CreateUserRequest.builder().user(scimUser).build());
            com.oracle.bmc.identitydomains.model.User created = response.getUser();
            log.info("Created user (Identity Domains API): {} in domain {} tenant {}", new Object[]{created.getUserName(), domain.get("displayName"), tenant.getUsername()});
            if (created.getId() != null) {
                this.applyIdentityDomainGroupAssignments(dc, created.getId(), params);
            }
            LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>();
            String id = StrUtil.isNotBlank((CharSequence)created.getOcid()) ? created.getOcid() : created.getId();
            result.put("id", id);
            result.put("name", created.getUserName());
            result.put("email", UserManagementService.firstEmailValue((com.oracle.bmc.identitydomains.model.User)created, (String)params.getEmail()));
            result.put("state", Boolean.FALSE.equals(created.getActive()) ? "INACTIVE" : "ACTIVE");
            LinkedHashMap<String, Object> linkedHashMap = result;
            return linkedHashMap;
        }
    }

    private static String firstEmailValue(com.oracle.bmc.identitydomains.model.User u, String fallback) {
        if (u.getEmails() != null) {
            for (Object o : u.getEmails()) {
                Map m;
                UserEmails ue;
                if (o instanceof UserEmails && StrUtil.isNotBlank((CharSequence)(ue = (UserEmails)o).getValue())) {
                    return ue.getValue();
                }
                if (!(o instanceof Map) || (m = (Map)o).get("value") == null) continue;
                return String.valueOf(m.get("value"));
            }
        }
        return fallback;
    }

    private void applyIdentityDomainGroupAssignments(IdentityDomainsClient dc, String userScimId, UserParams params) {
        List groupIds = params.getGroupIds();
        if (groupIds != null && !groupIds.isEmpty()) {
            for (String groupId : groupIds) {
                if (!StrUtil.isNotBlank((CharSequence)groupId)) continue;
                this.addUserToGroupIdentityDomains(dc, userScimId, groupId.trim());
            }
            return;
        }
        if (Boolean.TRUE.equals(params.getAddToAdminGroup())) {
            this.addUserToAdministratorsGroupIdentityDomains(dc, userScimId);
        }
    }

    private void addUserToAdministratorsGroupIdentityDomains(IdentityDomainsClient dc, String userScimId) {
        try {
            ListGroupsResponse listResp = dc.listGroups(com.oracle.bmc.identitydomains.requests.ListGroupsRequest.builder().filter("displayName eq \"Administrators\"").count(Integer.valueOf(50)).build());
            Groups groups = listResp.getGroups();
            if (groups == null || groups.getResources() == null || groups.getResources().isEmpty()) {
                log.warn("Administrators group not found in identity domain, skip addToAdminGroup");
                return;
            }
            Object raw = groups.getResources().get(0);
            if (!(raw instanceof com.oracle.bmc.identitydomains.model.Group)) {
                log.warn("Unexpected group resource type, skip addToAdminGroup");
                return;
            }
            com.oracle.bmc.identitydomains.model.Group adminGroup = (com.oracle.bmc.identitydomains.model.Group)raw;
            String groupId = adminGroup.getId();
            if (StrUtil.isBlank((CharSequence)groupId)) {
                return;
            }
            this.addUserToGroupIdentityDomains(dc, userScimId, groupId);
        }
        catch (Exception e) {
            log.warn("Failed to add user to Administrators in identity domain: {}", (Object)e.getMessage());
        }
    }

    private void addUserToGroupIdentityDomains(IdentityDomainsClient dc, String userScimId, String groupId) {
        try {
            GetGroupResponse getResp = dc.getGroup(com.oracle.bmc.identitydomains.requests.GetGroupRequest.builder().groupId(groupId).build());
            String ifMatch = UserManagementService.headerValueIgnoreCase((BmcResponse)getResp, (String)"etag");
            LinkedHashMap<String, String> member = new LinkedHashMap<String, String>();
            member.put("value", userScimId);
            member.put("type", "User");
            PatchOp patchOp = PatchOp.builder().schemas(List.of("urn:ietf:params:scim:api:messages:2.0:PatchOp")).operations(List.of(Operations.builder().op(Operations.Op.Add).path("members").value(List.of(member)).build())).build();
            PatchGroupRequest.Builder pr = PatchGroupRequest.builder().groupId(groupId).patchOp(patchOp);
            if (StrUtil.isNotBlank((CharSequence)ifMatch)) {
                pr.ifMatch(ifMatch);
            }
            dc.patchGroup(pr.build());
            log.info("Added user {} to identity domain group {}", (Object)userScimId, (Object)groupId);
        }
        catch (Exception e) {
            log.warn("Failed to add user {} to identity domain group {}: {}", new Object[]{userScimId, groupId, e.getMessage()});
        }
    }

    private static boolean isHiddenDomainGroupName(String name) {
        if (StrUtil.isBlank((CharSequence)name)) {
            return false;
        }
        String n = name.trim().toLowerCase();
        return "all domain users".equals(n);
    }

    public List<Map<String, Object>> listDomainGroups(String tenantId, String domainId) {
        if (StrUtil.isBlank((CharSequence)domainId)) {
            return this.listGroupsFiltered(tenantId);
        }
        try (OciClientService oci = this.domainManagementService.openOciClient(tenantId);){
            List domains = this.domainManagementService.listDomains(oci, false);
            Map selected = null;
            for (Map d : domains) {
                if (!domainId.equals(d.get("id"))) continue;
                selected = d;
                break;
            }
            if (selected == null) {
                throw new OciException("\u672a\u627e\u5230\u6307\u5b9a\u7684 Identity Domain");
            }
            if (UserManagementService.isDefaultDomainForClassicIam(selected)) {
                List list = this.listGroupsFiltered(tenantId);
                return list;
            }
            String url = (String)selected.get("url");
            if (StrUtil.isBlank((CharSequence)url)) {
                throw new OciException("\u8be5 Identity Domain \u7f3a\u5c11 URL\uff0c\u65e0\u6cd5\u5217\u51fa\u7ec4");
            }
            ArrayList<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
            try (IdentityDomainsClient dc = IdentityDomainsClient.builder().build((AbstractAuthenticationDetailsProvider)oci.getProvider());){
                ListGroupsResponse listResp;
                Groups wrapper;
                dc.setEndpoint(url);
                int startIndex = 1;
                int pageSize = 100;
                while ((wrapper = (listResp = dc.listGroups(com.oracle.bmc.identitydomains.requests.ListGroupsRequest.builder().count(Integer.valueOf(100)).startIndex(Integer.valueOf(startIndex)).build())).getGroups()) != null && wrapper.getResources() != null) {
                    if (wrapper.getResources().isEmpty()) {
                        break;
                    }
                    for (Object raw : wrapper.getResources()) {
                        String name;
                        if (!(raw instanceof com.oracle.bmc.identitydomains.model.Group)) continue;
                        com.oracle.bmc.identitydomains.model.Group g = (com.oracle.bmc.identitydomains.model.Group)raw;
                        String string = name = g.getDisplayName() != null ? g.getDisplayName() : g.getId();
                        if (UserManagementService.isHiddenDomainGroupName((String)name)) continue;
                        LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
                        map.put("id", g.getId());
                        map.put("name", name);
                        map.put("description", null);
                        result.add(map);
                    }
                    if (wrapper.getResources().size() < 100) {
                        break;
                    }
                    startIndex += 100;
                }
            }
            ArrayList<Map<String, Object>> arrayList = result;
            return arrayList;
        }
    }

    private List<Map<String, Object>> listGroupsFiltered(String tenantId) {
        List all = this.listGroups(tenantId);
        ArrayList<Map<String, Object>> filtered = new ArrayList<Map<String, Object>>();
        for (Map g : all) {
            String name = g.get("name") == null ? null : String.valueOf(g.get("name"));
            if (UserManagementService.isHiddenDomainGroupName((String)name)) continue;
            filtered.add(g);
        }
        return filtered;
    }

    private static String headerValueIgnoreCase(BmcResponse resp, String name) {
        if (resp.getHeaders() == null) {
            return null;
        }
        for (Map.Entry e : resp.getHeaders().entrySet()) {
            if (!name.equalsIgnoreCase((String)e.getKey()) || e.getValue() == null || ((List)e.getValue()).isEmpty()) continue;
            return (String)((List)e.getValue()).get(0);
        }
        return null;
    }

    public void resetPassword(UserParams params) {
        this.resetPasswordWithResult(params);
    }

    public String getResetPasswordResult(UserParams params) {
        return this.resetPasswordWithResult(params);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private String resetPasswordWithResult(UserParams params) {
        OciUser tenant = this.getTenant(params.getTenantId());
        String userId = params.getUserId();
        if (StrUtil.isBlank((CharSequence)userId)) {
            throw new OciException("userId \u4e0d\u80fd\u4e3a\u7a7a");
        }
        try (IdentityClient client = this.buildClient(tenant);){
            CreateOrResetUIPasswordResponse response = client.createOrResetUIPassword(CreateOrResetUIPasswordRequest.builder().userId(userId).build());
            if (response.getUIPassword() == null) return this.resetPasswordViaIdentityDomains(tenant, userId);
            if (!StrUtil.isNotBlank((CharSequence)response.getUIPassword().getPassword())) return this.resetPasswordViaIdentityDomains(tenant, userId);
            log.info("Password reset (classic IAM) for user: {}", (Object)userId);
            String string = response.getUIPassword().getPassword();
            return string;
        }
        catch (BmcException e) {
            int code = e.getStatusCode();
            if (code != 404 && code != 400) {
                throw e;
            }
            log.info("Classic CreateOrResetUIPassword returned {}, trying Identity Domains API for user {}", (Object)code, (Object)userId);
        }
        return this.resetPasswordViaIdentityDomains(tenant, userId);
    }

    private static String generateAdminResetPassword() {
        String chars = "ABCDEFGHJKMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789";
        ThreadLocalRandom r = ThreadLocalRandom.current();
        StringBuilder sb = new StringBuilder(16);
        for (int i = 0; i < 16; ++i) {
            sb.append(chars.charAt(r.nextInt(chars.length())));
        }
        return sb.toString();
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private String resetPasswordViaIdentityDomains(OciUser tenant, String classicUserOcid) {
        String newPassword = UserManagementService.generateAdminResetPassword();
        try (OciClientService oci = this.domainManagementService.openOciClient(tenant.getId());){
            List domains = this.domainManagementService.listDomains(oci, false);
            if (domains.isEmpty()) {
                throw new OciException("\u672a\u627e\u5230 Identity Domain\uff0c\u65e0\u6cd5\u901a\u8fc7\u57df API \u91cd\u7f6e\u5bc6\u7801");
            }
            UserPasswordChanger body = UserPasswordChanger.builder().schemas(List.of("urn:ietf:params:scim:schemas:oracle:idcs:UserPasswordChanger")).password(newPassword).bypassNotification(Boolean.valueOf(true)).build();
            for (Map d : domains) {
                String string;
                String scimUserId;
                IdentityDomainsClient dc;
                String domainLabel;
                block24: {
                    com.oracle.bmc.identitydomains.model.User domainUser;
                    block23: {
                        Users wrapper;
                        block22: {
                            String url = (String)d.get("url");
                            if (StrUtil.isBlank((CharSequence)url)) continue;
                            domainLabel = String.valueOf(d.get("displayName"));
                            dc = IdentityDomainsClient.builder().build((AbstractAuthenticationDetailsProvider)oci.getProvider());
                            dc.setEndpoint(url);
                            com.oracle.bmc.identitydomains.responses.ListUsersResponse listResp = dc.listUsers(com.oracle.bmc.identitydomains.requests.ListUsersRequest.builder().filter("ocid eq \"" + classicUserOcid + "\"").attributes("id,ocid,userName").count(Integer.valueOf(10)).build());
                            wrapper = listResp.getUsers();
                            if (wrapper != null && wrapper.getResources() != null && !wrapper.getResources().isEmpty()) break block22;
                            if (dc == null) continue;
                            dc.close();
                            continue;
                        }
                        Object raw = wrapper.getResources().get(0);
                        if (raw instanceof com.oracle.bmc.identitydomains.model.User) {
                            domainUser = (com.oracle.bmc.identitydomains.model.User)raw;
                            break block23;
                        }
                        if (dc == null) continue;
                        dc.close();
                        continue;
                    }
                    scimUserId = domainUser.getId();
                    if (!StrUtil.isBlank((CharSequence)scimUserId)) break block24;
                    if (dc == null) continue;
                    dc.close();
                    continue;
                }
                try {
                    try {
                        dc.putUserPasswordChanger(PutUserPasswordChangerRequest.builder().userPasswordChangerId(scimUserId).userPasswordChanger(body).build());
                    }
                    catch (BmcException ex) {
                        int sc = ex.getStatusCode();
                        if (sc != 401 && sc != 403) throw ex;
                        throw new OciException("\u91cd\u7f6e\u5bc6\u7801\u5931\u8d25\uff1aAPI Key \u5bf9\u5e94\u7528\u6237\u5728\u57df\u300c" + domainLabel + "\u300d\u4e2d\u6743\u9650\u4e0d\u8db3\u3002\u8bf7\u5728 OCI \u63a7\u5236\u53f0\uff1a\u8eab\u4efd\u4e0e\u5b89\u5168 \u2192 \u57df \u2192 \u8be5\u57df \u2192 \u7ba1\u7406\u5458 \u2192 \u5c06\u300c\u7528\u6237\u7ba1\u7406\u5458(User Administrator)\u300d\u6388\u4e88\u5f53\u524d API Key \u6240\u5c5e\u7528\u6237\u540e\u91cd\u8bd5\u3002");
                    }
                    log.info("Password reset (Identity Domains) domain={} userOcid={}", (Object)domainLabel, (Object)classicUserOcid);
                    string = newPassword;
                    if (dc == null) return string;
                }
                catch (Throwable throwable) {
                    try {
                        try {
                            if (dc == null) throw throwable;
                            try {
                                dc.close();
                                throw throwable;
                            }
                            catch (Throwable throwable2) {
                                throwable.addSuppressed(throwable2);
                            }
                            throw throwable;
                        }
                        catch (BmcException ex) {
                            if (ex.getStatusCode() != 404) throw ex;
                            log.debug("User OCID not in domain {}: {}", (Object)domainLabel, (Object)ex.getMessage());
                            continue;
                        }
                    }
                    catch (Throwable throwable3) {
                        throw throwable3;
                        throw new OciException("\u5728\u4efb\u4e00 Identity Domain \u4e2d\u672a\u627e\u5230\u8be5\u7528\u6237\uff08OCID\uff09\uff0c\u6216\u65e0\u6cd5\u5b8c\u6210\u5bc6\u7801\u91cd\u7f6e: " + classicUserOcid);
                    }
                }
                dc.close();
                return string;
            }
        }
    }

    public void clearMfa(UserParams params) {
        OciUser tenant = this.getTenant(params.getTenantId());
        try (IdentityClient client = this.buildClient(tenant);){
            ListMfaTotpDevicesResponse mfaResponse = client.listMfaTotpDevices(ListMfaTotpDevicesRequest.builder().userId(params.getUserId()).build());
            for (MfaTotpDeviceSummary device : mfaResponse.getItems()) {
                client.deleteMfaTotpDevice(DeleteMfaTotpDeviceRequest.builder().userId(params.getUserId()).mfaTotpDeviceId(device.getId()).build());
                log.info("Deleted MFA device: {} for user: {}", (Object)device.getId(), (Object)params.getUserId());
            }
        }
    }

    public List<Map<String, Object>> listGroups(String tenantId) {
        OciUser tenant = this.getTenant(tenantId);
        try (IdentityClient client = this.buildClient(tenant);){
            com.oracle.bmc.identity.responses.ListGroupsResponse response = client.listGroups(ListGroupsRequest.builder().compartmentId(tenant.getOciTenantId()).build());
            ArrayList result = new ArrayList();
            for (Group group : response.getItems()) {
                LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
                map.put("id", group.getId());
                map.put("name", group.getName());
                map.put("description", group.getDescription());
                map.put("state", group.getLifecycleState().getValue());
                result.add(map);
            }
            ArrayList arrayList = result;
            return arrayList;
        }
    }

    public void addUserToGroup(UserParams params) {
        OciUser tenant = this.getTenant(params.getTenantId());
        try (IdentityClient client = this.buildClient(tenant);){
            String adminGroupId = this.findAdminGroupId(client, tenant.getOciTenantId());
            if (adminGroupId == null) {
                throw new OciException("\u672a\u627e\u5230\u7ba1\u7406\u5458\u7ec4");
            }
            client.addUserToGroup(AddUserToGroupRequest.builder().addUserToGroupDetails(AddUserToGroupDetails.builder().userId(params.getUserId()).groupId(adminGroupId).build()).build());
            log.info("Added user {} to admin group", (Object)params.getUserId());
        }
    }

    public void removeUserFromGroup(UserParams params) {
        OciUser tenant = this.getTenant(params.getTenantId());
        try (IdentityClient client = this.buildClient(tenant);){
            String adminGroupId = this.findAdminGroupId(client, tenant.getOciTenantId());
            if (adminGroupId == null) {
                throw new OciException("\u672a\u627e\u5230\u7ba1\u7406\u5458\u7ec4");
            }
            ListUserGroupMembershipsResponse memberships = client.listUserGroupMemberships(ListUserGroupMembershipsRequest.builder().compartmentId(tenant.getOciTenantId()).userId(params.getUserId()).groupId(adminGroupId).build());
            for (UserGroupMembership membership : memberships.getItems()) {
                client.removeUserFromGroup(RemoveUserFromGroupRequest.builder().userGroupMembershipId(membership.getId()).build());
                log.info("Removed user {} from admin group", (Object)params.getUserId());
            }
        }
    }

    public List<String> getUserGroupNames(String tenantId, String userId) {
        return this.getUserGroups(tenantId, userId).stream().map(g -> g.get("name") == null ? null : String.valueOf(g.get("name"))).filter(CharSequenceUtil::isNotBlank).toList();
    }

    public List<Map<String, Object>> getUserGroups(String tenantId, String userId) {
        OciUser tenant = this.getTenant(tenantId);
        try (IdentityClient client = this.buildClient(tenant);){
            ListUserGroupMembershipsResponse memberships = client.listUserGroupMemberships(ListUserGroupMembershipsRequest.builder().compartmentId(tenant.getOciTenantId()).userId(userId).build());
            ArrayList<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
            if (memberships.getItems() == null) {
                ArrayList<Map<String, Object>> arrayList = result;
                return arrayList;
            }
            for (UserGroupMembership m : memberships.getItems()) {
                LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
                map.put("membershipId", m.getId());
                map.put("groupId", m.getGroupId());
                String name = m.getGroupId();
                try {
                    Group group = client.getGroup(GetGroupRequest.builder().groupId(m.getGroupId()).build()).getGroup();
                    if (group != null && StrUtil.isNotBlank((CharSequence)group.getName())) {
                        name = group.getName();
                    }
                }
                catch (Exception exception) {
                    // empty catch block
                }
                map.put("name", name);
                result.add(map);
            }
            ArrayList<Map<String, Object>> arrayList = result;
            return arrayList;
        }
    }

    public void syncUserGroups(String tenantId, String userId, List<String> targetGroupIds) {
        OciUser tenant = this.getTenant(tenantId);
        LinkedHashSet<String> target = new LinkedHashSet<String>();
        if (targetGroupIds != null) {
            for (String id : targetGroupIds) {
                if (!StrUtil.isNotBlank((CharSequence)id)) continue;
                target.add(id.trim());
            }
        }
        try (IdentityClient client = this.buildClient(tenant);){
            String compartmentId = tenant.getOciTenantId();
            ListUserGroupMembershipsResponse memberships = client.listUserGroupMemberships(ListUserGroupMembershipsRequest.builder().compartmentId(compartmentId).userId(userId).build());
            LinkedHashSet<String> currentGroupIds = new LinkedHashSet<String>();
            LinkedHashMap<String, String> membershipByGroupId = new LinkedHashMap<String, String>();
            if (memberships.getItems() != null) {
                for (UserGroupMembership m : memberships.getItems()) {
                    if (m.getGroupId() == null) continue;
                    currentGroupIds.add(m.getGroupId());
                    membershipByGroupId.put(m.getGroupId(), m.getId());
                }
            }
            for (String groupId : currentGroupIds) {
                String membershipId;
                if (target.contains(groupId) || !StrUtil.isNotBlank((CharSequence)(membershipId = (String)membershipByGroupId.get(groupId)))) continue;
                client.removeUserFromGroup(RemoveUserFromGroupRequest.builder().userGroupMembershipId(membershipId).build());
                log.info("Removed user {} from group {}", (Object)userId, (Object)groupId);
            }
            for (String groupId : target) {
                if (currentGroupIds.contains(groupId)) continue;
                client.addUserToGroup(AddUserToGroupRequest.builder().addUserToGroupDetails(AddUserToGroupDetails.builder().userId(userId).groupId(groupId).build()).build());
                log.info("Added user {} to group {}", (Object)userId, (Object)groupId);
            }
        }
    }

    private void addToAdminGroup(IdentityClient client, String compartmentId, String userId) {
        String adminGroupId = this.findAdminGroupId(client, compartmentId);
        if (adminGroupId == null) {
            log.warn("Admin group not found, skipping group assignment");
            return;
        }
        try {
            client.addUserToGroup(AddUserToGroupRequest.builder().addUserToGroupDetails(AddUserToGroupDetails.builder().userId(userId).groupId(adminGroupId).build()).build());
        }
        catch (Exception e) {
            log.warn("Failed to add user to admin group: {}", (Object)e.getMessage());
        }
    }

    public void updateUser(UserParams params) {
        OciUser tenant = this.getTenant(params.getTenantId());
        try (IdentityClient client = this.buildClient(tenant);){
            UpdateUserDetails.Builder builder = UpdateUserDetails.builder();
            if (StrUtil.isNotBlank((CharSequence)params.getEmail())) {
                builder.email(params.getEmail());
            }
            if (StrUtil.isNotBlank((CharSequence)params.getUserName())) {
                builder.description(params.getUserName());
            }
            client.updateUser(UpdateUserRequest.builder().userId(params.getUserId()).updateUserDetails(builder.build()).build());
            log.info("Updated user: {}", (Object)params.getUserId());
        }
    }

    public Map<String, Object> getUserCapabilities(String tenantId, String userId) {
        OciUser tenant = this.getTenant(tenantId);
        try (IdentityClient client = this.buildClient(tenant);){
            User user = client.getUser(GetUserRequest.builder().userId(userId).build()).getUser();
            Map map = UserManagementService.capabilitiesToMap((UserCapabilities)(user == null ? null : user.getCapabilities()));
            return map;
        }
    }

    public void updateUserCapabilities(UserParams params) {
        OciUser tenant = this.getTenant(params.getTenantId());
        Map caps = params.getCapabilities();
        if (caps == null || caps.isEmpty()) {
            throw new OciException("\u8bf7\u81f3\u5c11\u6307\u5b9a\u4e00\u9879\u7528\u6237\u6743\u9650");
        }
        UpdateUserCapabilitiesDetails.Builder builder = UpdateUserCapabilitiesDetails.builder();
        if (caps.containsKey("canUseConsolePassword")) {
            builder.canUseConsolePassword((Boolean)caps.get("canUseConsolePassword"));
        }
        if (caps.containsKey("canUseApiKeys")) {
            builder.canUseApiKeys((Boolean)caps.get("canUseApiKeys"));
        }
        if (caps.containsKey("canUseAuthTokens")) {
            builder.canUseAuthTokens((Boolean)caps.get("canUseAuthTokens"));
        }
        if (caps.containsKey("canUseSmtpCredentials")) {
            builder.canUseSmtpCredentials((Boolean)caps.get("canUseSmtpCredentials"));
        }
        if (caps.containsKey("canUseDbCredentials")) {
            builder.canUseDBCredentials((Boolean)caps.get("canUseDbCredentials"));
        }
        if (caps.containsKey("canUseCustomerSecretKeys")) {
            builder.canUseCustomerSecretKeys((Boolean)caps.get("canUseCustomerSecretKeys"));
        }
        if (caps.containsKey("canUseOAuth2ClientCredentials")) {
            builder.canUseOAuth2ClientCredentials((Boolean)caps.get("canUseOAuth2ClientCredentials"));
        }
        try (IdentityClient client = this.buildClient(tenant);){
            client.updateUserCapabilities(UpdateUserCapabilitiesRequest.builder().userId(params.getUserId()).updateUserCapabilitiesDetails(builder.build()).build());
            log.info("Updated user capabilities: {}", (Object)params.getUserId());
        }
    }

    private static Map<String, Object> capabilitiesToMap(UserCapabilities caps) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
        for (String key : CAPABILITY_KEYS) {
            map.put(key, UserManagementService.capabilityValue((UserCapabilities)caps, (String)key));
        }
        return map;
    }

    private static boolean capabilityValue(UserCapabilities caps, String key) {
        if (caps == null) {
            return false;
        }
        Boolean v = switch (key) {
            case "canUseConsolePassword" -> caps.getCanUseConsolePassword();
            case "canUseApiKeys" -> caps.getCanUseApiKeys();
            case "canUseAuthTokens" -> caps.getCanUseAuthTokens();
            case "canUseSmtpCredentials" -> caps.getCanUseSmtpCredentials();
            case "canUseDbCredentials" -> caps.getCanUseDbCredentials();
            case "canUseCustomerSecretKeys" -> caps.getCanUseCustomerSecretKeys();
            case "canUseOAuth2ClientCredentials" -> caps.getCanUseOAuth2ClientCredentials();
            default -> null;
        };
        return Boolean.TRUE.equals(v);
    }

    public static Map<String, Boolean> parseCapabilitiesMap(Object raw) {
        if (!(raw instanceof Map)) {
            return Map.of();
        }
        Map m = (Map)raw;
        LinkedHashMap<String, Boolean> out = new LinkedHashMap<String, Boolean>();
        for (String key : CAPABILITY_KEYS) {
            if (!m.containsKey(key)) continue;
            out.put(key, Boolean.TRUE.equals(m.get(key)));
        }
        return out;
    }

    public void updateUserState(String tenantId, String userId, boolean blocked) {
        OciUser tenant = this.getTenant(tenantId);
        try (IdentityClient client = this.buildClient(tenant);){
            client.updateUserState(UpdateUserStateRequest.builder().userId(userId).updateStateDetails(UpdateStateDetails.builder().blocked(Boolean.valueOf(blocked)).build()).build());
            log.info("User {} state updated, blocked={}", (Object)userId, (Object)blocked);
        }
    }

    public List<Map<String, Object>> listMfaDevices(String tenantId, String userId) {
        OciUser tenant = this.getTenant(tenantId);
        try (IdentityClient client = this.buildClient(tenant);){
            ListMfaTotpDevicesResponse response = client.listMfaTotpDevices(ListMfaTotpDevicesRequest.builder().userId(userId).build());
            ArrayList result = new ArrayList();
            for (MfaTotpDeviceSummary device : response.getItems()) {
                LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
                map.put("id", device.getId());
                map.put("state", device.getLifecycleState().getValue());
                map.put("isActivated", device.getIsActivated());
                map.put("timeCreated", device.getTimeCreated() != null ? device.getTimeCreated().toString() : null);
                result.add(map);
            }
            ArrayList arrayList = result;
            return arrayList;
        }
    }

    private String findAdminGroupId(IdentityClient client, String compartmentId) {
        com.oracle.bmc.identity.responses.ListGroupsResponse response = client.listGroups(ListGroupsRequest.builder().compartmentId(compartmentId).build());
        for (Group group : response.getItems()) {
            if (!"Administrators".equalsIgnoreCase(group.getName())) continue;
            return group.getId();
        }
        return null;
    }
}

