package com.example.demo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomItemRepository extends JpaRepository<CustomItem, Long> {
 //項目だけをユーザー名ごとに取得する
 List<CustomItem> findByUsernameAndIsEnabledTrue(String username);
}