package ru.vegxer.shopsample.catalog.filter;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.vegxer.shopsample.catalog.service.JwtService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws IOException, ServletException {
        if (!HttpMethod.GET.name().equals(request.getMethod())) {
            try {
                val jwt = getJwtFromRequest(request);
                if (jwtService.validateToken(jwt)) {
                    val userDetails = JwtUser.builder()
                        .username(jwtService.getUsernameFrom(jwt))
                        .authorities(jwtService.getAuthorities(jwt))
                        .build();

                    val authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    return;
                }
            } catch (Exception e) {
                log.error("Could not set user authentication in security context", e);
            }
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        val bearerToken = new AtomicReference<String>();
        Arrays.stream(request.getCookies())
            .filter(cookie -> "accessToken".equals(cookie.getName()))
            .findFirst()
            .ifPresentOrElse(cookie -> bearerToken.set(cookie.getValue()),
                () -> bearerToken.set(request.getHeader(HttpHeaders.AUTHORIZATION)));
        if (StringUtils.hasText(bearerToken.get())) {
            return bearerToken.get();
        }
        return null;
    }

    @Builder
    public static class JwtUser implements UserDetails {

        @Getter(onMethod_ = {@Override})
        private String password;

        @Getter(onMethod_ = {@Override})
        private String username;

        @Getter(onMethod_ = {@Override})
        private Collection<GrantedAuthority> authorities;


        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }
    }
}
