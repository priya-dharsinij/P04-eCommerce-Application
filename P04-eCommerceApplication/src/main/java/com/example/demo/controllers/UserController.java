package com.example.demo.controllers;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UserController {

	private static final Logger log = LoggerFactory.getLogger(UserController.class);
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@GetMapping("/id/{id}")
	public ResponseEntity<User> findById(@PathVariable Long id) {
		log.info("findById request: {}",id);
        Optional<User> optional = userRepository.findById(id);
        User user = optional.orElseThrow(() -> new ResourceNotFoundException("User not found for id: "+id));
        return ResponseEntity.ok(user);
	}
	
	@GetMapping("/{username}")
	public ResponseEntity<User> findByUserName(@PathVariable String username) {
		log.info("findByUserName request: {}",username);
		User user = userRepository.findByUsername(username);
        if(user == null) throw new ResourceNotFoundException("User not found for username: "+username);
		return ResponseEntity.ok(user);
	}
	
	@PostMapping("/create")
	public ResponseEntity<User> createUser(@RequestBody @Valid CreateUserRequest createUserRequest) {
		log.info("createUser request: {}",createUserRequest);
		User user = new User();
		user.setUsername(createUserRequest.getUsername());
		if(createUserRequest.getPassword().length()<7 || !createUserRequest.getPassword().equals(createUserRequest.getConfirmPassword())){
			log.info("Operation=CreateUser response=Error  Reason=Password doesn't meet requirements or wrong password. Cannot create user : "+createUserRequest.getUsername());
			return ResponseEntity.badRequest().build();
		}
		user.setPassword(bCryptPasswordEncoder.encode(createUserRequest.getPassword()));
        Cart cart = new Cart();
        cartRepository.save(cart);
        log.info("New cart created for user {} : {}",createUserRequest.getUsername(),cart);
        user.setCart(cart);
        User newUser = userRepository.save(user);
        log.info("Operation=CreateUser response=Success Message=Username created for: {}",createUserRequest.getUsername());
		return ResponseEntity.ok(user);
	}
	
}
