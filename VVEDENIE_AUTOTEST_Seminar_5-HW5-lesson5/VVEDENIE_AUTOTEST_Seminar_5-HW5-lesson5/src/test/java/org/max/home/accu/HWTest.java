package org.max.home.accu;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.jupiter.api.Test;
import org.max.home.accu.weather.DailyForecast;
import org.max.home.accu.weather.Headline;
import org.max.home.accu.weather.Weather;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HWTest extends AbstractTest {
    public static final String DAY_1_OF_DAILY_FORECASTS = "/forecasts/v1/daily/1day/50";
    public static final String DAY_10_OF_DAILY_FORECASTS = "/forecasts/v1/daily/10day/50";

    private static final Logger logger
            = LoggerFactory.getLogger(AbstractTest.class);

    @Test
    void getReturnStatus200ofForecasts1Day() throws IOException {

        logger.info("Тест на возврат статуса 200 запущен: ");

        ObjectMapper mapper = new ObjectMapper(); // ObjectMapper — часть библиотеки Jackson
        Weather weather = new Weather(); // погода
        Headline headline = new Headline(); // заголовки
        headline.setCategory("Category");
        headline.setText("Text");
        weather.setHeadline(headline);

        DailyForecast dailyForecast = new DailyForecast(); // ежедневный прогноз
        List<DailyForecast> dailyForecasts = new ArrayList<>();
        dailyForecasts.add(dailyForecast);
        weather.setDailyForecasts(dailyForecasts);

        logger.debug("Мокирование для GET запроса: /forecasts/v1/daily/1day/50: ");
        stubFor(get(urlPathEqualTo(DAY_1_OF_DAILY_FORECASTS))
                .willReturn(aResponse()
                        .withStatus(200).withBody(mapper.writeValueAsString(weather))));

        CloseableHttpClient httpClient = HttpClients.createDefault();

        HttpGet request = new HttpGet(getBaseUrl() + DAY_1_OF_DAILY_FORECASTS);
        logger.debug("http клиент создан");
        //when
        HttpResponse response = httpClient.execute(request);
        //then
        verify(getRequestedFor(urlPathEqualTo(DAY_1_OF_DAILY_FORECASTS)));
        assertEquals(200, response.getStatusLine().getStatusCode());

        Weather responseBody = mapper.readValue(response.getEntity().getContent(), Weather.class);
        assertEquals("Category", responseBody.getHeadline().getCategory());
        assertEquals("Text", responseBody.getHeadline().getText());
        assertEquals(1, responseBody.getDailyForecasts().size());

        //-------------------------------------------------------------------------------------------------------------
    }

    @Test
    void getReturnStatus401ofForecasts10Days() throws IOException {

        logger.info("Тест на возврат статуса 401 запущен: ");

        logger.debug("Мокирование для GET запроса: /forecasts/v1/daily/10day/50");

        stubFor(get(urlPathEqualTo(DAY_10_OF_DAILY_FORECASTS))
                .willReturn(aResponse()
                        .withStatus(401).withBody("Unauthorized")));

        CloseableHttpClient httpClient = HttpClients.createDefault();

        HttpGet request = new HttpGet(getBaseUrl() + DAY_10_OF_DAILY_FORECASTS);
        logger.debug("http клиент создан");
        //when
        HttpResponse response = httpClient.execute(request);
        //then
        verify(getRequestedFor(urlPathEqualTo(DAY_10_OF_DAILY_FORECASTS)));
        assertEquals(401, response.getStatusLine().getStatusCode());
        assertEquals("Unauthorized", convertResponseToString(response));

        // -------------------------------------------------------------------------------------------------------------

//        stubFor(get(urlPathMatching(DAY_10_OF_DAILY_FORECASTS + "401"))
//                .willReturn(aResponse().withStatus(401)));
//
//        given().queryParam("Code", "Unauthorized")
//                .when().get(getBaseUrl() + DAY_10_OF_DAILY_FORECASTS + "401" + "?yes=yes")
//                .then().statusCode(401);
    }
}
