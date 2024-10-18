package kr.mcv.bagil.skhcsApi

import kotlinx.serialization.Serializable

@Serializable
data class Body(
    val commands: List<String>
)