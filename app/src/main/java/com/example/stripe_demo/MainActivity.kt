package com.example.stripe_demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    lateinit var paymentSheet: PaymentSheet
    lateinit var customerConfig: PaymentSheet.CustomerConfiguration
    lateinit var paymentIntentClientSecret: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val edtAmount = findViewById<EditText>(R.id.etAmount)

        paymentSheet = PaymentSheet(this, ::onPaymentSheetResult)
        findViewById<Button>(R.id.btnPayment).setOnClickListener {
            val amount = edtAmount.text.toString().toInt()
            val paymentRequest = PaymentRequest(
                paymentMethodId = "pm_card_visa",
                amount = amount,
                currency = "vnd"
            )
            fetchPaymentIntent(paymentRequest)
        }
    }

    private fun fetchPaymentIntent(paymentRequest: PaymentRequest) {
        lifecycleScope.launch {
            try {
                val response = RetrofitInstance.api.createPaymentIntent(paymentRequest)
                if (response.isSuccessful && response.body() != null) {
                    val paymentIntent = response.body()!!.paymentIntent
                    val customer = response.body()!!.customer
                    val ephemeralKey = response.body()!!.ephemeralKey
                    val publishableKeyFromServer = response.body()!!.publishableKey
                    paymentIntentClientSecret = paymentIntent
                    customerConfig = PaymentSheet.CustomerConfiguration(
                        customer,
                        ephemeralKey
                    )
                    val publishableKey = publishableKeyFromServer
                    PaymentConfiguration.init(this@MainActivity, publishableKey)

                    // Kiểm tra giá trị không phải là null trước khi gọi hàm
                    if (paymentIntentClientSecret != null) {
                        presentPaymentSheet()
                    } else {
                        Toast.makeText(this@MainActivity, "Error: PaymentIntentClientSecret is null", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(this@MainActivity, "Error: ${response.message()}", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun onPaymentSheetResult(paymentSheetResult: PaymentSheetResult) {
        when(paymentSheetResult) {
            is PaymentSheetResult.Canceled -> {
                print("Canceled")
                Toast.makeText(this, "Payment Canceled", Toast.LENGTH_LONG).show()
            }
            is PaymentSheetResult.Failed -> {
                print("Error: ${paymentSheetResult.error}")
                Toast.makeText(this, "Error: ${paymentSheetResult.error}", Toast.LENGTH_LONG).show()
            }
            is PaymentSheetResult.Completed -> {
                // Display for example, an order confirmation screen
                print("Completed")
                Toast.makeText(this, "Payment Completed", Toast.LENGTH_LONG).show()
            }
        }
    }
    fun presentPaymentSheet() {
        paymentSheet.presentWithPaymentIntent(
            paymentIntentClientSecret!!,
            PaymentSheet.Configuration(
                merchantDisplayName = "Example",
                customer = customerConfig,
            )
        )
    }
}