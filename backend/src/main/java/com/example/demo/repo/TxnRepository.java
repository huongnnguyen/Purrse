package com.example.demo.repo;

import com.example.demo.model.Txn;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TxnRepository extends JpaRepository<Txn, Long> {
	List<Txn> findAllByOrderByDateDescIdDesc();
}
