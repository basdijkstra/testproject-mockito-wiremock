package wiremock;

import com.github.tomakehurst.wiremock.http.Fault;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import io.restassured.http.ContentType;
import org.apache.http.client.ClientProtocolException;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

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

    private void receivePostToCheckOut_ResponseDelayed() {

        stubFor(
            post(
                urlEqualTo("/checkout")
            )
                .withHeader("Scenario", equalTo("Delay"))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withFixedDelay(2000)));
    }

    @Test
    public void invokeWireMockDelayStub_checkStatusCodeAndResponseTime() {

        receivePostToCheckOut_ResponseDelayed();

        given().
            header("Scenario", "Delay").
        when().
            post("http://localhost:9876/checkout").
        then().
            assertThat().
            statusCode(200).
        and().
            time(org.hamcrest.Matchers.greaterThan(2000L), TimeUnit.MILLISECONDS);
    }

    private void receivePostToCheckOut_RespondWithGarbage() {

        stubFor(
            post(
                urlEqualTo("/checkout")
            )
                .withHeader("Scenario", equalTo("FaultyBody"))
                .willReturn(
                    aResponse()
                        .withFault(Fault.RANDOM_DATA_THEN_CLOSE)));
    }

    @Test(expected = ClientProtocolException.class)
    public void invokeWireMockFaultyBodyStub_checkThrowsClientProtocolException() {

        receivePostToCheckOut_RespondWithGarbage();

        given().
            header("Scenario", "FaultyBody").
        when().
            post("http://localhost:9876/checkout");
    }
}
