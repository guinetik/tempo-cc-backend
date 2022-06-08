package com.guinetik.challenge.remote;

import com.guinetik.challenge.model.Team;
import com.guinetik.challenge.model.TeamDetails;
import com.guinetik.challenge.model.User;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
class RemoteApiServiceTest {
    RemoteApiService service;

    @Mock
    FeignApiFactory<RemoteTeamsApi> remoteTeamsApi;

    @Mock
    FeignApiFactory<RemoteUserApi> remoteUserApi;

    @Autowired
    RemoteApiConfig apiConfig;

    @Autowired
    FeignClientConfig feign;

    RemoteApiServiceTest() {

    }

    @Test
    void getAllTeams() {
        this.service = new RemoteApiService(apiConfig, feign);
        List<Team> allTeams = service.getAllTeams();
        assertNotNull(allTeams);
    }

    @Test
    void getAllTeamsButFail() {
        this.service = new RemoteApiService(apiConfig, feign);
        // mock so we throw an error when calling the api
        Mockito.when(this.remoteTeamsApi.execute(Mockito.anyString())).thenThrow(new RuntimeException());
        // proxying the api before replacing with the mock
        FeignApiFactory<RemoteTeamsApi> remoteTeamsApiProxy = service.remoteTeamsApi;
        // replacing the autowired api with the mocks
        service.remoteTeamsApi = this.remoteTeamsApi;
        // calling the service, expecting a null response since we mocked to fail
        List<Team> allTeams = service.getAllTeams();
        assertNull(allTeams);
        // putting the proxy back to its original place
        service.remoteTeamsApi = remoteTeamsApiProxy;
    }

    @Test
    void getTeamById() {
        this.service = new RemoteApiService(apiConfig, feign);
        TeamDetails teamById = service.getTeamById("7676a4bf-adfe-415c-941b-1739af07039b");
        assertNotNull(teamById);
    }

    @Test
    void getTeamByIdButFail() {
        this.service = new RemoteApiService(apiConfig, feign);
        // mock so we throw an error when calling the api
        Mockito.when(this.remoteTeamsApi.execute(Mockito.anyString(), Mockito.any())).thenThrow(new RuntimeException());
        // proxying the api before replacing with the mock
        FeignApiFactory<RemoteTeamsApi> remoteTeamsApiProxy = service.remoteTeamsApi;
        // replacing the autowired api with the mocks
        service.remoteTeamsApi = this.remoteTeamsApi;
        // calling the service, expecting a null response since we mocked to fail
        TeamDetails teamById = service.getTeamById("7676a4bf-adfe-415c-941b-1739af07039b");
        assertNull(teamById);
        // putting the proxy back to its original place
        service.remoteTeamsApi = remoteTeamsApiProxy;
    }

    @Test
    void getAllUsers() {
        this.service = new RemoteApiService(apiConfig, feign);
        List<User> allUsers = service.getAllUsers();
        assertNotNull(allUsers);
    }

    @Test
    void getAllUsersButFail() {
        this.service = new RemoteApiService(apiConfig, feign);
        // mock so we throw an error when calling the api
        Mockito.when(this.remoteUserApi.execute(Mockito.anyString())).thenThrow(new RuntimeException());
        // proxying the api before replacing with the mock
        FeignApiFactory<RemoteUserApi> remoteUserApiProxy = service.remoteUserApi;
        // replacing the autowired api with the mocks
        service.remoteUserApi = this.remoteUserApi;
        // calling the service, expecting a null response since we mocked to fail
        List<User> allUsers = service.getAllUsers();
        assertNull(allUsers);
        // putting the proxy back to its original place
        service.remoteUserApi = remoteUserApiProxy;
    }

    @Test
    void getUserById() {
        this.service = new RemoteApiService(apiConfig, feign);
        User user = service.getUserById("371d2ee8-cdf4-48cf-9ddb-04798b79ad9e");
        assertNotNull(user);
    }

    @Test
    void getUserByIdButFail() {
        this.service = new RemoteApiService(apiConfig, feign);
        // mock so we throw an error when calling the api
        Mockito.when(this.remoteUserApi.execute(Mockito.anyString(), Mockito.any())).thenThrow(new RuntimeException());
        // proxying the api before replacing with the mock
        FeignApiFactory<RemoteUserApi> remoteUserApiProxy = service.remoteUserApi;
        // replacing the autowired api with the mocks
        service.remoteUserApi = this.remoteUserApi;
        // calling the service, expecting a null response since we mocked to fail
        User user = service.getUserById("371d2ee8-cdf4-48cf-9ddb-04798b79ad9e");
        assertNull(user);
        // putting the proxy back to its original place
        service.remoteUserApi = remoteUserApiProxy;
    }
}
