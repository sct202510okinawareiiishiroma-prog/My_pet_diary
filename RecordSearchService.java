package com.example.demo;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class RecordSearchService {

	/**
	 * 指定されたモードに基づいて、検索期間（開始・終了）を計算する
	 * @param mode "day", "week", "month", "year", "all"
	 * @return "start" と "end" の LocalDateTime を保持する Map
	 */
	public Map<String, LocalDateTime> calculatePeriod(String mode) {
		LocalDateTime start;
		LocalDateTime end = LocalDateTime.now().with(LocalTime.MAX); // 基本、今日の23:59:59まで

		switch (mode) {
		case "day":
			// 当日 0:00 〜 23:59
			start = LocalDateTime.now().with(LocalTime.MIN);
			break;

		case "week":
			// 直近7日間（今日を含めて7日前から）
			start = LocalDateTime.now().minusDays(6).with(LocalTime.MIN);
			break;

		case "month":
			// 当月初日 0:00 〜 当月末日 23:59
			start = LocalDateTime.now().with(TemporalAdjusters.firstDayOfMonth()).with(LocalTime.MIN);
			end = LocalDateTime.now().with(TemporalAdjusters.lastDayOfMonth()).with(LocalTime.MAX);
			break;

		case "year":
			// 当年 1月1日 0:00 〜 12月31日 23:59
			start = LocalDateTime.now().with(TemporalAdjusters.firstDayOfYear()).with(LocalTime.MIN);
			end = LocalDateTime.now().with(TemporalAdjusters.lastDayOfYear()).with(LocalTime.MAX);
			break;

		default:
            // 案A: 広大な範囲を指定して実質全件表示にする
            start = LocalDateTime.of(2000, 1, 1, 0, 0);
            end = LocalDateTime.of(2100, 12, 31, 23, 59);
            break;
    }
		Map<String, LocalDateTime> period = new HashMap<>();
		period.put("start", start);
		period.put("end", end);
		return period;
	}
}