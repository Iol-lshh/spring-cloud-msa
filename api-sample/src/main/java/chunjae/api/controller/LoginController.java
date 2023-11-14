package chunjae.api.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import chunjae.api.common.queryFactory.SaveResult;
import chunjae.api.common.security.dto.UserDto;
import chunjae.api.common.security.service.SecurityService;

@RestController
@RequestMapping("/login")
public class LoginController {
    
    @Autowired
    SecurityService securityService;

    @GetMapping("/test")
    public List<UserDto> test(){
        return securityService.list(0, 30);
    }

    // 로그인
    @GetMapping("/in")
    public SaveResult signIn(){
        // 헤더 내용을 필터에서 처리
        return SaveResult.SUCCESS;
    }

    // 회원가입
    @PostMapping("/up")
    public SaveResult signUp(@RequestBody Map<String, Object> map) {
        
        return securityService.trySignUp(UserDto.builder()
                                        .username(map.get("username").toString())
                                        .password(map.get("password").toString())
                                        .build());
    }

    // 로그아웃: {username} 주면, 전체 로그아웃. 
    //          {username, refresh} 주면, 해당 refresh에 대해 로그아웃
    @GetMapping("/out")
    public SaveResult logOut(){
        // 헤더 내용을 필터에서 처리
        return SaveResult.SUCCESS;
    }

    @GetMapping("/refresh")
    public SaveResult refresh(){
        // 헤더 내용을 필터에서 처리
        return SaveResult.SUCCESS;
    }
}
