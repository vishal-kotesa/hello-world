package com.infosys.infytel.customer.controller;

import java.net.URI;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.serviceregistry.ServiceRegistry;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.infosys.infytel.customer.dto.CustomerDTO;
import com.infosys.infytel.customer.dto.LoginDTO;
import com.infosys.infytel.customer.dto.PlanDTO;
import com.infosys.infytel.customer.service.CustomerService;
import com.infosys.infytel.customer.service.HystrixService;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@RestController
@CrossOrigin
//@RibbonClient(name = "custribbon")
public class CustomerController {

	Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	CustomerService custService;

	@Autowired
	RestTemplate template;

	
	@Autowired HystrixService hystrixService;
	 

	/*
	 * @Autowired DiscoveryClient client;
	 */

	// Create a new customer
	@PostMapping(value = "/customers", consumes = MediaType.APPLICATION_JSON_VALUE)
	public void createCustomer(@RequestBody CustomerDTO custDTO) {
		logger.info("Creation request for customer {}", custDTO);
		custService.createCustomer(custDTO);
	}

	// Login
	@PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
	public boolean login(@RequestBody LoginDTO loginDTO) {
		logger.info("Login request for customer {} with password {}", loginDTO.getPhoneNo(), loginDTO.getPassword());
		return custService.login(loginDTO);
	}

	// Fetches full profile of a specific customer
	@GetMapping(value = "/customers/{phoneNo}", produces = MediaType.APPLICATION_JSON_VALUE)
	public CustomerDTO getCustomerProfile(@PathVariable Long phoneNo) throws InterruptedException, ExecutionException {
		
		
		long overallStart=System.currentTimeMillis();
		logger.info("Profile request for customer {}", phoneNo);
	
		CustomerDTO custDTO=custService.getCustomerProfile(phoneNo);
		long planStart=System.currentTimeMillis();
		logger.info("Starting the request for Plan");
		Future<PlanDTO> planDTO=hystrixService.getSpecificPlans(custDTO.getCurrentPlan().getPlanId());
		long planStop=System.currentTimeMillis();
		
		
		
		long friendStart=System.currentTimeMillis();
		logger.info("Starting the request for Friend");
		Future<List<Long>> friends=hystrixService.getFriends(phoneNo);
		long friendStop=System.currentTimeMillis();
		
		custDTO.setCurrentPlan(planDTO.get());
		custDTO.setFriendAndFamily(friends.get());
		
		long overallStop=System.currentTimeMillis();
		logger.info("Time for plan {}", (planStop-planStart));
		logger.info("Time for friend {}", (friendStop-friendStart));
		logger.info("Time for entire request {}", (overallStop-overallStart));
		
		return custDTO;
	}


}
