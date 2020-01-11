package com.example.demo.controllers;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
public class OrderController {

	private static final Logger log = LoggerFactory.getLogger(OrderController.class);
	
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private OrderRepository orderRepository;
	
	
	@PostMapping("/submit/{username}")
	public ResponseEntity<UserOrder> submit(@PathVariable String username) {
		log.info("submitOrder request : {}",username);
		User user = userRepository.findByUsername(username);
		if(user == null) {
			log.info("Operation=submitOrder response=Error Message=User not found for username: {}",username);
			throw new ResourceNotFoundException("User not found for username: "+username);
		}
		UserOrder order = UserOrder.createFromCart(user.getCart());
		UserOrder newOrder = orderRepository.save(order);
		log.info("Operation=submitOrder response=Success Message=Order submitted for user: {}",username);
		return ResponseEntity.ok(order);
	}
	
	@GetMapping("/history/{username}")
	public ResponseEntity<List<UserOrder>> getOrdersForUser(@PathVariable String username) {
		log.info("getOrdersForUser request : {}",username);
		User user = userRepository.findByUsername(username);
		if(user == null) {
			log.info("Operation=getOrdersForUser response=Error Message=User not found for username: {}",username);
			throw new ResourceNotFoundException("User not found for username: "+username);
		}
		List<UserOrder> orderList = orderRepository.findByUser(user);
		log.info("Operation=getOrdersForUser response=Success Message=Orders for user {}: {}",user.getUsername(),orderList);
		return ResponseEntity.ok(orderList);
	}
}
