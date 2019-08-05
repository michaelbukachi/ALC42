package com.michaelbukachi.alc42.main

import java.io.Serializable

data class TravelDeal(
    var id: String? = null,
    var title: String? = null,
    var description: String? = null,
    var price: String? = null,
    var imageUrl: String? = null,
    var imageName: String? = null
) : Serializable