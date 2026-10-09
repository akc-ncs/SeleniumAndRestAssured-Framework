package com.poc.tests.api;

import com.poc.framework.api.ApiSpecs;
import com.poc.framework.api.Post;
import com.poc.framework.api.PostsClient;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.testng.Assert.assertEquals;

@Epic("API")
@Feature("Posts")
public class PostsApiTest {
    private final PostsClient posts = new PostsClient();

    @Test(groups = {"smoke", "api"})
    public void getPostReturnsExpectedPost() {
        posts.getPost(1).then()
                .statusCode(200)
                .spec(ApiSpecs.responseTimeUnder(3000))
                .body("id", equalTo(1))
                .body("title", notNullValue());
    }

    @Test(groups = {"regression", "api"})
    public void createPostEchoesPayload() {
        Post payload = new Post(null, 7, "PoC title", "PoC body");

        Response res = posts.createPost(payload);

        assertEquals(res.statusCode(), 201);
        Post created = res.as(Post.class);
        assertEquals(created.title(), payload.title());
        assertEquals(created.userId(), payload.userId());
    }
}
