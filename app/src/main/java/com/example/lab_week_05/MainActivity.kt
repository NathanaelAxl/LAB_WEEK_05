package com.example.lab_week_05

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.lab_week_05.api.CatApiService
import com.example.lab_week_05.model.ImageData
import retrofit2.*
import retrofit2.converter.moshi.MoshiConverterFactory

class MainActivity : AppCompatActivity() {

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.thecatapi.com/v1/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    private val catApiService: CatApiService by lazy {
        retrofit.create(CatApiService::class.java)
    }

    private val apiResponseView: TextView by lazy {
        findViewById<TextView>(R.id.api_response)
    }

    private val imageResultView: ImageView by lazy {
        findViewById<ImageView>(R.id.image_result)
    }

    private val imageLoader: ImageLoader by lazy {
        GlideLoader(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        getCatImageResponse()
    }

    private fun getCatImageResponse() {
        val call = catApiService.searchImages(1, "full")
        call.enqueue(object : Callback<List<ImageData>> {
            override fun onFailure(call: Call<List<ImageData>>, t: Throwable) {
                Log.e(MAIN_ACTIVITY, "Failed to get response", t)
            }

            override fun onResponse(
                call: Call<List<ImageData>>,
                response: Response<List<ImageData>>
            ) {
                if (response.isSuccessful) {
                    val image = response.body()
                    val firstImage = image?.firstOrNull()

                    val imageUrl = firstImage?.url.orEmpty()

                    val breedName = firstImage?.breeds?.firstOrNull()?.name ?: "Unknown"

                    if (imageUrl.isNotBlank()) {
                        imageLoader.loadImage(imageUrl, imageResultView)
                    } else {
                        Log.d(MAIN_ACTIVITY, "Missing image URL")
                    }

                    apiResponseView.text = getString(R.string.image_placeholder, breedName)
                } else {
                    Log.e(
                        MAIN_ACTIVITY,
                        "Failed to get response\n" + response.errorBody()?.string().orEmpty()
                    )
                }
            }
        })
    }


    companion object {
        const val MAIN_ACTIVITY = "MAIN_ACTIVITY"
    }
}
