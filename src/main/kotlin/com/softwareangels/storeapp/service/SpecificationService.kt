package com.softwareangels.storeapp.service

import com.softwareangels.storeapp.dto.ErrorDto
import com.softwareangels.storeapp.dto.Filter
import com.softwareangels.storeapp.exception.ValidationException
import com.softwareangels.storeapp.model.Product
import com.softwareangels.storeapp.model.AvailableField
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import java.sql.Timestamp
import java.time.Instant

@Service
class SpecificationService() {

    fun getSpecificationFromFilters(filters: MutableList<Filter>, availableFields: List<AvailableField>):
            Specification<Product> {

        var mapAvailableFields = availableFields.associate { it.name to it.dbFieldName }
        var spec = Specification.where(createSpecification(filters.removeAt(0), mapAvailableFields))
        for (filter in filters) {
            spec = spec.and(createSpecification(filter, mapAvailableFields))
        }
        return spec
    }

    private fun createSpecification(filter: Filter, availableField: Map<String, String>): Specification<Product> {

        val field = availableField[filter.id] ?: throw ValidationException(
            ErrorDto(
                timestamp = Timestamp.from(Instant.now()),
                status = 400,
                message = "Invalid filter ${filter.id}"
        ))

        if (filter.values.size > 1) {
            return Specification<Product> {
                    root, query, builder -> builder.and(root.get<String>(field).`in`(filter.values))
            }
        }
        return Specification<Product> {
                root, query, builder -> builder.equal(root.get<String>(field), filter.values[0])
        }
    }
}