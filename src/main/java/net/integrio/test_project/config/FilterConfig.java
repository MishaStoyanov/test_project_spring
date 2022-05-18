package net.integrio.test_project.config;

import net.integrio.test_project.filters.AuthFilter;
import net.integrio.test_project.filters.LogFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<LogFilter> loggingFilterRegistrationBean() {
        FilterRegistrationBean<LogFilter> registrationBean
                = new FilterRegistrationBean<>();
        registrationBean.setFilter(new LogFilter());
        registrationBean.addUrlPatterns("/users/*");
        registrationBean.setOrder(2);

        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<AuthFilter> authFilterFilterRegistrationBean() {
        FilterRegistrationBean<AuthFilter> registrationBean
                = new FilterRegistrationBean<>();

        registrationBean.setFilter(new AuthFilter());
        registrationBean.addUrlPatterns("/users/*");
        registrationBean.setOrder(1);

        return registrationBean;
    }
}
