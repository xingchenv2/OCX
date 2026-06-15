/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.ocxworker.config.SpaForwardController
 *  jakarta.servlet.http.HttpServletRequest
 *  org.springframework.boot.web.servlet.error.ErrorController
 *  org.springframework.stereotype.Controller
 *  org.springframework.web.bind.annotation.GetMapping
 */
package com.ocxworker.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaForwardController
implements ErrorController {
    @GetMapping(value={"/"})
    public String index() {
        return "forward:/index.html";
    }

    @GetMapping(value={"/error"})
    public String error(HttpServletRequest request) {
        return "forward:/index.html";
    }
}

