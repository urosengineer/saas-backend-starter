package com.urke.saasbackendstarter.mapper;

import com.urke.saasbackendstarter.domain.Role;
import com.urke.saasbackendstarter.domain.Permission;
import com.urke.saasbackendstarter.dto.RoleDTO;
import com.urke.saasbackendstarter.dto.PermissionDTO;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Mapper for converting between Role and RoleDTO.
 */
public class RoleMapper {

    public static PermissionDTO toDTO(Permission entity) {
        if (entity == null) return null;
        return PermissionDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }

    public static RoleDTO toDTO(Role entity) {
        if (entity == null) return null;
        return RoleDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .permissions(entity.getPermissions() != null
                        ? entity.getPermissions().stream()
                            .map(RoleMapper::toDTO)
                            .collect(Collectors.toSet())
                        : null)
                .build();
    }
}