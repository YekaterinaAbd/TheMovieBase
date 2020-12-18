package com.example.movies.domain.use_case

import com.example.movies.data.model.movie.FavouriteMovie
import com.example.movies.data.model.movie.WatchListMovie
import com.example.movies.data.network.API_KEY
import com.example.movies.domain.repository.MovieRepository

class LikesUseCase(private val movieRepository: MovieRepository) {

    suspend fun synchronizeLocalLikes(sessionId: String) =
        movieRepository.synchronizeLocalStatuses(sessionId)

    suspend fun updateIsFavourite(movie: FavouriteMovie, sessionId: String) =
        movieRepository.updateIsFavourite(movie, sessionId)

    suspend fun updateIsInWatchList(movie: WatchListMovie, sessionId: String) =
        movieRepository.updateIsInWatchList(movie, sessionId)

    suspend fun getMovieStates(id: Int, sessionId: String) =
        movieRepository.getMovieStates(id, API_KEY, sessionId)

}
