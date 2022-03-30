package com.softwareangels.storeapp.batch

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder

open class BaseJob {

    @Autowired
    private lateinit var restTemplate: RestTemplate

    @Value("\${app.api.api-key}")
    val apiKey: String = ""

    fun httpGet(url: String, page: Int, type: Class<*>) : ResponseEntity<out Any> {
        val builder = UriComponentsBuilder
            .fromHttpUrl(url)
            .queryParam("page", page)
        val headers = HttpHeaders()
        headers.add("API-Key", apiKey)

        return restTemplate.exchange(builder.build().encode().toUri(),
            HttpMethod.GET,
            HttpEntity<MultiValueMap<String, String>>(headers),
            type)
    }

    fun httpGet(url: String, type: Class<*>) : ResponseEntity<out Any> {
        val builder = UriComponentsBuilder
            .fromHttpUrl(url)

        val headers = HttpHeaders()
        headers.add("API-Key", apiKey)

        return restTemplate.exchange(builder.build().encode().toUri(),
            HttpMethod.GET,
            HttpEntity<String>(headers),
            type)
    }

}