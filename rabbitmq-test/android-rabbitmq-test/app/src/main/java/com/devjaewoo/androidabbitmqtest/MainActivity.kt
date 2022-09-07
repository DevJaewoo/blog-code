package com.devjaewoo.androidabbitmqtest

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.rabbitmq.client.ConnectionFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        CoroutineScope(Dispatchers.IO).launch {
            kotlin.runCatching {
                try {
                    val queueName = UUID.randomUUID().toString()

                    ConnectionFactory().apply { host = "192.168.25.7" }
                        .newConnection()
                        .createChannel().apply {
                            queueDeclare(queueName, false, false, true, null)
                            basicConsume(queueName, true,
                                { consumerTag, message ->
                                    Log.d(TAG, "DeliverCallback: tag: $consumerTag, message: ${message.body.toString(Charsets.UTF_8)}")
                                },
                                { consumerTag ->
                                    Log.d(TAG, "CancelCallback: $consumerTag")
                                }
                            )

                            RequestHandler.request(
                                "/rabbit/register",
                                JSONObject().apply { put("queue", queueName) },
                                { response ->
                                    Log.d(TAG, "onCreate: $response")
                                },
                                null,
                                false,
                                Request.Method.POST
                            )
                        }

                } catch (e: Exception) {
                    Log.e(TAG, "onCreate: ${e.message}", e.cause)
                    e.printStackTrace()
                }
            }
        }
    }
}