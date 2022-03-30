package com.softwareangels.storeapp.dto

import com.softwareangels.storeapp.model.Store
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

class RegionDTO(
    var name: String,
    var type: String,
    var clusters: String)

class StoreDTO(
    var name: String,
    var theme: String,
    var region: String
)

class ProductDTO(
    var product: String,
    var season: String,
    var store: String
)
