package com.example.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // 新規登録画面、CSS、JSなどはログインなしでアクセス可能にする
                .requestMatchers("/register", "/css/**", "/js/**").permitAll()
                // それ以外のページはすべてログインが必要
                .anyRequest().authenticated()
            )
            .formLogin(login -> login
                // 自作のログインページ（後ほど作成）を指定
                .loginPage("/login")
                // ログイン成功時の遷移先（トップページ）
                .defaultSuccessUrl("/", true)
                .permitAll()
            )
            .logout(logout -> logout
                // ログアウト成功時の遷移先
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // パスワードを「ハッシュ化」して保存するためのエンコーダー
        // これによりパスワードの安全性を強化
        return new BCryptPasswordEncoder();
    }
}