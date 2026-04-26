package com.example.demo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "custom_item_value")
@Data
public class CustomItemValue {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // カラム名の重複を避けるため、ID直接保持かオブジェクト保持かどちらかに寄せます
    // ここではControllerでの使いやすさを優先し、IDを直接扱う設定にします
    @Column(name = "pet_record_id")
    private Long petRecordId; 

    @Column(name = "custom_item_id")
    private Long customItemId; 
    
    @Column(name = "value")
    private Double value;

    // --- 以下、独自のロジックを持つセッターだけ残す ---
    
    public void setValue(Double value) {
        if (value != null) {
            if (value < 0) {
                this.value = 0.0;
            } else if (value >= 10000) { // 4桁上限
                this.value = 9999.9;
            } else {
                this.value = value;
            }
        }
    }

    // 標準的な getId, setId, getPetRecordId... などは @Data が作ってくれるので不要
}