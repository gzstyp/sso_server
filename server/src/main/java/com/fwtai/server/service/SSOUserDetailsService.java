package com.fwtai.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class SSOUserDetailsService implements UserDetailsService{

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(final String s) throws UsernameNotFoundException{
        final String user = "user";
        if(!user.equals(s)){
            throw new UsernameNotFoundException("用户不存在");
        }
        return new User(s,passwordEncoder.encode("123456"),AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER"));
    }
}