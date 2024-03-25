package com.iostate.example.persistence;

import com.iostate.example.persistence.entity.Order;
import com.iostate.orca.api.EntityManager;
import com.iostate.orca.api.EntityObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class OrderRepository {
    @Autowired
    private EntityManager entityManager;

    public Order find(Long id) {
        return entityManager.find(Order.class, id);
    }

    public List<Order> findAll() {
        return entityManager.findAll(Order.class);
    }

    public Order save(Order order) {
        entityManager.persist(order);
        return order;
    }

    public EntityObject save(EntityObject entity) {
        entityManager.persist(entity);
        return entity;
    }
}
