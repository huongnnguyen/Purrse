package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

@Entity
@Table(name = "accounts")
public class Account {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@NotBlank
	private String name;
	private String type;

	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal balance = BigDecimal.ZERO;

	public Long getId() { return id; }
	public Long getUserId() { return userId; }
	public void setUserId(Long userId) { this.userId = userId; }
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	public String getType() { return type; }
	public void setType(String type) { this.type = type; }
	public BigDecimal getBalance() { return balance; }
	public void setBalance(BigDecimal balance) { this.balance = balance; }
}
