package pl.umcs.picto3.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.config.ResourceHandlerRegistry
import org.springframework.web.reactive.config.WebFluxConfigurer

@Configuration
class StaticResourceConfig : WebFluxConfigurer {

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.addResourceHandler("/images/**")
            .addResourceLocations("classpath:/static/images/")

        registry.addResourceHandler("/symbols/**")
            .addResourceLocations("classpath:/static/symbols/")

        registry.addResourceHandler("/static/**")
            .addResourceLocations("classpath:/static/")
    }
}
