package com.nimbleways.springboilerplate.services.implementations;

import com.nimbleways.springboilerplate.entities.Order;
import com.nimbleways.springboilerplate.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderSerivce {

	@Autowired
	OrderRepository or ;

	public Order getOrder(Long orderId){
		Order order = or.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
		return order;
	}
}
