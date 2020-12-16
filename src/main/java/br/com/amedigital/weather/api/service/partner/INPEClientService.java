package br.com.amedigital.weather.api.service.partner;

import br.com.amedigital.weather.api.config.webclient.BaseWebClient;
import br.com.amedigital.weather.api.exception.NotFoundException;
import br.com.amedigital.weather.api.model.ErrorMessages;
import br.com.amedigital.weather.api.model.partner.response.INPEWeatherCityResponse;
import br.com.amedigital.weather.api.model.partner.response.INPECityResponse;
import com.newrelic.api.agent.Trace;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponents;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

import static org.slf4j.LoggerFactory.getLogger;

@Service
public class INPEClientService extends BaseWebClient {

    private static final Logger LOG = getLogger(INPEClientService.class);

    public static final String CITY_DATA = "city=#";
    public static final String CITY_WEATHER = "/cidade/#/previsao.xml";
    public static final String CITY_WEATHER_7DAYS = "/cidade/7dias/#/previsao.xml";

    @Autowired
    public INPEClientService(final WebClient webClient, @Value("${partner.url}") final String url) {
        super(webClient, url);
    }

    @Trace(dispatcher = true)
    public Mono<INPEWeatherCityResponse> findWeatherToCity(Integer cityCode) {
        LOG.debug("==== Find weather to city ====");

        return handleGenericMono(HttpMethod.GET,
                    urlWeather(CITY_WEATHER.replaceAll("#", String.valueOf(cityCode)), ""),
                    INPEWeatherCityResponse.class, MediaType.APPLICATION_XML_VALUE)
                .doOnError(throwable -> LOG.error("=== Error finding weather to city ===", throwable));
    }

    @Trace(dispatcher = true)
    public Mono<INPEWeatherCityResponse> findWeatherToCityNext7Days(Integer cityCode) {
        LOG.debug("==== Find weather to city in the next 7 days====");

        return handleGenericMono(HttpMethod.GET,
                    urlWeather( CITY_WEATHER_7DAYS.replaceAll("#", String.valueOf(cityCode)), ""),
                    INPEWeatherCityResponse.class, MediaType.APPLICATION_XML_VALUE)
                .doOnError(throwable -> LOG.error("=== Error finding weather to city in the next 7 days ===", throwable));
    }

    @Trace(dispatcher = true)
    public Mono<INPEWeatherCityResponse> findWeatherToCityByName(String cityName, String stateName) {
        LOG.debug("==== Find city data====");

        return handleGenericMono(HttpMethod.GET, urlWeather("/listaCidades", CITY_DATA.replaceAll("#", cityName)),
                INPECityResponse.class, MediaType.APPLICATION_XML_VALUE)
                .map(response -> response.getCity().stream().filter(
                        resp -> StringUtils.stripAccents(resp.getName().toUpperCase())
                                        .equals(StringUtils.stripAccents(cityName.toUpperCase())) &&
                                StringUtils.stripAccents(resp.getState().toUpperCase())
                                        .equals(StringUtils.stripAccents(stateName.toUpperCase())))
                        .collect(Collectors.toList()))
                .flatMap(city -> city.size() > 0 ? handleGenericMono(HttpMethod.GET, urlWeather(CITY_WEATHER.replaceAll("#", city.get(0).getCityCode().toString()), ""),
                            INPEWeatherCityResponse.class, MediaType.APPLICATION_XML_VALUE)
                        .map(weatherCity -> {
                            weatherCity.setCityCode(city.get(0).getCityCode());
                            return weatherCity;
                        }) : Mono.error(new NotFoundException(ErrorMessages.GENERIC_NOT_FOUND_EXCEPTION)));
    }

    protected UriComponents urlWeather(String path, String query) {
        return urlBuilder()
                .path(path)
                .query(query)
                .build();
    }

}
