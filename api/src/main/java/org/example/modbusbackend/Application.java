package org.example.modbusbackend;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {"org.example.modbusbackend.*"})
@EntityScan(basePackages = {"org.example.modbusbackend.*"})
@EnableJpaRepositories(basePackages = {"org.example.modbusbackend.*"})

public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

