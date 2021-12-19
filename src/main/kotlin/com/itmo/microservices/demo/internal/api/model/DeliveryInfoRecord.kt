package com.itmo.microservices.demo.internal.api.model

import java.util.*

data class DeliveryInfoRecord(
    val outcome: DeliverySubmissionOutcome,
    val preparedTime: Long,
    val attempts: Int,
    val submittedTime: Long,
    val transactionId: UUID,
    val submissionStartedTime: Long
)
