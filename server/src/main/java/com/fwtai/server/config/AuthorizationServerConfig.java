package com.fwtai.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import java.util.concurrent.TimeUnit;

/**
 * 认证服务器
*/
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    //认证服务器委托一个AuthenticationManager 来验证用户信息(校验用户名和密码的),也就是认证服务器需要知道是哪一个子系统客户端来访问(认证)资源服务器
    @Override
    public void configure(final AuthorizationServerEndpointsConfigurer endpoints) {
        //endpoints.tokenStore(jwtTokenStore()).accessTokenConverter(jwtAccessTokenConverter());
        final DefaultTokenServices tokenServices = (DefaultTokenServices) endpoints.getDefaultAuthorizationServerTokenServices();
        //tokenServices.setTokenStore(endpoints.getTokenStore());
        tokenServices.setTokenStore(jwtTokenStore());
        tokenServices.setSupportRefreshToken(true);
        tokenServices.setClientDetailsService(endpoints.getClientDetailsService());
        //tokenServices.setTokenEnhancer(endpoints.getTokenEnhancer());
        tokenServices.setTokenEnhancer(jwtAccessTokenConverter());
        // 一天有效期
        tokenServices.setAccessTokenValiditySeconds((int) TimeUnit.DAYS.toSeconds(1));
        endpoints.tokenServices(tokenServices);
    }

    //配置客户端索取令牌详情,作为授权服务器必须知道或配置有哪些第三方应用来向授权服务器获取索取令牌
    @Override
    public void configure(final ClientDetailsServiceConfigurer clients) throws Exception {
        // 定义了两个客户端应用的通行证
        clients.inMemory()
            .withClient("web1")
            .secret(new BCryptPasswordEncoder().encode("123456"))
            //.authorizedGrantTypes("authorization_code", "refresh_token")
            .authorizedGrantTypes("authorization_code", "refresh_token","password")
            .scopes("all")
            .autoApprove(true)
            //加上验证后回调地址???若是客户端不填写的话,那就是事先访问的url认证后跳转到原访问的URL,如不添加会报错->error="invalid_request", error_description="At least one redirect_uri must be registered with the client."
            .redirectUris("http://www.web1.com:8086/login")//这个是和authorization_code一起使用的,这个不能少!
            .and()//第2个子系统
            .withClient("web2")
            .secret(new BCryptPasswordEncoder().encode("123456"))
            //.authorizedGrantTypes("authorization_code", "refresh_token")
            .authorizedGrantTypes("authorization_code", "refresh_token","password")
            .scopes("all")
            .autoApprove(true)
            .redirectUris("http://www.web2.com:8087/login");//这个是和authorization_code一起使用的,这个不能少!
    }

    //从(认证)资源服务器获取token是需要认证成功之后才能获取(这里配置意思是带入自己的appid和appsecuret)进行验证
    @Override
    public void configure(final AuthorizationServerSecurityConfigurer security) {
        security.tokenKeyAccess("isAuthenticated()");
    }

    @Bean
    public TokenStore jwtTokenStore() {
        return new JwtTokenStore(jwtAccessTokenConverter());
    }

    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter(){
        final JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setSigningKey("testKey");//密钥密盐
        return converter;
    }
}