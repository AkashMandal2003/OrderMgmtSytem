package com.jocata.oms.um.jwt;

import com.jocata.oms.datamodel.um.form.PermissionForm;
import com.jocata.oms.datamodel.um.form.RoleForm;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;
import java.util.Date;

@Service
public class JWTService {
    private static final String SECRET_KEY = "a6eac1a0ae42d3e466b45dd4610e18decdb339b59bda075853ae0229fd4edc35";

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String email, Set<RoleForm> roles) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", email);

        claims.put("roles", roles == null ? Collections.emptyList() :
                roles.stream().map(RoleForm::getRoleName).collect(Collectors.toList()));

        claims.put("permissions", roles == null ? Collections.emptyList() :
                roles.stream()
                        .flatMap(role -> Optional.ofNullable(role.getPermissions())
                                .orElse(Collections.emptySet()).stream()
                                .map(PermissionForm::getPermissionName))
                        .collect(Collectors.toList()));

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
}
