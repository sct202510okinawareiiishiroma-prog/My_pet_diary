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
    List<PetRecord> findByUsername(String username);

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
            DATE(r.created_at) as created_at 
        FROM pet_records r
        INNER JOIN (
            -- 日ごとの「最新のID」「ごはん合計」「メモの連結」を算出するサブクエリ
            SELECT 
                MAX(id) as last_id,
                SUM(food) as total_food,
                GROUP_CONCAT(memo SEPARATOR '\n') as all_memos,
                DATE(created_at) as d_date
            FROM pet_records
            WHERE username = :username
            GROUP BY DATE(created_at)
        ) sums ON r.id = sums.last_id
        ORDER BY r.created_at DESC
        """, nativeQuery = true)
    List<PetRecord> findDailyTotalsByUsername(@Param("username") String username);
}