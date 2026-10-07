package com.example.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class AppUser {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;

	public AppUser() {}
	public AppUser(String name) { this.name = name; }

	public Long getId() { return id; }
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
}
