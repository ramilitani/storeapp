package com.softwareangels.storeapp.model.spec

class SearchCriteria(
    val key : String,
    val operation : SearchOperation,
    val value : String,
    val arguments : List<String>)