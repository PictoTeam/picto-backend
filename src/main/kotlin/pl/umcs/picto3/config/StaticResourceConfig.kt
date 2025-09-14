package pl.umcs.picto3.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class StaticResourceConfig : WebMvcConfigurer {
    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.addResourceHandler("/images/**")
            .addResourceLocations("file:src/main/resources/static/images/")
            .setCachePeriod(1800)

        registry.addResourceHandler("/symbols/**")
            .addResourceLocations("file:src/main/resources/static/symbols/")
            .setCachePeriod(1800)
    }
}
