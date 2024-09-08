package aodintsov.to_do_list.data.api

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private const val BASE_URL = "https://api.openai.com/"

    // Функция для создания OkHttpClient с Interceptor, который добавляет API ключ в заголовки
    private fun createOkHttpClient(apiKey: String): OkHttpClient {
        val interceptor = Interceptor { chain ->
            val original: Request = chain.request()
            val request = original.newBuilder()
                .header("Authorization", "Bearer $apiKey")
                .header("Content-Type", "application/json")
                .method(original.method(), original.body())
                .build()
            chain.proceed(request)
        }

        return OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()
    }

    // Функция для создания экземпляра Retrofit с переданным API-ключом
    fun createRetrofit(apiKey: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(createOkHttpClient(apiKey))  // Подключаем наш OkHttpClient с Interceptor
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Функция для создания сервиса OpenAI
    fun createOpenAIService(apiKey: String): OpenAIService {
        return createRetrofit(apiKey).create(OpenAIService::class.java)
    }
}
