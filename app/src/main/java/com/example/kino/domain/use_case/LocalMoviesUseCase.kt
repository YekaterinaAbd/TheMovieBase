package com.example.kino.domain.use_case

import com.example.kino.domain.model.Movie
import com.example.kino.domain.repository.MovieRepository

class LocalMoviesUseCase(private val movieRepository: MovieRepository) {

    suspend fun deleteLocalMovies() = movieRepository.deleteLocalMovies()

    suspend fun insertLocalMovies(movies: List<Movie>) = movieRepository.insertLocalMovies(movies)

    suspend fun updateLocalMovieIsLiked(isLiked: Boolean, id: Int) =
        movieRepository.updateLocalMovieIsCLicked(isLiked, id)
}