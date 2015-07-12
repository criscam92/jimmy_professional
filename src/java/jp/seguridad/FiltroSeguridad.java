package jp.seguridad;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
public class FiltroSeguridad implements Filter {

    
    private FilterConfig config;
    private String pattern;
    public static final String AUTH_KEY = "jp.usuario";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.config = filterConfig;
        this.pattern = this.config.getInitParameter("pattern");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (this.pattern != null && request instanceof HttpServletRequest) {
            String queryString = ((HttpServletRequest) request).getQueryString();
            if (queryString != null && queryString.contains(this.pattern)) {
                ((HttpServletRequest) request).getSession().invalidate();
            }
        }

        if (((HttpServletRequest) request).getSession().getAttribute(AUTH_KEY) == null) {
            ((HttpServletResponse) response).sendRedirect("../");
//            
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {
        this.config = null;
    }

}
