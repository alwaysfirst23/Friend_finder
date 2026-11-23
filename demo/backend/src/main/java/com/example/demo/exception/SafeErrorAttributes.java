package com.example.demo.exception;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.webmvc.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

/**
 * Убирает stack trace из ответов с ошибками, чтобы не раскрывать внутренности сервера.
 */
@Component
public class SafeErrorAttributes extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options) {
        Map<String, Object> attributes = new LinkedHashMap<>(
                super.getErrorAttributes(webRequest, options.excluding(ErrorAttributeOptions.Include.STACK_TRACE)));
        attributes.remove("trace");
        return attributes;
    }
}
