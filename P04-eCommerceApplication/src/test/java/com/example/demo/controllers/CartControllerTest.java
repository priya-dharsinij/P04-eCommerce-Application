package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTest {

    private CartController cartController;
    private UserRepository userRepository = mock(UserRepository.class);
    private ItemRepository itemRepository = mock(ItemRepository.class);
    private CartRepository cartRepository = mock(CartRepository.class);

    @Before
    public void setUp() {
        cartController = new CartController();
        TestUtils.injectObjects(cartController,"userRepository",userRepository);
        TestUtils.injectObjects(cartController,"itemRepository",itemRepository);
        TestUtils.injectObjects(cartController,"cartRepository",cartRepository);
    }

    @Test
    public void testAddToCart(){
       //given
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setItemId(1L);
        modifyCartRequest.setQuantity(2);
        modifyCartRequest.setUsername("test");

        User u = new User();
        Cart cart = new Cart();
        cart.setUser(u);
        u.setCart(cart);
        when(userRepository.findByUsername(anyString())).thenReturn(u);

        Item item = new Item();
        item.setPrice(BigDecimal.valueOf(2.99));
        when(itemRepository.findById(anyLong())).thenReturn(java.util.Optional.ofNullable(item));

        //when
        ResponseEntity<Cart> response = cartController.addTocart(modifyCartRequest);

        //then
        assertEquals(200,response.getStatusCodeValue());
        Cart c = response.getBody();
        assertEquals(modifyCartRequest.getQuantity(),c.getItems().size());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testAddToCartWhenUserDoesNotExist(){
        //given
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername("test");
        when(userRepository.findByUsername(anyString())).thenReturn(null);

        //when
        ResponseEntity<Cart> response = cartController.addTocart(modifyCartRequest);

        //then
        assertEquals(404,response.getStatusCodeValue());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testAddToCartWhenItemDoesNotExist(){
        //given
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());
        //when
        ResponseEntity<Cart> response = cartController.addTocart(modifyCartRequest);
        //then
        assertEquals(404,response.getStatusCodeValue());
    }

    @Test
    public void testRemoveFromCart(){
        //given
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername("test");
        modifyCartRequest.setQuantity(2);
        User u = new User();
        Item item = new Item();
        item.setPrice(BigDecimal.valueOf(2.99));
        Cart cart = new Cart();
        cart.setUser(u);
        cart.addItem(item);
        cart.addItem(item);
        u.setCart(cart);
        when(userRepository.findByUsername(anyString())).thenReturn(u);
        when(itemRepository.findById(anyLong())).thenReturn(java.util.Optional.ofNullable(item));

        //when
        ResponseEntity<Cart> response = cartController.removeFromcart(modifyCartRequest);
        //then
        assertEquals(200,response.getStatusCodeValue());
        Cart c = response.getBody();
        assertEquals(0,c.getItems().size());

    }
}
