/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.jcraft.jsch.Channel
 *  com.jcraft.jsch.ChannelSftp
 *  com.jcraft.jsch.ChannelSftp$LsEntry
 *  com.jcraft.jsch.Session
 *  com.jcraft.jsch.SftpATTRS
 *  com.jcraft.jsch.SftpException
 *  com.ociworker.webssh.WebSshConnectInfo
 *  com.ociworker.webssh.WebSshConnectInfoParser
 *  com.ociworker.webssh.WebSshFileService
 *  com.ociworker.webssh.WebSshJschSupport
 *  com.ociworker.webssh.WebSshUploadRegistry
 *  org.springframework.stereotype.Service
 *  org.springframework.web.multipart.MultipartFile
 */
package com.ociworker.webssh;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;
import com.ociworker.webssh.WebSshConnectInfo;
import com.ociworker.webssh.WebSshConnectInfoParser;
import com.ociworker.webssh.WebSshJschSupport;
import com.ociworker.webssh.WebSshUploadRegistry;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/*
 * Exception performing whole class analysis ignored.
 */
@Service
public class WebSshFileService {
    private final WebSshUploadRegistry uploadRegistry;

    public WebSshFileService(WebSshUploadRegistry uploadRegistry) {
        this.uploadRegistry = uploadRegistry;
    }

