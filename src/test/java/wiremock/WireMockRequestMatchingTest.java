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

    public void receivePostToCheckoutWithAdditionalRequestProperties_RespondWithHttp200AndTextBody() {

        stubFor(
            post(
                urlEqualTo("/checkout")
            )
                .withHeader("Content-Type", containing("text/plain"))
                .withCookie("sessionId", equalTo("abcde12345"))
                .withBasicAuth("john","supersecret")
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withBody("Checkout completed successfully.")));
    }

    @Test
    public void invokeWireMockRequestMatchingStub_checkStatusCodeAndBody() {

        receivePostToCheckoutWithAdditionalRequestProperties_RespondWithHttp200AndTextBody();

        given().
            auth().preemptive().basic("john","supersecret").
            contentType(ContentType.TEXT).
            cookie("sessionId","abcde12345").
        when().
            post("http://localhost:9876/checkout").
        then().
            assertThat().
            statusCode(200).
        and().
            body(org.hamcrest.Matchers.equalTo("Checkout completed successfully."));
    }
}
