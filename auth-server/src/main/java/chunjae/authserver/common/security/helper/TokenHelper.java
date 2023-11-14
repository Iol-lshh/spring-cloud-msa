package chunjae.authserver.common.security.helper;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import chunjae.authserver.common.security.service.SecurityService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class TokenHelper {
    
    private SecretKey accessSecret;
    private int accessValidity;
    private SecretKey refreshSecret;
    private int refreshValidity;
    
    @Autowired
    private SecurityService securityService;

    public TokenHelper( @Value("${jwt.access.secret}")                 String accessSecret, 
                        @Value("${jwt.access.token-validity-in-days}") int accessValidity,
                        @Value("${jwt.refresh.secret}")                 String refreshSecret, 
                        @Value("${jwt.refresh.token-validity-in-days}") int refreshValidity
                        ){

        this.accessSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(accessSecret));
        this.accessValidity = accessValidity;
        this.refreshSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(refreshSecret));
        this.refreshValidity = refreshValidity;
    }

    
    // 토큰 생성
    public String createToken(Authentication authentication, int period, SecretKey secretKey){

        // 토큰 만료 시간
        LocalDate validity = LocalDate.now().plusDays(period); 

        return Jwts.builder()
                .setSubject(authentication.getName())
                .setClaims(Map.of("username", authentication.getPrincipal(), 
                                    "regdate", LocalDateTime.now().toString()))
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .setExpiration(Date.valueOf(validity))
                .compact();
    }

    public String createAccessToken(Authentication authentication){
        // 토큰 만료일
        LocalDate validity = LocalDate.now().plusDays(this.accessValidity); 

        return Jwts.builder()
                .setSubject(authentication.getName())
                .setClaims(Map.of("username", authentication.getPrincipal(), 
                                    "regdate", LocalDateTime.now().toString()))
                .signWith(this.accessSecret, SignatureAlgorithm.HS512)
                .setExpiration(Date.valueOf(validity))
                .compact();
    }

    public String createAccessToken(String username){
        // 토큰 만료일
        LocalDate validity = LocalDate.now().plusDays(this.accessValidity); 

        return Jwts.builder()
                .setSubject(username)
                .setClaims(Map.of("username", username, 
                                    "regdate", LocalDateTime.now().toString()))
                .signWith(this.accessSecret, SignatureAlgorithm.HS512)
                .setExpiration(Date.valueOf(validity))
                .compact();
    }

    public String createRefreshToken(Authentication authentication){
        // 토큰 만료일
        LocalDate validity = LocalDate.now().plusDays(this.refreshValidity); 

        
        System.out.println(authentication.getPrincipal());
        System.out.println(authentication.getCredentials());
        

        return Jwts.builder()
                .setSubject(authentication.getName())
                .setClaims(Map.of("username", authentication.getPrincipal(), 
                                    "regdate", LocalDateTime.now().toString()))
                .signWith(this.refreshSecret, SignatureAlgorithm.HS512)
                .setExpiration(Date.valueOf(validity))
                .compact();
    }

    public String createRefreshToken(String username){
        // 토큰 만료일
        LocalDate validity = LocalDate.now().plusDays(this.refreshValidity); 

        return Jwts.builder()
                .setSubject(username)
                .setClaims(Map.of("username", username, 
                                    "regdate", LocalDateTime.now().toString()))
                .signWith(this.refreshSecret, SignatureAlgorithm.HS512)
                .setExpiration(Date.valueOf(validity))
                .compact();
    }

    // 토큰 검증
    public Jws<Claims> validateToken(String jwt, SecretKey secretKey){

        return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(jwt);
    }

    public Claims validateAccessToken(String jwt){
        // 1. 토큰 검증
        Jws<Claims> result = validateToken(jwt, this.accessSecret);

        // 2. 비활성화된 access token 확인 
        // - 활성화된 access 토큰 찾기는 비용이 크므로, 토큰 탈취 의심에 대한 로그아웃 처리를 할 것이라면 비활성화된 access token만을 확인하도록 한다.
        if(securityService.isDisabledAccessToken(jwt)){
            throw new ExpiredJwtException(result.getHeader(), result.getBody(), jwt);
        }
        return result.getBody();
    }

    public Claims validateRefreshToken(String jwt){
        return validateToken(jwt, this.refreshSecret).getBody();
    }
}
