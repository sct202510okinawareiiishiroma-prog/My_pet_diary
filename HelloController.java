package com.example.demo;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
	
	/**
	 * 【新機能】記録の削除
	 * @param id URLに含まれるデータのID（/delete/5 なら 5）
	 */
	@GetMapping("/delete/{id}")
	public String delete(@PathVariable("id") Long id) {
		// 指定されたIDのデータをデータベースから削除
		repository.deleteById(id);
		
		System.out.println("ID:" + id + " のデータを削除しました。");
		
		// 削除後は一覧画面に戻る
		return "redirect:/";
	}
}