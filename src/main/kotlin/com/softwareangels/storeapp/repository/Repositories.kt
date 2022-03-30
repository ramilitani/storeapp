package com.softwareangels.storeapp.repository

import com.softwareangels.storeapp.model.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.repository.CrudRepository

interface ClusterRepository : CrudRepository<Cluster, Long> {
    fun findByName(name: String): Cluster?
}

interface RegionRepository : CrudRepository<Region, Long> {
    fun findByName(name: String): Region?
}

interface StoreRepository : CrudRepository<Store, Long> {
    fun findByName(name: String): List<Store>?
    fun findByNameAndRegionName(name: String, region: String): Store?
}

interface ProductRepository : JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    fun findByProductSeasonAndProductModel(season: String, model: String) : List<Product>
    fun findByProductSeasonAndProductModelAndProductSize(season: String, model: String, size: String) : Product?

}

interface AvailableFieldsRepository : CrudRepository<AvailableField, Long> {
    fun findByName(name: String) : AvailableField?
}