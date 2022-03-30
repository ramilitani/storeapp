package com.softwareangels.storeapp.controller

import com.ninjasquad.springmockk.MockkBean
import com.softwareangels.storeapp.service.FiltersService
import com.softwareangels.storeapp.service.SpecificationService
import com.softwareangels.storeapp.service.StoresService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.test.web.servlet.MockMvc

open class BaseControllerTest {

    @Autowired
    protected lateinit var mockMvc : MockMvc

    @MockkBean
    protected lateinit var storesService: StoresService

    @MockkBean
    protected lateinit var filtersService: FiltersService

    @MockkBean
    protected lateinit var specificationService: SpecificationService
}