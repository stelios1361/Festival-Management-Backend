/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.festivalmanager.service;

import com.festivalmanager.dto.api.ApiResponse;
import com.festivalmanager.dto.user.DeleteUserRequest;
import com.festivalmanager.dto.user.LoginRequest;
import com.festivalmanager.dto.user.LogoutRequest;
import com.festivalmanager.dto.user.RegisterRequest;
import com.festivalmanager.dto.user.UpdateAccountStatusRequest;
import com.festivalmanager.dto.user.UpdateInfoRequest;
import com.festivalmanager.dto.user.UpdatePasswordRequest;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Stelios_pc
 */
public class UserServiceTest {
    
    public UserServiceTest() {
    }
    
    @BeforeAll
    public static void setUpClass() {
    }
    
    @AfterAll
    public static void tearDownClass() {
    }
    
    @BeforeEach
    public void setUp() {
    }
    
    @AfterEach
    public void tearDown() {
    }

    /**
     * Test of registerUser method, of class UserService.
     */
    @Test
    public void testRegisterUser() {
        System.out.println("registerUser");
        RegisterRequest request = null;
        UserService instance = null;
        ApiResponse<Map<String, Object>> expResult = null;
        ApiResponse<Map<String, Object>> result = instance.registerUser(request);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of loginUser method, of class UserService.
     */
    @Test
    public void testLoginUser() {
        System.out.println("loginUser");
        LoginRequest request = null;
        UserService instance = null;
        ApiResponse<Map<String, Object>> expResult = null;
        ApiResponse<Map<String, Object>> result = instance.loginUser(request);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of updateUserInfo method, of class UserService.
     */
    @Test
    public void testUpdateUserInfo() {
        System.out.println("updateUserInfo");
        UpdateInfoRequest request = null;
        UserService instance = null;
        ApiResponse<Map<String, Object>> expResult = null;
        ApiResponse<Map<String, Object>> result = instance.updateUserInfo(request);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of updateUserPassword method, of class UserService.
     */
    @Test
    public void testUpdateUserPassword() {
        System.out.println("updateUserPassword");
        UpdatePasswordRequest request = null;
        UserService instance = null;
        ApiResponse<Map<String, Object>> expResult = null;
        ApiResponse<Map<String, Object>> result = instance.updateUserPassword(request);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of updateAccountStatus method, of class UserService.
     */
    @Test
    public void testUpdateAccountStatus() {
        System.out.println("updateAccountStatus");
        UpdateAccountStatusRequest request = null;
        UserService instance = null;
        ApiResponse<Map<String, Object>> expResult = null;
        ApiResponse<Map<String, Object>> result = instance.updateAccountStatus(request);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of deleteUser method, of class UserService.
     */
    @Test
    public void testDeleteUser() {
        System.out.println("deleteUser");
        DeleteUserRequest request = null;
        UserService instance = null;
        ApiResponse<Map<String, Object>> expResult = null;
        ApiResponse<Map<String, Object>> result = instance.deleteUser(request);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of logOutUser method, of class UserService.
     */
    @Test
    public void testLogOutUser() {
        System.out.println("logOutUser");
        LogoutRequest request = null;
        UserService instance = null;
        ApiResponse<Map<String, Object>> expResult = null;
        ApiResponse<Map<String, Object>> result = instance.logOutUser(request);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
