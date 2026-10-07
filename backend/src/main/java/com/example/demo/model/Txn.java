package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "transactions")
public class Txn {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Column(name = "account_id", nullable = false)
	private Long accountId;

	@NotBlank
	private String merchant;

	@NotNull
	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal amount;

	@Column(name = "date", nullable = false)
	private LocalDate date;

	private String category;

	/** "income" or "expense" (anything that is not income is treated as an expense). */
	private String type;

	public boolean isIncome() { return "income".equalsIgnoreCase(type); }

	/** Effect on the account balance: income adds, anything else subtracts. */
	public BigDecimal signedAmount() {
		BigDecimal a = amount.abs();
		return isIncome() ? a : a.negate();
	}

	public Long getId() { return id; }
	public Long getAccountId() { return accountId; }
	public void setAccountId(Long accountId) { this.accountId = accountId; }
	public String getMerchant() { return merchant; }
	public void setMerchant(String merchant) { this.merchant = merchant; }
	public BigDecimal getAmount() { return amount; }
	public void setAmount(BigDecimal amount) { this.amount = amount; }
	public LocalDate getDate() { return date; }
	public void setDate(LocalDate date) { this.date = date; }
	public String getCategory() { return category; }
	public void setCategory(String category) { this.category = category; }
	public String getType() { return type; }
	public void setType(String type) { this.type = type; }
}
