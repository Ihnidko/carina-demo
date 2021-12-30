package com.qaprosoft.carina.demo.github;

import com.qaprosoft.carina.core.foundation.IAbstractTest;
import com.qaprosoft.carina.core.foundation.api.http.HttpResponseStatusType;
import com.qaprosoft.carina.core.foundation.utils.R;
import com.qaprosoft.carina.demo.api.github.DeleteRepositoryMethod;
import com.qaprosoft.carina.demo.api.github.GetRepositoryMethod;
import com.qaprosoft.carina.demo.api.github.PostRepositoryMethod;
import com.qaprosoft.carina.demo.api.github.GetRepositoriesMethod;
import io.restassured.path.json.JsonPath;
import org.apache.commons.lang3.RandomStringUtils;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.testng.Assert;
import org.testng.annotations.Test;

public class RepositoryApiTest implements IAbstractTest {

    @Test(description = "Get all repositories")
    public void testGetAllRepositories() {
         GetRepositoriesMethod getRepositoriesMethod = new GetRepositoriesMethod();
         getRepositoriesMethod.expectResponseStatus(HttpResponseStatusType.OK_200);
         getRepositoriesMethod.callAPI();
         getRepositoriesMethod.validateResponseAgainstSchema("api/github/_get/repos_sheme.json");
    }

    @Test(description = "Create repository")
    public void testCreateRepository() {
        PostRepositoryMethod postRepositoryMethod = new PostRepositoryMethod();
        postRepositoryMethod.expectResponseStatus(HttpResponseStatusType.CREATED_201);
        String rs = postRepositoryMethod.callAPI().asString();
        postRepositoryMethod.validateResponseAgainstSchema("api/github/_post/repo_scheme.json");
        postRepositoryMethod.validateResponse(JSONCompareMode.LENIENT);

        JsonPath jsonPath = new JsonPath(rs);
        String repoName = jsonPath.getString("name");
        String repoOwner = jsonPath.getString("owner.login");

        GetRepositoryMethod getRepositoryMethod = new GetRepositoryMethod(repoOwner, repoName);
        getRepositoryMethod.expectResponseStatus(HttpResponseStatusType.OK_200);
        getRepositoryMethod.callAPI();
    }

    @Test(description = "")
    public void testCreateRepositoryWithoutRequiredFields() {
        PostRepositoryMethod postRepositoryMethod = new PostRepositoryMethod();
        postRepositoryMethod.expectResponseStatus(HttpResponseStatusType.UNPROCESSABLE_ENTITY_422);
        postRepositoryMethod.removeProperty("name");
        postRepositoryMethod.callAPI();
    }

    @Test(description = "")
    public void testGetRepositoryWhichNotExist() {
        String randomRepoName = RandomStringUtils.randomAlphabetic(10);
        String repoOwner = R.CONFIG.get("github_user_name");
        String expectedNotFoundMessage = "Not Found";

        GetRepositoryMethod getRepositoryMethod = new GetRepositoryMethod(randomRepoName, repoOwner);
        getRepositoryMethod.expectResponseStatus(HttpResponseStatusType.NOT_FOUND_404);
        String rs = getRepositoryMethod.callAPI().asString();
        String actualNotFoundMessage = new JsonPath(rs).getString("message");
        Assert.assertEquals(actualNotFoundMessage, expectedNotFoundMessage, "Actual and expected not found message different");
    }

    @Test(description = "Remove created repository")
    public void testRemoveRepository() {
        PostRepositoryMethod postRepositoryMethod = new PostRepositoryMethod();
        postRepositoryMethod.expectResponseStatus(HttpResponseStatusType.CREATED_201);
        String rs = postRepositoryMethod.callAPI().asString();
        postRepositoryMethod.validateResponseAgainstSchema("api/github/_post/repo_scheme.json");
        postRepositoryMethod.validateResponse(JSONCompareMode.LENIENT);

        JsonPath jsonPath = new JsonPath(rs);
        String repoName = jsonPath.getString("name");
        String repoOwner = jsonPath.getString("owner.login");

        DeleteRepositoryMethod deleteRepositoryMethod = new DeleteRepositoryMethod(repoOwner, repoName);
        deleteRepositoryMethod.expectResponseStatus(HttpResponseStatusType.NO_CONTENT_204);
         deleteRepositoryMethod.callAPI();
    }
}