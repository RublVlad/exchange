package by.bsuir.exchange.filter;


import by.bsuir.exchange.entity.RoleEnum;
import by.bsuir.exchange.provider.SessionAttributesNameProvider;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter(urlPatterns = {"/*"})
public class RoleFilter implements Filter {
    private static final RoleEnum DEFAULT_ROLE = RoleEnum.GUEST;
    private static final long MOCK_ID = -1;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpSession session = req.getSession();
        if (session.getAttribute(SessionAttributesNameProvider.ROLE) == null){
            session.setAttribute(SessionAttributesNameProvider.ROLE, DEFAULT_ROLE);
            session.setAttribute(SessionAttributesNameProvider.ID, MOCK_ID);
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

}
