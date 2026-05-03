package com.retailnet.inventory.mapper;

import com.retailnet.inventory.dto.DemandForecastDTO;
import com.retailnet.inventory.entity.DemandForecast;
import org.springframework.stereotype.Component;

@Component
public class DemandForecastMapper implements BaseMapper<DemandForecast, DemandForecastDTO> {

    @Override
    public DemandForecastDTO toDTO(DemandForecast entity) {
        if (entity == null) return null;
        DemandForecastDTO dto = new DemandForecastDTO();
        dto.setForecastId(entity.getId());
        dto.setStockKeepUnit(entity.getStockKeepingUnit());
        dto.setPredictedDemand(entity.getPredictedDemand());
        dto.setForecastPeriod(entity.getForecastPeriod());
        dto.setConfidenceScore(entity.getConfidenceScore());
        return dto;
    }

    @Override
    public DemandForecast toEntity(DemandForecastDTO dto) {
        if (dto == null) return null;
        DemandForecast entity = new DemandForecast();
        entity.setId(dto.getForecastId());
        entity.setStockKeepingUnit(dto.getStockKeepUnit());
        entity.setPredictedDemand(dto.getPredictedDemand());
        entity.setForecastPeriod(dto.getForecastPeriod());
        entity.setConfidenceScore(dto.getConfidenceScore());
        return entity;
    }
}
