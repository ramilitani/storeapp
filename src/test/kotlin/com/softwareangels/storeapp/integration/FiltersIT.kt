package com.softwareangels.storeapp.integration

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.softwareangels.storeapp.dto.Filter
import com.softwareangels.storeapp.dto.FilterRequest
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import org.springframework.web.util.UriComponentsBuilder

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class FiltersIT : BaseITTest() {

    @Test
    fun `Assert list of available fields to use for filtering`() {
        val expectedJson = jacksonObjectMapper().writeValueAsString(listOf("Season","Cluster","Region","Region Type",
            "Model","Size","SKU","Store Name","Store Theme"))
        val response = restTemplate.getForEntity<String>("/filters/")
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).isEqualTo(expectedJson)
    }

    @ParameterizedTest
    @MethodSource("getFilters")
    fun `List values for a specific filter`(currentFilter: String, filterRequest: FilterRequest, expectedJson: List<String>) {
        val request = HttpEntity(filterRequest)
        val expectedJson = jacksonObjectMapper().writeValueAsString(expectedJson)
        val response = restTemplate.exchange(
            UriComponentsBuilder
                .fromUriString("/filters/$currentFilter/")
                .build()
                .toUri(), HttpMethod.POST, request, String::class.java)
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).isEqualTo(expectedJson)
    }

    companion object {
        @JvmStatic
        fun getFilters(): List<Arguments> {
            return listOf(
                Arguments.of(
                    "Region",
                    FilterRequest(filters = listOf<Filter>(Filter(id = "Cluster", values = listOf("Europe")))),
                    listOf("North EU", "South EU")),
                Arguments.of(
                    "Store Name",
                    FilterRequest(filters = listOf<Filter>(Filter(id = "Cluster", values = listOf("Europe")),
                        Filter(id = "Season", values = listOf("S17")))),
                    listOf("A hundredth store")),
            )
        }
    }
}