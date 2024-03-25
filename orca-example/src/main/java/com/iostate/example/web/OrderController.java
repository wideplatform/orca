package com.iostate.example.web;

import com.iostate.example.persistence.ItemRepository;
import com.iostate.example.persistence.OrderRepository;
import com.iostate.example.persistence.entity.Order;
import com.iostate.example.persistence.entity.OrderEntry;
import com.iostate.orca.api.EntityObject;
import com.iostate.orca.metadata.MetadataManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/orders")
public class OrderController {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private MetadataManager metadataManager;

    @GetMapping("/{id}")
    public Order get(@PathVariable Long id) {
        return orderRepository.find(id);
    }

    @GetMapping
    public List<Order> all() {
        return orderRepository.findAll();
    }

    @PostMapping
    public Order create(@RequestParam(defaultValue = "1") int quantity,
                        @RequestParam long itemId) {
        OrderEntry entry = new OrderEntry();
        entry.setQuantity(quantity);
        entry.setItem(itemRepository.find(itemId));
        Order order = new Order();
        order.getEntries().add(entry);
        return orderRepository.save(order);
    }

    @PostMapping("/nocode")
    public String nocode(@RequestParam(defaultValue = "1") int quantity,
                         @RequestParam long itemId) {
        EntityObject entry = metadataManager.findEntityByName("OrderEntry").newCeoInstance();
        entry.setFieldValue("quantity", quantity);
        entry.setFieldValue("item", Objects.requireNonNull(itemRepository.find(itemId)));
        EntityObject order = metadataManager.findEntityByName("Order").newCeoInstance();
        order.setFieldValue("entries", List.of(entry));
        return orderRepository.save(order).toString();
    }
}
