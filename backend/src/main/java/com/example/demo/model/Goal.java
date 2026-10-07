package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "goals")
public class Goal {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@NotBlank
	private String name;

	@NotNull
	@Positive
	@Column(name = "target_amount", nullable = false, precision = 10, scale = 2)
	private BigDecimal targetAmount;

	@Column(name = "current_amount", nullable = false, precision = 10, scale = 2)
	private BigDecimal currentAmount = BigDecimal.ZERO;

	private LocalDate deadline;

	public Long getId() { return id; }
	public Long getUserId() { return userId; }
	public void setUserId(Long userId) { this.userId = userId; }
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	public BigDecimal getTargetAmount() { return targetAmount; }
	public void setTargetAmount(BigDecimal targetAmount) { this.targetAmount = targetAmount; }
	public BigDecimal getCurrentAmount() { return currentAmount; }
	public void setCurrentAmount(BigDecimal currentAmount) { this.currentAmount = currentAmount; }
	public LocalDate getDeadline() { return deadline; }
	public void setDeadline(LocalDate deadline) { this.deadline = deadline; }
}
