package ru.ryzhkov.webfluxsecurity.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import ru.ryzhkov.webfluxsecurity.dto.UserDto;
import ru.ryzhkov.webfluxsecurity.entity.UserEntity;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto mapToDto(UserEntity entity);

    @InheritInverseConfiguration
    UserEntity mapToEntity(UserDto dto);
}
