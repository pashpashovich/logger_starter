package ru.clevertec.entity;


import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class HttpLog {
    private String method;
    private String uriEndpoint;
    private int status;
    private Map<String, String> requestHeaders;
    private Map<String, String> responseHeaders;
    private long executionTime;

    @Override
    public String toString() {
        return "\n=========================================\n" +
                "Request type: " + method + "\n" +
                "URI of endpoint: " + uriEndpoint + "\n" +
                "Status: " + status + "\n" +
                "Headers of request: " + requestHeaders + "\n" +
                "Headers of response: " + responseHeaders + "\n" +
                "Time of execution: " + executionTime + " ms" + "\n" +
                "=========================================";
    }
}
