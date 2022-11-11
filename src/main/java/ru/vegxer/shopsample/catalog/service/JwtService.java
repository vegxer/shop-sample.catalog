package ru.vegxer.shopsample.catalog.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtService {

    private final JwtKeyProvider jwtKeyProvider;

    public boolean validateToken(String jwt) {
        try {
            return jwt != null && Jwts.parser().setSigningKey(jwtKeyProvider.getPublicKey()).parseClaimsJws(jwt) != null;
        } catch (JwtException e) {
            log.warn("Invalid JWT!", e);
        }
        return false;
    }

    public String getUsernameFrom(String jwt) {
        return (String) getClaims(jwt).get("sub");
    }

    public Collection<GrantedAuthority> getAuthorities(String jwt) {
        return ((ArrayList<String>) getClaims(jwt).get("groups"))
            .stream()
            .map(role -> (GrantedAuthority) () -> role)
            .collect(Collectors.toList());
    }

    private Claims getClaims(String jwt) {
        return Jwts.parser()
            .setSigningKey(jwtKeyProvider.getPublicKey())
            .parseClaimsJws(jwt)
            .getBody();
    }
}
