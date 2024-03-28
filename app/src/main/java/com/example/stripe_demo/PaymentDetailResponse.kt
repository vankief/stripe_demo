package com.example.stripe_demo

data class PaymentDetailResponse(
    val paymentIntent: String,
    val ephemeralKey: String,
    val customer:   String,
    val publishableKey: String
)

