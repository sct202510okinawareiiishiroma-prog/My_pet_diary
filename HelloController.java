package com.example.demo;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HelloController {
	
	@Autowired
	private PetRecordRepository repository;
	
	@GetMapping("/")
	public String index(Model model) { // 引数をスッキリさせました
		List<PetRecord> records = repository.findAll();
		model.addAttribute("records", records);
		return "index";
	}

	@PostMapping("/save")
	public String save(
			@RequestParam("temperature") double temp,
			@RequestParam("weight") double weight,
			@RequestParam("humidity") int humidity,
			@RequestParam("memo") String memo,
			@RequestParam(value = "customTimestamp", required = false) String customTimestamp) {
		
		System.out.println("---MyPetDiary受信データ ---");
		System.out.println("気温:" + temp + "℃");
		System.out.println("湿度:" + humidity + "%");
		System.out.println("体重:" + weight + "kg");
		System.out.println("メモ:" + memo);
		
		if (weight > 10.0) {
			System.out.println("【異常検知】体重が10kgを超えています！チェックしてください。");
		}
		
		PetRecord record = new PetRecord();
		record.setTemperature(temp);
		record.setHumidity(humidity);
		record.setWeight(weight);
		record.setMemo(memo);
        
		// 日時が入力されていればセットするロジック
		if (customTimestamp != null && !customTimestamp.isEmpty()) {
			record.setCreatedAt(LocalDateTime.parse(customTimestamp));
		}
		
		repository.save(record);

		return "redirect:/";
	}
}