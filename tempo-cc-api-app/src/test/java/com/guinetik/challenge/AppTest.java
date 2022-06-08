package com.guinetik.challenge;

import com.guinetik.challenge.api.RolesApiDelegateImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class AppTest {

    @Autowired
    private RolesApiDelegateImpl rolesApiDelegate;

    @Test
    public void contextLoads() {
        assertNotNull(rolesApiDelegate);
    }
}
