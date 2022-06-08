package com.guinetik.challenge.service;

import com.guinetik.challenge.entity.*;
import com.guinetik.challenge.model.Role;
import com.guinetik.challenge.model.RoleRelationship;
import com.guinetik.challenge.model.TeamDetails;
import com.guinetik.challenge.model.User;
import com.guinetik.challenge.remote.RemoteApiService;
import com.guinetik.challenge.service.exeptions.EmptyRoleException;
import com.guinetik.challenge.service.exeptions.ServiceException;
import com.guinetik.challenge.service.exeptions.ServiceRequestException;
import com.guinetik.challenge.util.ServiceUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class RolesServiceTest {

    @MockBean
    private RoleRepository rolesRepo;
    @MockBean
    private TeamRoleRepository teamRoleRepo;
    @MockBean
    private TeamRepository teamRepo;
    @MockBean
    private UserRepository userRepo;
    @MockBean
    private RemoteApiService apiService;
    private String userId;
    private String teamId;
    private RoleEntity roleEntity;
    private Role role;
    private String roleName;
    private UserEntity userEntity;
    private TeamEntity teamEntity;
    private TeamRoleEntity teamRoleEntity;
    private User userModel;
    private TeamDetails teamModel;
    private Long roleId;

    @BeforeEach
    void setUp() {
        userId = "1";
        teamId = "1";
        roleEntity = new RoleEntity();
        role = new Role();
        roleName = "Developer";
        userEntity = new UserEntity();
        teamEntity = new TeamEntity();
        teamRoleEntity = new TeamRoleEntity();
        userModel = new User();
        teamModel = new TeamDetails();
        roleId = 1L;
    }

    @Test
    void createRole() {
        roleEntity.setId(1L);
        roleEntity.setName(roleName);
        //
        role.setId(1L);
        role.setName(roleName);
        //
        Mockito.when(rolesRepo.save(Mockito.any())).thenReturn(roleEntity);
        RolesService rolesService = new RolesService(this.rolesRepo, this.teamRoleRepo, this.teamRepo, this.userRepo, this.apiService);
        Role result = rolesService.createRole(roleName);
        assertEquals(role, result);
    }

    @Test
    void createRoleButThrowsError() {
        Mockito.when(rolesRepo.save(Mockito.any())).thenThrow(new RuntimeException());
        RolesService rolesService = new RolesService(this.rolesRepo, this.teamRoleRepo, this.teamRepo, this.userRepo, this.apiService);
        assertThrows(ServiceException.class, () -> rolesService.createRole(roleName));
    }

    @Test
    void getRoleByMembership() {
        //
        roleEntity.setId(1L);
        roleEntity.setName(roleName);
        //
        userEntity.setUserId(userId);
        userEntity.setId(1L);
        teamEntity.setTeamId(teamId);
        teamEntity.setId(1L);
        //
        teamRoleEntity.setId(1L);
        teamRoleEntity.setRole(roleEntity);
        teamRoleEntity.setTeam(teamEntity);
        teamRoleEntity.setUser(userEntity);
        //
        userModel.setId(userId);
        teamModel.setId(teamId);
        //
        Mockito.when(apiService.getUserById(userId)).thenReturn(userModel);
        Mockito.when(apiService.getTeamById(teamId)).thenReturn(teamModel);
        Mockito.when(teamRoleRepo.findFirstByTeamAndUser(teamEntity, userEntity)).thenReturn(teamRoleEntity);
        Mockito.when(userRepo.existsUserEntityByUserId(userId)).thenReturn(true);
        Mockito.when(userRepo.findFirstByUserId(userId)).thenReturn(userEntity);
        Mockito.when(userRepo.save(userEntity)).thenReturn(userEntity);
        Mockito.when(teamRepo.existsTeamEntityByTeamId(teamId)).thenReturn(true);
        Mockito.when(teamRepo.findFirstByTeamId(teamId)).thenReturn(teamEntity);
        //
        RolesService rolesService = new RolesService(this.rolesRepo, this.teamRoleRepo, this.teamRepo, this.userRepo, this.apiService);
        RoleRelationship roleByMembership = rolesService.getRoleByMembership(userId, teamId);
        assertEquals(teamRoleEntity.getRole().getId(), roleByMembership.getRoleId());
        assertEquals(teamRoleEntity.getTeam().getTeamId(), roleByMembership.getTeamId());
        assertEquals(teamRoleEntity.getUser().getUserId(), roleByMembership.getUserId());
        // failsafe coverage
        Mockito.when(teamRoleRepo.findFirstByTeamAndUser(Mockito.any(), Mockito.any())).thenThrow(new RuntimeException());
        assertThrows(ServiceException.class, () -> rolesService.getRoleByMembership(userId, teamId));
    }

    @Test
    void getRoleByMembershipButThrowsError() {
        roleEntity.setId(1L);
        roleEntity.setName(roleName);
        //
        userEntity.setUserId(userId);
        userEntity.setId(1L);
        //
        teamEntity.setTeamId(teamId);
        teamEntity.setId(1L);
        //
        teamRoleEntity.setId(1L);
        teamRoleEntity.setRole(roleEntity);
        teamRoleEntity.setTeam(teamEntity);
        teamRoleEntity.setUser(userEntity);
        //
        Mockito.when(apiService.getUserById(userId)).thenReturn(userModel);
        Mockito.when(apiService.getTeamById(teamId)).thenReturn(teamModel);
        Mockito.when(teamRoleRepo.findFirstByTeamAndUser(Mockito.any(), Mockito.any())).thenThrow(new RuntimeException());
        Mockito.when(userRepo.existsUserEntityByUserId(userId)).thenReturn(true);
        Mockito.when(userRepo.findFirstByUserId(userId)).thenReturn(userEntity);
        Mockito.when(userRepo.save(userEntity)).thenReturn(userEntity);
        Mockito.when(teamRepo.existsTeamEntityByTeamId(teamId)).thenReturn(true);
        Mockito.when(teamRepo.findFirstByTeamId(teamId)).thenReturn(teamEntity);
        RolesService rolesService = new RolesService(this.rolesRepo, this.teamRoleRepo, this.teamRepo, this.userRepo, this.apiService);
        assertThrows(ServiceRequestException.class, () -> rolesService.getRoleByMembership(userId, teamId));
    }

    @Test
    void getMembershipsByRole() {
        List<TeamRoleEntity> result = Collections.emptyList();
        Mockito.when(teamRoleRepo.findAllByRoleId(roleId)).thenReturn(result);
        Mockito.when(rolesRepo.findById(roleId)).thenReturn(Optional.of(new RoleEntity()));
        RolesService rolesService = new RolesService(this.rolesRepo, this.teamRoleRepo, this.teamRepo, this.userRepo, this.apiService);
        assertThrows(EmptyRoleException.class, () -> rolesService.getMembershipsByRole(roleId));
    }

    @Test
    void getMembershipsByRoleButThrowsError() {
        Mockito.when(teamRoleRepo.findAllByRoleId(roleId)).thenThrow(new RuntimeException());
        RolesService rolesService = new RolesService(this.rolesRepo, this.teamRoleRepo, this.teamRepo, this.userRepo, this.apiService);
        assertThrows(ServiceException.class, () -> rolesService.getMembershipsByRole(roleId));
    }

    @Test
    void assignRole() {
        roleEntity.setId(1L);
        roleEntity.setName(roleName);
        //
        userEntity.setUserId(userId);
        userEntity.setId(1L);
        //
        teamEntity.setTeamId(teamId);
        teamEntity.setId(1L);
        //
        teamRoleEntity.setRole(roleEntity);
        teamRoleEntity.setTeam(teamEntity);
        teamRoleEntity.setUser(userEntity);
        //
        teamModel.setId(teamId);
        teamModel.setName("Test");
        teamModel.setTeamLeadId(userId);
        teamModel.setTeamMemberIds(Collections.singletonList(userId));
        Mockito.when(rolesRepo.findById(1L)).thenReturn(Optional.of(roleEntity));
        Mockito.when(apiService.getUserById(userId)).thenReturn(userModel);
        Mockito.when(apiService.getTeamById(teamId)).thenReturn(teamModel);
        Mockito.when(teamRoleRepo.findFirstByTeamAndUser(teamEntity, userEntity)).thenReturn(teamRoleEntity);
        Mockito.when(userRepo.findFirstByUserId(userId)).thenReturn(userEntity);
        Mockito.when(userRepo.save(Mockito.any())).thenReturn(userEntity);
        Mockito.when(teamRepo.existsTeamEntityByTeamId(teamId)).thenReturn(true);
        Mockito.when(teamRepo.findFirstByTeamId(teamId)).thenReturn(teamEntity);
        Mockito.when(teamRoleRepo.existsByTeamAndUserAndRole(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(false);
        Mockito.when(teamRoleRepo.existsByTeamAndUser(Mockito.any(), Mockito.any())).thenReturn(true);
        Mockito.doNothing().when(teamRoleRepo).updateRoleRelationship(Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.when(teamRoleRepo.save(Mockito.any())).thenReturn(teamRoleEntity);
        RolesService rolesService = new RolesService(this.rolesRepo, this.teamRoleRepo, this.teamRepo, this.userRepo, this.apiService);
        RoleRelationship roleRelationship = rolesService.assignRole(roleEntity.getId(), userId, teamId);
        assertEquals(userId, roleRelationship.getUserId());
        assertEquals(teamId, roleRelationship.getTeamId());
        // failsafe coverage
        Mockito.when(teamRoleRepo.save(Mockito.any())).thenThrow(new RuntimeException());
        Mockito.when(teamRoleRepo.existsByTeamAndUser(Mockito.any(), Mockito.any())).thenReturn(false);
        assertThrows(ServiceException.class, () -> rolesService.assignRole(roleEntity.getId(), userId, teamId));
        // covering for when user is not assigned for the team
        Mockito.when(teamRoleRepo.existsByTeamAndUserAndRole(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(true);
        assertThrows(ServiceException.class, () -> rolesService.assignRole(roleEntity.getId(), userId, teamId));
        // covering for when user is already assigned this role
        teamModel.setTeamMemberIds(Collections.emptyList());
        Mockito.when(apiService.getTeamById(teamId)).thenReturn(teamModel);
        assertThrows(ServiceException.class, () -> rolesService.assignRole(roleEntity.getId(), userId, teamId));
    }

    @Test
    public void findAndValidateUserEntityButDontFindUser() {
        Mockito.when(this.apiService.getUserById(userId)).thenReturn(null);
        ServiceUtils serviceUtils = new ServiceUtils(this.teamRoleRepo, this.apiService, this.userRepo, this.teamRepo);
        assertThrows(ServiceRequestException.class, () -> serviceUtils.findAndValidateUserEntity(userId));
    }

    @Test
    public void findAndValidateTeamEntityButTeamIsNull() {
        ServiceUtils serviceUtils = new ServiceUtils(this.teamRoleRepo, this.apiService, this.userRepo, this.teamRepo);
        assertThrows(ServiceRequestException.class, () -> serviceUtils.findAndValidateTeamEntity(null));
    }

    @Test
    public void findAndValidateTeamEntityButCreateTeamOnDb() {
        TeamEntity teamEntity = new TeamEntity();
        teamEntity.setTeamId(teamId);
        teamEntity.setId(1L);
        Mockito.when(teamRepo.existsTeamEntityByTeamId(teamId)).thenReturn(false);
        Mockito.when(teamRepo.save(Mockito.any())).thenReturn(teamEntity);
        ServiceUtils serviceUtils = new ServiceUtils(this.teamRoleRepo, this.apiService, this.userRepo, this.teamRepo);
        serviceUtils.findAndValidateTeamEntity(teamId);
    }
}
