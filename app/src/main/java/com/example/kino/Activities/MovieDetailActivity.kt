package com.example.kino.Activities

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.kino.*
import com.example.kino.MovieClasses.Movie
import com.squareup.picasso.Picasso
import kotlinx.coroutines.*
import java.util.*
import kotlin.coroutines.CoroutineContext

class MovieDetailActivity : AppCompatActivity(), CoroutineScope {

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

    private var movieDao: MovieDao? = null

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_detail)

        movieDao = MovieDatabase.getDatabase(context = this).movieDao()
        bindViews()

        val postId = intent.getIntExtra("movie_id", 1)
        getMovie(id = postId)
    }

    private fun bindViews() {
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

        launch {
            val movie = withContext(Dispatchers.IO) {
                try {
                    val response = RetrofitService.getPostApi().getMovieById(id, ApiKey)
                    if (response.isSuccessful) {
                        val movieDetails = response.body()
                        if (movieDetails != null) {
                            if (movieDetails.runtime != null) {
                                movieDao?.updateMovieRuntime(movieDetails.runtime!!, id)
                            }
                            if (movieDetails.tagline != null) {
                                movieDao?.updateMovieTagline(movieDetails.tagline!!, id)
                            }
                        }
                        return@withContext movieDetails
                    } else {
                        return@withContext movieDao?.getMovie(id)
                    }

                } catch (e: Exception) {
                    movieDao?.getMovie(id)
                }
            }
            progressBar.visibility = View.GONE

            val movieDetails: Movie = movie as Movie
            title.text = movieDetails.title
            year.text = movieDetails.releaseDate
            genres.text = ""

            if (movieDetails.genres != null) {
                for (i in movieDetails.genres.indices) {
                    if (i == 0) genres.text =
                        movieDetails.genres[i].genre.toLowerCase(Locale.ROOT)
                    else genres.append(
                        ", " + movieDetails.genres[i].genre.toLowerCase(
                            Locale.ROOT
                        )
                    )
                }
            } else {
                genres.text =
                    movieDetails.genreNames.substring(0, movieDetails.genreNames.length - 2)
            }

            if (movieDetails.runtime != null)
                durationTime.text = movieDetails.runtime.toString() + " min"

            if (movieDetails.tagline != null)
                tagline.text = "«" + movieDetails.tagline + "»"

            description.text = movieDetails.overview
            rating.text = movieDetails.voteAverage.toString()
            votesCount.text = movieDetails.voteCount.toString()

            Picasso.get()
                .load("https://image.tmdb.org/t/p/w500" + movieDetails.posterPath)
                .into(poster)
        }
    }
}


