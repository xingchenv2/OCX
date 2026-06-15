/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.jcraft.jsch.Channel
 *  com.jcraft.jsch.Session
 *  com.ociworker.webssh.WebSshApiController
 *  com.ociworker.webssh.WebSshConnectInfo
 *  com.ociworker.webssh.WebSshConnectInfoParser
 *  com.ociworker.webssh.WebSshFileService
 *  com.ociworker.webssh.WebSshJschSupport
 *  com.ociworker.webssh.WebSshResponse
 *  com.ociworker.webssh.WebSshSysInfoService
 *  jakarta.servlet.http.HttpServletResponse
 *  org.springframework.web.bind.annotation.GetMapping
 *  org.springframework.web.bind.annotation.PostMapping
 *  org.springframework.web.bind.annotation.RequestMapping
 *  org.springframework.web.bind.annotation.RequestParam
 *  org.springframework.web.bind.annotation.RestController
 *  org.springframework.web.multipart.MultipartFile
 */
package com.ociworker.webssh;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.Session;
import com.ociworker.webssh.WebSshConnectInfo;
import com.ociworker.webssh.WebSshConnectInfoParser;
import com.ociworker.webssh.WebSshFileService;
import com.ociworker.webssh.WebSshJschSupport;
import com.ociworker.webssh.WebSshResponse;
import com.ociworker.webssh.WebSshSysInfoService;
import jakarta.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/*
 * Exception performing whole class analysis ignored.
 */
@RestController
@RequestMapping(value={"/webssh-api"})
public class WebSshApiController {
    private final WebSshSysInfoService sysInfoService;
    private final WebSshFileService fileService;

    public WebSshApiController(WebSshSysInfoService sysInfoService, WebSshFileService fileService) {
        this.sysInfoService = sysInfoService;
        this.fileService = fileService;
    }

    @GetMapping(value={"/config"})
    public Map<String, Object> config() {
        LinkedHashMap<String, Object> body = new LinkedHashMap<String, Object>();
        body.put("showFooter", false);
        return body;
    }

    @GetMapping(value={"/check"})
    public Map<String, Object> check(@RequestParam(value="sshInfo") String sshInfo) {
        long start = System.nanoTime();
        try {
            WebSshConnectInfo info = WebSshConnectInfoParser.parse((String)sshInfo);
            Session session = WebSshJschSupport.openSession((WebSshConnectInfo)info);
            WebSshJschSupport.closeQuietly((Session)session, (Channel[])new Channel[0]);
            LinkedHashMap<String, Boolean> data = new LinkedHashMap<String, Boolean>();
            data.put("savePass", true);
            return WebSshResponse.body((String)"success", data, (String)WebSshApiController.duration((long)start));
        }
        catch (Exception e) {
            return WebSshResponse.body((String)e.getMessage(), null, (String)WebSshApiController.duration((long)start));
        }
    }

    @GetMapping(value={"/sysinfo"})
    public Map<String, Object> sysinfo(@RequestParam(value="sshInfo") String sshInfo) {
        long start = System.nanoTime();
        try {
            return WebSshResponse.body((String)"success", (Object)this.sysInfoService.collect(sshInfo), (String)WebSshApiController.duration((long)start));
        }
        catch (Exception e) {
            return WebSshResponse.body((String)e.getMessage(), null, (String)WebSshApiController.duration((long)start));
        }
    }

    @GetMapping(value={"/file/list"})
    public Map<String, Object> fileList(@RequestParam(value="sshInfo") String sshInfo, @RequestParam(value="path", required=false) String path) {
        long start = System.nanoTime();
        try {
            return WebSshResponse.body((String)"success", (Object)this.fileService.listFiles(sshInfo, path), (String)WebSshApiController.duration((long)start));
        }
        catch (Exception e) {
            return WebSshResponse.body((String)e.getMessage(), null, (String)WebSshApiController.duration((long)start));
        }
    }

    @GetMapping(value={"/file/download"})
    public void fileDownload(@RequestParam(value="sshInfo") String sshInfo, @RequestParam(value="path", required=false) String path, HttpServletResponse response) throws Exception {
        String name = path != null && path.contains("/") ? path.substring(path.lastIndexOf(47) + 1) : "download";
        response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(name, StandardCharsets.UTF_8));
        response.setContentType("application/octet-stream");
        this.fileService.streamDownload(sshInfo, path, (OutputStream)response.getOutputStream());
    }

    @PostMapping(value={"/file/upload"})
    public Map<String, Object> fileUpload(@RequestParam(value="sshInfo") String sshInfo, @RequestParam(value="path", required=false) String path, @RequestParam(value="dir", required=false) String dir, @RequestParam(value="id", required=false) String id, @RequestParam(value="file") MultipartFile file) {
        long start = System.nanoTime();
        try {
            this.fileService.upload(sshInfo, path, dir, id, file);
            return WebSshResponse.body((String)"success", null, (String)WebSshApiController.duration((long)start));
        }
        catch (Exception e) {
            return WebSshResponse.body((String)e.getMessage(), null, (String)WebSshApiController.duration((long)start));
        }
    }

    private static String duration(long startNanos) {
        long ms = (System.nanoTime() - startNanos) / 1000000L;
        if (ms < 1000L) {
            return ms + "ms";
        }
        return String.format("%.3fs", (double)ms / 1000.0);
    }
}

