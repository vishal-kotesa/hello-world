package com.infosys.infytel.customer.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.infosys.infytel.customer.dto.PlanDTO;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.command.AsyncResult;

@Service
public class HystrixService {
	
	@Autowired RestTemplate template;
	//Async Methods
	
	@HystrixCommand
	public Future<PlanDTO> getSpecificPlans(final int planId) {
		return new AsyncResult<PlanDTO>() {
			@Override
			public PlanDTO invoke() {
				return template.getForObject("http://PLANMS"+"/plans/"+planId, PlanDTO.class);
			}
		};
		
	}
	@HystrixCommand
	public Future<List<Long>> getFriends(final long phoneNo)
	{
		return new AsyncResult<List<Long>>() {
		@SuppressWarnings("unchecked")
		@Override
		public List<Long> invoke(){
			return template.getForObject("http://FRIENDMS"+"/customers/"+phoneNo+"/friends", List.class);
		}
		};
		
	}
	
	/*Synchronous Methods
	@HystrixCommand(fallbackMethod="getSpecificPlanFallback")
	public PlanDTO getSpecificPlan(int planId) {
			return template.getForObject("http://PLANMS"+"/plans/"+planId, PlanDTO.class);
	}
	public PlanDTO getSpecificPlanFallback(int planId) {
			return new PlanDTO();
	}
	@SuppressWarnings("unchecked")
	@HystrixCommand(fallbackMethod="getFriendsFallback")
	
	public List<Long> getFriends(long phoneNo){
		return template.getForObject("http://FRIENDMS"+"/customers/"+phoneNo+"/friends", List.class);
	}
	public List<Long> getFriendsFallback(long phoneNo){
		List<Long> friends=new ArrayList<>();
		friends.add(-1L);
		return friends;
	}*/
	
	
}
