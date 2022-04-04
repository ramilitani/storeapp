package com.softwareangels.storeapp.controller

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.softwareangels.storeapp.dto.Filter
import com.softwareangels.storeapp.dto.FilterRequest
import com.softwareangels.storeapp.model.AvailableField
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.PropertySource
import org.springframework.data.domain.PageRequest
import org.springframework.http.MediaType
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest
@PropertySource("classpath:application-test.yml")
class FiltersControllerTest : BaseControllerTest() {

    @Test
    fun `List all the available fields to filter`() {

        every { filtersService.getAllAvailableFieldsToFilter() } returns listOf(
            AvailableField(name = "Cluster", dbFieldName = ""),
            AvailableField(name = "Region", dbFieldName = ""),
            AvailableField(name = "Season", dbFieldName = ""),
        )

        mockMvc.perform(get("/filters/").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("\$.[0]").value("Cluster"))
            .andExpect(jsonPath("\$.[1]").value("Region"))
            .andExpect(jsonPath("\$.[2]").value("Season"))
    }

    @Test
    fun `List values for a specific filter`() {
        val payload = FilterRequest(filters = listOf<Filter>(Filter(id = "Region", values = listOf("Europe"))))
        val json = jacksonObjectMapper().writeValueAsString(payload)

        every { filtersService.listRemainingFilter(any(), any(), any()) } returns setOf("North EU", "South EU")

        mockMvc.perform(post("/filters/Region")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("\$.[0]").value("North EU"))
            .andExpect(jsonPath("\$.[1]").value("South EU"))
    }
}