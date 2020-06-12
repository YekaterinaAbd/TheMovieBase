package com.example.kino.view.activities

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.kino.R
import com.example.kino.model.database.MovieDao
import com.example.kino.model.database.MovieDatabase
import com.example.kino.model.movie.Movie
import com.example.kino.model.repository.MovieRepositoryImpl
import com.example.kino.utils.IMAGE_URL
import com.example.kino.utils.INTENT_KEY
import com.example.kino.utils.RetrofitService
import com.example.kino.view_model.MovieDetailsViewModel
import com.example.kino.view_model.SharedViewModel
import com.squareup.picasso.Picasso
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
    private lateinit var like: ImageView

    private lateinit var movieDetailsViewModel: MovieDetailsViewModel
    private lateinit var movie: Movie
    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_detail)
        setViewModel()
        bindViews()

        val postId = intent.getIntExtra(INTENT_KEY, 1)
        getMovie(id = postId)
    }

    private fun setViewModel() {
        sharedViewModel = ViewModelProviders.of(this).get(SharedViewModel::class.java)
        val movieDao: MovieDao = MovieDatabase.getDatabase(this).movieDao()
        movieDetailsViewModel =
            MovieDetailsViewModel(this, MovieRepositoryImpl(movieDao, RetrofitService.getPostApi()))
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
        like = findViewById(R.id.btnLike)

        like.setOnClickListener {
            if (movie.isClicked) {
                movie.isClicked = false
                like.setImageResource(R.drawable.ic_turned_in_not_black_24dp)
            } else {
                movie.isClicked = true
                like.setImageResource(R.drawable.ic_turned_in_black_24dp)
            }
            movieDetailsViewModel.updateLike(movie)
            sharedViewModel.setMovie(movie)
        }
    }

    private fun getMovie(id: Int) {

        movieDetailsViewModel.getMovie(id)
        movieDetailsViewModel.liveData.observe(this, Observer { result ->
            when (result) {
                is MovieDetailsViewModel.State.HideLoading -> {
                    progressBar.visibility = View.GONE
                }
                is MovieDetailsViewModel.State.Result -> {
                    if (result.movie != null) {
                        bindData(result.movie)
                    }
                }
            }
        })
    }

    private fun bindData(movie: Movie) {
        title.text = movie.title
        year.text = movie.releaseDate
        description.text = movie.overview
        rating.text = movie.voteAverage.toString()
        votesCount.text = movie.voteCount.toString()

        genres.text = ""

        if (movie.genres != null) {
            for (i in movie.genres.indices) {
                if (i == 0) genres.text = movie.genres[i].genre.toLowerCase(Locale.ROOT)
                else genres.append(", " + movie.genres[i].genre.toLowerCase(Locale.ROOT))
            }
        } else {
            genres.text = movie.genreNames.substring(0, movie.genreNames.length - 2)
        }

        if (movie.runtime != null) {
            val runtime: String = movie.runtime.toString()
            durationTime.text = getString(R.string.duration_time, runtime)
        }

        if (movie.tagline != null) {
            val taglineText: String = movie.tagline.toString()
            tagline.text = getString(R.string.tagline, taglineText)
        }

        if (movie.isClicked) {
            like.setImageResource(R.drawable.ic_turned_in_black_24dp)
        } else {
            like.setImageResource(R.drawable.ic_turned_in_not_black_24dp)
        }

        Picasso.get()
            .load(IMAGE_URL + movie.posterPath)
            .into(poster)


    }
}


