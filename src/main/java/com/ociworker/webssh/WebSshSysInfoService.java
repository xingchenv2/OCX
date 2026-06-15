/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.jcraft.jsch.Channel
 *  com.jcraft.jsch.Session
 *  com.ociworker.webssh.WebSshConnectInfo
 *  com.ociworker.webssh.WebSshConnectInfoParser
 *  com.ociworker.webssh.WebSshJschSupport
 *  com.ociworker.webssh.WebSshSysInfoService
 *  org.springframework.stereotype.Service
 */
package com.ociworker.webssh;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.Session;
import com.ociworker.webssh.WebSshConnectInfo;
import com.ociworker.webssh.WebSshConnectInfoParser;
import com.ociworker.webssh.WebSshJschSupport;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;

/*
 * Exception performing whole class analysis ignored.
 */
@Service
public class WebSshSysInfoService {
    private static final String CMD = String.join((CharSequence)"; ", "echo \"===OS===\"", "(cat /etc/os-release 2>/dev/null | grep -m1 PRETTY_NAME | cut -d'\"' -f2) || uname -s", "echo \"===ARCH===\"", "uname -m", "echo \"===CPU_MODEL===\"", "grep -m1 'model name' /proc/cpuinfo 2>/dev/null | cut -d: -f2 | xargs || sysctl -n machdep.cpu.brand_string 2>/dev/null || echo unknown", "echo \"===CPU_CORES===\"", "nproc 2>/dev/null || sysctl -n hw.ncpu 2>/dev/null || echo 1", "echo \"===MEM===\"", "free -b 2>/dev/null | awk '/^Mem:/{print $2\" \"$3}' || echo \"0 0\"", "echo \"===DISK===\"", "df -B1 / 2>/dev/null | awk 'NR==2{print $2\" \"$3}' || echo \"0 0\"", "echo \"===LOAD===\"", "cat /proc/loadavg 2>/dev/null | awk '{print $1\" \"$2\" \"$3}' || uptime | sed 's/.*load average[s]*: //' | tr ',' ' ' | awk '{print $1\" \"$2\" \"$3}'", "echo \"===UPTIME===\"", "cat /proc/uptime 2>/dev/null | awk '{print int($1)}' || echo \"0\"", "echo \"===CPU_USAGE===\"", "cat /proc/stat 2>/dev/null | awk '/^cpu /{a=$2+$3+$4; t=$2+$3+$4+$5+$6+$7+$8; print a\" \"t}'; sleep 0.5; cat /proc/stat 2>/dev/null | awk '/^cpu /{a=$2+$3+$4; t=$2+$3+$4+$5+$6+$7+$8; print a\" \"t}'", "echo \"===TRAFFIC===\"", "cat /proc/net/dev 2>/dev/null | awk 'NR>2 && $1!~\"lo:\" {gsub(/:$/,\"\",$1); rx+=$2; tx+=$10} END{print rx\" \"tx}' || echo \"0 0\"", "echo \"===END===\"");

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Map<String, String> collect(String sshInfoB64) throws Exception {
        WebSshConnectInfo info = WebSshConnectInfoParser.parse((String)sshInfoB64);
        Session session = WebSshJschSupport.openSession((WebSshConnectInfo)info);
        try {
            String raw = WebSshJschSupport.execCombined((Session)session, (String)CMD);
            Map map = WebSshSysInfoService.parse((String)raw);
            return map;
        }
        finally {
            WebSshJschSupport.closeQuietly((Session)session, (Channel[])new Channel[0]);
        }
    }

    static Map<String, String> parse(String raw) {
        Matcher traffic;
        Matcher load;
        Matcher disk;
        LinkedHashMap<String, String> info = new LinkedHashMap<String, String>();
        info.put("os", "unknown");
        info.put("arch", "unknown");
        info.put("cpuModel", "unknown");
        info.put("cpuCores", "0");
        info.put("memTotal", "0");
        info.put("memUsed", "0");
        info.put("diskTotal", "0");
        info.put("diskUsed", "0");
        info.put("load", "0 0 0");
        info.put("uptime", "0");
        info.put("cpuUsage", "0");
        info.put("rxTotal", "0");
        info.put("txTotal", "0");
        if (raw == null) {
            return info;
        }
        WebSshSysInfoService.putSection(info, (String)"os", (String)raw, (String)"===OS===", (String)"===ARCH===");
        WebSshSysInfoService.putSection(info, (String)"arch", (String)raw, (String)"===ARCH===", (String)"===CPU_MODEL===");
        WebSshSysInfoService.putSection(info, (String)"cpuModel", (String)raw, (String)"===CPU_MODEL===", (String)"===CPU_CORES===");
        WebSshSysInfoService.putSection(info, (String)"cpuCores", (String)raw, (String)"===CPU_CORES===", (String)"===MEM===");
        Matcher mem = Pattern.compile("===MEM===\\s*([0-9]+)\\s+([0-9]+)").matcher(raw);
        if (mem.find()) {
            info.put("memTotal", mem.group(1));
            info.put("memUsed", mem.group(2));
        }
        if ((disk = Pattern.compile("===DISK===\\s*([0-9]+)\\s+([0-9]+)").matcher(raw)).find()) {
            info.put("diskTotal", disk.group(1));
            info.put("diskUsed", disk.group(2));
        }
        if ((load = Pattern.compile("===LOAD===\\s*([0-9.]+\\s+[0-9.]+\\s+[0-9.]+)").matcher(raw)).find()) {
            info.put("load", load.group(1).trim());
        }
        WebSshSysInfoService.putSection(info, (String)"uptime", (String)raw, (String)"===UPTIME===", (String)"===CPU_USAGE===");
        Matcher cpu = Pattern.compile("===CPU_USAGE===\\s*([0-9]+)\\s+([0-9]+)\\s+([0-9]+)\\s+([0-9]+)", 32).matcher(raw);
        if (cpu.find()) {
            long a1 = Long.parseLong(cpu.group(1));
            long t1 = Long.parseLong(cpu.group(2));
            long a2 = Long.parseLong(cpu.group(3));
            long t2 = Long.parseLong(cpu.group(4));
            if (t2 > t1 && t1 > 0L) {
                double usage = 100.0 * (double)(a2 - a1) / (double)(t2 - t1);
                info.put("cpuUsage", String.format("%.1f", usage));
            }
        }
        if ((traffic = Pattern.compile("===TRAFFIC===\\s*([0-9]+)\\s+([0-9]+)").matcher(raw)).find()) {
            info.put("rxTotal", traffic.group(1));
            info.put("txTotal", traffic.group(2));
        }
        return info;
    }

    private static void putSection(Map<String, String> info, String key, String raw, String start, String end) {
        String val;
        int s = raw.indexOf(start);
        if (s < 0) {
            return;
        }
        int e = raw.indexOf(end, s += start.length());
        if (!(val = (e > s ? raw.substring(s, e) : raw.substring(s)).trim()).isEmpty()) {
            info.put(key, val);
        }
    }
}

