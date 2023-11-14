package chunjae.api.common.security.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import chunjae.api.common.queryFactory.SaveResult;
import chunjae.api.common.security.dto.UserDto;
import chunjae.api.common.security.dto.UserTokenDto;

public interface SecurityService extends UserDetailsService{

    ///////////// 유저 CRUD /////////////
    @Override
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
    
    Optional<UserDto> loadUser(String username);
    
    List<UserDto> list(int page, int pageSize);

    UserDto createUser(UserDto userDto);
    
    // 회원가입 시도
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_UNCOMMITTED)
    default SaveResult trySignUp(UserDto userDto){
        SaveResult result;
        // 아이디 중복 확인
        if(loadUser(userDto.getUsername()).isEmpty()){
            // 아이디 생성
            createUser(userDto);

            // TODO 기본 USER 권한 생성


            result = SaveResult.SUCCESS;
        }else{
            throw new DisabledException("이미 있는 아이디입니다.");
        }

        return result;
    };

    ///////////// 토큰 CRUD /////////////
    Optional<UserTokenDto> loadUserTokenDtoByRefreshToken(String refreshToken);
    
    List<UserTokenDto> loadUserTokenDtoByUsername(String username); 

    SaveResult createUserToken(UserTokenDto userTokenDto);

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_UNCOMMITTED)
    SaveResult updateUserToken(UserTokenDto userTokenDto);

    // 토큰 저장 및 갱신
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_UNCOMMITTED)
    default SaveResult saveUserToken(String accessToken, String refreshToken, String username){
        SaveResult result;
        
        Optional<UserTokenDto> maybeUserTokenDto = loadUserTokenDtoByRefreshToken(refreshToken);

        if(maybeUserTokenDto.isEmpty()){
            result = createUserToken(UserTokenDto.builder()
                                            .refresh(refreshToken)
                                            .access(accessToken)
                                            .username(username)
                                            .isNotDeleted(true)
                                            .build());
        }else{
            result = updateUserToken(maybeUserTokenDto.get()
                                            .setAccess(accessToken));
        }

        return result;
    }
    
    // 소프트 딜리트
    // refresh 토큰 만료시킴
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_UNCOMMITTED)
    default SaveResult deleteUserToken(String refreshToken){
        SaveResult result;
        
        Optional<UserTokenDto> maybeUserTokenDto = loadUserTokenDtoByRefreshToken(refreshToken);

        if(!maybeUserTokenDto.isEmpty()){
            result = updateUserToken(maybeUserTokenDto.get()
                                            .setNotDeleted(false));
            saveDisabledAccessToken(maybeUserTokenDto.get().getAccess());
        }else{
            throw new DisabledException("잘못된 토큰입니다.");
        }

        return result;
    }

    // username에 대한 전체 토큰 만료
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_UNCOMMITTED)
    default SaveResult deleteUserTokens(String username){
        SaveResult result;
        
        List<UserTokenDto> userTokenDtos = loadUserTokenDtoByUsername(username);

        if(!userTokenDtos.isEmpty()){
            result = userTokenDtos.stream()
                        .filter(userTokenDto -> {
                            saveDisabledAccessToken(userTokenDto.getAccess());
                            return !updateUserToken(userTokenDto.setNotDeleted(false)).equals(SaveResult.SUCCESS);
                        })
                        .count() < 1 ? SaveResult.SUCCESS : SaveResult.FAIL;
        }else{
            throw new DisabledException("잘못된 토큰입니다.");
        }

        return result;
    }

    ///////////// DisabledAccessToken CRUD /////////////
    boolean isDisabledAccessToken(String accessToken);
    
    SaveResult saveDisabledAccessToken(String refreshToken);
    
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_UNCOMMITTED)
    SaveResult saveDisabledAccessTokenByRefresh(String refreshToken);


    // 스케쥴 - 1시간 마다
    @Scheduled(fixedDelay = 1000 * 60 * 60)
    // @Scheduled(fixedDelay = 1000 * 60) // 테스트 - 1분
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_UNCOMMITTED)
    default SaveResult deleteExpiredDisabledAccessTokens(){
        System.out.println("제거: " + LocalDateTime.now());
        SaveResult result = SaveResult.FAIL;
        result = deleteDisabledAccessTokensLessThan(LocalDateTime.now()
                                                .minusDays(1));
        return result;
    }

    // 하드 딜리트
    SaveResult deleteDisabledAccessToken(String accessToken);
   
    SaveResult deleteDisabledAccessTokensLessThan(LocalDateTime dateTime);
}
