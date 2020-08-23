package kr.bistroad.orderservice

import io.swagger.models.Swagger
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.DocumentationType.SWAGGER_2
import springfox.documentation.swagger.common.SwaggerPluginSupport.SWAGGER_PLUGIN_ORDER
import springfox.documentation.swagger2.web.SwaggerTransformationContext
import springfox.documentation.swagger2.web.WebMvcSwaggerTransformationFilter
import javax.servlet.http.HttpServletRequest


@Component
@Order(SWAGGER_PLUGIN_ORDER)
class SwaggerBasePathFilter : WebMvcSwaggerTransformationFilter {
    override fun transform(context: SwaggerTransformationContext<HttpServletRequest?>): Swagger? {
        val swagger = context.specification
        return swagger.basePath("/v1" + swagger.basePath)
    }

    override fun supports(documentationType: DocumentationType): Boolean {
        return documentationType == SWAGGER_2
    }
}