package com.bookkeeper.AssetSummary.model.mapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public abstract class BaseMapper<E, D> {

    public abstract E convertToEntity(D dto);

    public abstract D convertToDto(E entity);

    public Collection<E> convertToEntity(Collection<D> dto) {
        return dto.stream().map(this::convertToEntity).collect(Collectors.toList());
    }

    public Collection<D> convertToDto(Collection<E> entity) {
        return entity.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public List<E> convertToEntityList(Collection<D> dto) {
        return new ArrayList<>(convertToEntity(dto));
    }

    public List<D> convertToDtoList(Collection<E> entity) {
        return new ArrayList<>(convertToDto(entity));
    }
}
