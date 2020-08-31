package com.saturn9er.notes.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/error")
public class ErrorController {

    @GetMapping("/")
    public String getBaseErrorPage() {
        return "error/index";
    }

    @GetMapping("/{code}")
    public String getErrorPage(@PathVariable String code) {
        switch (code) {
            case "403":
                return "error/403";
            case "404":
                return "error/404";
            default:
                return "error/index";
        }
    }

}
