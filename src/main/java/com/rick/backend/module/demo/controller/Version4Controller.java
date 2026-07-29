package com.rick.backend.module.demo.controller;

import com.rick.backend.module.common.model.ApiVersion;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("{version}/test")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@ApiVersion("v4")
public class Version4Controller {

    @GetMapping("abc")
    public String v3() {
        return "v4";
    }
}