package com.example.demo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "custom_item")
@Data
public class CustomItem {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String username;

    // DBのカラム名が 'item_name' の場合。もしDB側が 'name' なら (name = "name") に書き換えてください
    @Column(name = "item_name") 
    private String itemName;

    // DBのカラム名が 'calc_type' の場合
    @Column(name = "calc_type")
    private String calcType; 
    
    @Column(name = "is_enabled")
    private boolean isEnabled = true;
}