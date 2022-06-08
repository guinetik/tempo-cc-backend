package com.guinetik.challenge.remote;

import com.guinetik.challenge.model.TeamDetails;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class FeignApiFactoryTest {

    @Autowired
    private RemoteApiConfig apiConfig;
    @Autowired
    private FeignClientConfig feign;

    @Test
    void execute() {
        FeignApiFactory<RemoteTeamsApi> remoteTeamsApi = new FeignApiFactory<>(RemoteTeamsApi.class, apiConfig, feign);
        TeamDetails result = remoteTeamsApi.execute("getTeamById", "7676a4bf-adfe-415c-941b-1739af07039b");
        assertNotNull(result);
    }

    @Test
    void executeButThrowError() {
        FeignApiFactory<RemoteTeamsApi> remoteTeamsApi = new FeignApiFactory<>(RemoteTeamsApi.class, apiConfig, feign);
        assertThrows(RemoteApiException.class, () -> remoteTeamsApi.execute("THISMETHODDOESNTEXLIST"));
    }
}
