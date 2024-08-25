package com.example.kita_app

import android.content.Context
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.InputStream
import java.security.KeyStore
import java.security.cert.CertificateFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

object RetrofitClient {

    private const val BASE_URL = "https://127.0.0.1:5000/" // Android emulator's localhost: https://10.0.2.2:5000/

    private var token: String? = null

    fun setToken(newToken: String) {
        token = newToken
    }

    private fun getUnsafeOkHttpClient(context: Context): OkHttpClient.Builder {
        return try {
            val cf: CertificateFactory = CertificateFactory.getInstance("X.509")
            val caInput: InputStream = context.resources.openRawResource(R.raw.cert) // cert.crt in res/raw
            val ca = cf.generateCertificate(caInput)
            caInput.close()

            val keyStore: KeyStore = KeyStore.getInstance(KeyStore.getDefaultType()).apply {
                load(null, null)
                setCertificateEntry("ca", ca)
            }

            val tmf: TrustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm()).apply {
                init(keyStore)
            }

            val trustManagers = tmf.trustManagers
            val sslContext: SSLContext = SSLContext.getInstance("TLS").apply {
                init(null, trustManagers, java.security.SecureRandom())
            }

            val sslSocketFactory = sslContext.socketFactory
            val trustManager = trustManagers[0] as X509TrustManager

            OkHttpClient.Builder().apply {
                sslSocketFactory(sslSocketFactory, trustManager)
                hostnameVerifier { _, _ -> true }
            }
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    fun getClient(context: Context): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        val authInterceptor = Interceptor { chain ->
            val request: Request = if (token != null) {
                val newRequest = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
                newRequest
            } else {
                chain.request()
            }
            chain.proceed(request)
        }

        return getUnsafeOkHttpClient(context)
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .build()
    }

    fun getInstance(context: Context): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(getClient(context))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}