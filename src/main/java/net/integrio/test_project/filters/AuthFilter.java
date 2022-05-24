package net.integrio.test_project.filters;

import lombok.extern.java.Log;
import net.integrio.test_project.resources.Constants;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

//@Component
//@Order(2)
@Log
//@WebFilter(urlPatterns = {"/users/*"})
public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("Initialization AuthFilter");
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpSession session = request.getSession();
       // log.info("username = " + request.getParameter(Constants.login) + " password = " + request.getParameter(Constants.password));
        if (session.getAttribute(Constants.isAuthorized) != null && (boolean) session.getAttribute(Constants.isAuthorized)) {
            filterChain.doFilter(request, response);
        } else {
            log.info("non authorized:"+request.getRequestURL());
            response.sendRedirect(request.getContextPath()+"/login");
        }
    }

    @Override
    public void destroy() {
    }

}
