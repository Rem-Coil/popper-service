package com.remcoil.data.model.bobbin

import kotlinx.serialization.Serializable

@Serializable
class Bobbin(
    val id: Int,
    val taskId: Int,
    val bobbinNumber: String
)