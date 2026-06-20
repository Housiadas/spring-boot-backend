package com.housi.backend.infrastructure.persistence.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.housi.backend.domain.model.User;
import com.housi.backend.infrastructure.persistence.entity.UserEntity;

@Mapper(
        componentModel = "spring",
        uses = {RolePersistenceMapper.class})
public interface UserPersistenceMapper {

    @Mapping(target = "authorities", ignore = true)
    User toDomain(UserEntity entity);

    @Mapping(target = "authorities", ignore = true)
    UserEntity toEntity(User domain);

    List<User> toDomainList(List<UserEntity> entities);
}
