package com.gggsl.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gggsl.common.exception.ExceptionType;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
@Slf4j
public class CoreqiLogoutSuccessHandler implements LogoutSuccessHandler {
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        PrintWriter out = null;
        try {
            response.setStatus(HttpServletResponse.SC_OK);

            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> res = Maps.newHashMap();
            res.put("code", ExceptionType.NOT_AUTHENTICATED_EXCEPTION.getCode());
            res.put("message", ExceptionType.NOT_AUTHENTICATED_EXCEPTION.getMessage());
            res.put("data", request.getRequestURL().toString());
            out = response.getWriter();
            out.append(mapper.writeValueAsString(res));
        } catch (Exception e) {
            log.error("处理登出异常, {}", e);
            response.sendError(500);
        }
    }
}
