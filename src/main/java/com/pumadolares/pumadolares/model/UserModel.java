package com.pumadolares.pumadolares.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;

@Entity
public class UserModel {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private String name;

  private String email;

  private double currency = 0.0;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "JOB_ID", nullable = false)
  private JobModel job;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public double getCurrency() {
    return currency;
  }

  public void setCurrency(double currency) {
    double newCurrency = Math.max((double) 0, currency);
    this.currency = newCurrency;
  }

  @PrePersist
  public void PrePersist() {
    this.job = new JobModel();
    this.job.setId(1);
    this.job.setName("Sin establecer");

  }

  public void setJob(JobModel job) {
    this.job = job;
  }

}