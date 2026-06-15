package com.ocxworker.service;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import com.ocxworker.exception.OciException;
import com.ocxworker.mapper.OciUserMapper;
import com.ocxworker.model.entity.OciUser;
import com.ocxworker.model.params.UserParams;
import com.oracle.bmc.Region;
import com.oracle.bmc.auth.SimpleAuthenticationDetailsProvider;
import com.oracle.bmc.identity.IdentityClient;
import com.oracle.bmc.identity.model.AddUserToGroupDetails;
import com.oracle.bmc.identity.model.CreateUserDetails;
import com.oracle.bmc.identity.model.MfaTotpDeviceSummary;
import com.oracle.bmc.identity.model.UpdateStateDetails;
import com.oracle.bmc.identity.model.UpdateUserCapabilitiesDetails;
import com.oracle.bmc.identity.model.UpdateUserDetails;
import com.oracle.bmc.identity.model.User;
import com.oracle.bmc.identity.model.UserCapabilities;
import com.oracle.bmc.identity.model.UserGroupMembership;
import com.oracle.bmc.identity.model.CreateUserDetails.Builder;
import com.oracle.bmc.identity.requests.AddUserToGroupRequest;
import com.oracle.bmc.identity.requests.CreateOrResetUIPasswordRequest;
import com.oracle.bmc.identity.requests.CreateUserRequest;
import com.oracle.bmc.identity.requests.DeleteMfaTotpDeviceRequest;
import com.oracle.bmc.identity.requests.GetUserRequest;
import com.oracle.bmc.identity.requests.ListMfaTotpDevicesRequest;
import com.oracle.bmc.identity.requests.ListUserGroupMembershipsRequest;
import com.oracle.bmc.identity.requests.ListUsersRequest;
import com.oracle.bmc.identity.requests.RemoveUserFromGroupRequest;
import com.oracle.bmc.identity.requests.UpdateUserCapabilitiesRequest;
import com.oracle.bmc.identity.requests.UpdateUserRequest;
import com.oracle.bmc.identity.requests.UpdateUserStateRequest;
import com.oracle.bmc.identity.responses.CreateOrResetUIPasswordResponse;
import com.oracle.bmc.identity.responses.CreateUserResponse;
import com.oracle.bmc.identity.responses.ListMfaTotpDevicesResponse;
import com.oracle.bmc.identity.responses.ListUserGroupMembershipsResponse;
import com.oracle.bmc.identity.responses.ListUsersResponse;
import com.oracle.bmc.identitydomains.IdentityDomainsClient;
import com.oracle.bmc.identitydomains.model.Group;
import com.oracle.bmc.identitydomains.model.Groups;
import com.oracle.bmc.identitydomains.model.Operations;
import com.oracle.bmc.identitydomains.model.PatchOp;
import com.oracle.bmc.identitydomains.model.UserEmails;
import com.oracle.bmc.identitydomains.model.UserName;
import com.oracle.bmc.identitydomains.model.UserPasswordChanger;
import com.oracle.bmc.identitydomains.model.Users;
import com.oracle.bmc.identitydomains.model.Operations.Op;
import com.oracle.bmc.identitydomains.model.UserEmails.Type;
import com.oracle.bmc.identitydomains.requests.GetGroupRequest;
import com.oracle.bmc.identitydomains.requests.ListGroupsRequest;
import com.oracle.bmc.identitydomains.requests.PatchGroupRequest;
import com.oracle.bmc.identitydomains.requests.PutUserPasswordChangerRequest;
import com.oracle.bmc.identitydomains.responses.GetGroupResponse;
import com.oracle.bmc.identitydomains.responses.ListGroupsResponse;
import com.oracle.bmc.model.BmcException;
import com.oracle.bmc.responses.BmcResponse;
import jakarta.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UserManagementService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(UserManagementService.class);
    private static final String SCIM_SCHEMA_USER = "urn:ietf:params:scim:schemas:core:2.0:User";
    private static final String SCIM_SCHEMA_PATCH_OP = "urn:ietf:params:scim:api:messages:2.0:PatchOp";
    private static final String SCHEMA_USER_PASSWORD_CHANGER = "urn:ietf:params:scim:schemas:oracle:idcs:UserPasswordChanger";
    public static final List<String> CAPABILITY_KEYS = List.of(
        "canUseConsolePassword",
        "canUseApiKeys",
        "canUseAuthTokens",
        "canUseSmtpCredentials",
        "canUseDbCredentials",
        "canUseCustomerSecretKeys",
        "canUseOAuth2ClientCredentials"
    );
    @Resource
    private OciUserMapper userMapper;
    @Resource
    private DomainManagementService domainManagementService;

    private IdentityClient buildClient(OciUser tenant) {
        SimpleAuthenticationDetailsProvider provider = SimpleAuthenticationDetailsProvider.builder()
            .tenantId(tenant.getOciTenantId())
            .userId(tenant.getOciUserId())
            .fingerprint(tenant.getOciFingerprint())
            .privateKeySupplier(() -> {
                try {
                    ByteArrayInputStream var5;
                    try (
                        FileInputStream fis = new FileInputStream(tenant.getOciKeyPath());
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ) {
                        byte[] buffer = new byte[1024];

                        int bytesRead;
                        while ((bytesRead = fis.read(buffer)) != -1) {
                            baos.write(buffer, 0, bytesRead);
                        }

                        var5 = new ByteArrayInputStream(baos.toByteArray());
                    }

                    return var5;
                } catch (Exception var10) {
                    throw new RuntimeException("Failed to read private key: " + var10.getMessage());
                }
            })
            .region(Region.valueOf(tenant.getOciRegion()))
            .build();
        return IdentityClient.builder().build(provider);
    }

    private OciUser getTenant(String tenantId) {
        OciUser tenant = (OciUser)this.userMapper.selectById(tenantId);
        if (tenant == null) {
            throw new OciException("租户不存在");
        } else {
            return tenant;
        }
    }

    public List<Map<String, Object>> listUsers(String tenantId) {
        OciUser tenant = this.getTenant(tenantId);
        IdentityClient client = this.buildClient(tenant);

        Object var14;
        try {
            ListUsersResponse response = client.listUsers(ListUsersRequest.builder().compartmentId(tenant.getOciTenantId()).build());
            List<Map<String, Object>> result = new ArrayList<>();

            for (User user : response.getItems()) {
                Map<String, Object> map = new LinkedHashMap<>();
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
                } catch (Exception var12) {
                }

                map.put("isMfaActivated", hasMfa);
                result.add(map);
            }

            var14 = result;
        } catch (Throwable var13) {
            if (client != null) {
                try {
                    client.close();
                } catch (Throwable var11) {
                    var13.addSuppressed(var11);
                }
            }

            throw var13;
        }

        if (client != null) {
            client.close();
        }

        return (List<Map<String, Object>>)var14;
    }

    public Map<String, Object> createUser(UserParams params) {
        OciUser tenant = this.getTenant(params.getTenantId());
        String domainId = StrUtil.trimToNull(params.getDomainId());
        if (domainId == null) {
            return this.createUserViaIamApi(tenant, params);
        } else {
            Map var11;
            try (OciClientService oci = this.domainManagementService.openOciClient(tenant.getId())) {
                List<Map<String, Object>> domains = this.domainManagementService.listDomains(oci, false);
                Map<String, Object> selected = null;

                for (Map<String, Object> d : domains) {
                    if (domainId.equals(d.get("id"))) {
                        selected = d;
                        break;
                    }
                }

                if (selected == null) {
                    throw new OciException("未找到指定的 Identity Domain，请刷新域列表后重试");
                }

                if (isDefaultDomainForClassicIam(selected)) {
                    return this.createUserViaIamApi(tenant, params);
                }

                var11 = this.createUserViaIdentityDomainsApi(oci, selected, params, tenant);
            }

            return var11;
        }
    }

    private Map<String, Object> createUserViaIamApi(OciUser tenant, UserParams params) {
        IdentityClient client = this.buildClient(tenant);

        Object var8;
        try {
            Builder builder = CreateUserDetails.builder()
                .compartmentId(tenant.getOciTenantId())
                .name(params.getUserName())
                .description(params.getEmail() != null ? params.getEmail() : params.getUserName())
                .email(params.getEmail());
            CreateUserResponse response = client.createUser(CreateUserRequest.builder().createUserDetails(builder.build()).build());
            User created = response.getUser();
            log.info("Created user (IAM API): {} in tenant: {}", created.getName(), tenant.getUsername());
            if (Boolean.TRUE.equals(params.getAddToAdminGroup())) {
                this.addToAdminGroup(client, tenant.getOciTenantId(), created.getId());
            }

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("id", created.getId());
            result.put("name", created.getName());
            result.put("email", created.getEmail());
            result.put("state", created.getLifecycleState().getValue());
            var8 = result;
        } catch (Throwable var10) {
            if (client != null) {
                try {
                    client.close();
                } catch (Throwable var9) {
                    var10.addSuppressed(var9);
                }
            }

            throw var10;
        }

        if (client != null) {
            client.close();
        }

        return (Map<String, Object>)var8;
    }

    private static boolean isDefaultDomainForClassicIam(Map<String, Object> domain) {
        return "Default".equals(String.valueOf(domain.get("displayName")));
    }

    private Map<String, Object> createUserViaIdentityDomainsApi(OciClientService oci, Map<String, Object> domain, UserParams params, OciUser tenant) {
        String url = (String)domain.get("url");
        if (StrUtil.isBlank(url)) {
            throw new OciException("该 Identity Domain 缺少 URL，无法创建用户");
        } else {
            String loginName = StrUtil.blankToDefault(StrUtil.trim(params.getUserName()), "User");
            UserName scimName = UserName.builder().formatted(loginName).givenName(loginName).familyName(loginName).build();
            com.oracle.bmc.identitydomains.model.User.Builder ub = com.oracle.bmc.identitydomains.model.User.builder()
                .schemas(List.of("urn:ietf:params:scim:schemas:core:2.0:User"))
                .userName(loginName)
                .name(scimName)
                .active(Boolean.TRUE)
                .description(StrUtil.isNotBlank(params.getEmail()) ? params.getEmail() : loginName);
            if (StrUtil.isNotBlank(params.getEmail())) {
                ub.emails(List.of(UserEmails.builder().value(params.getEmail()).type(Type.Work).primary(true).build()));
            }

            com.oracle.bmc.identitydomains.model.User scimUser = ub.build();
            IdentityDomainsClient dc = IdentityDomainsClient.builder().build(oci.getProvider());

            Object var15;
            try {
                dc.setEndpoint(url);
                com.oracle.bmc.identitydomains.responses.CreateUserResponse response = dc.createUser(
                    com.oracle.bmc.identitydomains.requests.CreateUserRequest.builder().user(scimUser).build()
                );
                com.oracle.bmc.identitydomains.model.User created = response.getUser();
                log.info(
                    "Created user (Identity Domains API): {} in domain {} tenant {}",
                    new Object[]{created.getUserName(), domain.get("displayName"), tenant.getUsername()}
                );
                if (created.getId() != null) {
                    this.applyIdentityDomainGroupAssignments(dc, created.getId(), params);
                }

                Map<String, Object> result = new LinkedHashMap<>();
                String id = StrUtil.isNotBlank(created.getOcid()) ? created.getOcid() : created.getId();
                result.put("id", id);
                result.put("name", created.getUserName());
                result.put("email", firstEmailValue(created, params.getEmail()));
                result.put("state", Boolean.FALSE.equals(created.getActive()) ? "INACTIVE" : "ACTIVE");
                var15 = result;
            } catch (Throwable var17) {
                if (dc != null) {
                    try {
                        dc.close();
                    } catch (Throwable var16) {
                        var17.addSuppressed(var16);
                    }
                }

                throw var17;
            }

            if (dc != null) {
                dc.close();
            }

            return (Map<String, Object>)var15;
        }
    }

    private static String firstEmailValue(com.oracle.bmc.identitydomains.model.User u, String fallback) {
        if (u.getEmails() != null) {
            for (Object o : u.getEmails()) {
                if (o instanceof UserEmails ue && StrUtil.isNotBlank(ue.getValue())) {
                    return ue.getValue();
                }

                if (o instanceof Map<?, ?> m && m.get("value") != null) {
                    return String.valueOf(m.get("value"));
                }
            }
        }

        return fallback;
    }

    private void applyIdentityDomainGroupAssignments(IdentityDomainsClient dc, String userScimId, UserParams params) {
        List<String> groupIds = params.getGroupIds();
        if (groupIds != null && !groupIds.isEmpty()) {
            for (String groupId : groupIds) {
                if (StrUtil.isNotBlank(groupId)) {
                    this.addUserToGroupIdentityDomains(dc, userScimId, groupId.trim());
                }
            }
        } else {
            if (Boolean.TRUE.equals(params.getAddToAdminGroup())) {
                this.addUserToAdministratorsGroupIdentityDomains(dc, userScimId);
            }
        }
    }

    private void addUserToAdministratorsGroupIdentityDomains(IdentityDomainsClient dc, String userScimId) {
        try {
            ListGroupsResponse listResp = dc.listGroups(ListGroupsRequest.builder().filter("displayName eq \"Administrators\"").count(50).build());
            Groups groups = listResp.getGroups();
            if (groups == null || groups.getResources() == null || groups.getResources().isEmpty()) {
                log.warn("Administrators group not found in identity domain, skip addToAdminGroup");
                return;
            }

            if (!(groups.getResources().get(0) instanceof Group adminGroup)) {
                log.warn("Unexpected group resource type, skip addToAdminGroup");
                return;
            }

            String groupId = adminGroup.getId();
            if (StrUtil.isBlank(groupId)) {
                return;
            }

            this.addUserToGroupIdentityDomains(dc, userScimId, groupId);
        } catch (Exception var8) {
            log.warn("Failed to add user to Administrators in identity domain: {}", var8.getMessage());
        }
    }

    private void addUserToGroupIdentityDomains(IdentityDomainsClient dc, String userScimId, String groupId) {
        try {
            GetGroupResponse getResp = dc.getGroup(GetGroupRequest.builder().groupId(groupId).build());
            String ifMatch = headerValueIgnoreCase(getResp, "etag");
            Map<String, Object> member = new LinkedHashMap<>();
            member.put("value", userScimId);
            member.put("type", "User");
            PatchOp patchOp = PatchOp.builder()
                .schemas(List.of("urn:ietf:params:scim:api:messages:2.0:PatchOp"))
                .operations(List.of(Operations.builder().op(Op.Add).path("members").value(List.of(member)).build()))
                .build();
            com.oracle.bmc.identitydomains.requests.PatchGroupRequest.Builder pr = PatchGroupRequest.builder().groupId(groupId).patchOp(patchOp);
            if (StrUtil.isNotBlank(ifMatch)) {
                pr.ifMatch(ifMatch);
            }

            dc.patchGroup(pr.build());
            log.info("Added user {} to identity domain group {}", userScimId, groupId);
        } catch (Exception var9) {
            log.warn("Failed to add user {} to identity domain group {}: {}", new Object[]{userScimId, groupId, var9.getMessage()});
        }
    }

    private static boolean isHiddenDomainGroupName(String name) {
        if (StrUtil.isBlank(name)) {
            return false;
        } else {
            String n = name.trim().toLowerCase();
            return "all domain users".equals(n);
        }
    }

    public List<Map<String, Object>> listDomainGroups(String tenantId, String domainId) {
        if (StrUtil.isBlank(domainId)) {
            return this.listGroupsFiltered(tenantId);
        } else {
            Object var25;
            try (OciClientService oci = this.domainManagementService.openOciClient(tenantId)) {
                List<Map<String, Object>> domains = this.domainManagementService.listDomains(oci, false);
                Map<String, Object> selected = null;

                for (Map<String, Object> d : domains) {
                    if (domainId.equals(d.get("id"))) {
                        selected = d;
                        break;
                    }
                }

                if (selected == null) {
                    throw new OciException("未找到指定的 Identity Domain");
                }

                if (isDefaultDomainForClassicIam(selected)) {
                    return this.listGroupsFiltered(tenantId);
                }

                String url = (String)selected.get("url");
                if (StrUtil.isBlank(url)) {
                    throw new OciException("该 Identity Domain 缺少 URL，无法列出组");
                }

                List<Map<String, Object>> result = new ArrayList<>();
                IdentityDomainsClient dc = IdentityDomainsClient.builder().build(oci.getProvider());

                try {
                    dc.setEndpoint(url);
                    int startIndex = 1;
                    int pageSize = 100;

                    while (true) {
                        ListGroupsResponse listResp = dc.listGroups(ListGroupsRequest.builder().count(100).startIndex(startIndex).build());
                        Groups wrapper = listResp.getGroups();
                        if (wrapper == null || wrapper.getResources() == null || wrapper.getResources().isEmpty()) {
                            break;
                        }

                        for (Object raw : wrapper.getResources()) {
                            if (raw instanceof Group) {
                                Group g = (Group)raw;
                                String name = g.getDisplayName() != null ? g.getDisplayName() : g.getId();
                                if (!isHiddenDomainGroupName(name)) {
                                    Map<String, Object> map = new LinkedHashMap<>();
                                    map.put("id", g.getId());
                                    map.put("name", name);
                                    map.put("description", null);
                                    result.add(map);
                                }
                            }
                        }

                        if (wrapper.getResources().size() < 100) {
                            break;
                        }

                        startIndex += 100;
                    }
                } catch (Throwable var20) {
                    if (dc != null) {
                        try {
                            dc.close();
                        } catch (Throwable var19) {
                            var20.addSuppressed(var19);
                        }
                    }

                    throw var20;
                }

                if (dc != null) {
                    dc.close();
                }

                var25 = result;
            }

            return (List<Map<String, Object>>)var25;
        }
    }

    private List<Map<String, Object>> listGroupsFiltered(String tenantId) {
        List<Map<String, Object>> all = this.listGroups(tenantId);
        List<Map<String, Object>> filtered = new ArrayList<>();

        for (Map<String, Object> g : all) {
            String name = g.get("name") == null ? null : String.valueOf(g.get("name"));
            if (!isHiddenDomainGroupName(name)) {
                filtered.add(g);
            }
        }

        return filtered;
    }

    private static String headerValueIgnoreCase(BmcResponse resp, String name) {
        if (resp.getHeaders() == null) {
            return null;
        } else {
            for (Entry<String, List<String>> e : resp.getHeaders().entrySet()) {
                if (name.equalsIgnoreCase(e.getKey()) && e.getValue() != null && !e.getValue().isEmpty()) {
                    return e.getValue().get(0);
                }
            }

            return null;
        }
    }

    public void resetPassword(UserParams params) {
        this.resetPasswordWithResult(params);
    }

    public String getResetPasswordResult(UserParams params) {
        return this.resetPasswordWithResult(params);
    }

    private String resetPasswordWithResult(UserParams params) {
        OciUser tenant = this.getTenant(params.getTenantId());
        String userId = params.getUserId();
        if (StrUtil.isBlank(userId)) {
            throw new OciException("userId 不能为空");
        } else {
            try {
                IdentityClient client = this.buildClient(tenant);

                String var6;
                label67: {
                    try {
                        CreateOrResetUIPasswordResponse response = client.createOrResetUIPassword(
                            CreateOrResetUIPasswordRequest.builder().userId(userId).build()
                        );
                        if (response.getUIPassword() != null && StrUtil.isNotBlank(response.getUIPassword().getPassword())) {
                            log.info("Password reset (classic IAM) for user: {}", userId);
                            var6 = response.getUIPassword().getPassword();
                            break label67;
                        }
                    } catch (Throwable var8) {
                        if (client != null) {
                            try {
                                client.close();
                            } catch (Throwable var7) {
                                var8.addSuppressed(var7);
                            }
                        }

                        throw var8;
                    }

                    if (client != null) {
                        client.close();
                    }

                    return this.resetPasswordViaIdentityDomains(tenant, userId);
                }

                if (client != null) {
                    client.close();
                }

                return var6;
            } catch (BmcException var9) {
                int code = var9.getStatusCode();
                if (code != 404 && code != 400) {
                    throw var9;
                } else {
                    log.info("Classic CreateOrResetUIPassword returned {}, trying Identity Domains API for user {}", code, userId);
                    return this.resetPasswordViaIdentityDomains(tenant, userId);
                }
            }
        }
    }

    private static String generateAdminResetPassword() {
        String chars = "ABCDEFGHJKMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789";
        ThreadLocalRandom r = ThreadLocalRandom.current();
        StringBuilder sb = new StringBuilder(16);

        for (int i = 0; i < 16; i++) {
            sb.append(chars.charAt(r.nextInt(chars.length())));
        }

        return sb.toString();
    }

    private String resetPasswordViaIdentityDomains(OciUser tenant, String classicUserOcid) {
        String newPassword = generateAdminResetPassword();

        try (OciClientService oci = this.domainManagementService.openOciClient(tenant.getId())) {
            List<Map<String, Object>> domains = this.domainManagementService.listDomains(oci, false);
            if (domains.isEmpty()) {
                throw new OciException("未找到 Identity Domain，无法通过域 API 重置密码");
            }

            UserPasswordChanger body = UserPasswordChanger.builder()
                .schemas(List.of("urn:ietf:params:scim:schemas:oracle:idcs:UserPasswordChanger"))
                .password(newPassword)
                .bypassNotification(true)
                .build();

            for (Map<String, Object> d : domains) {
                String url = (String)d.get("url");
                if (!StrUtil.isBlank(url)) {
                    String domainLabel = String.valueOf(d.get("displayName"));

                    try {
                        IdentityDomainsClient dc = IdentityDomainsClient.builder().build(oci.getProvider());

                        label128: {
                            label127: {
                                label158: {
                                    String ex;
                                    try {
                                        dc.setEndpoint(url);
                                        com.oracle.bmc.identitydomains.responses.ListUsersResponse listResp = dc.listUsers(
                                            com.oracle.bmc.identitydomains.requests.ListUsersRequest.builder()
                                                .filter("ocid eq \"" + classicUserOcid + "\"")
                                                .attributes("id,ocid,userName")
                                                .count(10)
                                                .build()
                                        );
                                        Users wrapper = listResp.getUsers();
                                        if (wrapper == null || wrapper.getResources() == null || wrapper.getResources().isEmpty()) {
                                            break label158;
                                        }

                                        if (!(wrapper.getResources().get(0) instanceof com.oracle.bmc.identitydomains.model.User domainUser)) {
                                            break label127;
                                        }

                                        String scimUserId = domainUser.getId();
                                        if (StrUtil.isBlank(scimUserId)) {
                                            break label128;
                                        }

                                        try {
                                            dc.putUserPasswordChanger(
                                                PutUserPasswordChangerRequest.builder().userPasswordChangerId(scimUserId).userPasswordChanger(body).build()
                                            );
                                        } catch (BmcException var21) {
                                            int sc = var21.getStatusCode();
                                            if (sc != 401 && sc != 403) {
                                                throw var21;
                                            }

                                            throw new OciException(
                                                "重置密码失败：API Key 对应用户在域「"
                                                    + domainLabel
                                                    + "」中权限不足。请在 OCI 控制台：身份与安全 → 域 → 该域 → 管理员 → 将「用户管理员(User Administrator)」授予当前 API Key 所属用户后重试。"
                                            );
                                        }

                                        log.info("Password reset (Identity Domains) domain={} userOcid={}", domainLabel, classicUserOcid);
                                        ex = newPassword;
                                    } catch (Throwable var22) {
                                        if (dc != null) {
                                            try {
                                                dc.close();
                                            } catch (Throwable var20) {
                                                var22.addSuppressed(var20);
                                            }
                                        }

                                        throw var22;
                                    }

                                    if (dc != null) {
                                        dc.close();
                                    }

                                    return ex;
                                }

                                if (dc != null) {
                                    dc.close();
                                }
                                continue;
                            }

                            if (dc != null) {
                                dc.close();
                            }
                            continue;
                        }

                        if (dc != null) {
                            dc.close();
                        }
                    } catch (BmcException var23) {
                        if (var23.getStatusCode() != 404) {
                            throw var23;
                        }

                        log.debug("User OCID not in domain {}: {}", domainLabel, var23.getMessage());
                    }
                }
            }
        }

        throw new OciException("在任一 Identity Domain 中未找到该用户（OCID），或无法完成密码重置: " + classicUserOcid);
    }

    public void clearMfa(UserParams params) {
        OciUser tenant = this.getTenant(params.getTenantId());
        IdentityClient client = this.buildClient(tenant);

        try {
            ListMfaTotpDevicesResponse mfaResponse = client.listMfaTotpDevices(ListMfaTotpDevicesRequest.builder().userId(params.getUserId()).build());

            for (MfaTotpDeviceSummary device : mfaResponse.getItems()) {
                client.deleteMfaTotpDevice(DeleteMfaTotpDeviceRequest.builder().userId(params.getUserId()).mfaTotpDeviceId(device.getId()).build());
                log.info("Deleted MFA device: {} for user: {}", device.getId(), params.getUserId());
            }
        } catch (Throwable var8) {
            if (client != null) {
                try {
                    client.close();
                } catch (Throwable var7) {
                    var8.addSuppressed(var7);
                }
            }

            throw var8;
        }

        if (client != null) {
            client.close();
        }
    }

    public List<Map<String, Object>> listGroups(String tenantId) {
        OciUser tenant = this.getTenant(tenantId);
        IdentityClient client = this.buildClient(tenant);

        Object var11;
        try {
            com.oracle.bmc.identity.responses.ListGroupsResponse response = client.listGroups(
                com.oracle.bmc.identity.requests.ListGroupsRequest.builder().compartmentId(tenant.getOciTenantId()).build()
            );
            List<Map<String, Object>> result = new ArrayList<>();

            for (com.oracle.bmc.identity.model.Group group : response.getItems()) {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("id", group.getId());
                map.put("name", group.getName());
                map.put("description", group.getDescription());
                map.put("state", group.getLifecycleState().getValue());
                result.add(map);
            }

            var11 = result;
        } catch (Throwable var10) {
            if (client != null) {
                try {
                    client.close();
                } catch (Throwable var9) {
                    var10.addSuppressed(var9);
                }
            }

            throw var10;
        }

        if (client != null) {
            client.close();
        }

        return (List<Map<String, Object>>)var11;
    }

    public void addUserToGroup(UserParams params) {
        OciUser tenant = this.getTenant(params.getTenantId());
        IdentityClient client = this.buildClient(tenant);

        try {
            String adminGroupId = this.findAdminGroupId(client, tenant.getOciTenantId());
            if (adminGroupId == null) {
                throw new OciException("未找到管理员组");
            }

            client.addUserToGroup(
                AddUserToGroupRequest.builder()
                    .addUserToGroupDetails(AddUserToGroupDetails.builder().userId(params.getUserId()).groupId(adminGroupId).build())
                    .build()
            );
            log.info("Added user {} to admin group", params.getUserId());
        } catch (Throwable var7) {
            if (client != null) {
                try {
                    client.close();
                } catch (Throwable var6) {
                    var7.addSuppressed(var6);
                }
            }

            throw var7;
        }

        if (client != null) {
            client.close();
        }
    }

    public void removeUserFromGroup(UserParams params) {
        OciUser tenant = this.getTenant(params.getTenantId());
        IdentityClient client = this.buildClient(tenant);

        try {
            String adminGroupId = this.findAdminGroupId(client, tenant.getOciTenantId());
            if (adminGroupId == null) {
                throw new OciException("未找到管理员组");
            }

            ListUserGroupMembershipsResponse memberships = client.listUserGroupMemberships(
                ListUserGroupMembershipsRequest.builder().compartmentId(tenant.getOciTenantId()).userId(params.getUserId()).groupId(adminGroupId).build()
            );

            for (UserGroupMembership membership : memberships.getItems()) {
                client.removeUserFromGroup(RemoveUserFromGroupRequest.builder().userGroupMembershipId(membership.getId()).build());
                log.info("Removed user {} from admin group", params.getUserId());
            }
        } catch (Throwable var9) {
            if (client != null) {
                try {
                    client.close();
                } catch (Throwable var8) {
                    var9.addSuppressed(var8);
                }
            }

            throw var9;
        }

        if (client != null) {
            client.close();
        }
    }

    public List<String> getUserGroupNames(String tenantId, String userId) {
        return this.getUserGroups(tenantId, userId)
            .stream()
            .map(g -> g.get("name") == null ? null : String.valueOf(g.get("name")))
            .filter(CharSequenceUtil::isNotBlank)
            .toList();
    }

    public List<Map<String, Object>> getUserGroups(String tenantId, String userId) {
        OciUser tenant = this.getTenant(tenantId);
        IdentityClient client = this.buildClient(tenant);

        Object var16;
        label62: {
            try {
                ListUserGroupMembershipsResponse memberships = client.listUserGroupMemberships(
                    ListUserGroupMembershipsRequest.builder().compartmentId(tenant.getOciTenantId()).userId(userId).build()
                );
                List<Map<String, Object>> result = new ArrayList<>();
                if (memberships.getItems() == null) {
                    var16 = result;
                    break label62;
                }

                for (UserGroupMembership m : memberships.getItems()) {
                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put("membershipId", m.getId());
                    map.put("groupId", m.getGroupId());
                    String name = m.getGroupId();

                    try {
                        com.oracle.bmc.identity.model.Group group = client.getGroup(
                                com.oracle.bmc.identity.requests.GetGroupRequest.builder().groupId(m.getGroupId()).build()
                            )
                            .getGroup();
                        if (group != null && StrUtil.isNotBlank(group.getName())) {
                            name = group.getName();
                        }
                    } catch (Exception var13) {
                    }

                    map.put("name", name);
                    result.add(map);
                }

                var16 = result;
            } catch (Throwable var14) {
                if (client != null) {
                    try {
                        client.close();
                    } catch (Throwable var12) {
                        var14.addSuppressed(var12);
                    }
                }

                throw var14;
            }

            if (client != null) {
                client.close();
            }

            return (List<Map<String, Object>>)var16;
        }

        if (client != null) {
            client.close();
        }

        return (List<Map<String, Object>>)var16;
    }

    public void syncUserGroups(String tenantId, String userId, List<String> targetGroupIds) {
        OciUser tenant = this.getTenant(tenantId);
        LinkedHashSet<String> target = new LinkedHashSet<>();
        if (targetGroupIds != null) {
            for (String id : targetGroupIds) {
                if (StrUtil.isNotBlank(id)) {
                    target.add(id.trim());
                }
            }
        }

        IdentityClient client = this.buildClient(tenant);

        try {
            String compartmentId = tenant.getOciTenantId();
            ListUserGroupMembershipsResponse memberships = client.listUserGroupMemberships(
                ListUserGroupMembershipsRequest.builder().compartmentId(compartmentId).userId(userId).build()
            );
            Set<String> currentGroupIds = new LinkedHashSet<>();
            Map<String, String> membershipByGroupId = new LinkedHashMap<>();
            if (memberships.getItems() != null) {
                for (UserGroupMembership m : memberships.getItems()) {
                    if (m.getGroupId() != null) {
                        currentGroupIds.add(m.getGroupId());
                        membershipByGroupId.put(m.getGroupId(), m.getId());
                    }
                }
            }

            for (String groupId : currentGroupIds) {
                if (!target.contains(groupId)) {
                    String membershipId = membershipByGroupId.get(groupId);
                    if (StrUtil.isNotBlank(membershipId)) {
                        client.removeUserFromGroup(RemoveUserFromGroupRequest.builder().userGroupMembershipId(membershipId).build());
                        log.info("Removed user {} from group {}", userId, groupId);
                    }
                }
            }

            for (String groupIdx : target) {
                if (!currentGroupIds.contains(groupIdx)) {
                    client.addUserToGroup(
                        AddUserToGroupRequest.builder().addUserToGroupDetails(AddUserToGroupDetails.builder().userId(userId).groupId(groupIdx).build()).build()
                    );
                    log.info("Added user {} to group {}", userId, groupIdx);
                }
            }
        } catch (Throwable var15) {
            if (client != null) {
                try {
                    client.close();
                } catch (Throwable var14) {
                    var15.addSuppressed(var14);
                }
            }

            throw var15;
        }

        if (client != null) {
            client.close();
        }
    }

    private void addToAdminGroup(IdentityClient client, String compartmentId, String userId) {
        String adminGroupId = this.findAdminGroupId(client, compartmentId);
        if (adminGroupId == null) {
            log.warn("Admin group not found, skipping group assignment");
        } else {
            try {
                client.addUserToGroup(
                    AddUserToGroupRequest.builder().addUserToGroupDetails(AddUserToGroupDetails.builder().userId(userId).groupId(adminGroupId).build()).build()
                );
            } catch (Exception var6) {
                log.warn("Failed to add user to admin group: {}", var6.getMessage());
            }
        }
    }

    public void updateUser(UserParams params) {
        OciUser tenant = this.getTenant(params.getTenantId());
        IdentityClient client = this.buildClient(tenant);

        try {
            com.oracle.bmc.identity.model.UpdateUserDetails.Builder builder = UpdateUserDetails.builder();
            if (StrUtil.isNotBlank(params.getEmail())) {
                builder.email(params.getEmail());
            }

            if (StrUtil.isNotBlank(params.getUserName())) {
                builder.description(params.getUserName());
            }

            client.updateUser(UpdateUserRequest.builder().userId(params.getUserId()).updateUserDetails(builder.build()).build());
            log.info("Updated user: {}", params.getUserId());
        } catch (Throwable var7) {
            if (client != null) {
                try {
                    client.close();
                } catch (Throwable var6) {
                    var7.addSuppressed(var6);
                }
            }

            throw var7;
        }

        if (client != null) {
            client.close();
        }
    }

    public Map<String, Object> getUserCapabilities(String tenantId, String userId) {
        OciUser tenant = this.getTenant(tenantId);
        IdentityClient client = this.buildClient(tenant);

        Map var6;
        try {
            User user = client.getUser(GetUserRequest.builder().userId(userId).build()).getUser();
            var6 = capabilitiesToMap(user == null ? null : user.getCapabilities());
        } catch (Throwable var8) {
            if (client != null) {
                try {
                    client.close();
                } catch (Throwable var7) {
                    var8.addSuppressed(var7);
                }
            }

            throw var8;
        }

        if (client != null) {
            client.close();
        }

        return var6;
    }

    public void updateUserCapabilities(UserParams params) {
        OciUser tenant = this.getTenant(params.getTenantId());
        Map<String, Boolean> caps = params.getCapabilities();
        if (caps != null && !caps.isEmpty()) {
            com.oracle.bmc.identity.model.UpdateUserCapabilitiesDetails.Builder builder = UpdateUserCapabilitiesDetails.builder();
            if (caps.containsKey("canUseConsolePassword")) {
                builder.canUseConsolePassword(caps.get("canUseConsolePassword"));
            }

            if (caps.containsKey("canUseApiKeys")) {
                builder.canUseApiKeys(caps.get("canUseApiKeys"));
            }

            if (caps.containsKey("canUseAuthTokens")) {
                builder.canUseAuthTokens(caps.get("canUseAuthTokens"));
            }

            if (caps.containsKey("canUseSmtpCredentials")) {
                builder.canUseSmtpCredentials(caps.get("canUseSmtpCredentials"));
            }

            if (caps.containsKey("canUseDbCredentials")) {
                builder.canUseDBCredentials(caps.get("canUseDbCredentials"));
            }

            if (caps.containsKey("canUseCustomerSecretKeys")) {
                builder.canUseCustomerSecretKeys(caps.get("canUseCustomerSecretKeys"));
            }

            if (caps.containsKey("canUseOAuth2ClientCredentials")) {
                builder.canUseOAuth2ClientCredentials(caps.get("canUseOAuth2ClientCredentials"));
            }

            IdentityClient client = this.buildClient(tenant);

            try {
                client.updateUserCapabilities(
                    UpdateUserCapabilitiesRequest.builder().userId(params.getUserId()).updateUserCapabilitiesDetails(builder.build()).build()
                );
                log.info("Updated user capabilities: {}", params.getUserId());
            } catch (Throwable var9) {
                if (client != null) {
                    try {
                        client.close();
                    } catch (Throwable var8) {
                        var9.addSuppressed(var8);
                    }
                }

                throw var9;
            }

            if (client != null) {
                client.close();
            }
        } else {
            throw new OciException("请至少指定一项用户权限");
        }
    }

    private static Map<String, Object> capabilitiesToMap(UserCapabilities caps) {
        Map<String, Object> map = new LinkedHashMap<>();

        for (String key : CAPABILITY_KEYS) {
            map.put(key, capabilityValue(caps, key));
        }

        return map;
    }

    private static boolean capabilityValue(UserCapabilities caps, String key) {
        if (caps == null) {
            return false;
        } else {
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
    }

    public static Map<String, Boolean> parseCapabilitiesMap(Object raw) {
        if (raw instanceof Map<?, ?> m) {
            LinkedHashMap out = new LinkedHashMap();

            for (String key : CAPABILITY_KEYS) {
                if (m.containsKey(key)) {
                    out.put(key, Boolean.TRUE.equals(m.get(key)));
                }
            }

            return out;
        } else {
            return Map.of();
        }
    }

    public void updateUserState(String tenantId, String userId, boolean blocked) {
        OciUser tenant = this.getTenant(tenantId);
        IdentityClient client = this.buildClient(tenant);

        try {
            client.updateUserState(
                UpdateUserStateRequest.builder().userId(userId).updateStateDetails(UpdateStateDetails.builder().blocked(blocked).build()).build()
            );
            log.info("User {} state updated, blocked={}", userId, blocked);
        } catch (Throwable var9) {
            if (client != null) {
                try {
                    client.close();
                } catch (Throwable var8) {
                    var9.addSuppressed(var8);
                }
            }

            throw var9;
        }

        if (client != null) {
            client.close();
        }
    }

    public List<Map<String, Object>> listMfaDevices(String tenantId, String userId) {
        OciUser tenant = this.getTenant(tenantId);
        IdentityClient client = this.buildClient(tenant);

        Object var12;
        try {
            ListMfaTotpDevicesResponse response = client.listMfaTotpDevices(ListMfaTotpDevicesRequest.builder().userId(userId).build());
            List<Map<String, Object>> result = new ArrayList<>();

            for (MfaTotpDeviceSummary device : response.getItems()) {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("id", device.getId());
                map.put("state", device.getLifecycleState().getValue());
                map.put("isActivated", device.getIsActivated());
                map.put("timeCreated", device.getTimeCreated() != null ? device.getTimeCreated().toString() : null);
                result.add(map);
            }

            var12 = result;
        } catch (Throwable var11) {
            if (client != null) {
                try {
                    client.close();
                } catch (Throwable var10) {
                    var11.addSuppressed(var10);
                }
            }

            throw var11;
        }

        if (client != null) {
            client.close();
        }

        return (List<Map<String, Object>>)var12;
    }

    private String findAdminGroupId(IdentityClient client, String compartmentId) {
        com.oracle.bmc.identity.responses.ListGroupsResponse response = client.listGroups(
            com.oracle.bmc.identity.requests.ListGroupsRequest.builder().compartmentId(compartmentId).build()
        );

        for (com.oracle.bmc.identity.model.Group group : response.getItems()) {
            if ("Administrators".equalsIgnoreCase(group.getName())) {
                return group.getId();
            }
        }

        return null;
    }
}
