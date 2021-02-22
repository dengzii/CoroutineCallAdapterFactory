package com.dengzii.coroutineadapter

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.dengzii.calladapter.coroutine.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit

class SecondaryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_secondary)

        val httpClient = OkHttpClient.Builder().build()
        val retrofit = Retrofit.Builder().run {
            baseUrl("https://www.baidu.com/")
            client(httpClient)
            addCallAdapterFactory(CoroutineCallAdapterFactory.create())
//            addConverterFactory(MoshiConvertFactory.create())
            build()
        }

        val api = retrofit.create(Api::class.java)

        findViewById<View>(R.id.bt_a).setOnClickListener {
            api.gank().launch {
                onSuccess { it1 ->
                    findViewById<TextView>(R.id.tv_screen).text =
                        it1.data.map { it.type }.toString()
                }
                onFail {
                    println("onError")
                    it.printStackTrace()
                }
                onCancel {
                    println("onCancel")
                }
                onStart {
                    println("onStart.")
                }
                onComplete {
                    println("onComplete.")
                }
            }
        }

        findViewById<View>(R.id.bt_b).setOnClickListener {

        }
    }
}