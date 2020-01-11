package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {
    private ItemController itemController;
    private ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void setUp(){
        itemController = new ItemController();
        TestUtils.injectObjects(itemController,"itemRepository",itemRepository);
    }

    @Test
    public void testGetItems(){
        //given
        Item item1 = new Item();
        Item item2 = new Item();
        List<Item> items = new ArrayList<>(Arrays.asList(item1,item2));
        when(itemRepository.findAll()).thenReturn(items);
        
       // when
        ResponseEntity<List<Item>> response = itemController.getItems();
        List<Item> itemList = response.getBody();

        //then
        assertNotNull(itemList);
        assertEquals(2,itemList.size());
    }

    @Test
    public void testGetItemById() {
        //given
        Item item = new Item();
        item.setId(1L);
        item.setName("Round Widget");
        when(itemRepository.findById(anyLong())).thenReturn(java.util.Optional.ofNullable(item));

        //when
        ResponseEntity<Item> response = itemController.getItemById(1L);

        Item itemById = response.getBody();

        //then
        assertNotNull(itemById);
        assertEquals(item.getName(),itemById.getName());
    }

    @Test
    public void testGetItemsByName() {

        //given
        Item item1 = new Item();
        item1.setName("Round Widget");
        Item item2 = new Item();
        item2.setName("Round Widget");

        List<Item> items = new ArrayList<>(Arrays.asList(item1,item2));
        when(itemRepository.findByName(anyString())).thenReturn(items);

        //when
        ResponseEntity<List<Item>> response = itemController.getItemsByName("Round Widget");
        List<Item> itemList = response.getBody();

        //then
        assertNotNull(itemList);
        assertEquals(2,itemList.size());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testGetItemByNameWhenDoesNotExist() {
        //given
        when(itemRepository.findByName(anyString())).thenReturn(Collections.emptyList());

        //when
        ResponseEntity<List<Item>> response = itemController.getItemsByName("Round Widget");

        //then
        assertEquals(404,response.getStatusCodeValue());
    }
}
