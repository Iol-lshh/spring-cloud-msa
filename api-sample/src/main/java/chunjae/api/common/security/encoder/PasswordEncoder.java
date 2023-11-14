package chunjae.api.common.security.encoder;

import java.security.NoSuchAlgorithmException;

public interface PasswordEncoder {
    String encode(CharSequence rawPassword) throws NoSuchAlgorithmException;
    boolean matches(CharSequence rawPassword, String encodedPassword) throws NoSuchAlgorithmException;

    default boolean upgradeEncodeing(String encodedPassword){
        return false;
    }
}
