package com.guinetik.challenge;

import com.guinetik.challenge.service.crawler.TeamCrawlerService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.concurrent.Executor;

import static springfox.documentation.spi.DocumentationType.SWAGGER_2;

@SpringBootApplication
@EnableAsync
@EnableSwagger2
public class App {

    @Value("${application.feature.toggle.crawler.enabled}")
    private boolean isCrawlerEnabled;
    @Value("${application.feature.toggle.crawler.poolsize}")
    private int crawlerPoolSize;
    @Value("${application.feature.toggle.crawler.poolsize.max}")
    private int crawlerMaxPoolSize;
    @Value("${application.feature.toggle.crawler.queue.capacity}")
    private int crawlerQueueCapacity;
    private final TeamCrawlerService teamCrawler;

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    public App(TeamCrawlerService serviceEnforcer) {
        this.teamCrawler = serviceEnforcer;
    }

    @Bean
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(crawlerPoolSize);
        executor.setMaxPoolSize(crawlerMaxPoolSize);
        executor.setQueueCapacity(crawlerQueueCapacity);
        executor.setThreadNamePrefix("TeamCrawlerThreads-");
        executor.initialize();
        return executor;
    }

    @Bean
    public Docket swagger() {
        return new Docket(SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build();
    }

    @EventListener(ApplicationReadyEvent.class)
    public void fetchTeams() {
        if (isCrawlerEnabled) teamCrawler.fetchTeams();
    }
}
