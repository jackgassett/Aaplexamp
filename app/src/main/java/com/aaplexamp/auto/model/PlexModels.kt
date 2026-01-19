package com.aaplexamp.auto.model

import com.google.gson.annotations.SerializedName

data class PlexTrack(
    @SerializedName("ratingKey") val ratingKey: String,
    @SerializedName("key") val key: String,
    @SerializedName("parentRatingKey") val parentRatingKey: String, // Album ID
    @SerializedName("grandparentRatingKey") val grandparentRatingKey: String, // Artist ID
    @SerializedName("title") val title: String,
    @SerializedName("parentTitle") val albumTitle: String?,
    @SerializedName("grandparentTitle") val artistName: String?,
    @SerializedName("duration") val duration: Long,
    @SerializedName("index") val trackNumber: Int,
    @SerializedName("thumb") val thumb: String?,
    @SerializedName("parentThumb") val albumThumb: String?,
    @SerializedName("Media") val media: List<PlexMedia>?
)

data class PlexMedia(
    @SerializedName("Part") val part: List<PlexPart>?
)

data class PlexPart(
    @SerializedName("key") val key: String,
    @SerializedName("file") val file: String
)

data class PlexAlbum(
    @SerializedName("ratingKey") val ratingKey: String,
    @SerializedName("key") val key: String,
    @SerializedName("title") val title: String,
    @SerializedName("parentTitle") val artistName: String?,
    @SerializedName("thumb") val thumb: String?,
    @SerializedName("year") val year: Int?
)

data class PlexContainer<T>(
    @SerializedName("MediaContainer") val mediaContainer: MediaContainer<T>
)

data class MediaContainer<T>(
    @SerializedName("size") val size: Int,
    @SerializedName("Metadata") val metadata: List<T>?,
    @SerializedName("Directory") val directory: List<T>?
)
