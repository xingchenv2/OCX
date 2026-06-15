package com.ocxworker.controller;

import com.ocxworker.exception.OciException;
import com.ocxworker.mapper.OciUserMapper;
import com.ocxworker.model.entity.OciUser;
import com.ocxworker.service.OciGenerativeOpenAiService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class OpenAiV1Controller {
    @Resource
    private OciGenerativeOpenAiService generativeOpenAiService;
    @Resource
    private OciUserMapper ociUserMapper;

    @RequestMapping(
        value = {"/v1", "/v1/**"},
        method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.PATCH, RequestMethod.DELETE, RequestMethod.HEAD}
    )
    public void v1Proxy(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String id = (String)request.getAttribute("ociworker.openai.ociUserId");
        if (id == null) {
            response.setStatus(401);
        } else {
            OciUser u = (OciUser)this.ociUserMapper.selectById(id);
            if (u == null) {
                response.setStatus(403);
            } else {
                try {
                    this.generativeOpenAiService.proxy(u, request, response);
                } catch (OciException var6) {
                    error(response, 502, var6.getMessage() != null ? var6.getMessage() : "OCI 错误");
                } catch (IOException var7) {
                    if (!response.isCommitted()
                        && (
                            var7.getMessage() == null
                                || !var7.getMessage().toLowerCase().contains("broken") && !var7.getMessage().toLowerCase().contains("aborted")
                        )) {
                        error(response, 502, var7.getMessage() != null ? var7.getMessage() : "转发出错");
                    }
                } catch (Exception var8) {
                    error(response, 500, var8.getMessage() != null ? var8.getMessage() : "internal_error");
                }
            }
        }
    }

    private static void error(HttpServletResponse response, int status, String message) throws IOException {
        if (!response.isCommitted()) {
            response.setStatus(status);
            response.setContentType("application/json; charset=utf-8");
            String safe = message == null ? "" : message.replace("\\", "\\\\").replace("\"", "\\'");
            response.getOutputStream().write(String.format("{\"error\":{\"type\":\"oci_error\",\"message\":\"%s\"}}", safe).getBytes(StandardCharsets.UTF_8));
        }
    }
}
