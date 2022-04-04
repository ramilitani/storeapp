package com.softwareangels.storeapp.integration

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.softwareangels.storeapp.dto.Filter
import com.softwareangels.storeapp.dto.FilterRequest
import com.softwareangels.storeapp.dto.StoreResponse
import com.softwareangels.storeapp.dto.UpdateStoreNameRequest
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.test.context.ActiveProfiles
import org.springframework.web.util.UriComponentsBuilder

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class StoresIT : BaseITTest() {

    @Test
    fun `Filters all stores by filter`() {
        val request = HttpEntity(
            FilterRequest(filters = listOf<Filter>(
                Filter(id = "Season", values = listOf("S18")),
                Filter(id = "Region", values = listOf("South EU", "North EU")),
                Filter(id = "SKU", values = listOf("12000000-XS")),
            ))
        )
        val expectedJson = jacksonObjectMapper().writeValueAsString(listOf(
            StoreResponse(name = "A body store", theme = "What an a rattly Store!", region = "South EU", cluster = "Europe"),
            StoreResponse(name = "A body store", theme = "What an a rattly Store!", region = "North EU", cluster = "Europe")))

        val response = restTemplate.exchange(
            UriComponentsBuilder
                .fromUriString("/stores/")
                .build()
                .toUri(), HttpMethod.POST, request, String::class.java)
        Assertions.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        Assertions.assertThat(response.body).isEqualTo(expectedJson)
    }

    @Test
    fun `Update the name of a given store`() {
        val request = HttpEntity(UpdateStoreNameRequest(name = "Update new store name"))

        val expectedJson = jacksonObjectMapper().writeValueAsString(listOf(
            StoreResponse(name = "Update new store name", theme = "What an a rattly Store!", region = "North EU", cluster = "Europe"),
            StoreResponse(name = "Update new store name", theme = "What an a rattly Store!", region = "South EU", cluster = "Europe")))

        val requestFactory = HttpComponentsClientHttpRequestFactory()
        restTemplate.restTemplate.setRequestFactory(requestFactory)

        val response = restTemplate.exchange(
            UriComponentsBuilder
                .fromUriString("/stores/A body store")
                .build()
                .toUri(), HttpMethod.PATCH, request, String::class.java)
        Assertions.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        Assertions.assertThat(response.body).isEqualTo(expectedJson)

    }
}