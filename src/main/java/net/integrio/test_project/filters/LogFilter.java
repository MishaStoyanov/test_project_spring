package net.integrio.test_project.filters;

import lombok.extern.java.Log;
import net.integrio.test_project.resources.Constants;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Log
//@Component
//@Order(1)
//@WebFilter(urlPatterns = {"/users/*"})
public class LogFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("Initialization LogFilter");
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpSession session = request.getSession();
        log.info("\nAuthorization successful\nUsername: " + session.getAttribute(Constants.username) +
                "\nSession id: " + session.getId() + "\nPage: " + request.getRequestURI());
        filterChain.doFilter(request, response);

    }

    @Override
    public void destroy() {
    }
}
