package kr.bistroad.orderservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.cloud.netflix.ribbon.RibbonClient

@SpringBootApplication
@EnableDiscoveryClient
@RibbonClient(name = "order-service")
class BistroadOrderServiceApplication

fun main(args: Array<String>) {
    runApplication<BistroadOrderServiceApplication>(*args)
}
