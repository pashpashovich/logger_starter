package ru.clevertec.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "endpoint.logging")
public record EndpointLoggingProperties(boolean active) {}
