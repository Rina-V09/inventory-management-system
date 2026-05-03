package com.retailnet.inventory.repository;

import com.retailnet.inventory.entity.DemandForecast;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DemandForecastRepository extends JpaRepository<DemandForecast, Long> {
}
