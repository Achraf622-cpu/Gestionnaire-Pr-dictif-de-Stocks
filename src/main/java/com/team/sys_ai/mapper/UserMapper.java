package com.team.sys_ai.mapper;

import com.team.sys_ai.dto.UserCreateDTO;
import com.team.sys_ai.dto.UserDTO;
import com.team.sys_ai.entity.Entrepot;
import com.team.sys_ai.entity.User;
import org.mapstruct.*;

import java.util.List;


@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "entrepotAssigne.id", target = "entrepotAssigneId")
    @Mapping(source = "entrepotAssigne.nom", target = "entrepotAssigneNom")
    UserDTO toDTO(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "lastLogin", ignore = true)
    @Mapping(target = "entrepotAssigne", ignore = true)
    @Mapping(target = "password", ignore = true) // Password handled separately
    @Mapping(target = "actif", constant = "true")
    User toEntity(UserCreateDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "lastLogin", ignore = true)
    @Mapping(target = "entrepotAssigne", ignore = true)
    @Mapping(target = "password", ignore = true)
    void updateEntity(UserDTO dto, @MappingTarget User user);

    List<UserDTO> toDTOList(List<User> users);


    default Entrepot mapEntrepot(Long entrepotId) {
        if (entrepotId == null) {
            return null;
        }
        Entrepot entrepot = new Entrepot();
        entrepot.setId(entrepotId);
        return entrepot;
    }
}
