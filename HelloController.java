package com.example.demo;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
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

	// 【追加】カスタム項目用のリポジトリを注入
	@Autowired
	private CustomItemRepository customItemRepository;

	@GetMapping("/")
	public String index(Model model, Principal principal) {
		String username = principal.getName();

		// --個人データ（健康記録）取得--
		List<PetRecord> records = repository.findDailyTotalsByUsername(username);
		model.addAttribute("records", records);

		// 全ての項目ではなく、IsEnabled が true（有効）なものだけを取得するように変更
	    List<CustomItem> customItems = customItemRepository.findByUsernameAndIsEnabledTrue(username);
	    model.addAttribute("customItems", customItems);

		// --ユーザー情報を取得して画面に渡す--
		Optional<User> userOpt = userRepository.findById(username);
		if (userOpt.isPresent()) {
			model.addAttribute("user", userOpt.get());
		}

		model.addAttribute("editingRecord", new PetRecord());
		return "index";
	}
	
	// クラス冒頭のAutowiredに追加
	@Autowired
	private CustomItemValueRepository customItemValueRepository;

	@PostMapping("/save")
	public String save(
	        @ModelAttribute PetRecord record, 
	        @RequestParam Map<String, String> allParams, // すべてのリクエストパラメータを受け取る
	        Principal principal) {
	    
	    String username = principal.getName();
	    PetRecord savedRecord;

	    // 1. 通常の記録（体重、気温など）を保存
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
	    // 送信された全パラメータから "customItem_" で始まるものを探す
	    for (String key : allParams.keySet()) {
	        if (key.startsWith("customItem_")) {
	            try {
	                // keyは "customItem_5" のような形式なので、"_" 以降のIDを取り出す
	                Long itemId = Long.parseLong(key.split("_")[1]);
	                String valueStr = allParams.get(key);

	                if (valueStr != null && !valueStr.isEmpty()) {
	                    Double value = Double.parseDouble(valueStr);

	                    // 値保存用のエンティティを作成して保存
	                    CustomItemValue valEntity = new CustomItemValue();
	                    valEntity.setPetRecord(savedRecord); // どの日の記録か
	                    valEntity.setCustomItem(customItemRepository.findById(itemId).orElse(null)); // どの項目か
	                    valEntity.setValue(value);
	                    
	                    customItemValueRepository.save(valEntity);
	                }
	            } catch (Exception e) {
	                // 数値変換エラーなどはログに出力してスキップ（現場では適切にハンドリングします）
	                e.printStackTrace();
	            }
	        }
	    }

	    return "redirect:/";
	}

	@GetMapping("/edit/{id}")
	public String edit(@PathVariable("id") Long id, Model model, Principal principal) {
		Optional<PetRecord> recordOpt = repository.findById(id);
		if (recordOpt.isPresent() && recordOpt.get().getUsername().equals(principal.getName())) {
			model.addAttribute("editingRecord", recordOpt.get());
			//model.addAttribute("records", repository.findByUsername(principal.getName()));
			return "index";
		}
		return "redirect:/";
	}

	@GetMapping("/delete/{id}")
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
				// 1. 保存先フォルダのパスを指定 (プロジェクト直下の user-photos フォルダ)
				String uploadDir = "user-photos/";
				Path uploadPath = Paths.get(uploadDir);

				// フォルダが存在しない場合は作成する
				if (!Files.exists(uploadPath)) {
					Files.createDirectories(uploadPath);
				}

				// 2. ファイル名の重複を防ぐため、UUID（ランダムなID）を使ってファイル名を生成
				// 元のファイル形式に関わらず、リサイズ後は .jpg として統一保存
				String fileName = UUID.randomUUID().toString() + ".jpg";

				// 3. Thumbnailator を使ってリサイズ処理
				// 幅800px、高さ800pxに収まるようにリサイズ（比率は維持されます）
				Thumbnails.of(file.getInputStream())
						.size(800, 800)
						.outputFormat("jpg")
						.toFile(uploadDir + fileName);

				// 4. DBには「ファイル名」だけを保存する
				String username = principal.getName();
				Optional<User> userOpt = userRepository.findById(username);

				if (userOpt.isPresent()) {
					User user = userOpt.get();
					user.setFavoritePhoto(fileName); // ここでファイル名をセット
					userRepository.save(user);
				}

			} catch (Exception e) {
				e.printStackTrace();
				// 実際の運用ではここでエラーメッセージを画面に返す処理を入れますが、
				// まずは動作確認のためスタックトレースを表示させます
			}
		}
		return "redirect:/";
	}
}