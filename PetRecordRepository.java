package com.example.demo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
//--[JpaRepository]を継承し、標準装備を整える
public interface PetRecordRepository extends JpaRepository <PetRecord,Long> {
	// --ユーザー名で日記を絞り込むメソッド --
    List<PetRecord> findByUsername(String username);

}

//--ここから先はSpringBootが自動生成してくれる