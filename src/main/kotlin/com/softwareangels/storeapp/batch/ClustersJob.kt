package com.softwareangels.storeapp.batch

import com.softwareangels.storeapp.model.Cluster
import com.softwareangels.storeapp.repository.ClusterRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Recover
import org.springframework.retry.annotation.Retryable
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class ClustersJob(val clusterRepository: ClusterRepository) : BaseJob() {

    val log = LoggerFactory.getLogger(ClustersJob::class.java)

    @Value("\${app.api.clusters}")
    val apiHost: String = ""

    var page: Int = 0

    @Scheduled(fixedDelayString = "\${app.scheduler.delay}", initialDelayString = "\${app.scheduler.initial-delay.clusters}")
    @Retryable(value = [JobException::class], maxAttemptsExpression = "\${app.retry.max-attempts.clusters}",
        backoff = Backoff(delayExpression = "\${app.retry.delay}"))
    fun getClusters() {
        log.info("Running ClustersJob")
        do {
            try {
                val res = httpGet(apiHost, page, Array<Cluster>::class.java) as ResponseEntity<Array<Cluster>>

                if(res.body?.isEmpty() == true) break

                for (cluster in res.body!!) {
                    var clusterFromDB = clusterRepository.findByName(cluster.name)
                    if (clusterFromDB == null) {
                        clusterRepository.save(cluster)
                    }
                }
                page += 1

            } catch (ex: Exception) {
                log.error("Error running ClusterJob on page $page. Error: ${ex.message}")
                throw JobException(message = "Attempt to retry JobCluster on page $page")
            }
        } while (true)

        log.info("ClustersJob has finished")
    }

    @Recover
    fun recover(exception: JobException) {
        log.warn("Exhausted attempts to run the batch ClustersJob")
    }
}


