package com.example.stripe_demo

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiInterface {

    @POST("/payment-sheet")
    suspend fun createPaymentIntent(@Body data: PaymentRequest): Response<PaymentDetailResponse>
}