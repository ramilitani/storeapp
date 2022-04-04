package com.softwareangels.storeapp.controller

import com.softwareangels.storeapp.dto.FilterRequest
import com.softwareangels.storeapp.dto.UpdateStoreNameRequest
import com.softwareangels.storeapp.service.StoresService
import io.swagger.v3.oas.annotations.Operation
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.PageRequest
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/stores")
class StoresController(private val service : StoresService) {

    @Operation(summary = "Fetch stores",
        description = "Filters all stores matching the given filters")
    @PostMapping
    fun fetchStores(@RequestBody filters : FilterRequest,
                   @RequestParam(defaultValue = "0") page : Int) = service.fetchStores(filters, page)

    @Operation(summary = "Update store name",
        description = "Updates the name of a given Store")
    @PatchMapping("/{storeName}")
    fun updateStoreName(@PathVariable storeName : String,
                        @RequestBody newName : UpdateStoreNameRequest) =
        service.updateStoreName(storeName, newName)
}