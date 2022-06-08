package com.guinetik.challenge.remote;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class storing remote API properties
 */
@Configuration
public class RemoteApiConfig {
    @Value("${application.remoteapi.baseUrl}")
    protected String baseUrl;
    @Value("${application.feign.logger.folder}")
    protected String loggerFolder;
    @Value("${application.feign.connecttimeoutmillis}")
    protected Long connectTimeout;
    @Value("${application.feign.readtimeoutmillis}")
    protected Long readTimeout;
}
