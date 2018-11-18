package be.tim.codingtim;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.withJsonPath;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;

class ApiCallerTest {

    private final MockWebServer mockWebServer = new MockWebServer();
    //mockWebServer.url() starts server on random port
    private final ApiCaller apiCaller = new ApiCaller(WebClient.create(mockWebServer.url("/").toString()));

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void call() throws InterruptedException {
        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .setBody("{\"y\": \"value for y\", \"z\": 789}")
        );
        SimpleResponseDto response = apiCaller.callApi().block();
        assertThat(response, is(not(nullValue())));
        assertThat(response.getY(), is("value for y"));
        assertThat(response.getZ(), is(789));

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        recordedRequest.getHeader("Authorization").equals("customAuth");
        DocumentContext context = JsonPath.parse(recordedRequest.getBody().inputStream());
        assertThat(context, isJson(allOf(
                withJsonPath("$.a", is("value1")),
                withJsonPath("$.b", is(123))
        )));
    }
}