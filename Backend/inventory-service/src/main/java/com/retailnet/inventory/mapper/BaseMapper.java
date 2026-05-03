package com.retailnet.inventory.mapper;

import java.util.ArrayList;
import java.util.List;

/**
 * Generic Base Mapper to standardize DTO-Entity conversion across the RetailNet system.
 * This interface provides a default implementation for List conversions to reduce boilerplate code.
 *
 * @param <E> The Entity type (Database model)
 * @param <D> The DTO type (API model)
 */
public interface BaseMapper<E, D> {

    /**
     * Maps a single Entity to its corresponding DTO.
     */
    D toDTO(E entity);

    /**
     * Maps a single DTO back to its database Entity.
     */
    E toEntity(D dto);

    /**
     * Converts a List of Entities into a List of DTOs using a traditional loop.
     * This method is inherited by all mappers, so you don't have to write it again.
     */
    default List<D> toDTOList(List<E> entities) {
        if (entities == null) {
            return new ArrayList<>();
        }

        List<D> dtos = new ArrayList<>();
        for (E entity : entities) {
            dtos.add(toDTO(entity));
        }
        return dtos;
    }
}