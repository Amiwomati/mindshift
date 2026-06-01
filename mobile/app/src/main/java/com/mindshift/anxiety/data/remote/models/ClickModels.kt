package com.mindshift.anxiety.data.remote.models

import com.google.gson.annotations.SerializedName

data class ClickRequest(
    @SerializedName("clicked_at")
    val clickedAt: String
)

data class SyncRequest(
    val clicks: List<ClickRequest>
)

data class SyncResponse(
    val message: String,
    val queued: Int
)
