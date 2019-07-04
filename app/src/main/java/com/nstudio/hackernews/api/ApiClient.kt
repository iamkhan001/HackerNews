package com.nstudio.hackernews.api

import com.nstudio.hackernews.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

class ApiClient {

    companion object {

        private var  retrofit: Retrofit? = null

        fun getClient(): Retrofit {

            if (retrofit == null) {
                val logging = HttpLoggingInterceptor()
                logging.level = HttpLoggingInterceptor.Level.BODY


                val httpClient = OkHttpClient.Builder()

                //want to avoid custom url encoding , here is solution
                httpClient.addInterceptor { chain ->
                    val request = chain.request()
                    var string = request.url().toString()
                    string = string.replace("%2C", ",")
                    //string = string.replace("%3D", "=");
                    val newRequest = Request.Builder()
                        .url(string)
                        .build()
                    chain.proceed(newRequest)
                }

                httpClient.connectTimeout(1, TimeUnit.MINUTES)
                httpClient.readTimeout(1, TimeUnit.MINUTES)
                httpClient.writeTimeout(1, TimeUnit.MINUTES)
                httpClient.followRedirects(true)
                httpClient.followSslRedirects(true)


                if (BuildConfig.DEBUG) {
                    httpClient.addInterceptor(logging)
                }

                val builder = Retrofit.Builder().baseUrl("https://hacker-news.firebaseio.com/")
                retrofit = builder.client(httpClient.build())
                    .build()
            }

            return retrofit as Retrofit
        }
    }




}