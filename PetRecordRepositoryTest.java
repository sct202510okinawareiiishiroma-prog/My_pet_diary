package com.example.demo;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

@DataJpaTest
// 既存のMySQLデータベースに対してテストを実行するための設定
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PetRecordRepositoryTest {

    @Autowired
    private PetRecordRepository petRecordRepository;

    @Test
    @DisplayName("期間指定集計クエリが、Object[]の正しいインデックスに値を返すか検証")
    void testFindDailyTotalsByPeriodWithCustomAggregated() {
        // 1. テスト条件の設定
        String username = "testUser";
        LocalDateTime start = LocalDateTime.of(2026, 4, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2026, 4, 30, 23, 59);

        // 2. クエリの実行（SQLの構文エラーがあればここでエラーが出る）
        List<Object[]> results = petRecordRepository.findDailyTotalsByPeriodWithCustomAggregated(username, start, end);

        // 3. 検証（アサーション）
        // データが存在する場合のみ実行
        if (!results.isEmpty()) {
            Object[] firstRow = results.get(0);
            
            // リポジトリのコメントにあるインデックス番号と一致しているか確認
            assertNotNull(firstRow[0], "created_atが取得できていること");
            assertNotNull(firstRow[7], "IDが取得できていること");
            
            // SQL内のSUM(p.food)が正しく動作しているか（Double型などの数値であること）
            if (firstRow[4] != null) {
                assertTrue(firstRow[4] instanceof Number, "foodは数値型であること");
            }
            
            System.out.println("テスト成功: 取得件数 " + results.size());
        }
    }
}