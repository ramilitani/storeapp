package com.softwareangels.storeapp.service

import com.softwareangels.storeapp.dto.*
import com.softwareangels.storeapp.exception.ValidationException
import com.softwareangels.storeapp.model.Store
import com.softwareangels.storeapp.repository.ProductRepository
import com.softwareangels.storeapp.repository.StoreRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.sql.Timestamp
import java.time.Instant

@Service
class StoresService (
    private val filtersService: FiltersService,
    private val storeRepository: StoreRepository,
    private val allStoreDataRepository : ProductRepository,
    private val specificationService: SpecificationService) {

    @Value("\${app.page-limit}")
    val pageLimit: Int = 0

    fun fetchStores(filterRequest: FilterRequest, page: Int) : List<StoreResponse> {
        val fields = filtersService.getAllAvailableFieldsToFilter();
        var specs = specificationService.getSpecificationFromFilters(filterRequest.filters as MutableList<Filter>, fields)
        var data = allStoreDataRepository.findAll(specs, PageRequest.of(page, pageLimit)).content

        return data.distinctBy { Pair(it.region, it.storeName)}.map { StoreResponse(
            name = it.storeName,
            theme = it.storeTheme,
            region = it.region,
            cluster = it.cluster) }
    }

    fun updateStoreName(storeName: String, newStoreName: UpdateStoreNameRequest): List<StoreResponse> {
        val stores : List<Store>? = storeRepository.findByName(storeName)
            ?: throw ValidationException(ErrorDto(
                timestamp = Timestamp.from(Instant.now()),
                status = 400,
                message = "There is no store with name $storeName"
            ))

        val listOfUpdatedStores = mutableListOf<StoreResponse>()

        for (store in stores!!) {
            val updatedStore = storeRepository.save(Store(
                name = newStoreName.name,
                theme = store?.theme!!,
                region = store.region,
                id = store.id
            ))

            listOfUpdatedStores.add(StoreResponse(
                name = updatedStore.name,
                theme = updatedStore.theme,
                region = updatedStore.region.name,
                cluster = updatedStore.region.cluster.name
            ))
        }

        return listOfUpdatedStores

    }
}