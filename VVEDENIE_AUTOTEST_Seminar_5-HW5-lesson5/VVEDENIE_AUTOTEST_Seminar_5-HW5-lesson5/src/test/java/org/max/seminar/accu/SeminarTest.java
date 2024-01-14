package org.max.seminar.accu;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.given;

public class SeminarTest extends AbstractTest {

    public static final String LOCATIONS_V_1_CITIES_AUTOCOMPLETE = "/locations/v1/cities/autocomplete"; // ctr + alt + c

    @Test
    void get_shouldReturn200WithRestAssure() {

        stubFor(get(urlPathEqualTo(LOCATIONS_V_1_CITIES_AUTOCOMPLETE))
                .withQueryParam("s", equalTo("string"))
                .withQueryParam("i", containing("integer"))
                .willReturn(aResponse().withStatus(200).withBody("Привет!!!")));

        stubFor(get(urlPathMatching(LOCATIONS_V_1_CITIES_AUTOCOMPLETE + "400"))
                .willReturn(aResponse().withStatus(400)));

//        given().when().get(getBaseUrl() + LOCATIONS_V_1_CITIES_AUTOCOMPLETE)
//                .then().statusCode(200);

        given().queryParam("q", "Samara")
                .when().get(getBaseUrl() + LOCATIONS_V_1_CITIES_AUTOCOMPLETE + "400" + "?yes=yes")
                .then().statusCode(400);

        HashMap<String, String> hashMap = new HashMap();
        hashMap.put("s", "string");
        hashMap.put("i", "integer123");

        String string = given().queryParams(hashMap).when().get(getBaseUrl() // alt + enter позволяет получить ответ запроса
                        + LOCATIONS_V_1_CITIES_AUTOCOMPLETE)
                .then().statusCode(200).extract().body().asString();

        Assertions.assertEquals("Привет!!!", string);

    }
}