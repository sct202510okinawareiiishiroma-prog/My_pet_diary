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
	private String itemName;
	private String calcType; // "SUM" または "LATEST"
	
	@Column(name = "is_enabled")
	private boolean isEnabled = true; // デフォルトは true (表示)

	// Getter と Setter
	public boolean isEnabled() { return isEnabled; }
	public void setEnabled(boolean isEnabled) { this.isEnabled = isEnabled; }
	}