package com.example.kino.presentation.ui.movie_details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.kino.R
import com.example.kino.data.network.IMAGE_URL
import com.example.kino.data.model.movie.RemoteMovie
import com.example.kino.domain.model.Movie
import com.example.kino.presentation.ui.lists.SharedViewModel
import com.example.kino.presentation.utils.constants.INTENT_KEY
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject

class MovieDetailsFragment : Fragment() {

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

    private val movieDetailsViewModel: MovieDetailsViewModel by inject()
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_movie_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViews(view)
        val bundle = this.arguments

        if (bundle != null) {
            getMovie(bundle.getInt(INTENT_KEY))
        }
    }

//    private fun setPlayer(view: View){
//
//        val youTubePlayerView: YouTubePlayerView = view.findViewById(R.id.youtube_player_view)
//        lifecycle.addObserver(youTubePlayerView)
//
//        youTubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
//            override fun onReady(youTubePlayer: YouTubePlayer) {
//                val videoId = "S0Q4gqBUs7c"
//                youTubePlayer.loadVideo(videoId, 0f)
//            }
//        })
//    }

    private fun bindViews(view: View) = with(view) {
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
        requireActivity().themeModeImage.visibility = View.GONE
    }

    private fun getMovie(id: Int) {

        movieDetailsViewModel.getMovie(id)
        movieDetailsViewModel.liveData.observe(requireActivity(), Observer { result ->
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
            //genres.text = movie.genreNames
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

        like.setOnClickListener {
            if (movie.isClicked) {
                movie.isClicked = false
                like.setImageResource(R.drawable.ic_turned_in_not_black_24dp)
            } else {
                movie.isClicked = true
                like.setImageResource(R.drawable.ic_turned_in_black_24dp)
            }
            movieDetailsViewModel.updateLikeStatus(movie)
            sharedViewModel.setMovie(movie)
        }

        Picasso.get()
            .load(IMAGE_URL + movie.posterPath)
            .into(poster)
    }
}