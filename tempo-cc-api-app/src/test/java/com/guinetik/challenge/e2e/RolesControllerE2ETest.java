package com.guinetik.challenge.e2e;

import com.guinetik.challenge.model.Role;
import com.guinetik.challenge.model.RoleRelationship;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RolesControllerE2ETest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private RestTemplate patchRestTemplate;

    private static Long roleId;

    @Test
    @Order(1)
    void createRole() {
        Role role = new Role();
        role.setName("Engineer");
        String resourceUrl = "http://localhost:" + port + "/tempo-cc-backend/roles";
        Role roleResponse = this.restTemplate.postForObject(resourceUrl, role, Role.class);
        this.roleId = roleResponse.getId();
        assertEquals(role.getName(), roleResponse.getName());
    }

    @Test
    @Order(2)
    void assignRole() throws JsonProcessingException {
        this.patchRestTemplate = restTemplate.getRestTemplate();
        HttpClient httpClient = HttpClientBuilder.create().build();
        this.patchRestTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(httpClient));
        //
        RoleRelationship roleRelationship = new RoleRelationship();
        roleRelationship.setRoleId(roleId);
        roleRelationship.setUserId("ed6b0199-b442-4734-8aa4-c63f5e6046e7");
        roleRelationship.setTeamId("c02f6b75-641a-40eb-8294-d89550cb2395");
        //
        String resourceUrl = "http://localhost:" + port + "/tempo-cc-backend/roles";
        //
        ResponseEntity responseEntity = patchRestTemplate.exchange(resourceUrl,
                HttpMethod.PATCH,
                getPostRequestHeaders(
                        new ObjectMapper().writeValueAsString(roleRelationship)
                ),
                RoleRelationship.class);
        //
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    @Order(3)
    void LookUpARoleForAMembership() {
        String resourceUrl = "http://localhost:" + port + "/tempo-cc-backend/roles?userId=ed6b0199-b442-4734-8aa4-c63f5e6046e7&teamId=c02f6b75-641a-40eb-8294-d89550cb2395";
        assertThat(this.restTemplate.getForObject(resourceUrl, String.class)).contains("roleId\":"+roleId);
    }

    @Test
    @Order(4)
    void LookUpMembershipsForARole() {
        String resourceUrl = "http://localhost:" + port + "/tempo-cc-backend/roles/" + roleId;
        assertThat(this.restTemplate.getForObject(resourceUrl, String.class)).contains("roleId\":"+roleId);
    }


    public HttpEntity getPostRequestHeaders(String jsonPostBody) {
        List acceptTypes = new ArrayList();
        acceptTypes.add(MediaType.APPLICATION_JSON_UTF8);

        HttpHeaders reqHeaders = new HttpHeaders();
        reqHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
        reqHeaders.setAccept(acceptTypes);

        return new HttpEntity(jsonPostBody, reqHeaders);
    }
}
