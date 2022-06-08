package com.guinetik.challenge.remote;

import com.guinetik.challenge.model.Team;
import com.guinetik.challenge.model.TeamDetails;
import com.guinetik.challenge.model.User;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service used to simplify communication with the remote API.
 * It wasn't clear for me in the specs how I should handle failures here,
 * so I'm keeping it simple, surrounding with try/catch and returning null on catch
 * trusting that the method callers will null check results for me.
 * Also all requests are logged by feign in /var/app/log so I could go there or send it to Kibana to debug in production.
 */
@Service
public class RemoteApiService implements RemoteTeamsApi, RemoteUserApi {

    FeignApiFactory<RemoteTeamsApi> remoteTeamsApi;
    FeignApiFactory<RemoteUserApi> remoteUserApi;

    public RemoteApiService(RemoteApiConfig apiConfig, FeignClientConfig feign) {
        remoteTeamsApi = new FeignApiFactory<>(RemoteTeamsApi.class, apiConfig, feign);
        remoteUserApi = new FeignApiFactory<>(RemoteUserApi.class, apiConfig, feign);
    }

    @Cacheable(value = "allTeamsCache", key = "'allTeamsCache'", unless="#result == null")
    @Override
    public List<Team> getAllTeams() {
        try {
            return remoteTeamsApi.execute("getAllTeams");
        } catch (Exception e) {
            return null;
        }
    }

    @Cacheable(value="getTeamById", key="#teamId", unless="#result == null")
    @Override
    public TeamDetails getTeamById(String teamId) {
        try {
            return remoteTeamsApi.execute("getTeamById", teamId);
        } catch (Exception e) {
            return null;
        }
    }

    @Cacheable(value = "getAllUsers", unless="#result == null")
    @Override
    public List<User> getAllUsers() {
        try {
            return remoteUserApi.execute("getAllUsers");
        } catch (Exception e) {
            return null;
        }
    }

    @Cacheable(value="getUserById", key="#userId", unless="#result == null")
    @Override
    public User getUserById(String userId) {
        try {
            return remoteUserApi.execute("getUserById", userId);
        } catch (Exception e) {
            return null;
        }
    }
}
