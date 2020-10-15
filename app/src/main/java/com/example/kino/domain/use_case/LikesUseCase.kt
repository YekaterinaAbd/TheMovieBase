package com.example.kino.domain.use_case

import com.example.kino.data.model.movie.SelectedMovie
import com.example.kino.data.network.API_KEY
import com.example.kino.domain.repository.MovieRepository

class LikesUseCase(private val movieRepository: MovieRepository) {

    suspend fun loadLocalLikes(sessionId: String) = movieRepository.loadLocalLikes(sessionId)

    suspend fun updateLikeStatus(movie: SelectedMovie, sessionId: String) =
        movieRepository.updateLikeStatus(movie, sessionId)

    suspend fun getRemoteMovieLike(id: Int, sessionId: String) =
        movieRepository.getRemoteMovieStates(id, API_KEY, sessionId)
}
