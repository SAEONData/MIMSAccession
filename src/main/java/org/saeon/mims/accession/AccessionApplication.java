package org.saeon.mims.accession;

import org.saeon.mims.accession.service.user.UserService;
import org.saeon.mims.accession.springconfig.filters.IngestAuthenticationFilter;
import org.saeon.mims.accession.springconfig.filters.SessionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;

import javax.servlet.http.HttpSessionListener;

@SpringBootApplication
public class AccessionApplication {

    @Autowired
    private UserService userService;

    @Bean
    public FilterRegistrationBean ingestAuthenticationFilter() {
        final FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(new IngestAuthenticationFilter(userService));
        registrationBean.addUrlPatterns("/ingest/home");

        return registrationBean;
    }

    @Bean
    public ServletListenerRegistrationBean<HttpSessionListener> sessionListener() {
        return new ServletListenerRegistrationBean<>(new SessionListener(userService));
    }

    public static void main(String[] args) {
        SpringApplication.run(AccessionApplication.class, args);
    }

}
