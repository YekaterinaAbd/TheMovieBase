package com.example.movies.data.model.account

import com.google.gson.annotations.SerializedName

data class Account(
    @SerializedName("avatar") val avatar: Avatar?,
    @SerializedName("id") val id: Int?,
    @SerializedName("name") val name: String?,
    @SerializedName("include_adult") val includeAdult: Boolean?,
    @SerializedName("username") val username: String?
)

data class Avatar(
    @SerializedName("gravatar") val gravatar: Gravatar?
)

data class Gravatar(
    @SerializedName("hash") val hash: String?
)