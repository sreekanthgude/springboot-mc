package com.example.admin.security;

import com.example.admin.model.User;
import com.example.admin.service.UserDetailsServiceImpl;
import com.example.admin.utility.JwtUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AuthTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String jwt = parseJwt(request);
            System.out.println("jwt---");
            System.out.println("request---------------->"+request);
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                String username = jwtUtils.getUserNameFromJwtToken(jwt);
                System.out.println("username---->"+username);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                System.out.println("userDetails-55----->"+userDetails.getAuthorities());
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else{
               /* System.out.println("in---->");
                ObjectMapper mapper = new ObjectMapper();
                User user = mapper.readValue(request.getReader(),User.class);
                System.out.println("user------->"+user.getUsername());
                //String  test = request.getInputStream().lines().collect(Collectors.joining(System.lineSeparator()));
                //System.out.println("test--->"+test);*/
                if(!request.getRequestURI().contains("/h2")) {
                    System.out.println("request---->" + request.getRequestURI());
                    String body = null;
                    String userName = null;
                    final StringBuilder builder = new StringBuilder();
                    try (BufferedReader reader = request.getReader()) {
                        if (reader == null) {
                            System.out.println("Empty!!!");
                        }
                        String line;
                        while ((line = reader.readLine()) != null) {
                            builder.append(line);
                        }
                        body = builder.toString();
                        System.out.println("body--------------->" + body);
                    }
                    ObjectMapper mapper = new ObjectMapper();
                    if (body != null && !"".equals(body)) {
                        JsonNode actualObj = mapper.readTree(body);
                        if (actualObj.has("username")) {
                            userName = actualObj.get("username").asText();
                        }
                    }
                    System.out.println("userName------>" + userName);
                    UserDetails userDetails = userDetailsService.loadUserByUsername(userName);
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication: {}", e);
        }

         filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = ((HttpServletRequest) request).getHeader(org.springframework.http.HttpHeaders.AUTHORIZATION);
        System.out.println("headerAuth--->"+headerAuth);
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7, headerAuth.length());
        }

        return null;
    }
}
