package com.poc.framework.api;

import com.poc.framework.config.ConfigManager;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

/** Shared request/response specs. AllureRestAssured attaches every request/response to the report. */
public final class ApiSpecs {
    private ApiSpecs() {}

    public static RequestSpecification request() {
        return new RequestSpecBuilder()
                .setBaseUri(ConfigManager.require("api.base.url"))
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addFilter(new AllureRestAssured())
                .build();
    }

    public static ResponseSpecification responseTimeUnder(long millis) {
        return new ResponseSpecBuilder()
                .expectResponseTime(org.hamcrest.Matchers.lessThan(millis))
                .build();
    }
}
