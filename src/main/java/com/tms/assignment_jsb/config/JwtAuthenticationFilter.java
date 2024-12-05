package com.tms.assignment_jsb.config;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.tms.assignment_jsb.repository.UserGroupsRepository;
import com.tms.assignment_jsb.service.AppUserDetailsService;
import com.tms.assignment_jsb.service.JwtService;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter{
 
  private final JwtService jwtService;
  // private final AccountsRepository accountsRepository;
  private final AppUserDetailsService appUserDetailsService;
  private final UserGroupsRepository userGroupsRepository;

  public JwtAuthenticationFilter(JwtService jwtService, AppUserDetailsService appUserDetailsService, UserGroupsRepository userGroupsRepository) {
    this.jwtService = jwtService;
    this.appUserDetailsService = appUserDetailsService;
    this.userGroupsRepository = userGroupsRepository;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
    String token = null;
    // String username = null;

    if (request.getCookies() != null) {
      for (Cookie cookie : request.getCookies()) {
        if ("token".equals(cookie.getName())) {
          token = cookie.getValue();
          break;
        }
      }
    }

    if (token != null) {
            try {
                Claims claims = jwtService.extractClaims(token);
                String username = claims.getSubject();

                if (username != null) {
                    request.setAttribute("username", username);

                    // Dynamically check if the user is an admin
                    boolean isAdmin = userGroupsRepository.findByUsernameAndGroupname(username, "Admin").isPresent();
                    request.setAttribute("isAdmin", isAdmin);

                    // Check if the user belongs to the "PL" group
                    boolean isPL = userGroupsRepository.findByUsernameAndGroupname(username, "PL").isPresent();
                    request.setAttribute("isPL", isPL);

                    // Check if the user belongs to the "PM" group
                    boolean isPM = userGroupsRepository.findByUsernameAndGroupname(username, "PM").isPresent();
                    request.setAttribute("isPM", isPM);

                    // Fetch all groups for the user
                    List<String> groups = userGroupsRepository.findAllByUsername(username).stream()
                            .map(group -> group.getId().getGroupname())
                            .toList();
                    request.setAttribute("groups", groups);
                }

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = appUserDetailsService.loadUserByUsername(username);
                    if (userDetails != null && !jwtService.isTokenExpired(token)) {
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                }
            } catch (Exception e) {
                logger.error("JWT token validation failed", e);
            }
        }

    filterChain.doFilter(request, response);
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
    String path = request.getServletPath();
    return path.equals("/logout"); // Skip JWT filter for logout
  }
}
