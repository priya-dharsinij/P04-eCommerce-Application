package com.example.demo.controllers;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.stream.IntStream;

@RestController
@RequestMapping("/api/cart")
public class CartController {

	private static final Logger log = LoggerFactory.getLogger(CartController.class);
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CartRepository cartRepository;
	
	@Autowired
	private ItemRepository itemRepository;
	
	@PostMapping("/addToCart")
	public ResponseEntity<Cart> addTocart(@RequestBody ModifyCartRequest request) {
		log.info("addTocart request: {}",request);
		User user = userRepository.findByUsername(request.getUsername());
		if(user == null) {
			log.info("Operation=addToCart response=Error Message=User not found for username: {}",request.getUsername());
            throw new ResourceNotFoundException("User not found for username: "+request.getUsername());
		}
		Optional<Item> item = itemRepository.findById(request.getItemId());
		if(!item.isPresent()) {
			log.info("Operation=addToCart response=Error Message=Item not found for id: {}",request.getItemId());
            throw new ResourceNotFoundException("Item not found for id: "+request.getItemId());
			//return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		Cart cart = user.getCart();
		IntStream.range(0, request.getQuantity())
			.forEach(i -> cart.addItem(item.get()));
		cartRepository.save(cart);
		log.info("Operation=addToCart response=Success Message=Items added to cart: {}",cart.getId());
		return ResponseEntity.ok(cart);
	}
	
	@PostMapping("/removeFromCart")
	public ResponseEntity<Cart> removeFromcart(@RequestBody ModifyCartRequest request) {
		log.info("removeFromCart request: {}",request);
		User user = userRepository.findByUsername(request.getUsername());
		if(user == null) {
            log.info("Operation=removeFromCart response=Error Message=User not found for username: {}",request.getUsername());
            throw new ResourceNotFoundException("User not found for username: "+request.getUsername());
		}
		Optional<Item> item = itemRepository.findById(request.getItemId());
		if(!item.isPresent()) {
            log.info("Operation=removeFromCart response=Error Message=Item not found for id: {}",request.getItemId());
            throw new ResourceNotFoundException("Item not found for id: "+request.getItemId());
		}
		Cart cart = user.getCart();
		IntStream.range(0, request.getQuantity())
			.forEach(i -> cart.removeItem(item.get()));
		cartRepository.save(cart);
		log.info("Operation=removeFromCart response=Success Message=Items removed from cart: {}",cart.getId());
		return ResponseEntity.ok(cart);
	}
		
}
