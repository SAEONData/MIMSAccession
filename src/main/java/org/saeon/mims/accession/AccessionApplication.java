package org.saeon.mims.accession;

import lombok.extern.slf4j.Slf4j;
import org.saeon.mims.accession.model.user.User;
import org.saeon.mims.accession.service.user.UserService;
import org.saeon.mims.accession.springconfig.filters.IngestAuthenticationFilter;
import org.saeon.mims.accession.springconfig.filters.SessionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.repository.init.Jackson2RepositoryPopulatorFactoryBean;

import javax.servlet.http.HttpSessionListener;

@SpringBootApplication
@Slf4j
public class AccessionApplication {

    @Autowired
    private UserService userService;

    @Value("${admin.user}")
    private String adminUserEmail;

    @Bean
    public FilterRegistrationBean ingestAuthenticationFilter() {
        final FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(new IngestAuthenticationFilter(userService));
        registrationBean.addUrlPatterns("/ingest/*");
        registrationBean.addUrlPatterns("/manage/*");
        registrationBean.addUrlPatterns("/accession/*");

        return registrationBean;
    }

    @Bean
    public Jackson2RepositoryPopulatorFactoryBean getRespositoryPopulator() {
        User user = userService.getUserByEmail(adminUserEmail);
        log.info("Admin user population required? {}", user == null ? "Yes" : "No");
        Jackson2RepositoryPopulatorFactoryBean factory = new Jackson2RepositoryPopulatorFactoryBean();
        if (user == null) {
            factory.setResources(new Resource[]{new ClassPathResource("user-data.json")});

        }
        return factory;
    }

    @Bean
    public ServletListenerRegistrationBean<HttpSessionListener> sessionListener() {
        return new ServletListenerRegistrationBean<>(new SessionListener(userService));
    }

    public static void main(String[] args) {
        SpringApplication.run(AccessionApplication.class, args);
    }

}
