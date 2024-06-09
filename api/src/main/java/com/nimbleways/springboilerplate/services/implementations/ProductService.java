package com.nimbleways.springboilerplate.services.implementations;

import java.time.LocalDate;
import java.util.Set;

import com.nimbleways.springboilerplate.contollers.MyController;
import com.nimbleways.springboilerplate.entities.Order;
import com.nimbleways.springboilerplate.repositories.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;

@Service
@RequiredArgsConstructor
public class ProductService {


    final ProductRepository pr;

    final NotificationService ns;
	@Autowired
	OrderRepository or ;

	private static final Logger LOGGER = LoggerFactory.getLogger(ProductService.class);


	public void handleProduct(Set<Product> products) {
		for (Product p : products) {
			switch (p.getProductType()) {
			case NORMAL:
				LOGGER.info("Handle a NORMAL product:"+ p.getName());
				handleNormalProduct(p);
				break;
			case SEASONAL:
				LOGGER.info("Handle a SEASONAL product:"+ p.getName());
				handleSeasonalProduct(p);
				break;
			case EXPIRABLE:
				LOGGER.info("Handle a EXPIRABLE product:"+ p.getName());
				handleExpiredProduct(p);
				break;
			case FLASHSALE:
				LOGGER.info("Handle a FLASHSALE product :"+ p.getName());
				handleFlashSaleProduct(p);
				break;
			default:
				throw new IllegalArgumentException("Unknown product type: " + p.getProductType());
			}
		}
	}


    public void notifyDelay(int leadTime, Product p) {
        p.setLeadTime(leadTime);
        pr.save(p);
        ns.sendDelayNotification(leadTime, p.getName());
    }


	public void handleNormalProduct(Product p) {
		if (p.getAvailable() > 0) {
			p.setAvailable(p.getAvailable() - 1);
			pr.save(p);
		} else {
			int leadTime = p.getLeadTime();
			if (leadTime > 0) {
				notifyDelay(leadTime, p);
			}
		}
	}

    public void handleSeasonalProduct(Product p) {
        if (LocalDate.now().plusDays(p.getLeadTime()).isAfter(p.getSeasonEndDate())) {
            ns.sendOutOfStockNotification(p.getName());
            p.setAvailable(0);
            pr.save(p);
        } else if (p.getSeasonStartDate().isAfter(LocalDate.now())) {
            ns.sendOutOfStockNotification(p.getName());
            pr.save(p);
        } else {
            notifyDelay(p.getLeadTime(), p);
        }
    }

    public void handleExpiredProduct(Product p) {
        if (p.getAvailable() > 0 && p.getExpiryDate().isAfter(LocalDate.now())) {
            p.setAvailable(p.getAvailable() - 1);
            pr.save(p);
        } else {
            ns.sendExpirationNotification(p.getName(), p.getExpiryDate());
            p.setAvailable(0);
            pr.save(p);
        }
    }

	public void handleFlashSaleProduct(Product p) {
		if (LocalDate.now().isAfter(p.getFlashSaleEndDate()) || p.getFlashSaleQuantitySold() >= p.getMaximumFlashSaleQuantity()) {
			ns.sendOutOfStockNotification(p.getName());
			p.setAvailable(0);
			pr.save(p);
		} else {
			p.setFlashSaleQuantitySold(p.getFlashSaleQuantitySold() + 1);
			pr.save(p);
		}
	}

}