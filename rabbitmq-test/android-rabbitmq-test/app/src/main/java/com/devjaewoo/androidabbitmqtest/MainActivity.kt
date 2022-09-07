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

        //네트워크 작업은 Main Thread에서 하면 Exception 터짐
        CoroutineScope(Dispatchers.IO).launch {
            kotlin.runCatching {
                try {
                    //랜덤 UUID 생성
                    val queueName = UUID.randomUUID().toString()

                    //RabbitMQ 서버와 연결
                    ConnectionFactory().apply { host = "192.168.25.7" }
                        .newConnection()
                        .createChannel().apply {
                            //UUID로 Queue 생성
                            queueDeclare(queueName, false, false, true, null)

                            //생성한 Queue에 Callback Listener 등록
                            basicConsume(queueName, true,
                                { consumerTag, message ->
                                    Log.d(TAG, "DeliverCallback: tag: $consumerTag, message: ${message.body.toString(Charsets.UTF_8)}")
                                },
                                { consumerTag ->
                                    Log.d(TAG, "CancelCallback: $consumerTag")
                                }
                            )

                            //Spring 서버에 Queue UUID 전송
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