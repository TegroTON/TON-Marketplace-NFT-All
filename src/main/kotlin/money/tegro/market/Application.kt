package money.tegro.market

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.boot.sql.init.dependency.DependsOnDatabaseInitialization
import org.springframework.context.annotation.Configuration
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories

@Configuration
@ConfigurationPropertiesScan("money.tegro.market.properties")
@EnableR2dbcRepositories("money.tegro.market.repository")
@SpringBootApplication
@DependsOnDatabaseInitialization
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
