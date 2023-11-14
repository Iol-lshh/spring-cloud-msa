package chunjae.authserver.service;

import java.time.LocalDateTime;
// import java.util.Base64;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import chunjae.authserver.common.SaveResult;
import chunjae.authserver.common.security.dto.UserDelegatorImpl;
import chunjae.authserver.common.security.dto.UserDto;
import chunjae.authserver.common.security.dto.UserTokenDto;
import chunjae.authserver.common.security.service.SecurityService;
import chunjae.authserver.domain.entity.DisabledAccessToken;
import chunjae.authserver.domain.entity.User;
import chunjae.authserver.domain.entity.UserToken;
import chunjae.authserver.domain.repository.DisabledAccessTokenRepository;
import chunjae.authserver.domain.repository.UserRepository;
import chunjae.authserver.domain.repository.UserTokenRepository;

@Service
public class SecurityServiceImpl implements SecurityService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserTokenRepository userTokenRepository;

    @Autowired
    private DisabledAccessTokenRepository disabledAccessTokenRepository;

    @Override
    public List<UserDto> list(int page, int pageSize){
        return userRepository.findAll(PageRequest.of(page, pageSize, Sort.by("RegDate").descending()))
                            .map(elem -> UserDto.builder()
                                                .username(elem.getUsername())
                                                .password(elem.getPassword())
                                                .build())
                            .toList();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> maybeUser = userRepository.findUserByUsername(username);
        return new UserDelegatorImpl(maybeUser.orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 사용자"))
                                            .toDto());
    }

    @Override
    public Optional<UserDto> loadUser(String username) {
        return userRepository.findUserByUsername(username).map(user -> user.toDto());
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        // byte[] bp = Base64.getDecoder().decode(map.get("password").toString()); 
        String bp = userDto.getPassword();

        return userRepository.save(User.builder()
                                        .username(userDto.getUsername())
                                        .password(bp)
                                        .regDate(LocalDateTime.now())
                                        //for db not null
                                        .a1("")
                                        .a2("")
                                        // .a3("")
                                        .a4("")
                                        .a5("")
                                        .a6("")
                                        .a7(0)
                                        .a8(0)
                                        .a9(0)
                                        .a10("0")
                                        .a11("6")
                                        .a12("N")
                                        .a13("N")
                                        .build())
                                .toDto();
    }

    @Override
    public Optional<UserTokenDto> loadUserTokenDtoByRefreshToken(String refreshToken) {
        return userTokenRepository.findUserTokenByRefreshAndDeleteYN(refreshToken, 'N')
                                .map(userToken -> userToken.toDto());
    }

    @Override
    public List<UserTokenDto> loadUserTokenDtoByUsername(String username) {
        return userTokenRepository.findListByUsernameAndDeleteYN(username, 'N')
                                .stream()
                                .map(userToken -> userToken.toDto())
                                .toList();
    }

    @Override
    public SaveResult createUserToken(UserTokenDto userTokenDto) {
        userTokenRepository.save(UserToken.builder()
                                        .refresh(userTokenDto.getRefresh())
                                        .access(userTokenDto.getAccess())
                                        .username(userTokenDto.getUsername())
                                        .regDate(LocalDateTime.now())
                                        .deleteYN('N')
                                        .build());
        return SaveResult.SUCCESS;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_UNCOMMITTED)
    public SaveResult updateUserToken(UserTokenDto userTokenDto) throws DisabledException  {
        UserToken userToken = userTokenRepository.findUserTokenByRefreshAndDeleteYN(userTokenDto.getRefresh(), 'N')
                                                .orElseThrow(() -> new DisabledException("존재하지 않는 refresh 토큰"));
        userTokenRepository.save(userToken.setAccess(userTokenDto.getAccess())
                                        .setDeleteYN(userTokenDto.isNotDeleted()?'N':'Y'));
        return SaveResult.SUCCESS;
    }

    @Override
    public boolean isDisabledAccessToken(String accessToken) {
        return disabledAccessTokenRepository.existsById(accessToken);
    }

    @Override
    public SaveResult saveDisabledAccessToken(String accessToken) {
        disabledAccessTokenRepository.save(DisabledAccessToken.builder()
                                                .access(accessToken)
                                                .regDate(LocalDateTime.now())
                                                .build());
        return SaveResult.SUCCESS;
    }

    @Override
    public SaveResult saveDisabledAccessTokenByRefresh(String refreshToken) {
        String accessToken = userTokenRepository.findUserTokenByRefreshAndDeleteYN(refreshToken, 'N')
                                                .orElseThrow(() -> new DisabledException("존재하지 않는 refresh 토큰"))
                                                .getAccess();
        
        return saveDisabledAccessToken(accessToken);
    }

    @Override
    public SaveResult deleteDisabledAccessToken(String accessToken) {
        disabledAccessTokenRepository.deleteByAccess(accessToken);
        return SaveResult.SUCCESS;   
    }

    @Override
    public SaveResult deleteDisabledAccessTokensLessThan(LocalDateTime dateTime) {
        disabledAccessTokenRepository.deleteAllLessThan(dateTime);
        return SaveResult.SUCCESS;
    }
}
