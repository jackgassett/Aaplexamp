package com.aaplexamp.auto.auth

import com.google.gson.annotations.SerializedName

data class PlexAuthPin(
    @SerializedName("id") val id: Int,
    @SerializedName("code") val code: String,
    @SerializedName("authToken") val authToken: String?
)

data class PlexAuthPinResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("code") val code: String
)

data class PlexUser(
    @SerializedName("username") val username: String,
    @SerializedName("email") val email: String,
    @SerializedName("authToken") val authToken: String
)

data class PlexUserResponse(
    @SerializedName("user") val user: PlexUser
)

data class PlexServer(
    @SerializedName("name") val name: String,
    @SerializedName("host") val host: String?,
    @SerializedName("port") val port: Int?,
    @SerializedName("machineIdentifier") val machineIdentifier: String,
    @SerializedName("accessToken") val accessToken: String,
    @SerializedName("owned") val owned: Boolean,
    @SerializedName("publicAddress") val publicAddress: String?,
    @SerializedName("localAddresses") val localAddresses: String?,
    @SerializedName("provides") val provides: String?,
    @SerializedName("httpsRequired") val httpsRequired: Boolean?,
    @SerializedName("relay") val relay: Boolean?,
    @SerializedName("publicAddressMatches") val publicAddressMatches: Boolean?,
    @SerializedName("connections") val connections: List<PlexConnection>?
)

data class PlexConnection(
    @SerializedName("protocol") val protocol: String,
    @SerializedName("address") val address: String,
    @SerializedName("port") val port: Int,
    @SerializedName("uri") val uri: String,
    @SerializedName("local") val local: Boolean,
    @SerializedName("relay") val relay: Boolean?,
    @SerializedName("IPv6") val ipv6: Boolean?
)

data class PlexResourcesResponse(
    @SerializedName("Device") val devices: List<PlexServer>?
)

data class MediaContainer(
    @SerializedName("Device") val devices: List<PlexServer>?
)

data class PlexServersResponse(
    @SerializedName("MediaContainer") val mediaContainer: MediaContainer
)
