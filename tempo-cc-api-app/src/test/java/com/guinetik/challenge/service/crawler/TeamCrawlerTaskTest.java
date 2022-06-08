package com.guinetik.challenge.service.crawler;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

class TeamCrawlerTaskTest {

    @Test
    void fetchTeam() throws ExecutionException, InterruptedException {
        TeamCrawlerTask task = new TeamCrawlerTask(null, null, null, null);
        CompletableFuture<Boolean> taskResult = task.fetchTeam("1");
        CompletableFuture.allOf(taskResult).join();
        assertFalse(taskResult.get());
    }
}
