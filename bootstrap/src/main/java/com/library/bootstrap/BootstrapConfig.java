package com.library.bootstrap;

import com.library.core.application.ApplicationConfig;
import com.library.infrastructure.mysql.MysqlDbConfig;
import com.library.infrastructure.security.config.AuthenticationConfig;
import com.library.infrastructure.security.config.PasswordEncoderConfig;
import com.library.infrastructure.security.config.SecurityConfig;
import com.library.infrastructure.web.config.OpenApiConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan(basePackages = "com.library")
@Import(
        {ApplicationConfig.class,
                MysqlDbConfig.class,
                OpenApiConfig.class,
                SecurityConfig.class,
        AuthenticationConfig.class,
                PasswordEncoderConfig.class}

)
public class BootstrapConfig {
}
