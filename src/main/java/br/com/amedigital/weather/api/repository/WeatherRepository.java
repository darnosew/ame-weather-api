package br.com.amedigital.weather.api.repository;

import br.com.amedigital.weather.api.entity.WeatherEntity;
import org.jdbi.v3.core.Jdbi;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class WeatherRepository extends BaseRepository {

    public WeatherRepository(Jdbi jdbi, Scheduler jdbcScheduler) {
        super(jdbi, jdbcScheduler);
    }

    public Flux<WeatherEntity> save(List<WeatherEntity> entities) {

        List<WeatherEntity> weatherEntities = new ArrayList<>();

        return async(() -> jdbi.inTransaction(handle -> {

            entities.stream().forEach(entity -> {
                entity.setId(UUID.randomUUID().toString());
                handle.createUpdate(sqlLocator.locate("sql.save-weather"))
                        .bindBean(entity)
                        .execute();
                weatherEntities.add(entity);
            });

            return weatherEntities;
        })).flatMapIterable(e -> e);
    }
}
