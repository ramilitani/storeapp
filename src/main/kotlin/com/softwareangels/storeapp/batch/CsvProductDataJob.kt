package com.softwareangels.storeapp.batch

import com.opencsv.CSVReader
import com.opencsv.bean.CsvToBean
import com.opencsv.bean.CsvToBeanBuilder
import com.opencsv.bean.HeaderColumnNameMappingStrategy
import com.opencsv.bean.MappingStrategy
import com.softwareangels.storeapp.dto.ProductCSV
import com.softwareangels.storeapp.model.Product
import com.softwareangels.storeapp.repository.ProductRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Recover
import org.springframework.retry.annotation.Retryable
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.util.StreamUtils
import java.io.File
import java.io.FileOutputStream
import java.io.FileReader

@Service
class CsvProductDataJob(val allStoreDataRepository: ProductRepository) : BaseJob() {

    val log = LoggerFactory.getLogger(CsvProductDataJob::class.java)

    @Value("\${app.api.productsCSV}")
    val apiHost: String = ""

    @Scheduled(fixedDelayString = "\${app.scheduler.delay}", initialDelayString = "\${app.scheduler.initial-delay.csvProducts}")
    @Retryable(value = [JobException::class], maxAttemptsExpression = "\${app.retry.max-attempts.csvProducts}",
        backoff = Backoff(delayExpression = "\${app.retry.delay}"))
    fun getProductsCSV() {
        log.info("Running CsvProductDataJob")
        do {
            try {
                val res = httpGet(apiHost, ByteArray::class.java)

                val file: File = File.createTempFile("productCSV", ".csv")
                StreamUtils.copy(res.body!! as ByteArray, FileOutputStream(file))

                val csvReader = CSVReader(FileReader(file))
                val csvToBean: CsvToBean<ProductCSV> = CsvToBeanBuilder<ProductCSV>(csvReader)
                    .withType(ProductCSV::class.java)
                    .withMappingStrategy(getMappingStrategy())
                    .build()
                val list = csvToBean.parse() as List<ProductCSV>

                for (productCSV in list) {
                    var dataFromDB = allStoreDataRepository.findByProductSeasonAndProductModelAndProductSize(
                        productCSV.season!!, productCSV.model!!, productCSV.size!!)

                    allStoreDataRepository.save(Product(
                        cluster = dataFromDB?.cluster ?: "",
                        region = dataFromDB?.region ?: "",
                        regionType = dataFromDB?.regionType ?: "",
                        storeName = dataFromDB?.storeName ?: "",
                        storeTheme = dataFromDB?.storeTheme ?: "",
                        productSeason = productCSV.season,
                        productModel = productCSV.model,
                        productSize = productCSV.size,
                        productSku = productCSV.sku!!,
                        productEan = productCSV.ean?.toLong()!!,
                        productDesc = productCSV.description!!,
                        id = dataFromDB?.id))
                }

                break

            } catch (ex: Exception) {
                log.error("Error running CsvProductDataJob. Error: ${ex.message}")
                throw JobException(message = "Attempt to retry CsvProductDataJob")
            }
        } while (true)

        log.info("CsvProductDataJob has finished")
    }

    @Recover
    fun recover(exception: JobException) {
        log.warn("Exhausted attempts to run the batch CsvProductDataJob")
    }

    private fun getMappingStrategy() : MappingStrategy<ProductCSV> {
        val result : MappingStrategy<ProductCSV> = HeaderColumnNameMappingStrategy()
        result.setType(ProductCSV::class.java)
        return result
    }
}