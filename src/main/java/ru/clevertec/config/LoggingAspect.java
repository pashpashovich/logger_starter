package ru.clevertec.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import ru.clevertec.entity.HttpLog;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Slf4j
public class LoggingAspect {

    private static final String START_TIME_ATTRIBUTE = "startTime";

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void restControllerPointcut() {
    }

    @Before("restControllerPointcut()")
    public void logBefore(JoinPoint joinPoint) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            long start = System.currentTimeMillis();
            request.setAttribute(START_TIME_ATTRIBUTE, start);
        }
    }

    @AfterReturning(pointcut = "restControllerPointcut()", returning = "response")
    public void logAfterReturning(JoinPoint joinPoint, Object response) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            HttpServletResponse servletResponse = attributes.getResponse();

            if (request != null && servletResponse != null) {
                long start = (long) request.getAttribute(START_TIME_ATTRIBUTE);
                long end = System.currentTimeMillis();
                long duration = end - start;

                String method = request.getMethod();
                String requestURL = request.getRequestURL().toString();
                int statusCode = servletResponse.getStatus();

                Map<String, String> requestHeaders = getHeaders(request);
                Map<String, String> responseHeaders = getHeaders(servletResponse);

                HttpLog httpLog = createMessage(method, requestURL, statusCode, duration, requestHeaders, responseHeaders);

                log.info("HTTP Log: {}", httpLog);
            }
        }
    }

    private Map<String, String> getHeaders(HttpServletRequest request) {
        Map<String, String> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames != null && headerNames.hasMoreElements()) {
            String key = headerNames.nextElement();
            String value = request.getHeader(key);
            headers.put(key, value);
        }
        return headers;
    }

    private Map<String, String> getHeaders(HttpServletResponse response) {
        Map<String, String> headers = new HashMap<>();
        for (String header : response.getHeaderNames()) {
            String value = response.getHeader(header);
            headers.put(header, value);
        }
        return headers;
    }

    private HttpLog createMessage(String method, String requestURL, int statusCode, long duration,
                                  Map<String, String> requestHeaders,
                                  Map<String, String> responseHeaders) {
        HttpLog httpLog = new HttpLog();
        httpLog.setMethod(method);
        httpLog.setUriEndpoint(requestURL);
        httpLog.setStatus(statusCode);
        httpLog.setRequestHeaders(requestHeaders);
        httpLog.setResponseHeaders(responseHeaders);
        httpLog.setExecutionTime(duration);
        return httpLog;
    }
}
