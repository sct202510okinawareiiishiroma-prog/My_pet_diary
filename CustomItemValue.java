package com.example.demo;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "custom_item_value")
public class CustomItemValue {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "pet_record_id")
    private PetRecord petRecord; 
    
    @ManyToOne
    @JoinColumn(name = "custom_item_id")
    private CustomItem customItem;
    private Double value;

    // --- 以下、GetterとSetter ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public PetRecord getPetRecord() { return petRecord; }
    public void setPetRecord(PetRecord petRecord) { this.petRecord = petRecord; }
    
    public CustomItem getCustomItem() { return customItem; }
    public void setCustomItem(CustomItem customItem) { this.customItem = customItem; }
    
    public Double getValue() { return value; }
 // CustomItemValue.java の setValue メソッドを修正
    public void setValue(Double value) {
        if (value != null) {
            if (value < 0) {
                this.value = 0.0;
            } else if (value >= 10000) { // 4桁上限 (9999.9)
                this.value = 9999.9;
            } else {
                this.value = value;
            }
        }
    }
}