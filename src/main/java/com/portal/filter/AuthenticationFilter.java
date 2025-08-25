package com.portal.filter;

import com.portal.controller.LoginController;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Filtro para verificar autenticação nas páginas administrativas
 */
public class AuthenticationFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Inicialização do filtro
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);
        
        String requestURI = httpRequest.getRequestURI();
        
        // Permitir acesso à página de login
        if (requestURI.endsWith("login.xhtml")) {
            chain.doFilter(request, response);
            return;
        }
        
        // Verificar se o usuário está logado
        LoginController loginController = null;
        if (session != null) {
            loginController = (LoginController) session.getAttribute("loginController");
        }
        
        if (loginController != null && loginController.isLogado()) {
            // Usuário autenticado, permitir acesso
            chain.doFilter(request, response);
        } else {
            // Usuário não autenticado, redirecionar para login
            String contextPath = httpRequest.getContextPath();
            httpResponse.sendRedirect(contextPath + "/pages/admin/login.xhtml");
        }
    }

    @Override
    public void destroy() {
        // Limpeza do filtro
    }
}