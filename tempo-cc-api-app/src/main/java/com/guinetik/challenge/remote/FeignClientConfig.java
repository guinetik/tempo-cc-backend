package com.guinetik.challenge.remote;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Feign;
import feign.Logger;
import feign.Request;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;

/**
 * Small component responsible for creating Feign Api client instances
 * I know that OpenFeign provides a spring boot starter. However, there's a point where inversion of control gets in the way too much.
 * In this case, from my experience I don't mind having a single configuration class
 * where I can fine tune all aspects of how my app is going to interact with a remote API.
 * The declarative aspects of the OpenFeign framework already does so much of the heavy lifting (think of how much more code you would write using RequestTemplate in comparison)
 * And in this class we leverage most of the configuration options available
 */
@Component
public class FeignClientConfig {
    Feign.Builder builder;

    private final RemoteApiConfig apiConfig;

    public FeignClientConfig(RemoteApiConfig apiConfig) {
        this.apiConfig = apiConfig;
    }

    public Feign.Builder createDefaultWithJacksonMappers(ObjectMapper encoderMapper, ObjectMapper decoderMapper, String logName) {
        this.builder = Feign.builder();
        FeignClientConfig config = this
                .withJacksonEncoder(encoderMapper)
                .withJacksonDecoder(decoderMapper)
                .withLogLevelFULL()
                .withCustomTimeout()
                .logToFile(getLogName(logName));
        return config.getBuilder();
    }

    public static String getLogName(String name) {
        if (name != null) {
            return name + "@" + new Date().getTime() + ".log";
        }
        return "";
    }

    public Feign.Builder createDefault(String logName) {
        this.builder = Feign.builder();
        return createDefaultWithJacksonMappers(null, null, logName);
    }

    public FeignClientConfig withJacksonEncoder(ObjectMapper mapper) {
        if (mapper == null)
            this.builder.encoder(new JacksonEncoder());
        else
            this.builder.encoder(new JacksonEncoder(mapper));
        return this;
    }

    public FeignClientConfig withJacksonDecoder(ObjectMapper mapper) {
        if (mapper == null) {
            this.builder.decoder(new JacksonDecoder());
        } else {
            this.builder.decoder(new JacksonDecoder(mapper));
        }

        return this;
    }

    public FeignClientConfig withLogLevelFULL() {
        this.builder.logLevel(Logger.Level.FULL);
        return this;
    }

    public FeignClientConfig logToFile(String fileName) {
        String logFolder = apiConfig.loggerFolder;
        boolean k = false;
        if (fileName != null && !fileName.isEmpty() && !logFolder.isEmpty()) {
            try {
                Path p = new File(logFolder).toPath();
                Files.createDirectories(p);
                if (p.toFile().isDirectory() && Files.isWritable(p)) {
                    this.builder.logger(new Logger.JavaLogger().appendToFile(logFolder + fileName));
                    k = true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (!k) this.builder.logger(new Logger.ErrorLogger());
        return this;
    }

    public Feign.Builder getBuilder() {
        return this.builder;
    }

    private FeignClientConfig withCustomTimeout() {
        Long connectTimeoutMillis = apiConfig.connectTimeout;
        Long readTimeoutMillis = apiConfig.readTimeout;

        if (connectTimeoutMillis == null) {
            connectTimeoutMillis = 10000L;
        }
        if (readTimeoutMillis == null) {
            readTimeoutMillis = 80000L;
        }

        Request.Options options = new Request.Options(connectTimeoutMillis.intValue(), readTimeoutMillis.intValue());
        this.builder.options(options);
        return this;
    }
}
