package com.guinetik.challenge.service.crawler;

import com.guinetik.challenge.model.Team;
import com.guinetik.challenge.remote.RemoteApiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class TeamCrawlerService {

    private static final Logger logger = LoggerFactory.getLogger(TeamCrawlerService.class);
    private final RemoteApiService apiService;

    private final BeanFactory beanFactory;

    public TeamCrawlerService(RemoteApiService apiService, BeanFactory beanFactory) {
        this.apiService = apiService;
        this.beanFactory = beanFactory;
    }

    public CrawlerResult fetchTeams() {
        long start = System.currentTimeMillis();
        List<Team> teams = apiService.getAllTeams();
        List<CompletableFuture<Boolean>> tasks = new ArrayList<>();
        teams.forEach(team -> {
            logger.info("fetching team: " + team.getId());
            final TeamCrawlerTask task = beanFactory.getBean(TeamCrawlerTask.class);
            CompletableFuture<Boolean> teamFetched = task.fetchTeam(team.getId());
            tasks.add(teamFetched);
        });
        CompletableFuture.allOf(tasks.toArray(new CompletableFuture<?>[0])).join();
        long elapsedTime = (System.currentTimeMillis() - start);
        CrawlerResult result = new CrawlerResult("fetchTeams", teams.size(), elapsedTime);
        logger.info("Elapsed time: " + elapsedTime);
        return result;
    }

}
