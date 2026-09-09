package br.edu.malhaia.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(MalhaiaProperties.class)
public class AppConfig {
}
