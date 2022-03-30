package com.softwareangels.storeapp.dto

import com.opencsv.bean.CsvBindByName

class ProductCSV(
    @CsvBindByName val season: String? = null,
    @CsvBindByName val model: String? = null,
    @CsvBindByName val size: String? = null,
    @CsvBindByName val sku: String? = null,
    @CsvBindByName val ean: String? = null,
    @CsvBindByName val description: String? = null
)