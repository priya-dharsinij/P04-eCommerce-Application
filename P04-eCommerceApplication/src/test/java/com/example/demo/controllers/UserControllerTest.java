package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    private UserController userController;
    private UserRepository userRepository = mock(UserRepository.class);
    private CartRepository cartRepository = mock(CartRepository.class);
    private BCryptPasswordEncoder bCryptPasswordEncoder = mock(BCryptPasswordEncoder.class);


    @Before
    public void setUp() {
        userController = new UserController();
        TestUtils.injectObjects(userController,"userRepository",userRepository);
        TestUtils.injectObjects(userController,"cartRepository",cartRepository);
        TestUtils.injectObjects(userController,"bCryptPasswordEncoder",bCryptPasswordEncoder);
    }

    @Test
    public void testCreateUser(){
        //given
        when(bCryptPasswordEncoder.encode("password")).thenReturn("hashedPassword");
        CreateUserRequest r = new CreateUserRequest();
        r.setUsername("test");
        r.setPassword("password");
        r.setConfirmPassword("password");

        //when
        ResponseEntity<User> response = userController.createUser(r);

        //then
        assertEquals(200, response.getStatusCodeValue());

        User u = response.getBody();
        assertNotNull(u);
        assertEquals(0, u.getId());
        assertEquals(r.getUsername(), u.getUsername());
        assertEquals("hashedPassword", u.getPassword());
    }

    @Test
    public void testCreateUser_InvalidPassword() {
        //given
        CreateUserRequest r = new CreateUserRequest();
        r.setUsername("test");
        r.setPassword("pass");
        r.setConfirmPassword("pass");

        //when
        ResponseEntity<User> response = userController.createUser(r);

        //then
        assertEquals(400, response.getStatusCodeValue());

    }


    @Test
    public void testFindById(){
        //given
        User u = new User();
        u.setUsername("test");
        u.setId(1L);
        when(userRepository.findById(anyLong())).thenReturn(java.util.Optional.ofNullable(u));

        //when
        ResponseEntity<User> response = userController.findById(1L);

        User userById = response.getBody();

        //then
        assertNotNull(userById);
        assertEquals(u.getUsername(),userById.getUsername());
    }


    @Test
    public void testFindByUserName(){
        //given
        User u = new User();
        u.setId(1L);
        u.setUsername("test");
        when(userRepository.findByUsername(anyString())).thenReturn(u);

        //when
        ResponseEntity<User> response = userController.findByUserName("test");

        User userById = response.getBody();

        //then
        assertNotNull(userById);
        assertEquals(u.getUsername(),userById.getUsername());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testFindByUserNameWhenDoesNotExist() {
        //given
        when(userRepository.findByUsername(anyString())).thenReturn(null);

        //when
        ResponseEntity<User> response = userController.findByUserName("test");

        //then
        assertEquals(404, response.getStatusCodeValue());
    }

}

