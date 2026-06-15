package com.ocxworker.webssh;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class WebSshFileService {
    private final WebSshUploadRegistry uploadRegistry;

    public WebSshFileService(WebSshUploadRegistry uploadRegistry) {
        this.uploadRegistry = uploadRegistry;
    }

    public Map<String, Object> listFiles(String sshInfoB64, String path) throws Exception {
        WebSshConnectInfo info = WebSshConnectInfoParser.parse(sshInfoB64);
        Session session = WebSshJschSupport.openSession(info);
        ChannelSftp sftp = WebSshJschSupport.openSftp(session);

        Object var22;
        try {
            String home = detectHomeDir(sftp, info.getUsername());
            String resolved = resolveListPath(path, home, info.getUsername());
            Vector<LsEntry> entries = sftp.ls(resolved);
            List<Map<String, Object>> list = new ArrayList<>();

            for (LsEntry e : entries) {
                String name = e.getFilename();
                if (!".".equals(name) && !"..".equals(name)) {
                    SftpATTRS attrs = e.getAttrs();
                    boolean dir = attrs.isDir();
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("Name", name);
                    row.put("IsDir", dir);
                    row.put("Size", dir ? String.valueOf(attrs.getSize()) : byteFmt(attrs.getSize()));
                    row.put("ModifyTime", formatTime(attrs.getMTime()));
                    list.add(row);
                }
            }

            list.sort(
                Comparator.<Map<String, Object>, Boolean>comparing(m -> !(Boolean)m.get("IsDir")).reversed().thenComparing(m -> String.valueOf(m.get("Name")))
            );
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("list", list);
            data.put("home", home);
            var22 = data;
        } catch (SftpException var19) {
            if (var19.id == 2) {
                throw new IllegalArgumentException("Directory " + path + ": no such file or directory");
            }

            throw var19;
        } finally {
            WebSshJschSupport.closeQuietly(session, sftp);
        }

        return (Map<String, Object>)var22;
    }

    public void streamDownload(String sshInfoB64, String path, OutputStream out) throws Exception {
        WebSshConnectInfo info = WebSshConnectInfoParser.parse(sshInfoB64);
        Session session = WebSshJschSupport.openSession(info);
        ChannelSftp sftp = WebSshJschSupport.openSftp(session);

        try {
            String resolved = path;
            if (path == null || path.isBlank()) {
                resolved = detectHomeDir(sftp, info.getUsername());
            }

            try (InputStream in = sftp.get(resolved)) {
                in.transferTo(out);
            }
        } finally {
            WebSshJschSupport.closeQuietly(session, sftp);
        }
    }

    public String upload(String sshInfoB64, String path, String subDir, String uploadId, MultipartFile file) throws Exception {
        WebSshConnectInfo info = WebSshConnectInfoParser.parse(sshInfoB64);
        Session session = WebSshJschSupport.openSession(info);
        ChannelSftp sftp = WebSshJschSupport.openSftp(session);

        String var31;
        try {
            String base = path;
            if (path == null || path.isBlank()) {
                base = detectHomeDir(sftp, info.getUsername());
            }

            base = base.replaceAll("/+$", "");
            if (subDir != null && !subDir.isBlank()) {
                String dir = base + "/" + subDir.replaceAll("^/+|/+$", "");
                mkdirsIfMissing(sftp, dir);
                base = dir;
            }

            String dst = base + "/" + file.getOriginalFilename();
            this.uploadRegistry.track(uploadId);

            try (InputStream in = file.getInputStream()) {
                byte[] buf = new byte[8192];
                OutputStream dstOut = sftp.put(dst);

                int n;
                while ((n = in.read(buf)) >= 0) {
                    if (n != 0) {
                        dstOut.write(buf, 0, n);
                        this.uploadRegistry.add(uploadId, n);
                    }
                }

                dstOut.close();
            } finally {
                this.uploadRegistry.remove(uploadId);
            }

            var31 = dst;
        } finally {
            WebSshJschSupport.closeQuietly(session, sftp);
        }

        return var31;
    }

    private static String resolveListPath(String path, String home, String username) {
        if ("/".equals(path) && !"/".equals(home) && !"root".equals(username)) {
            return home;
        } else if (path != null && !path.isBlank()) {
            return path;
        } else {
            return "root".equals(username) ? "/" : home;
        }
    }

    private static String detectHomeDir(ChannelSftp sftp, String username) throws SftpException {
        try {
            return sftp.pwd();
        } catch (SftpException var7) {
            if ("root".equals(username)) {
                return "/root";
            } else {
                String u1 = "/usr/home/" + username;

                try {
                    sftp.stat(u1);
                    return u1;
                } catch (SftpException var6) {
                    String u2 = "/home/" + username;

                    try {
                        sftp.stat(u2);
                        return u2;
                    } catch (SftpException var5) {
                        return "/home";
                    }
                }
            }
        }
    }

    private static void mkdirsIfMissing(ChannelSftp sftp, String path) throws SftpException {
        try {
            sftp.stat(path);
        } catch (SftpException var3) {
            if (var3.id == 2) {
                sftp.mkdir(path);
            }
        }
    }

    private static String formatTime(int mtime) {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault()).format(Instant.ofEpochSecond((long)mtime));
    }

    static String byteFmt(long bytes) {
        if (bytes <= 0L) {
            return "0B";
        } else {
            String[] units = new String[]{"B", "K", "M", "G", "T", "P", "E"};
            int unit = 0;

            double value;
            for (value = (double)bytes; value >= 1024.0 && unit < units.length - 1; unit++) {
                value /= 1024.0;
            }

            String s = String.format("%.2f", value).replaceAll("\\.00$", "");
            return s + units[unit];
        }
    }
}
