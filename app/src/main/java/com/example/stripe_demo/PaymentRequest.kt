package com.example.stripe_demo

data class PaymentRequest(
    val paymentMethodId: String,
    val amount: Int,
    val currency: String
)
