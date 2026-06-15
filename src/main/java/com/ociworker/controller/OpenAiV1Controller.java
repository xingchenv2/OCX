/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.ociworker.controller.OpenAiV1Controller
 *  com.ociworker.exception.OciException
 *  com.ociworker.mapper.OciUserMapper
 *  com.ociworker.model.entity.OciUser
 *  com.ociworker.service.OciGenerativeOpenAiService
 *  jakarta.annotation.Resource
 *  jakarta.servlet.http.HttpServletRequest
 *  jakarta.servlet.http.HttpServletResponse
 *  org.springframework.stereotype.Controller
 *  org.springframework.web.bind.annotation.RequestMapping
 *  org.springframework.web.bind.annotation.RequestMethod
 */
package com.ociworker.controller;

import com.ociworker.exception.OciException;
import com.ociworker.mapper.OciUserMapper;
import com.ociworker.model.entity.OciUser;
import com.ociworker.service.OciGenerativeOpenAiService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/*
 * Exception performing whole class analysis ignored.
 */
@Controller
public class OpenAiV1Controller {
    @Resource
    private OciGenerativeOpenAiService generativeOpenAiService;
    @Resource
    private OciUserMapper ociUserMapper;

    @RequestMapping(value={"/v1", "/v1/**"}, method={RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.PATCH, RequestMethod.DELETE, RequestMethod.HEAD})
    public void v1Proxy(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String id = (String)request.getAttribute("ociworker.openai.ociUserId");
        if (id == null) {
            response.setStatus(401);
            return;
        }
        OciUser u = (OciUser)this.ociUserMapper.selectById((Serializable)((Object)id));
        if (u == null) {
            response.setStatus(403);
            return;
        }
        try {
            this.generativeOpenAiService.proxy(u, request, response);
        }
        catch (OciException e) {
            OpenAiV1Controller.error((HttpServletResponse)response, (int)502, (String)(e.getMessage() != null ? e.getMessage() : "OCI \u9519\u8bef"));
        }
        catch (IOException e) {
            if (!(response.isCommitted() || e.getMessage() != null && (e.getMessage().toLowerCase().contains("broken") || e.getMessage().toLowerCase().contains("aborted")))) {
                OpenAiV1Controller.error((HttpServletResponse)response, (int)502, (String)(e.getMessage() != null ? e.getMessage() : "\u8f6c\u53d1\u51fa\u9519"));
            }
        }
        catch (Exception e) {
            OpenAiV1Controller.error((HttpServletResponse)response, (int)500, (String)(e.getMessage() != null ? e.getMessage() : "internal_error"));
        }
    }

    private static void error(HttpServletResponse response, int status, String message) throws IOException {
        if (response.isCommitted()) {
            return;
        }
        response.setStatus(status);
        response.setContentType("application/json; charset=utf-8");
        String safe = message == null ? "" : message.replace("\\", "\\\\").replace("\"", "\\'");
        response.getOutputStream().write(String.format("{\"error\":{\"type\":\"oci_error\",\"message\":\"%s\"}}", safe).getBytes(StandardCharsets.UTF_8));
    }
}

