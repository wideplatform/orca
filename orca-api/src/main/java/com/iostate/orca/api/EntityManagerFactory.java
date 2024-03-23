package com.iostate.orca.api;

public class EntityManagerFactory {

    private static EntityManager defaultEntityManager;

    private EntityManagerFactory() {}

    public static EntityManager getDefault() {
        return defaultEntityManager;
    }

    public static void setDefault(EntityManager entityManager) {
        defaultEntityManager = entityManager;
    }
}
