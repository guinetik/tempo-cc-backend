package com.guinetik.challenge.service.crawler;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hibernate.annotations.Immutable;

@Getter
@AllArgsConstructor
@Immutable
public class CrawlerResult {
    private String name;
    private int itemsProcessed;
    private Long elapsedTime;
}
