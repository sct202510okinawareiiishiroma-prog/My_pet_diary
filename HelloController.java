package com.example.demo;

import java.security.Principal;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class HelloController {

	@Autowired
	private PetRecordRepository repository;

	@Autowired
	private UserRepository userRepository;

	@GetMapping("/")
	public String index(Model model, Principal principal) {
		String username = principal.getName();
		//--個人データ取得--
		List<PetRecord> records = repository.findByUsername(username);
		model.addAttribute("records", records);
		//--ユーザー情報を取得して画面に渡す--
		Optional<User> userOpt = userRepository.findById(username);
		if (userOpt.isPresent()) {
			model.addAttribute("user", userOpt.get());
		}
		model.addAttribute("editingRecord", new PetRecord()); // 新規投稿用空オブジェクト
		return "index";
	}

	@PostMapping("/save")
	public String save(@ModelAttribute PetRecord record, Principal principal) {
		String username = principal.getName();

		if (record.getId() != null) {
			// 【更新処理】既存のデータをDBから読み込む
			Optional<PetRecord> existingOpt = repository.findById(record.getId());
			if (existingOpt.isPresent()) {
				PetRecord existing = existingOpt.get();
				// セキュリティ：自分のデータかチェック
				if (existing.getUsername().equals(username)) {
					existing.setWeight(record.getWeight());
					existing.setTemperature(record.getTemperature());
					existing.setHumidity(record.getHumidity());
					existing.setMemo(record.getMemo());
					// createdAt は existing のものを保持したまま保存される
					repository.save(existing);
				}
			}
		} else {
			// 【新規登録処理】
			record.setUsername(username);
			repository.save(record);
		}
		return "redirect:/";
	}

	@GetMapping("/edit/{id}")
	public String edit(@PathVariable("id") Long id, Model model, Principal principal) {
		Optional<PetRecord> recordOpt = repository.findById(id);
		if (recordOpt.isPresent() && recordOpt.get().getUsername().equals(principal.getName())) {
			model.addAttribute("editingRecord", recordOpt.get());
			model.addAttribute("records", repository.findByUsername(principal.getName()));
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

	//--写真アップロード処理--
	@PostMapping("/uploadPhoto")
	public String uploadPhoto(@RequestParam("photo") MultipartFile file, Principal principal) {
		if (!file.isEmpty()) {
			try {
				// 画像ファイルを「Base64」という文字列データに変換
				String base64Image = Base64.getEncoder().encodeToString(file.getBytes());

				// 現在のログインユーザーを取得して、画像データをセットして保存
				String username = principal.getName();
				Optional<User> userOpt = userRepository.findById(username);

				if (userOpt.isPresent()) {
					User user = userOpt.get();
					user.setFavoritePhoto(base64Image);
					userRepository.save(user);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return "redirect:/"; // トップページに戻る
	}
}