package com.itmo.microservices.shop.payment.impl.entity;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Objects;

@Entity
public class FinancialOperationType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;

    public FinancialOperationType(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public FinancialOperationType() {

    }

    @Override
    public String toString() {
        return "FinancialOperationType{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        FinancialOperationType financialOperationType = (FinancialOperationType) o;
        return Objects.equals(id, financialOperationType.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
