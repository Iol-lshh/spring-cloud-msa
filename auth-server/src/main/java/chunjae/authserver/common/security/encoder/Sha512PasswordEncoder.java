package chunjae.authserver.common.security.encoder;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Sha512PasswordEncoder implements PasswordEncoder{

    @Override
    public String encode(CharSequence rawPassword) throws NoSuchAlgorithmException {
        return hashWithSHA512(rawPassword.toString());
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) throws NoSuchAlgorithmException {
        return encodedPassword.equals(encode(rawPassword));
    }

    private String hashWithSHA512(String input) throws NoSuchAlgorithmException{
        StringBuilder output = new StringBuilder();
        
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        byte[] digested = md.digest(input.getBytes());
        for(int i = 0; i < digested.length; i++){
            output.append(Integer.toHexString(0xFF & digested[i]));
        }

        return output.toString();
    }    
}
