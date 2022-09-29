package money.tegro.market.configuration

import money.tegro.market.AddressHelper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.thymeleaf.spring5.SpringWebFluxTemplateEngine
import org.thymeleaf.spring5.view.reactive.ThymeleafReactiveViewResolver


@Configuration
class ThymeleafConfiguration {
    @Bean
    fun thymeleafReactiveViewResolver(@Autowired templateEngine: SpringWebFluxTemplateEngine): ThymeleafReactiveViewResolver {
        val thymeleafViewResolver = ThymeleafReactiveViewResolver()
        thymeleafViewResolver.templateEngine = templateEngine
        thymeleafViewResolver.addStaticVariable("addressHelper", AddressHelper())
        return thymeleafViewResolver
    }
}
