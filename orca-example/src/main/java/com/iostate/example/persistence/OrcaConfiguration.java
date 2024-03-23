package com.iostate.example.persistence;

import com.iostate.orca.api.ConnectionProvider;
import com.iostate.orca.api.EntityManager;
import com.iostate.orca.core.EntityManagerImpl;
import com.iostate.orca.metadata.MetadataManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrcaConfiguration {
    @Bean
    public MetadataManager metadataManager() {
        return new MetadataManager();
    }

    @Bean
    public EntityManager entityManager(MetadataManager metadataManager, ConnectionProvider connectionProvider) {
        return new EntityManagerImpl(metadataManager, connectionProvider).asDefault();
    }
}
