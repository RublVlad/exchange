package by.bsuir.exchange.filter;

import by.bsuir.exchange.provider.ConfigurationProvider;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter(urlPatterns = {"/*"})
public class SessionFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse res = (HttpServletResponse) servletResponse;

        HttpSession session = req.getSession(false);

        if (session == null) {
            res.sendRedirect(ConfigurationProvider.getProperty(ConfigurationProvider.LOGIN_PAGE_PATH));
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }
}
