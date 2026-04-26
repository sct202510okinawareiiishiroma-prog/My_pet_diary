package com.example.demo;

import java.time.LocalDateTime;
import java.util.regex.Pattern;

import org.springframework.web.util.HtmlUtils; // エスケープ用ライブラリ

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "pet_records")
public class PetRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private Double temperature;
    private Integer humidity;
    private Double weight;
    
    @Column(nullable = false)
    private Double food = 0.0;
    
    @Column(columnDefinition = "TEXT")
    private String memo;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    //@OneToMany(mappedBy = "petRecord") 
    //private List<CustomItemValue> customValues;

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    // --- 改修した Getter & Setter ---

    // 【setUsername】英数字のみ、255文字上限、エスケープ
    public void setUsername(String username) {
        if (username != null) {
            String escaped = HtmlUtils.htmlEscape(username);
            if (escaped.length() > 255) {
                escaped = escaped.substring(0, 255);
            }
            // 英数字のみ許可するチェック
            if (Pattern.matches("^[a-zA-Z0-9]*$", username)) {
                this.username = escaped;
            }
        }
    }

    // 【setTemperature】数値であること（型で担保）
    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public void setHumidity(Integer humidity) {
        if (humidity != null) {
            if (humidity < 0) this.humidity = 0;
            else if (humidity > 100) this.humidity = 100;
            else this.humidity = humidity;
        }
    }

    // 【setWeight】数値、マイナス不可、3桁上限(999.9)
    public void setWeight(Double weight) {
        if (weight != null) {
            if (weight < 0) this.weight = 0.0;
            else if (weight >= 1000) this.weight = 999.9;
            else this.weight = weight;
        }
    }
    
    // 【setFood】数値、マイナス不可、4桁上限(9999.9)
    public void setFood(Double food) {
        if (food != null) {
            if (food < 0) this.food = 0.0;
            else if (food >= 10000) this.food = 9999.9;
            else this.food = food;
        }
    }

    // 【setMemo】エスケープ処理
    public void setMemo(String memo) {
        if (memo != null) {
            this.memo = HtmlUtils.htmlEscape(memo);
        }
    }

    // 【setCreatedAt】未来の日付は許可しない（現在時刻に丸める）
    public void setCreatedAt(LocalDateTime createdAt) {
        if (createdAt != null && createdAt.isAfter(LocalDateTime.now())) {
            this.createdAt = LocalDateTime.now();
        } else {
            this.createdAt = createdAt;
        }
    }

    //public List<CustomItemValue> getCustomValues() { return customValues; }
    //public void setCustomValues(List<CustomItemValue> customValues) { this.customValues = customValues; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public Double getTemperature() { return temperature; }
    public Integer getHumidity() { return humidity; }
    public Double getWeight() { return weight; }
    public Double getFood() { return food; }
    public String getMemo() { return memo; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}