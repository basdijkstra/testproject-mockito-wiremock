package wiremock;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import io.restassured.http.ContentType;
import org.junit.Rule;
import org.junit.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.given;

public class WireMockSimulateErrorScenariosTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(9876);

    private void receivePostToCheckOut_RespondWithHttp503() {

        stubFor(
            post(
                urlEqualTo("/checkout")
            )
                .withHeader("Scenario", equalTo("ServiceUnavailable"))
                .willReturn(
                    aResponse()
                        .withStatus(503)));
    }

    @Test
    public void invokeWireMockServiceUnavailableStub_checkStatusCodeIs503() {

        receivePostToCheckOut_RespondWithHttp503();

        given().
            header("Scenario", "ServiceUnavailable").
        when().
            post("http://localhost:9876/checkout").
        then().
            assertThat().
            statusCode(503);
    }
}
