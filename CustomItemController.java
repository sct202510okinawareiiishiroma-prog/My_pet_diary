package com.example.demo;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/custom-item")
public class CustomItemController {

    @Autowired
    private CustomItemRepository customItemRepository;

    /**
     * 項目の保存（新規作成 兼 更新）
     */
    @PostMapping("/save")
    public String saveCustomItem(
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam("itemName") String itemName,
            @RequestParam(value = "calcType", required = false, defaultValue = "SUM") String calcType,
            Principal principal) {
        
        CustomItem item;
        if (id != null) {
            // 編集の場合：既存データを取得
            item = customItemRepository.findById(id).orElse(new CustomItem());
        } else {
            // 新規作成の場合
            item = new CustomItem();
            item.setCalcType(calcType);
        }
        
        item.setUsername(principal.getName());
        item.setItemName(itemName);
        item.setEnabled(true); // 保存時は表示状態にする
        
        customItemRepository.save(item);
        return "redirect:/";
    }

    /**
     * 項目の削除（非表示化）
     */
    @PostMapping("/delete")
    public String deleteCustomItem(@RequestParam("id") Long id) {
        customItemRepository.findById(id).ifPresent(item -> {
            item.setEnabled(false); // 物理削除せず非表示にするだけ
            customItemRepository.save(item);
        });
        return "redirect:/";
    }
}