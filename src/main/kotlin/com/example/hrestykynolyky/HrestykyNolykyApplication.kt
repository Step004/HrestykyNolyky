package com.example.hrestykynolyky

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import javax.sql.DataSource

@SpringBootApplication
class HrestykyNolykyApplication {
    @Bean
    fun dataSource(): DataSource {
        return DataSourceBuilder.create()
            .driverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver")
            .url("jdbc:sqlserver://buspark.database.windows.net:1433;database=BusParkDB")
            .username("busAdmin@buspark")
            .password("20TarasFake")
            .build()
    }
}

fun main(args: Array<String>) {
    runApplication<HrestykyNolykyApplication>(*args)
}
