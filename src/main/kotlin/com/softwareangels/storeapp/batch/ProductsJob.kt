package com.softwareangels.storeapp.batch

import com.softwareangels.storeapp.dto.ProductDTO
import com.softwareangels.storeapp.model.Product
import com.softwareangels.storeapp.model.Store
import com.softwareangels.storeapp.repository.ProductRepository
import com.softwareangels.storeapp.repository.StoreRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Recover
import org.springframework.retry.annotation.Retryable
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class ProductsJob(
    val allStoreDataRepository: ProductRepository,
    val storeRepository: StoreRepository) : BaseJob() {

    val log = LoggerFactory.getLogger(ProductsJob::class.java)

    @Value("\${app.api.products}")
    val apiHost: String = ""

    var page: Int = 0

    val sizes = arrayOf("XS", "S", "L", "XL", "XXL", "XXXL")


    @Scheduled(fixedDelayString = "\${app.scheduler.delay}", initialDelayString = "\${app.scheduler.initial-delay.products}")
    @Retryable(value = [JobException::class], maxAttemptsExpression = "\${app.retry.max-attempts.products}",
        backoff = Backoff(delayExpression = "\${app.retry.delay}"))
    fun getProducts() {
        log.info("Running ProductsJob")
        do {
            try {
                val res = httpGet(apiHost, page, Array<ProductDTO>::class.java) as ResponseEntity<Array<ProductDTO>>

                if(res.body?.isEmpty() == true) break

                for (response in res.body!!) {
                    val stores = storeRepository.findByName(response.store)
                    val allStoreData = allStoreDataRepository.findByProductSeasonAndProductModel(response.season, response.product)

                    if (allStoreData.isEmpty()) {
                        generateProductData(stores!!, response)
                    } else {
                        updateProductData(allStoreData, stores!!, response)
                    }
                }

                page += 1

            } catch (ex: Exception) {
                log.error("Error running ProductsJob on page $page. Error: ${ex.message}")
                throw JobException(message = "Attempt to retry ProductsJob on page $page")
            }
        } while (true)

        log.info("ProductsJob has finished")
    }

    private fun generateProductData(stores: List<Store>, product: ProductDTO) {
        for (store in stores) {
            for (size in sizes) {
                allStoreDataRepository.save(
                    Product(
                        cluster = store?.region?.cluster?.name,
                        region = store?.region?.name,
                        regionType = store?.region?.type,
                        storeName = store?.name,
                        storeTheme = store?.theme,
                        productSeason = product.season,
                        productModel = product.product,
                        productSize = size,
                        productSku = null,
                        productEan = null,
                        productDesc = null,
                        id = null
                    )
                )
            }
        }
    }

    private fun updateProductData(products: List<Product>, stores: List<Store>, product: ProductDTO) {
        for (allStore in products) {
            var index = 0
            var id : Long? = allStore.id
            do {
                allStoreDataRepository.save(
                    Product(
                        cluster = stores[index].region?.cluster?.name,
                        region = stores[index].region?.name,
                        regionType = stores[index].region?.type,
                        storeName = stores[index].name,
                        storeTheme = stores[index].theme,
                        productSeason = product.season,
                        productModel = product.product,
                        productSize = allStore.productSize,
                        productSku = allStore.productSku,
                        productEan = allStore.productEan,
                        productDesc = allStore.productDesc,
                        id = id)
                )
                index+=1
                id = null

            } while(index < stores.size)
        }
    }

    @Recover
    fun recover(exception: JobException) {
        log.warn("Exhausted attempts to run the batch ProductsJob")
    }
}