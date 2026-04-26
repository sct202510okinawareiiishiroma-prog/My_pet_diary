package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    // JpaRepositoryを継承することで、標準的な保存・削除・検索機能が使えます。
    // 第2引数のStringは、User.javaで@Idに設定したusernameの型です。
}