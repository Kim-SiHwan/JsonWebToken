package com.example.jsonwebtoken.controller;

import com.example.jsonwebtoken.dto.JoinDto;
import com.example.jsonwebtoken.dto.LoginDto;
import com.example.jsonwebtoken.entity.Member;
import com.example.jsonwebtoken.jwt.JwtFilter;
import com.example.jsonwebtoken.jwt.JwtTokenProvider;
import com.example.jsonwebtoken.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SampleController {
    private final MemberService memberService;
    private final AuthenticationManagerBuilder managerBuilder;
    private final JwtTokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/save")
    public ResponseEntity save(@RequestBody JoinDto dto){
        Member member = Member.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role("ROLE_USER")
                .build();

        return new ResponseEntity(memberService.save(member),HttpStatus.CREATED);
    }

    @PostMapping("/auth")
    public ResponseEntity authorize(@RequestBody LoginDto loginDto){
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(loginDto.getUsername(),loginDto.getPassword());

        Authentication auth = managerBuilder.getObject().authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(auth);

        String jwt= tokenProvider.createToken(auth);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer "+jwt);
        HashMap<String,String> map =new HashMap<>();
        map.put("token",jwt);
        return new ResponseEntity(map, httpHeaders, HttpStatus.OK);
    }

    @GetMapping("/user")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<Member> getMyUserInfo() {
        return new ResponseEntity("USE ANYONE",HttpStatus.OK);
    }

    @GetMapping("/user/{username}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Member> getUserInfo(@PathVariable String username) {
        return new ResponseEntity("Only ADMIN",HttpStatus.OK);
    }


}
