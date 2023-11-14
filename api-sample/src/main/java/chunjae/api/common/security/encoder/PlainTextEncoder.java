package chunjae.api.common.security.encoder;

public class PlainTextEncoder implements PasswordEncoder{
    // test용: 그대로 반환

    @Override
    public String encode(CharSequence rawPassword) {
        return rawPassword.toString();
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return rawPassword.equals(encodedPassword);
    }
}
