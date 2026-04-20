package com.example.demo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PetRecordRepository extends JpaRepository<PetRecord, Long> {

    /**
     * 通常の全件取得（ユーザー別）
     */
    //List<PetRecord> findByUsername(String username);
    List<PetRecord> findDailyTotalsByUsername(String username);

    /**
     * 日別累計・最新値取得クエリ
     * * 仕組み：
     * 1. サブクエリ(sums)で、日ごとの「最新ID(MAX)」「ごはん合計(SUM)」「メモ連結(GROUP_CONCAT)」を算出。
     * 2. メインクエリで、その最新IDに紐づく「体重・気温・湿度」の行データをINNER JOINで取得。
     * これにより、MAX(weight)のような「数値の最大」ではなく「最新の数値」を正確に表示できます。
     */
    @Query(value = """
        SELECT 
            r.id, 
            r.username, 
            r.temperature, 
            r.humidity, 
            r.weight, 
            sums.total_food as food, 
            sums.all_memos as memo, 
            DATE(r.created_at) as created_at,
            sums.aggregated_custom_values -- ★追加：集計されたカスタム値の文字列
        FROM pet_records r
        INNER JOIN (
            SELECT 
                MAX(p.id) as last_id,
                SUM(p.food) as total_food,
                GROUP_CONCAT(p.memo SEPARATOR '\n') as all_memos,
                -- ★ここからカスタム項目の集計ロジック
                (
                    SELECT GROUP_CONCAT(CONCAT(ci.id, ':', agg.val))
                    FROM custom_item ci
                    LEFT JOIN (
                        -- 各項目について SUM か LATEST(MAX(id)) かを判定して集計
                        SELECT 
                            civ.custom_item_id,
                            CASE 
                                WHEN i.calc_type = 'SUM' THEN SUM(civ.value)
                                ELSE (SELECT value FROM custom_item_value WHERE id = MAX(civ.id))
                            END as val
                        FROM custom_item_value civ
                        JOIN custom_item i ON civ.custom_item_id = i.id
                        JOIN pet_records pr ON civ.pet_record_id = pr.id
                        WHERE DATE(pr.created_at) = DATE(p.created_at)
                        GROUP BY civ.custom_item_id
                    ) agg ON ci.id = agg.custom_item_id
                    WHERE ci.username = :username
                ) as aggregated_custom_values
            FROM pet_records p
            WHERE p.username = :username
            GROUP BY DATE(p.created_at)
        ) sums ON r.id = sums.last_id
        ORDER BY r.created_at DESC
        """, nativeQuery = true)
    List<Object[]> findDailyTotalsWithCustomAggregated(@Param("username") String username);
}