package com.softwareangels.storeapp.controller

import com.softwareangels.storeapp.dto.FilterRequest
import com.softwareangels.storeapp.service.FiltersService
import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.info.Info
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.PageRequest
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/filters")
class FiltersController(private val service : FiltersService) {

    @Operation(summary = "Get Filters Available",
        description = "Lists all the available fields for which users can use to filter for Stores")
    @GetMapping("/")
    fun getAllAvailableFilters() : List<String> = service.getAllAvailableFieldsToFilter().map { it.name }

    @Operation(summary = "Provide values to the user he can chosse to filter from",
        description = "Lists all possible remaining values that a user can select of a given filter, after using other filters")
    @PostMapping("/{filter}")
    fun listPossibleValues(@PathVariable filter : String,
                           @RequestBody filterRequest : FilterRequest,
                           @RequestParam(defaultValue = "0") page : Int) =
        service.listRemainingFilter(filter, filterRequest.filters, page)

}