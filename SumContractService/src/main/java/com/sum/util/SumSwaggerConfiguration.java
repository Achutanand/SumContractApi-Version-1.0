package com.sum.util;

import static springfox.documentation.builders.PathSelectors.regex;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SumSwaggerConfiguration {

	@Bean
	public Docket getDocketInfo() {
		return new Docket(DocumentationType.SWAGGER_2)
				.groupName("sum")
				.apiInfo(getApiInfo())
				.select()
				.paths(regex("/api.*")).build();

	}

	private ApiInfo getApiInfo() {
		return new ApiInfoBuilder()
				.title("Sum Contract Service API")
				.description("Provides the Abilitiy to Sum User Provided List of Numbers")
				.termsOfServiceUrl("https://www.marlabs.com")
				.license("@Powered By Sum Total")
				.licenseUrl("https://www.marlabs.com").version("1.0").build();
	}

}
