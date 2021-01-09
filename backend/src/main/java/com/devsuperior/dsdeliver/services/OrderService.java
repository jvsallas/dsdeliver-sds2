package com.devsuperior.dsdeliver.services;

import com.devsuperior.dsdeliver.dto.OrderDTO;
import com.devsuperior.dsdeliver.dto.ProductDTO;
import com.devsuperior.dsdeliver.entities.Order;
import com.devsuperior.dsdeliver.entities.OrderStatus;
import com.devsuperior.dsdeliver.entities.Product;
import com.devsuperior.dsdeliver.repositories.OrderRepository;
import com.devsuperior.dsdeliver.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<OrderDTO> getAllOrders() {
        List<Order> products = orderRepository.findOrderWithProducts();
        return products.stream().map(p -> new OrderDTO(p)).collect(Collectors.toList());
    }


    @Transactional
    public OrderDTO createOrder(OrderDTO orderDTO) {
        Order order = new Order(null, orderDTO.getAddress(), orderDTO.getLatitude(), orderDTO.getLongitude(),
                Instant.now(), OrderStatus.PENDING);

        for (ProductDTO p : orderDTO.getProducts()) {
            Optional<Product> product = productRepository.findById(p.getId());
            if  (product.isPresent()){
                order.getProducts().add(product.get());
            }
        }
        order = orderRepository.save(order);
        return new OrderDTO(order);
    }

    @Transactional
    public OrderDTO setDelivered(Long id){
        Order order = orderRepository.getOne(id);
        order.setStatus(OrderStatus.DELIVERED);
        order = orderRepository.save(order);
        return new OrderDTO(order);
    }

}
