package com.housi.backend.mappers.base;

import com.housi.backend.mappers.annotations.ToEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.Collection;

/**
 * The interface Base mapper management.
 *
 * @param <E> the type parameter Entity
 * @param <C> the type parameter CreateRequest
 * @param <U> the type parameter UpdateRequest
 * @param <R> the type parameter Response
 */
public interface ManagementBaseMapper<E, C, U, R> {

  @ToEntity
  E toEntity(C request);

  @ToEntity
  E update(U request, @MappingTarget E entity);

  @ToEntity
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  E patch(U request, @MappingTarget E entity);

  R toManagementResponse(E entity);

  Collection<R> toManagementResponse(Collection<E> entity);
}
