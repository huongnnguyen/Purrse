package com.example.demo.api;

import com.example.demo.model.*;
import com.example.demo.repo.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PurrseService {
	private final UserRepository users;
	private final AccountRepository accounts;
	private final TxnRepository txns;
	private final GoalRepository goals;

	public PurrseService(UserRepository users, AccountRepository accounts, TxnRepository txns, GoalRepository goals) {
		this.users = users;
		this.accounts = accounts;
		this.txns = txns;
		this.goals = goals;
	}

	/** Purrse is single-user for now: use the first user, creating one if the table is empty. */
	@Transactional
	public Long defaultUserId() {
		return users.findAll().stream().findFirst()
				.orElseGet(() -> users.save(new AppUser("Me"))).getId();
	}

	static ResponseStatusException bad(String msg) {
		return new ResponseStatusException(HttpStatus.BAD_REQUEST, msg);
	}

	static ResponseStatusException notFound(String what) {
		return new ResponseStatusException(HttpStatus.NOT_FOUND, what + " not found");
	}

	// ---- accounts ----
	public List<Account> listAccounts() { return accounts.findAll(); }

	@Transactional
	public Account createAccount(Account a) {
		a.setUserId(defaultUserId());
		if (a.getBalance() == null) a.setBalance(BigDecimal.ZERO);
		if (a.getBalance().signum() < 0) throw bad("Starting balance cannot be negative");
		return accounts.save(a);
	}

	@Transactional
	public void deleteAccount(Long id) {
		Account a = accounts.findById(id).orElseThrow(() -> notFound("Account"));
		txns.deleteAll(txns.findAll().stream().filter(t -> id.equals(t.getAccountId())).toList());
		accounts.delete(a);
	}

	// ---- transactions ----
	public List<Txn> listTransactions() { return txns.findAllByOrderByDateDescIdDesc(); }

	private void applyToBalance(Account acct, BigDecimal delta) {
		BigDecimal next = acct.getBalance().add(delta);
		if (next.signum() < 0) throw bad("That would take " + acct.getName() + " below $0.00");
		acct.setBalance(next);
		accounts.save(acct);
	}

	@Transactional
	public Txn createTransaction(Txn t) {
		Account acct = accounts.findById(t.getAccountId()).orElseThrow(() -> bad("Pick a valid account"));
		if (t.getAmount().signum() <= 0) throw bad("Amount must be greater than 0");
		if (t.getDate() == null) t.setDate(java.time.LocalDate.now());
		t.setType("income".equalsIgnoreCase(t.getType()) ? "income" : "expense");
		applyToBalance(acct, t.signedAmount());
		return txns.save(t);
	}

	@Transactional
	public void deleteTransaction(Long id) {
		Txn t = txns.findById(id).orElseThrow(() -> notFound("Transaction"));
		accounts.findById(t.getAccountId()).ifPresent(a -> applyToBalance(a, t.signedAmount().negate()));
		txns.delete(t);
	}

	// ---- goals ----
	public List<Goal> listGoals() { return goals.findAll(); }

	@Transactional
	public Goal createGoal(Goal g) {
		g.setUserId(defaultUserId());
		g.setCurrentAmount(g.getCurrentAmount() == null ? BigDecimal.ZERO : g.getCurrentAmount());
		if (g.getCurrentAmount().signum() < 0) throw bad("Saved amount cannot be negative");
		return goals.save(g);
	}

	@Transactional
	public Goal contribute(Long id, BigDecimal amount) {
		Goal g = goals.findById(id).orElseThrow(() -> notFound("Goal"));
		BigDecimal next = g.getCurrentAmount().add(amount);
		if (next.signum() < 0) throw bad("Goal cannot go below $0.00");
		g.setCurrentAmount(next);
		return goals.save(g);
	}

	@Transactional
	public void deleteGoal(Long id) {
		if (!goals.existsById(id)) throw notFound("Goal");
		goals.deleteById(id);
	}

	// ---- dashboard ----
	public Map<String, Object> dashboard() {
		List<Txn> all = txns.findAllByOrderByDateDescIdDesc();
		BigDecimal income = BigDecimal.ZERO, expenses = BigDecimal.ZERO;
		Map<String, BigDecimal> byCategory = new TreeMap<>();
		for (Txn t : all) {
			BigDecimal a = t.getAmount().abs();
			if (t.isIncome()) {
				income = income.add(a);
			} else {
				expenses = expenses.add(a);
				byCategory.merge(t.getCategory() == null || t.getCategory().isBlank() ? "Uncategorized" : t.getCategory(), a, BigDecimal::add);
			}
		}
		BigDecimal total = accounts.findAll().stream().map(Account::getBalance).reduce(BigDecimal.ZERO, BigDecimal::add);
		List<Map<String, Object>> cats = byCategory.entrySet().stream()
				.sorted((x, y) -> y.getValue().compareTo(x.getValue()))
				.map(e -> Map.<String, Object>of("category", e.getKey(), "total", e.getValue()))
				.collect(Collectors.toList());

		Map<String, Object> out = new LinkedHashMap<>();
		out.put("totalBalance", total);
		out.put("income", income);
		out.put("expenses", expenses);
		out.put("net", income.subtract(expenses));
		out.put("spendByCategory", cats);
		return out;
	}
}
