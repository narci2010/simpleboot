package com.lhy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.PropertySource;

import com.lhy.config.BootProperties;

@PropertySource(value={"file:./application.properties"})
@EnableConfigurationProperties(BootProperties.class)
@EnableCaching
public class Application extends SpringBootServletInitializer{

    @Override  
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {  
    	return application.sources(Application.class);  
    } 
    
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