    public Map<String, Object> listFiles(String sshInfoB64, String path) throws Exception {
        LinkedHashMap<String, Object> linkedHashMap;
        WebSshConnectInfo info = WebSshConnectInfoParser.parse((String)sshInfoB64);
        Session session = WebSshJschSupport.openSession((WebSshConnectInfo)info);
        ChannelSftp sftp = WebSshJschSupport.openSftp((Session)session);
        try {
            String home = WebSshFileService.detectHomeDir((ChannelSftp)sftp, (String)info.getUsername());
            String resolved = WebSshFileService.resolveListPath((String)path, (String)home, (String)info.getUsername());
            Vector entries = sftp.ls(resolved);
            ArrayList<Map> list = new ArrayList<Map>();
            for (ChannelSftp.LsEntry e : entries) {
                String name = e.getFilename();
                if (".".equals(name) || "..".equals(name)) continue;
                SftpATTRS attrs = e.getAttrs();
                boolean dir = attrs.isDir();
                LinkedHashMap<String, Object> row = new LinkedHashMap<String, Object>();
                row.put("Name", name);
                row.put("IsDir", dir);
                row.put("Size", dir ? String.valueOf(attrs.getSize()) : WebSshFileService.byteFmt((long)attrs.getSize()));
                row.put("ModifyTime", WebSshFileService.formatTime((int)attrs.getMTime()));
                list.add(row);
            }
            list.sort(Comparator.comparing(m -> (Boolean)m.get("IsDir") == false).reversed().thenComparing(m -> String.valueOf(m.get("Name"))));
            LinkedHashMap<String, Object> data = new LinkedHashMap<String, Object>();
            data.put("list", list);
            data.put("home", home);
            linkedHashMap = data;
        }
        catch (SftpException e) {
            try {
                if (e.id == 2) {
                    throw new IllegalArgumentException("Directory " + path + ": no such file or directory");
                }
                throw e;
            }
            catch (Throwable throwable) {
                WebSshJschSupport.closeQuietly((Session)session, (Channel[])new Channel[]{sftp});
                throw throwable;
            }
        }
        WebSshJschSupport.closeQuietly((Session)session, (Channel[])new Channel[]{sftp});
        return linkedHashMap;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void streamDownload(String sshInfoB64, String path, OutputStream out) throws Exception {
        WebSshConnectInfo info = WebSshConnectInfoParser.parse((String)sshInfoB64);
        Session session = WebSshJschSupport.openSession((WebSshConnectInfo)info);
        ChannelSftp sftp = WebSshJschSupport.openSftp((Session)session);
        try {
            String resolved = path;
            if (resolved == null || resolved.isBlank()) {
                resolved = WebSshFileService.detectHomeDir((ChannelSftp)sftp, (String)info.getUsername());
            }
            try (InputStream in = sftp.get(resolved);){
                in.transferTo(out);
            }
        }
        catch (Throwable throwable) {
            WebSshJschSupport.closeQuietly((Session)session, (Channel[])new Channel[]{sftp});
            throw throwable;
        }
        WebSshJschSupport.closeQuietly((Session)session, (Channel[])new Channel[]{sftp});
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String upload(String sshInfoB64, String path, String subDir, String uploadId, MultipartFile file) throws Exception {
        String string;
        WebSshConnectInfo info = WebSshConnectInfoParser.parse((String)sshInfoB64);
        Session session = WebSshJschSupport.openSession((WebSshConnectInfo)info);
        ChannelSftp sftp = WebSshJschSupport.openSftp((Session)session);
        try {
            Object base = path;
            if (base == null || ((String)base).isBlank()) {
                base = WebSshFileService.detectHomeDir((ChannelSftp)sftp, (String)info.getUsername());
            }
            base = ((String)base).replaceAll("/+$", "");
            if (subDir != null && !subDir.isBlank()) {
                String dir = (String)base + "/" + subDir.replaceAll("^/+|/+$", "");
                WebSshFileService.mkdirsIfMissing((ChannelSftp)sftp, (String)dir);
                base = dir;
            }
            String dst = (String)base + "/" + file.getOriginalFilename();
            this.uploadRegistry.track(uploadId);
            try (InputStream in = file.getInputStream();){
                int n;
                byte[] buf = new byte[8192];
                OutputStream dstOut = sftp.put(dst);
                while ((n = in.read(buf)) >= 0) {
                    if (n == 0) continue;
                    dstOut.write(buf, 0, n);
                    this.uploadRegistry.add(uploadId, n);
                }
                dstOut.close();
            }
            finally {
                this.uploadRegistry.remove(uploadId);
            }
            string = dst;
        }
        catch (Throwable throwable) {
            WebSshJschSupport.closeQuietly((Session)session, (Channel[])new Channel[]{sftp});
            throw throwable;
        }
        WebSshJschSupport.closeQuietly((Session)session, (Channel[])new Channel[]{sftp});
        return string;
    }

    private static String resolveListPath(String path, String home, String username) {
        if ("/".equals(path) && !"/".equals(home) && !"root".equals(username)) {
            return home;
        }
        if (path == null || path.isBlank()) {
            return "root".equals(username) ? "/" : home;
        }
        return path;
    }

    private static String detectHomeDir(ChannelSftp sftp, String username) throws SftpException {
        try {
            return sftp.pwd();
        }
        catch (SftpException sftpException) {
            if ("root".equals(username)) {
                return "/root";
            }
            String u1 = "/usr/home/" + username;
            try {
                sftp.stat(u1);
                return u1;
            }
            catch (SftpException sftpException2) {
                String u2 = "/home/" + username;
                try {
                    sftp.stat(u2);
                    return u2;
                }
                catch (SftpException sftpException3) {
                    return "/home";
                }
            }
        }
    }

    private static void mkdirsIfMissing(ChannelSftp sftp, String path) throws SftpException {
        block2: {
            try {
                sftp.stat(path);
            }
            catch (SftpException e) {
                if (e.id != 2) break block2;
                sftp.mkdir(path);
            }
        }
    }

    private static String formatTime(int mtime) {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault()).format(Instant.ofEpochSecond(mtime));
    }

    static String byteFmt(long bytes) {
        double value;
        if (bytes <= 0L) {
            return "0B";
        }
        String[] units = new String[]{"B", "K", "M", "G", "T", "P", "E"};
        int unit = 0;
        for (value = (double)bytes; value >= 1024.0 && unit < units.length - 1; value /= 1024.0, ++unit) {
        }
        String s = String.format("%.2f", value).replaceAll("\\.00$", "");
        return s + units[unit];
    }
}

