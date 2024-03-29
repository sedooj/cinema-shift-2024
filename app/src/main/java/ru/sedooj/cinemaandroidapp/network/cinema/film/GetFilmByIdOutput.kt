package ru.sedooj.cinemaandroidapp.network.cinema.film

import kotlinx.serialization.Serializable

@Serializable
data class GetFilmByIdOutput(
    val success: Boolean,
    val film: Film
) {

    @Serializable
    data class Film(
        val id: Long,
        val name: String,
        val originalName: String,
        val description: String,
        val releaseDate: String,
        val actors: List<Actor>,
        val directors: List<Director>,
        val runtime: Int,
        val ageRating: String,
        val genres: List<String>,
        val userRatings: Map<String, String>,
        val img: String,
        val country: Country
    )

    @Serializable
    data class Actor(
        val id: Long,
        val professions: List<String>,
        val fullName: String
    )
    @Serializable
    data class Director(
        val id: Long,
        val professions: List<String>,
        val fullName: String
    )

    @Serializable
    data class Country(
        val id: Long,
        val code: String,
        val code2: String,
        val name: String
    )
}

