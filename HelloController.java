package com.example.demo;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import net.coobird.thumbnailator.Thumbnails;

@Controller
public class HelloController {

	@Autowired
	private PetRecordRepository repository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CustomItemRepository customItemRepository;

	@Autowired
	private CustomItemValueRepository customItemValueRepository;

	@Autowired
	private RecordSearchService recordSearchService;

	@GetMapping("/")
	public String index(
			@RequestParam(name = "mode", defaultValue = "day") String mode,
			Model model, 
			Principal principal) {
		
		String username = principal.getName();

		// 期間計算とデータ取得
		Map<String, LocalDateTime> period = recordSearchService.calculatePeriod(mode);
		LocalDateTime start = period.get("start");
		LocalDateTime end = period.get("end");

		List<Object[]> records = repository.findDailyTotalsByPeriodWithCustomAggregated(username, start, end);
		
		model.addAttribute("records", records);
		model.addAttribute("currentMode", mode);
		
		// 【追加】新規登録用の空のオブジェクトを渡す
	    if (!model.containsAttribute("editingRecord")) {
	        model.addAttribute("editingRecord", new PetRecord());
	    }

		// 有効なカスタム項目の取得
	    List<CustomItem> customItems = customItemRepository.findByUsernameAndIsEnabledTrue(username);
	    model.addAttribute("customItems", customItems);

		// 写真取得
	    Optional<User> userOpt = userRepository.findById(username);
	    if (userOpt.isPresent()) {
	        // "user" という名前でオブジェクトを渡す
	        model.addAttribute("user", userOpt.get());
	    }
	    
	    // カスタム項目新規登録用の空のオブジェクトを渡す
	    model.addAttribute("customItem", new CustomItem());
	    
		return "index";
	}

	// ★ 復活：保存・更新処理（カスタム項目対応）
	@PostMapping("/save")
	public String save(
	        @ModelAttribute PetRecord record, 
	        @RequestParam Map<String, String> allParams, 
	        Principal principal) {
	    
	    String username = principal.getName();
	    PetRecord savedRecord;

	    // 1. 通常の記録（体重、気温など）を保存または更新
	    if (record.getId() != null) {
	        Optional<PetRecord> existingOpt = repository.findById(record.getId());
	        if (existingOpt.isPresent() && existingOpt.get().getUsername().equals(username)) {
	            PetRecord existing = existingOpt.get();
	            existing.setWeight(record.getWeight());
	            existing.setTemperature(record.getTemperature());
	            existing.setHumidity(record.getHumidity());
	            existing.setFood(record.getFood());
	            existing.setMemo(record.getMemo());
	            savedRecord = repository.save(existing);
	        } else {
	            return "redirect:/";
	        }
	    } else {
	        record.setUsername(username);
	        savedRecord = repository.save(record);
	    }

	    // 2. 動的なカスタム項目の値を保存
	    for (String key : allParams.keySet()) {
	        if (key.startsWith("customItem_")) {
	            try {
	                Long itemId = Long.parseLong(key.split("_")[1]);
	                String valueStr = allParams.get(key);

	                if (valueStr != null && !valueStr.isEmpty()) {
	                    Double value = Double.parseDouble(valueStr);
	                    CustomItemValue valEntity = new CustomItemValue();
	                    valEntity.setPetRecord(savedRecord);
	                    valEntity.setCustomItem(customItemRepository.findById(itemId).orElse(null));
	                    valEntity.setValue(value);
	                    customItemValueRepository.save(valEntity);
	                }
	            } catch (Exception e) {
	                e.printStackTrace();
	            }
	        }
	    }
	    return "redirect:/?mode=day";
	}

	// ★ 復活：編集画面表示
	@GetMapping("/edit/{id}")
	public String edit(@PathVariable("id") Long id, Model model, Principal principal) {
		Optional<PetRecord> recordOpt = repository.findById(id);
		if (recordOpt.isPresent() && recordOpt.get().getUsername().equals(principal.getName())) {
			model.addAttribute("editingRecord", recordOpt.get());
			
			// 編集画面でもリストを表示する必要があるため、indexと同様の処理を行う
			return index("day", model, principal); 
		}
		return "redirect:/";
	}

	@PostMapping("/delete/{id}")
	public String delete(@PathVariable("id") Long id, Principal principal) {
		Optional<PetRecord> recordOpt = repository.findById(id);
		if (recordOpt.isPresent() && recordOpt.get().getUsername().equals(principal.getName())) {
			repository.deleteById(id);
		}
		return "redirect:/";
	}

	@PostMapping("/uploadPhoto")
	public String uploadPhoto(@RequestParam("photo") MultipartFile file, Principal principal) {
		if (!file.isEmpty()) {
			try {
				String uploadDir = "user-photos/";
				Path uploadPath = Paths.get(uploadDir);
				if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);
				
				String fileName = UUID.randomUUID().toString() + ".jpg";
				Thumbnails.of(file.getInputStream()).size(800, 800).outputFormat("jpg").toFile(uploadDir + fileName);

				Optional<User> userOpt = userRepository.findById(principal.getName());
				if (userOpt.isPresent()) {
					User user = userOpt.get();
					user.setFavoritePhoto(fileName);
					userRepository.save(user);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return "redirect:/";
	}
}