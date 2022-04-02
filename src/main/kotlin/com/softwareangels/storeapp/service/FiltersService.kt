package com.softwareangels.storeapp.service

import com.softwareangels.storeapp.dto.ErrorDto
import com.softwareangels.storeapp.dto.Filter
import com.softwareangels.storeapp.exception.ValidationException
import com.softwareangels.storeapp.model.AvailableField
import com.softwareangels.storeapp.model.Product
import com.softwareangels.storeapp.repository.ProductRepository
import com.softwareangels.storeapp.repository.AvailableFieldsRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.PageRequest
import org.springframework.data.util.Streamable
import org.springframework.stereotype.Service
import java.sql.Timestamp
import java.time.Instant
import kotlin.reflect.KProperty1

@Service
class FiltersService (
    private val availableFieldsRepository: AvailableFieldsRepository,
    private val allStoreDataRepository: ProductRepository,
    private val specificationService: SpecificationService){

    @Value("\${app.page-limit}")
    val pageLimit: Int = 0

    fun getAllAvailableFieldsToFilter() : List<AvailableField>  {
        var source: MutableIterable<AvailableField> = availableFieldsRepository.findAll()
        return Streamable.of(source).toList()
    }

    fun listRemainingFilter(currentFilter: String, filters : List<Filter>, page: Int): Set<String> {

        val availableField = availableFieldsRepository.findByName(currentFilter) ?: throw ValidationException(
            ErrorDto(
                timestamp = Timestamp.from(Instant.now()),
                status = 400,
                message = "Invalid filter $currentFilter"
            ))

        val fields = getAllAvailableFieldsToFilter();
        var specs = specificationService.getSpecificationFromFilters(filters as MutableList<Filter>, fields)
        val data = allStoreDataRepository.findAll(specs, PageRequest.of(page, pageLimit)).content
        return convertToSet(data
            .map<Product?, String> { readInstanceProperty(it!!, availableField?.dbFieldName!!)}
            .filter { name -> name.isNotBlank() })
    }

    private fun <R> readInstanceProperty(instance: Any, propertyName: String): R {
        val property = instance::class.members
            .first { it.name == propertyName } as KProperty1<Any, *>

        return property.get(instance) as R
    }

    private fun <T> convertToSet(list: List<T>): Set<T> {
        return HashSet(list)
    }

}