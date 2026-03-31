package app.lawnchair.auth

import android.content.Context
import app.lawnchair.util.kotlinxJson
import com.squareup.wire.GrpcClient
import me.textnow.api.loyalty.v1.LoyaltyServiceClient
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

class TextNowApiClient private constructor(context: Context) {

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(TextNowSessionInterceptor(context.applicationContext))
        .build()

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(kotlinxJson.asConverterFactory("application/json".toMediaType()))
        .build()

    private val grpcClient: GrpcClient = GrpcClient.Builder()
        .client(okHttpClient)
        .baseUrl(BASE_URL)
        .build()

    val loyaltyService: LoyaltyServiceClient = grpcClient.create(LoyaltyServiceClient::class)

    companion object {
        private const val BASE_URL = "https://api.prod.textnow.me/"

        @Volatile
        private var instance: TextNowApiClient? = null

        fun getInstance(context: Context): TextNowApiClient {
            return instance ?: synchronized(this) {
                instance ?: TextNowApiClient(context.applicationContext).also { instance = it }
            }
        }
    }
}
