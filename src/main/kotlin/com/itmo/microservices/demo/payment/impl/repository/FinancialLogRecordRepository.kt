package com.itmo.microservices.demo.payment.impl.repository

import com.itmo.microservices.demo.payment.api.model.UserAccountFinancialLogRecordDto
import com.itmo.microservices.demo.payment.impl.entity.FinancialLogRecordEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface FinancialLogRecordRepository: JpaRepository<FinancialLogRecordEntity, UUID>