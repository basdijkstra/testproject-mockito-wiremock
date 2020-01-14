package wiremock;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import io.restassured.http.ContentType;
import org.junit.Rule;
import org.junit.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.given;

public class WireMockRequestMatchingTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(9876);

    public void createStubUsingRequestMatchingTechniques() {

        stubFor(
            get(
                urlEqualTo("/request-matching")
            )
                .withHeader("Content-Type", containing("text/plain"))
                .withCookie("myCookie", equalTo("chocolateChip"))
                .withBasicAuth("username","password")
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withBody("Your request satisfied all conditions.")));
    }

    @Test
    public void invokeWireMockRequestMatchingStub_checkStatusCodeAndBody() {

        createStubUsingRequestMatchingTechniques();

        given().
            auth().preemptive().basic("username","password").
            contentType(ContentType.TEXT).
            cookie("myCookie","chocolateChip").
        when().
            get("http://localhost:9876/request-matching").
        then().
            assertThat().
            statusCode(200).
        and().
            body(org.hamcrest.Matchers.equalTo("Your request satisfied all conditions."));
    }
}
