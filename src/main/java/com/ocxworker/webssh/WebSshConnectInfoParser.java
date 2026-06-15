package com.ocxworker.webssh;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Base64;

final class WebSshConnectInfoParser {
    private static final ObjectMapper JSON = new ObjectMapper();

    private WebSshConnectInfoParser() {
    }

    static WebSshConnectInfo parse(String sshInfoB64) throws Exception {
        if (sshInfoB64 != null && !sshInfoB64.isBlank()) {
            byte[] decoded = Base64.getDecoder().decode(sshInfoB64.trim());
            WebSshConnectInfo info = (WebSshConnectInfo)JSON.readValue(decoded, WebSshConnectInfo.class);
            info.normalizeHostname();
            return info;
        } else {
            throw new IllegalArgumentException("sshInfo is empty");
        }
    }
}
