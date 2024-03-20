package com.iostate.example.persistence;

import com.iostate.example.persistence.entity.Item;
import com.iostate.orca.api.EntityManager;
import com.iostate.orca.api.EntityObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class ItemRepository {
    @Autowired
    private EntityManager entityManager;

    public Item find(Long id) {
        return entityManager.find(Item.class, id);
    }

    public List<Item> findAll() {
        return entityManager.findAll(Item.class);
    }

    public Item save(Item item) {
        entityManager.persist(item);
        return item;
    }

    public EntityObject save(EntityObject entity) {
        entityManager.persist(entity);
        return entity;
    }
}
