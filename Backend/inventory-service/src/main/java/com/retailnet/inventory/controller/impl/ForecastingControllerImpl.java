package com.retailnet.inventory.controller.impl;

import com.retailnet.inventory.controller.ForecastingController;
import com.retailnet.inventory.dto.DemandForecastDTO;
import com.retailnet.inventory.entity.DemandForecast;
import com.retailnet.inventory.mapper.DemandForecastMapper;
import com.retailnet.inventory.service.ForecastingService;
import com.retailnet.inventory.utils.LogConstant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ForecastingControllerImpl implements ForecastingController {

    private final ForecastingService forecastingService;
    private final DemandForecastMapper demandForecastMapper;

    private static final String CLASS_NAME = "ForecastingControllerImpl";

    @Override
    public ResponseEntity<DemandForecastDTO> generateProductForecast(Long productId) {
        final String METHOD_NAME = "generateProductForecast";

        log.info(LogConstant.INSIDE_CLASS_METHOD, CLASS_NAME, METHOD_NAME);

        DemandForecast result = forecastingService.createForecastForProduct(productId);

        return ResponseEntity.ok(demandForecastMapper.toDTO(result));
    }

    @Override
    public ResponseEntity<List<DemandForecastDTO>> generateAllForecasts() {
        final String METHOD_NAME = "generateAllForecasts";

        log.info(LogConstant.INSIDE_CLASS_METHOD, CLASS_NAME, METHOD_NAME);

        List<DemandForecast> results = forecastingService.generateAllForecasts();

        return ResponseEntity.ok(results.stream()
                .map(demandForecastMapper::toDTO)
                .collect(Collectors.toList()));
    }

    @Override
    public ResponseEntity<List<DemandForecastDTO>> getAllForecasts() {
        final String METHOD_NAME = "getAllForecasts";

        log.info(LogConstant.INSIDE_CLASS_METHOD, CLASS_NAME, METHOD_NAME);

        List<DemandForecast> forecasts = forecastingService.getAllForecasts();

        return ResponseEntity.ok(forecasts.stream()
                .map(demandForecastMapper::toDTO)
                .collect(Collectors.toList()));
    }
}