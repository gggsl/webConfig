package com.gggsl.config;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gggsl.common.dto.Result;
import com.gggsl.common.exception.ExceptionType;
import com.gggsl.common.utils.RedisTemplateUtil;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.session.InvalidSessionStrategy;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, jsr250Enabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    UserDetailsService userDetailsService;
    @Autowired
    RedisTemplateUtil redisTemplateUtil;

    private static final String[] staticPaths = {
            "/v2/api-docs",
            "/webjars/**",
            "/swagger-resources/**",
            "/swagger-ui.html",
            "/user/test",
            "/user/hello1",
            "/user/hello2",
            "/api/image/**"
    };

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(staticPaths);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        //http.headers().frameOptions().disable();
        http
                .csrf().disable()
                .cors().configurationSource(CorsConfigurationSource())
                .and()
                .authorizeRequests()
                //.antMatchers("/user/test").permitAll()
                //.requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                .anyRequest().authenticated()
                .and()
                .exceptionHandling().authenticationEntryPoint(new MyAuthenticationEntryPoint())
                .and()
                .formLogin()
                //.loginPage("/login.html")
                .loginProcessingUrl("/user/login")
                .permitAll()
                .successHandler(successHandler())
                .failureHandler(failureHandler())
                //.and().sessionManagement().invalidSessionStrategy(new SessionExpireHandler())
                .and().logout()
                .logoutUrl("/user/logout")
                .logoutSuccessHandler(new CoreqiLogoutSuccessHandler())
                .deleteCookies("JSESSIONID")
                .permitAll();


    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(daoAuthenticationProvider());

    }

    @Bean
    public DDProvider daoAuthenticationProvider() {
        DDProvider provider = new DDProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(new BCryptPasswordEncoder());
        return provider;
    }

    @Bean
    BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public static class SessionExpireHandler implements InvalidSessionStrategy {
        @Override
        public void onInvalidSessionDetected(HttpServletRequest request, HttpServletResponse response)
                throws IOException {
            Result<String> result = new Result<>();
            result.setCode(ExceptionType.NOT_AUTHENTICATED_EXCEPTION.getCode());
            result.setMessage(ExceptionType.NOT_AUTHENTICATED_EXCEPTION.getMessage());
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().append(JSON.toJSONString(result)).flush();
        }
    }

    private AuthenticationSuccessHandler successHandler() {
        return (request, response, authentication) -> {
            String username =  request.getParameter("username");
            String failKey = "user:loginCount:fail:" + username;
            String lockKey = "user:loginTime:lock:"+username;
            boolean hasFail = redisTemplateUtil.hasKey(failKey);
            boolean hasLock = redisTemplateUtil.hasKey(lockKey);
            if (hasFail) {
                redisTemplateUtil.del(failKey);
            }
            if (hasLock) {
                redisTemplateUtil.del(lockKey);
            }
            request.getRequestDispatcher("/user/login").forward(request, response);
        };
    }

    private AuthenticationFailureHandler failureHandler() {
        return (request, response, exception) -> {
            log.error("err err err");
            System.out.println("err err err err");
            String username = request.getParameter("username");
            String failKey = "user:loginCount:fail:" + username;
            boolean hasFail = redisTemplateUtil.hasKey(failKey);
            if (hasFail) {
                Integer failCount = redisTemplateUtil.get(failKey).toString().toInt();
                if (failCount < 2) {
                    redisTemplateUtil.incr(failKey, 1);
                } else redisTemplateUtil.set("user:loginTime:lock:" + username, username, 60 * 30);
            }
            else {
                redisTemplateUtil.set("user:loginCount:fail:" + username, 1, 60 * 30);
            }
        };
    }

    static public class MyAuthenticationEntryPoint implements AuthenticationEntryPoint {
        @Override
        public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json; charset=utf-8");
            PrintWriter out = null;
            try {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> res = Maps.newHashMap();
                res.put("code", ExceptionType.NOT_AUTHENTICATED_EXCEPTION.getCode());
                res.put("message", ExceptionType.NOT_AUTHENTICATED_EXCEPTION.getMessage());
                res.put("data", request.getRequestURL().toString());
                out = response.getWriter();
                out.append(mapper.writeValueAsString(res));
            } catch (Exception e) {
                log.error("处理未登录异常, {}", e);
                response.sendError(500);
            }
        }

    }


    private CorsConfigurationSource CorsConfigurationSource() {
        UrlBasedCorsConfigurationSource source =   new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("http://127.0.0.1:8088");	//同源配置，*表示任何请求都视为同源，若需指定ip和端口可以改为如“localhost：8080”，多个以“，”分隔；
        corsConfiguration.addAllowedHeader("*");//header，允许哪些header，本案中使用的是token，此处可将*替换为token；
        corsConfiguration.addAllowedMethod("*");	//允许的请求方法，PSOT、GET等
        corsConfiguration.setAllowCredentials(true);
        source.registerCorsConfiguration("/**",corsConfiguration); //配置允许跨域访问的url
        return source;
    }



}
