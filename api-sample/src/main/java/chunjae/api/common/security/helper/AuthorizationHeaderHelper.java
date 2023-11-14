package chunjae.api.common.security.helper;

import java.util.Base64;

import jakarta.servlet.http.HttpServletRequest;

public class AuthorizationHeaderHelper {


    public static String[] decodeBasic(HttpServletRequest request) {
        String encodedCredentials = request.getHeader("Authorization").substring("Basic".length()).trim();
        byte[] decodedBytes = Base64.getDecoder().decode(encodedCredentials);
        String decodedCredentials = new String(decodedBytes);
        String[] credentials = decodedCredentials.split(":");
        return credentials;
    }

    //Bearer 디코딩
    public static String[] decodeBearer(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.startsWith("Bearer ")) {
            String[] tokens = authorization.substring("Bearer".length()).trim().split(",");
            // tokens[0] : access token
            // tokens[1] : refresh token
            return tokens;
        }
        return new String[0];
    }
}
