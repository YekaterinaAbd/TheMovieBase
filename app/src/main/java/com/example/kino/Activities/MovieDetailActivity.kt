package com.example.kino.Activities

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.kino.ApiKey
import com.example.kino.MovieClasses.MovieDetailed
import com.example.kino.R
import com.example.kino.RetrofitService
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class MovieDetailActivity : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar
    private lateinit var poster: ImageView
    private lateinit var title: TextView
    private lateinit var year: TextView
    private lateinit var tvGenres: TextView
    private lateinit var runtime: TextView
    private lateinit var tagline: TextView
    private lateinit var description: TextView
    private lateinit var rating: TextView
    private lateinit var votesCount: TextView
    private lateinit var companies: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_detail)

        bindViews()

        val postId = intent.getIntExtra("movie_id", 1)
        getMovie(id = postId)
    }

    private fun bindViews(){
        progressBar = findViewById(R.id.progressBar)
        poster = findViewById(R.id.poster)
        title = findViewById(R.id.name)
        year = findViewById(R.id.year)
        tvGenres = findViewById(R.id.genres)
        runtime = findViewById(R.id.runtime)
        tagline = findViewById(R.id.tagline)
        description = findViewById(R.id.description)
        rating = findViewById(R.id.rating)
        votesCount = findViewById(R.id.votes_count)
        companies = findViewById(R.id.companies)
    }

    private fun getMovie(id: Int) {
        RetrofitService.getPostApi().getMovieById(id, ApiKey).enqueue(object : Callback<MovieDetailed> {
            override fun onFailure(call: Call<MovieDetailed>, t: Throwable) {
                progressBar.visibility = View.GONE
            }

            override fun onResponse(call: Call<MovieDetailed>, response: Response<MovieDetailed>) {
                progressBar.visibility = View.GONE
                val movie = response.body()
                if (movie != null) {
                    title.text = movie.title
                    year.text = movie.releaseDate

                    tvGenres.text = ""

                   for(i in movie.genres.indices){
                        if(i == 0) tvGenres.text  = movie.genres[i].genre.toLowerCase(Locale.ROOT)
                        else tvGenres.append(", " + movie.genres[i].genre.toLowerCase(Locale.ROOT))
                    }

                    runtime.text = movie.runtime.toString() + " min"
                    tagline.text = "«" + movie.tagline + "»"
                    description.text = movie.overview
                    rating.text = movie.voteAverage.toString()
                    votesCount.text = movie.voteCount.toString()

                    var companiesString = ""

                    for(company in movie.productionCompanies){
                        companiesString += (company.name + ", ")
                    }
                    companies.text = companiesString.substring(0, companiesString.length-2)

                    Picasso.get()
                        .load("https://image.tmdb.org/t/p/w500" + movie.posterPath)
                        .into(poster)
                }
            }
        })
    }
}
