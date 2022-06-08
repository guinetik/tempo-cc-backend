package com.guinetik.challenge.service.crawler;

import com.guinetik.challenge.model.Team;
import com.guinetik.challenge.remote.RemoteApiService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class TeamCrawlerServiceTest {

    @Autowired
    private BeanFactory beanFactory;

    @Mock
    private RemoteApiService apiService;

    private TeamCrawlerService service;

    @Test
    void fetchTeams() {
        Team t = new Team();
        t.setId("c02f6b75-641a-40eb-8294-d89550cb2395");
        List<Team> mockTeams = Collections.singletonList(t);
        Mockito.when(apiService.getAllTeams()).thenReturn(mockTeams);
        this.service = new TeamCrawlerService(apiService, beanFactory);
        CrawlerResult crawlerResult = this.service.fetchTeams();
        assertNotNull(crawlerResult);
        assertEquals(1, crawlerResult.getItemsProcessed());
        System.out.println(crawlerResult.getName());
        System.out.println(crawlerResult.getElapsedTime());
    }
}
