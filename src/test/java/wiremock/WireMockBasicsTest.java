package wiremock;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.*;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.*;

public class WireMockBasicsTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(9876);

    private void receivePostToCheckout_RespondWithHttp200() {

        stubFor(
            post(
                urlEqualTo("/checkout")
            )
                .willReturn(
                    aResponse()
                        .withStatus(200)
                )
        );
    }

    @Test
    public void postToCheckoutStub_checkStatusCode_shouldEqual200() {

        receivePostToCheckout_RespondWithHttp200();

        given().
        when().
            post("http://localhost:9876/checkout").
        then().
            assertThat().
            statusCode(200);
    }
}
