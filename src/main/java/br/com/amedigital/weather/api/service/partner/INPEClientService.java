package br.com.amedigital.weather.api.service.partner;

import br.com.amedigital.weather.api.config.webclient.BaseWebClient;
import br.com.amedigital.weather.api.model.partner.response.INPEWeatherCityResponse;
import com.newrelic.api.agent.Trace;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponents;
import reactor.core.publisher.Mono;

import static org.slf4j.LoggerFactory.getLogger;

@Service
public class INPEClientService extends BaseWebClient {

    private static final Logger LOG = getLogger(INPEClientService.class);

    public static final String CITY_WEATHER = "cidade/#/previsao.xml";
    public static final String CITY_WEATHER_7DAYS = "cidade/7dias/#/previsao.xml";

    @Autowired
    public INPEClientService(final WebClient webClient, @Value("${partner.url}") final String url) {
        super(webClient, url);
    }

    @Trace(dispatcher = true)
    public Mono<INPEWeatherCityResponse> findWeatherToCity(Integer cityCode) {
        LOG.debug("==== Find weather to city ====");

        return handleGenericMono(HttpMethod.GET,
                    urlWeather(CITY_WEATHER.replaceAll("#", String.valueOf(cityCode))),
                    INPEWeatherCityResponse.class, MediaType.APPLICATION_XML_VALUE)
                .doOnError(throwable -> LOG.error("=== Error finding weather to city ===", throwable));
    }

    @Trace(dispatcher = true)
    public Mono<INPEWeatherCityResponse> findWeatherToCityNext7Days(Integer cityCode) {
        LOG.debug("==== Find weather to city in the next 7 days====");

        return handleGenericMono(HttpMethod.GET,
                    urlWeather( CITY_WEATHER_7DAYS.replaceAll("#", String.valueOf(cityCode))),
                    INPEWeatherCityResponse.class, MediaType.APPLICATION_XML_VALUE)
                .doOnError(throwable -> LOG.error("=== Error finding weather to city in the next 7 days ===", throwable));
    }

    protected UriComponents urlWeather(String url) {
        return urlBuilder()
                .pathSegment(url)
                .build();
    }

}
