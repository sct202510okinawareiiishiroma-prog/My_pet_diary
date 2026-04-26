package com.example.demo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users") // データベースのテーブル名を指定
public class User {

    @Id // 主キー（ユーザーを一意に識別するID）
    @Column(length = 50)
    private String username; // ユーザーID

    @Column(nullable = false)
    private String password; // 暗号化されたパスワード

    @Column(nullable = false)
    private boolean enabled; // アカウントが有効かどうか
    
    @Column(length = 255) 
    private String favoritePhoto;

    // --- 追加したフィールドのゲッターとセッター ---
    public String getFavoritePhoto() {
        return favoritePhoto;
    }

    public void setFavoritePhoto(String favoritePhoto) {
        this.favoritePhoto = favoritePhoto;
    }

    // --- ゲッターとセッター（Eclipseの機能で自動生成も可能です） ---

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}