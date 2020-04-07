package com.example.kino.Activities

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.kino.ApiKey
import com.example.kino.MovieClasses.MovieDetails
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
    private lateinit var genres: TextView
    private lateinit var durationTime: TextView
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
        poster = findViewById(R.id.ivPoster)
        title = findViewById(R.id.tvName)
        year = findViewById(R.id.tvYear)
        genres = findViewById(R.id.tvGenres)
        durationTime = findViewById(R.id.duration)
        tagline = findViewById(R.id.tvTagline)
        description = findViewById(R.id.description)
        rating = findViewById(R.id.rating)
        votesCount = findViewById(R.id.votesCount)
        companies = findViewById(R.id.companies)
    }

    private fun getMovie(id: Int) {
        RetrofitService.getPostApi().getMovieById(id, ApiKey).enqueue(object : Callback<MovieDetails> {
            override fun onFailure(call: Call<MovieDetails>, t: Throwable) {
                progressBar.visibility = View.GONE
            }

            override fun onResponse(call: Call<MovieDetails>, response: Response<MovieDetails>) {
                progressBar.visibility = View.GONE
                val movieDetails = response.body()
                if (movieDetails != null) {
                    title.text = movieDetails.title
                    year.text = movieDetails.releaseDate

                    genres.text = ""

                   for(i in movieDetails.genres.indices){
                        if(i == 0) genres.text  = movieDetails.genres[i].genre.toLowerCase(Locale.ROOT)
                        else genres.append(", " + movieDetails.genres[i].genre.toLowerCase(Locale.ROOT))
                    }

                    durationTime.text = movieDetails.runtime.toString() + " min"
                    tagline.text = "«" + movieDetails.tagline + "»"
                    description.text = movieDetails.overview
                    rating.text = movieDetails.voteAverage.toString()
                    votesCount.text = movieDetails.voteCount.toString()

                    var companiesString = ""

                    for(company in movieDetails.productionCompanies){
                        companiesString += (company.name + ", ")
                    }
                    companies.text = companiesString.substring(0, companiesString.length-2)

                    Picasso.get()
                        .load("https://image.tmdb.org/t/p/w500" + movieDetails.posterPath)
                        .into(poster)
                }
            }
        })
    }
}
