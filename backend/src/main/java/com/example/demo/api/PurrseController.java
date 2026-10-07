package com.example.demo.api;

import com.example.demo.model.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class PurrseController {
	private final PurrseService svc;

	public PurrseController(PurrseService svc) { this.svc = svc; }

	@GetMapping("/dashboard")
	public Map<String, Object> dashboard() { return svc.dashboard(); }

	@GetMapping("/accounts")
	public List<Account> accounts() { return svc.listAccounts(); }

	@PostMapping("/accounts")
	@ResponseStatus(HttpStatus.CREATED)
	public Account createAccount(@Valid @RequestBody Account a) { return svc.createAccount(a); }

	@DeleteMapping("/accounts/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteAccount(@PathVariable Long id) { svc.deleteAccount(id); }

	@GetMapping("/transactions")
	public List<Txn> transactions() { return svc.listTransactions(); }

	@PostMapping("/transactions")
	@ResponseStatus(HttpStatus.CREATED)
	public Txn createTransaction(@Valid @RequestBody Txn t) { return svc.createTransaction(t); }

	@DeleteMapping("/transactions/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteTransaction(@PathVariable Long id) { svc.deleteTransaction(id); }

	@GetMapping("/goals")
	public List<Goal> goals() { return svc.listGoals(); }

	@PostMapping("/goals")
	@ResponseStatus(HttpStatus.CREATED)
	public Goal createGoal(@Valid @RequestBody Goal g) { return svc.createGoal(g); }

	/** Body: {"amount": 25.00}. Use a negative amount to take money back out. */
	@PostMapping("/goals/{id}/contribute")
	public Goal contribute(@PathVariable Long id, @RequestBody Map<String, BigDecimal> body) {
		BigDecimal amt = body.get("amount");
		if (amt == null) throw PurrseService.bad("amount is required");
		return svc.contribute(id, amt);
	}

	@DeleteMapping("/goals/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteGoal(@PathVariable Long id) { svc.deleteGoal(id); }
}
