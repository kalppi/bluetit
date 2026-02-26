package dev.jarno.bluetit.clip.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.core.env.Environment
import org.springframework.orm.jpa.JpaVendorAdapter
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import javax.sql.DataSource

@TestConfiguration
class TestJpaConfiguration {
    @Primary
    @Bean
    fun entityManagerFactory(
        dataSource: DataSource,
        @Autowired(required = false) jpaVendorAdapter: JpaVendorAdapter?,
        environment: Environment
    ): LocalContainerEntityManagerFactoryBean {
        val em = LocalContainerEntityManagerFactoryBean()
        em.dataSource = dataSource
        em.setPackagesToScan("dev.jarno.bluetit.clip", "dev.jarno.bluetit.outbox")
        if (jpaVendorAdapter != null) {
            em.jpaVendorAdapter = jpaVendorAdapter
        }

        // Get datasource URL to determine if it's H2 or PostgreSQL
        val connection = dataSource.connection
        val url = connection.metaData.url
        connection.close()

        // Set appropriate Hibernate properties based on the database
        val properties = mutableMapOf<String, Any>()
        when {
            url.contains("h2") -> {
                properties["hibernate.hbm2ddl.auto"] = "create-drop"
                properties["hibernate.dialect"] = "org.hibernate.dialect.H2Dialect"
            }
            url.contains("postgresql") -> {
                properties["hibernate.hbm2ddl.auto"] = environment.getProperty("spring.jpa.hibernate.ddl-auto", "create")
                properties["hibernate.dialect"] = "org.hibernate.dialect.PostgreSQLDialect"
            }
        }

        if (properties.isNotEmpty()) {
            em.setJpaPropertyMap(properties)
        }

        return em
    }
}



