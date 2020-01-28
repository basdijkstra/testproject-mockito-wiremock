package wiremock;

import com.github.tomakehurst.wiremock.http.Fault;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.github.tomakehurst.wiremock.stubbing.Scenario;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.given;

public class WireMockStatefulStubTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(9876);

    private void createStatefulStub_responseDependingOnShoppingCarContents() {

        stubFor(post(urlEqualTo("/checkout")).inScenario("Stateful Checkout")
            .whenScenarioStateIs(Scenario.STARTED)
            .willReturn(aResponse()
                .withStatus(400)
                .withBody("Shopping cart is empty!")
            ));

        stubFor(post(urlEqualTo("/addtocart")).inScenario("Stateful Checkout")
            .whenScenarioStateIs(Scenario.STARTED)
            .willReturn(aResponse()
                .withStatus(200)
                .withBody("Item added to shopping cart.")
            )
            .willSetStateTo("SHOPPING_CART_CONTAINS_ITEMS"));

        stubFor(post(urlEqualTo("/checkout")).inScenario("Stateful Checkout")
            .whenScenarioStateIs("SHOPPING_CART_CONTAINS_ITEMS")
            .willReturn(aResponse()
                .withStatus(200)
                .withBody("Checkout completed successfully.")
            ));
    }

    @Test
    public void invokeWireMockStatefulStub_completeStatefulCheckoutScenario() {

        createStatefulStub_responseDependingOnShoppingCarContents();

        given().
        when().
            post("http://localhost:9876/checkout").
        then().
            statusCode(400).
        and().
            body(org.hamcrest.Matchers.equalTo("Shopping cart is empty!"));

        given().
        when().
            post("http://localhost:9876/addtocart").
        then().
            statusCode(200).
        and().
            body(org.hamcrest.Matchers.equalTo("Item added to shopping cart."));

        given().
        when().
            post("http://localhost:9876/checkout").
        then().
            statusCode(200).
        and().
            body(org.hamcrest.Matchers.equalTo("Checkout completed successfully."));
    }
}
