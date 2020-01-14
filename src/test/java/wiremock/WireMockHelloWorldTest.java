package wiremock;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.*;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.*;

public class WireMockHelloWorldTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(9876);

    public void createHelloWorldStub() {

        stubFor(
            get(
                urlEqualTo("/hello-world")
            )
                .willReturn(
                    aResponse()
                        .withHeader("Content-Type", "text/plain")
                        .withStatus(200)
                        .withBody("Hello world!")));
    }

    @Test
    public void invokeWireMockHelloWorldStub_checkStatusCodeAndBody() {

        createHelloWorldStub();

        given().
        when().
            get("http://localhost:9876/hello-world").
        then().
            assertThat().
            statusCode(200).
        and().
            body(org.hamcrest.Matchers.equalTo("Hello world!"));
    }
}
