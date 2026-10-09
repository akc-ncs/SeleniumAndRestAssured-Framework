package com.poc.framework.api;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

/** Thin API client: one method per endpoint, returns Response so tests own the assertions. */
public class PostsClient {

    @Step("GET /posts/{id}")
    public Response getPost(int id) {
        return given().spec(ApiSpecs.request()).when().get("/posts/{id}", id);
    }

    @Step("POST /posts")
    public Response createPost(Post body) {
        return given().spec(ApiSpecs.request()).body(body).when().post("/posts");
    }
}
