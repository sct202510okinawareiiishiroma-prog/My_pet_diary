package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository; // ユーザー保存用のリポジトリ（後ほど作成）

    @Autowired
    private PasswordEncoder passwordEncoder; // パスワード暗号化用

    /**
     * 新規会員登録画面を表示する
     */
    @GetMapping("/register")
    public String showRegistrationForm() {
        return "register"; // register.htmlを呼び出す
    }

    /**
     * 会員登録処理を実行する
     */
    @PostMapping("/register")
    public String registerUser(@RequestParam("username") String username, 
                               @RequestParam("password") String password) {
        
        // 1. 新しいユーザーオブジェクトを作成
        User user = new User();
        user.setUsername(username);
        
        // 2. パスワードを暗号化（ハッシュ化）してセット
        // 生のパスワードをそのままDBに入れるのは非常に危険なため、必ず暗号化します
        user.setPassword(passwordEncoder.encode(password));
        user.setEnabled(true); // アカウントを有効に設定
        
        // 3. データベースに保存
        userRepository.save(user);
        
        // 4. 登録完了後、ログイン画面へリダイレクト
        return "redirect:/login?register_success";
    }

    /**
     * ログイン画面を表示する
     */
    @GetMapping("/login")
    public String login() {
        return "login"; // login.html（今後作成）を呼び出す
    }
}