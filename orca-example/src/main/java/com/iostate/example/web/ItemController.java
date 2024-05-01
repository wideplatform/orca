package com.iostate.example.web;

import com.iostate.example.persistence.ItemRepository;
import com.iostate.example.persistence.entity.Item;
import com.iostate.orca.api.EntityObject;
import com.iostate.orca.metadata.MetadataManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private MetadataManager metadataManager;

    @GetMapping("/{id}")
    public Item get(@PathVariable Long id) {
        return itemRepository.find(id);
    }

    @GetMapping
    public List<Item> all() {
        return itemRepository.findAll();
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

    // the no-code way
    @PostMapping("/nocode")
    public String nocode(@RequestParam String name,
                         @RequestParam(defaultValue = "true") boolean visible,
                         @RequestParam BigDecimal price,
                         @RequestParam(defaultValue = "1") int quantity) {
        EntityObject item = metadataManager.findEntityByName("Item").newCeoInstance();
        item.setFieldValue("name", name);
        item.setFieldValue("visible", visible);
        item.setFieldValue("price", price);
        item.setFieldValue("quantity", quantity);
        return itemRepository.save(item).toString();
    }
}
