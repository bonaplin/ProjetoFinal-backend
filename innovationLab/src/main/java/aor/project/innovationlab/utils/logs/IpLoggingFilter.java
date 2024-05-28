package aor.project.innovationlab.utils.logs;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.ThreadContext;

import java.io.IOException;

public class IpLoggingFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Implementação do método init se necessário
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest) {
            String ipAddress = request.getRemoteAddr();
            ThreadContext.put("ipAddress", ipAddress);
        }

        try {
            chain.doFilter(request, response);
        } finally {
            ThreadContext.remove("ipAddress");
        }
    }

    @Override
    public void destroy() {
        // Implementação do método destroy se necessário
    }
}