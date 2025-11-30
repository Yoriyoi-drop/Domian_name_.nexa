package com.myproject.nexa.mapper;

import com.myproject.nexa.dto.response.RoleResponse;
import com.myproject.nexa.entities.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    RoleResponse toResponse(Role role);

    List<RoleResponse> toResponseList(List<Role> roles);
}