package com.example.demo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate; // 追加：API通信用

@Controller
public class HelloController {
	
	@Autowired
	private PetRecordRepository repository;
	
	/**
	 * Open-Meteo APIから沖縄県那覇市の現在の天気情報を取得するメソッド
	 */
	private Map<String, Object> getCurrentWeather() {
		// 那覇市の緯度(26.2124)・経度(127.6809)を指定
		String url = "https://api.open-meteo.com/v1/forecast?latitude=26.2124&longitude=127.6809&current=temperature_2m,relative_humidity_2m,weather_code&timezone=Asia/Tokyo";
		
		try {
			RestTemplate restTemplate = new RestTemplate();
			// APIを実行して結果をMap形式で受け取る
			Map<String, Object> response = restTemplate.getForObject(url, Map.class);
			// "current"（現在の気象データ）の部分だけを抜き出して返す
			return (Map<String, Object>) response.get("current");
		} catch (Exception e) {
			System.out.println("天気情報の取得に失敗しました: " + e.getMessage());
			return null;
		}
	}

	@GetMapping("/")
	public String index(Model model) {
		List<PetRecord> records = repository.findAll();
		model.addAttribute("records", records);
		
		// 【追加】那覇市の現在の天気を取得して画面に渡す
		Map<String, Object> weather = getCurrentWeather();
		model.addAttribute("currentWeather", weather);
		
		// フォーム用の空オブジェクト
		model.addAttribute("editingRecord", new PetRecord());
		
		//天気情報を取得して画面に渡す
		model.addAttribute("currentWeather",getCurrentWeather());
		
		return "index";
	}

	@PostMapping("/save")
	public String save(
			@RequestParam(value = "id", required = false) Long id,
			@RequestParam("temperature") double temp,
			@RequestParam("weight") double weight,
			@RequestParam("humidity") int humidity,
			@RequestParam("memo") String memo,
			@RequestParam(value = "customTimestamp", required = false) String customTimestamp) {
		
		PetRecord record;
		if (id != null) {
			record = repository.findById(id).orElse(new PetRecord());
		} else {
			record = new PetRecord();
		}

		record.setTemperature(temp);
		record.setHumidity(humidity);
		record.setWeight(weight);
		record.setMemo(memo);
        
		if (customTimestamp != null && !customTimestamp.isEmpty()) {
			record.setCreatedAt(LocalDateTime.parse(customTimestamp));
		}
		
		repository.save(record);
		return "redirect:/";
	}

	@GetMapping("/edit/{id}")
	public String edit(@PathVariable("id") Long id, Model model) {
		Optional<PetRecord> record = repository.findById(id);
		if (record.isPresent()) {
			model.addAttribute("editingRecord", record.get());
			model.addAttribute("records", repository.findAll());
			
			// 編集画面でも最新の天気を表示させる
			model.addAttribute("currentWeather", getCurrentWeather());
			
			return "index";
		}
		return "redirect:/";
	}

	@GetMapping("/delete/{id}")
	public String delete(@PathVariable("id") Long id) {
		repository.deleteById(id);
		System.out.println("ID:" + id + " のデータを削除しました。");
		return "redirect:/";
	}
}