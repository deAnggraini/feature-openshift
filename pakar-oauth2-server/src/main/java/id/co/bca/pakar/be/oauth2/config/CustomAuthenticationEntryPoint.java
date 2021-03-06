package id.co.bca.pakar.be.oauth2.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;


public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());


    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException)
            throws ServletException {

        this.log.info("##### authException : " +authException.getMessage());

        // --- ALGORITHM
        HashMap<String, Object> map = new HashMap();
        HashMap<String, String> status = new HashMap<>();

        status.put("code", "401");
        status.put("message", authException.getMessage());

        map.put("data", 0);
        map.put("status", status);

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(response.getOutputStream(), map);
        } catch (Exception e) {
            throw new ServletException();
        }
    }
}
