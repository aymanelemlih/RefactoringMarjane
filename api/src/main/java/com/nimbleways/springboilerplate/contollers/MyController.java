package com.nimbleways.springboilerplate.contollers;

import com.nimbleways.springboilerplate.dto.product.ProcessOrderResponse;
import com.nimbleways.springboilerplate.entities.Order;
import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.entities.enums.ProductType;
import com.nimbleways.springboilerplate.repositories.OrderRepository;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.implementations.OrderSerivce;
import com.nimbleways.springboilerplate.services.implementations.ProductService;

import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
public class MyController {
    @Autowired
    private ProductService productService;


    @Autowired
    private OrderSerivce orderSerivce;

	private static final Logger LOGGER = LoggerFactory.getLogger(MyController.class);


	@PostMapping("{orderId}/processOrder")
    @ResponseStatus(HttpStatus.OK)
    public ProcessOrderResponse processOrder(@PathVariable Long orderId) {
        List<Long> ids = new ArrayList<>();
        ids.add(orderId);
        Order order = orderSerivce.getOrder(orderId);
		productService.handleProduct(order.getItems());
        return new ProcessOrderResponse(order.getId());
    }

}
