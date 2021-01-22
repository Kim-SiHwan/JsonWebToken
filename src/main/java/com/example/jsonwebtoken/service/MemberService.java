package com.example.jsonwebtoken.service;

import com.example.jsonwebtoken.entity.Member;
import com.example.jsonwebtoken.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {
    private final MemberRepository repository;

    public String save(Member member) {
        if (validateDuplicateMember(member)) {
            repository.save(member);
            return "가입완료!";
        }else{
            return "이미 존재하는 회원입니다.";
        }
    }

    private Boolean validateDuplicateMember(Member member) {
        Optional getMember = repository.findByUsername(member.getUsername());
        if (!getMember.isEmpty()) {
            return false;
        }
        return true;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        Member member = repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("해당 유저가 없습니다."));
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(member.getRole()));
        return new User(member.getUsername(), member.getPassword(), authorities);
    }

}
