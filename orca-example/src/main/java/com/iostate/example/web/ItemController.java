package com.iostate.example.web;

import com.iostate.example.persistence.ItemRepository;
import com.iostate.example.persistence.entity.Item;
import com.iostate.orca.api.MapBackedPO;
import com.iostate.orca.api.PersistentObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/items")
public class ItemController {
    @Autowired
    private ItemRepository itemRepository;

    @GetMapping("/{id}")
    public Item get(@PathVariable Long id) {
        return itemRepository.find(id);
    }

    @PostMapping
    public Item create(@RequestParam String name,
                       @RequestParam(defaultValue = "true") boolean visible,
                       @RequestParam BigDecimal price,
                       @RequestParam(defaultValue = "1") int quantity) {
        Item item = new Item();
        item.setName(name);
        item.setVisible(visible);
        item.setPrice(price);
        item.setQuantity(quantity);
        return itemRepository.save(item);
    }

    @PostMapping("/nocode")
    public String nocode(@RequestParam String name,
                         @RequestParam(defaultValue = "true") boolean visible,
                         @RequestParam BigDecimal price,
                         @RequestParam(defaultValue = "1") int quantity) {
        PersistentObject item = new MapBackedPO("Item");
        item.setFieldValue("name", name);
        item.setFieldValue("visible", visible);
        item.setFieldValue("price", price);
        item.setFieldValue("quantity", quantity);
        return itemRepository.save(item).toString();
    }
}
