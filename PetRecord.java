package com.example.demo;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

// [@Entity]このクラスがデータベースの「表（テーブル）」と結びつくデータ
@Entity
//[@Table]MySQLで作ったテーブル名「pet_records」とこのクラスを紐づけます
@Table(name = "pet_records") 
public class PetRecord {
	
	// 【@Id】この項目がデータの「主キー（背番号）」であることを示します
	@Id
	// 【@GeneratedValue】IDを1, 2, 3...と自動で増やすように設定します
	@GeneratedValue(strategy = GenerationType.IDENTITY) // IDを自動で増やす設定
	private Long id;
	
	//--ここからペットの記録データ--
	
	//気温を保存する変数
	private Double temperature;
	//湿度を保存する変数
	private Integer humidity;
	//体重を保存する変数
	private Double weight;
	//メモを保存する変数
	private String memo;
	
	//時刻の手動入力と自動記録に関する機能
	@Column(name = "created_at")
	private LocalDateTime createdAt;
	
	//データベースに現在時刻の自動セット
	@PrePersist
	protected void onCreate() {
		if (this.createdAt == null) {
			    this.createdAt = LocalDateTime.now();
		}
	}
	
	//--「Getter」と「Setter」のメソッド--
	public Long getId() { return id; }
	public void setId(Long id) { this.id = id;}
	
	public Double getTemperature() { return temperature; }
	public void setTemperature(Double temp) { this.temperature = temp; }
	
	public Integer getHumidity() { return humidity; }
	public void setHumidity(Integer humi) { this.humidity = humi; }
	
	public Double getWeight() { return weight; }
	public void setWeight(Double weight) { this.weight = weight; }
	
	public String getMemo() { return memo; }
	public void setMemo(String memo) { this.memo = memo; }
	
	public LocalDateTime getCreatedAt() { return createdAt; }
	public void setCreatedAt(LocalDateTime dateTime) {this.createdAt = dateTime; }
}
