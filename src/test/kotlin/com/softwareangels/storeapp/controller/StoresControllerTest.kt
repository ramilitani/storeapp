package com.softwareangels.storeapp.controller

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ninjasquad.springmockk.MockkBean
import com.softwareangels.storeapp.dto.Filter
import com.softwareangels.storeapp.dto.FilterRequest
import com.softwareangels.storeapp.dto.StoreResponse
import com.softwareangels.storeapp.dto.UpdateStoreNameRequest
import com.softwareangels.storeapp.service.FiltersService
import com.softwareangels.storeapp.service.StoresService
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.data.domain.PageRequest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest
class StoresControllerTest : BaseControllerTest() {

    @Test
    fun `Filters all stores matching the given filters`() {
        val payload = FilterRequest(filters = listOf(Filter(id = "Region", values = listOf("North EU"))))
        val result = listOf(StoreResponse(
            name = "Store xyz",
            theme = "Store theme",
            region = "Store region",
            cluster = "Store cluster"))
        val expectedJson =  jacksonObjectMapper().writeValueAsString(result)

        every { storesService.fetchStores(any(), any()) } returns result

        mockMvc.perform(post("/stores?page=1")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(jacksonObjectMapper().writeValueAsString(payload)))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(expectedJson))
    }

    @Test
    fun `Update the name of a given store`() {
        val result = listOf(StoreResponse(
            name = "Store xyz",
            theme = "Store theme",
            region = "Store region",
            cluster = "Store cluster"))
        val expectedJson =  jacksonObjectMapper().writeValueAsString(result)

        every { storesService.updateStoreName("Store xyz", any()) } returns result

        mockMvc.perform(patch("/stores/Store xyz")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jacksonObjectMapper().writeValueAsString(UpdateStoreNameRequest(name = "New name"))))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(expectedJson))
    }
}