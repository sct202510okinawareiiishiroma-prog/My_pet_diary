package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
                               @RequestParam("password") String password,
                               RedirectAttributes redirectAttributes) {
        
        // 重複チェック
        if (userRepository.existsById(username)) {
            redirectAttributes.addFlashAttribute("errorMessage", "そのユーザーIDは既に使用されています。");
            return "redirect:/register";
        }
        
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setEnabled(true);
        
        userRepository.save(user);
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