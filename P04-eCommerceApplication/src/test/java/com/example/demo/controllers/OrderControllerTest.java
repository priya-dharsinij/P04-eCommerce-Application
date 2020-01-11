package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {
    private OrderController orderController;
    private UserRepository userRepository = mock(UserRepository.class);
    private OrderRepository orderRepository = mock(OrderRepository.class);

    @Before
    public void setUp() {
        orderController = new OrderController();
        TestUtils.injectObjects(orderController,"userRepository",userRepository);
        TestUtils.injectObjects(orderController,"orderRepository",orderRepository);
    }

    @Test
    public void testSubmit(){
        //given
        User u = new User();
        Item item = new Item();
        item.setPrice(BigDecimal.valueOf(2.99));
        Cart cart = new Cart();
        cart.setUser(u);
        cart.addItem(item);
        u.setCart(cart);
        when(userRepository.findByUsername(anyString())).thenReturn(u);
        //when
        ResponseEntity<UserOrder> response = orderController.submit("test");
        //then
        assertEquals(200,response.getStatusCodeValue());
        UserOrder userOrder = response.getBody();
        assertEquals(item.getPrice(),userOrder.getTotal());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testSubmitWhenUserDoesNotExist(){
        //given
        when(userRepository.findByUsername(anyString())).thenReturn(null);
        //when
        ResponseEntity<UserOrder> response = orderController.submit("test");

        //then
        assertEquals(404,response.getStatusCodeValue());
    }

    @Test
    public void testGetOrdersForUser(){
        //given
        User u = new User();
        u.setUsername("test");
        u.setId(1L);
        when(userRepository.findByUsername(anyString())).thenReturn(u);
        UserOrder userOrder1 = new UserOrder();
        userOrder1.setUser(u);
        UserOrder userOrder2 = new UserOrder();
        userOrder2.setUser(u);
        when(orderRepository.findByUser(any())).thenReturn(Arrays.asList(userOrder1,userOrder2));

        //when
        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("test");
        //then
        assertEquals(2,response.getBody().size());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testGetOrdersForUserWhenUserDoesNotExist(){
        //given
        when(userRepository.findByUsername(anyString())).thenReturn(null);
        //when
        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("test");
        //then
        assertEquals(404,response.getStatusCodeValue());
    }

}
