package com.example.kino.domain.use_case

import com.example.kino.data.model.movie.FavouriteMovie
import com.example.kino.data.model.movie.WatchListMovie
import com.example.kino.data.network.API_KEY
import com.example.kino.domain.repository.MovieRepository

class LikesUseCase(private val movieRepository: MovieRepository) {

    suspend fun synchronizeLocalLikes(sessionId: String) =
        movieRepository.synchronizeLocalStatuses(sessionId)

    suspend fun updateIsFavourite(movie: FavouriteMovie, sessionId: String) =
        movieRepository.updateIsFavourite(movie, sessionId)

    suspend fun updateIsInWatchList(movie: WatchListMovie, sessionId: String) =
        movieRepository.updateIsInWatchList(movie, sessionId)

    suspend fun getRemoteMovieStatuses(id: Int, sessionId: String) =
        movieRepository.getRemoteMovieStatuses(id, API_KEY, sessionId)

}
