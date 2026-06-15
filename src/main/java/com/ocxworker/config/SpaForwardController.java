package com.ocxworker.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaForwardController implements ErrorController {
    @GetMapping({"/"})
    public String index() {
        return "forward:/index.html";
    }

    @GetMapping({"/error"})
    public String error(HttpServletRequest request) {
        return "forward:/index.html";
    }
}
