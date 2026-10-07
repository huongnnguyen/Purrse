package com.example.demo;

import com.example.demo.api.PurrseService;
import com.example.demo.model.Account;
import com.example.demo.model.Goal;
import com.example.demo.model.Txn;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PurrseApplicationTests {

	@Autowired
	PurrseService svc;

	private Account account(String name, String balance) {
		Account a = new Account();
		a.setName(name);
		a.setType("checking");
		a.setBalance(new BigDecimal(balance));
		return svc.createAccount(a);
	}

	private Txn txn(Long accountId, String amount, String type, String category) {
		Txn t = new Txn();
		t.setAccountId(accountId);
		t.setMerchant("Test Merchant");
		t.setAmount(new BigDecimal(amount));
		t.setType(type);
		t.setCategory(category);
		t.setDate(LocalDate.now());
		return svc.createTransaction(t);
	}

	private BigDecimal balanceOf(Long id) {
		return svc.listAccounts().stream().filter(a -> a.getId().equals(id)).findFirst().orElseThrow().getBalance();
	}

	@Test
	void transactionsMoveTheBalanceAndCanBeReversed() {
		Account a = account("Main", "100.00");
		Txn spend = txn(a.getId(), "30.25", "expense", "Dining");
		assertEquals(0, new BigDecimal("69.75").compareTo(balanceOf(a.getId())));

		txn(a.getId(), "50.00", "income", "Paycheck");
		assertEquals(0, new BigDecimal("119.75").compareTo(balanceOf(a.getId())));

		svc.deleteTransaction(spend.getId());
		assertEquals(0, new BigDecimal("150.00").compareTo(balanceOf(a.getId())));
	}

	@Test
	void overdraftIsRejected() {
		Account a = account("Tiny", "10.00");
		assertThrows(ResponseStatusException.class, () -> txn(a.getId(), "10.01", "expense", "Dining"));
		assertEquals(0, new BigDecimal("10.00").compareTo(balanceOf(a.getId())));
	}

	@Test
	void dashboardSummarisesSpendingByCategory() {
		Account a = account("Dash", "500.00");
		txn(a.getId(), "40.00", "expense", "ZZ-Groceries");
		txn(a.getId(), "10.00", "expense", "ZZ-Groceries");
		var dash = svc.dashboard();
		assertTrue(((BigDecimal) dash.get("expenses")).compareTo(new BigDecimal("50.00")) >= 0);
		assertNotNull(dash.get("spendByCategory"));
	}

	@Test
	void goalsAcceptContributions() {
		Goal g = new Goal();
		g.setName("Trip");
		g.setTargetAmount(new BigDecimal("1000.00"));
		g = svc.createGoal(g);
		g = svc.contribute(g.getId(), new BigDecimal("125.50"));
		assertEquals(0, new BigDecimal("125.50").compareTo(g.getCurrentAmount()));
		Long id = g.getId();
		assertThrows(ResponseStatusException.class, () -> svc.contribute(id, new BigDecimal("-500")));
	}
}
