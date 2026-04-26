package com.example.demo;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PetRecordRepository extends JpaRepository<PetRecord, Long> {

	/**
	 * 通常の全件取得（ユーザー別）
	 * ※現在は日別集計の findDailyTotalsByUsername を主に使用
	 */
	List<PetRecord> findDailyTotalsByUsername(String username);

	/**
	 * 【既存】日別累計・最新値取得クエリ（全期間）
	 */
	@Query(value = """
			SELECT
			    DATE(r.created_at) as created_at,    -- [0] 日付
			    r.weight,                            -- [1] 体重
			    r.temperature,                       -- [2] 気温
			    r.humidity,                          -- [3] 湿度
			    sums.total_food as food,             -- [4] ごはん
			    sums.aggregated_custom_values,       -- [5] カスタム項目
			    sums.all_memos as memo,              -- [6] メモ
			    r.id                                 -- [7] ID
			FROM pet_records r
			INNER JOIN (
			    SELECT
			        MAX(p.id) as last_id,
			        SUM(p.food) as total_food,
			        GROUP_CONCAT(p.memo SEPARATOR '\\n') as all_memos,
			        (
			            SELECT GROUP_CONCAT(CONCAT(ci.id, ':', agg.val))
			            FROM custom_item ci
			            LEFT JOIN (
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

	/**
	 * 【新規追加】日別累計・最新値取得クエリ（期間指定版）
	 */
	@Query(value = """
			SELECT
			    DATE(r.created_at) as created_at,    -- [0] 日付
			    r.weight,                            -- [1] 体重
			    r.temperature,                       -- [2] 気温
			    r.humidity,                          -- [3] 湿度
			    sums.total_food as food,             -- [4] ごはん
			    sums.aggregated_custom_values,       -- [5] カスタム項目 ★ここに入れ替え
			    sums.all_memos as memo,              -- [6] メモ
			    r.id                                 -- [7] ID
			FROM pet_records r
			INNER JOIN (
			    SELECT
			        MAX(p.id) as last_id,
			        SUM(p.food) as total_food,
			        GROUP_CONCAT(p.memo SEPARATOR '\\n') as all_memos,
			        (
			            SELECT GROUP_CONCAT(CONCAT(ci.id, ':', agg.val))
			            FROM custom_item ci
			            LEFT JOIN (
			                SELECT
			                    civ.custom_item_id,
			                    CASE
			                        WHEN i.calc_type = 'SUM' THEN SUM(civ.value)
			                        ELSE (SELECT value FROM custom_item_value WHERE id = MAX(civ.id))
			                    END as val
			                FROM custom_item_value civ
			                JOIN custom_item i ON civ.custom_item_id = i.id
			                JOIN pet_records pr ON civ.pet_record_id = pr.id
			                WHERE pr.username = :username
			                  AND pr.created_at BETWEEN :start AND :end
			                GROUP BY civ.custom_item_id
			            ) agg ON ci.id = agg.custom_item_id
			            WHERE ci.username = :username
			        ) as aggregated_custom_values
			    FROM pet_records p
			    WHERE p.username = :username
			      AND p.created_at BETWEEN :start AND :end
			    GROUP BY DATE(p.created_at)
			) sums ON r.id = sums.last_id
			ORDER BY r.created_at DESC
			""", nativeQuery = true)
	List<Object[]> findDailyTotalsByPeriodWithCustomAggregated(
			@Param("username") String username,
			@Param("start") LocalDateTime start,
			@Param("end") LocalDateTime end);
}