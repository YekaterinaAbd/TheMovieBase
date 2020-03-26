package com.example.kino

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.kino.MovieClasses.MovieDetailed
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MovieDetailActivity : AppCompatActivity() {

    val API_KEY: String = "d118a5a4e56930c8ce9bd2321609d877"

    private lateinit var progressBar: ProgressBar
    private lateinit var tvTitle: TextView
    private lateinit var tvBody: TextView

    private lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_detail)

        progressBar = findViewById(R.id.progressBar)
        tvTitle = findViewById(R.id.tvTitle)
        tvBody = findViewById(R.id.tvBody)
        imageView = findViewById(R.id.imageView)

        val postId = intent.getIntExtra("movie_id", 1)
        getMovie(id = postId)
    }

    private fun getMovie(id: Int) {
        RetrofitService.getPostApi().getMovieById(id, API_KEY).enqueue(object : Callback<MovieDetailed> {
            override fun onFailure(call: Call<MovieDetailed>, t: Throwable) {
                progressBar.visibility = View.GONE
            }

            override fun onResponse(call: Call<MovieDetailed>, response: Response<MovieDetailed>) {
                progressBar.visibility = View.GONE
                val movie = response.body()
                if (movie != null) {
                    tvBody.text = movie.overview
                    tvTitle.text = movie.title

                    Picasso.get()
                        .load("https://image.tmdb.org/t/p/w500" + movie.poster_path)
                        .into(imageView)
                }
            }
        })
    }
}
