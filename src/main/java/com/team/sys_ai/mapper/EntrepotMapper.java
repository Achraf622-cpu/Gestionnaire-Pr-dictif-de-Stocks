package com.team.sys_ai.mapper;

import com.team.sys_ai.dto.EntrepotDTO;
import com.team.sys_ai.entity.Entrepot;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;


@Mapper(componentModel = "spring")
public interface EntrepotMapper {

    EntrepotDTO toDTO(Entrepot entrepot);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "stocks", ignore = true)
    @Mapping(target = "gestionnaires", ignore = true)
    Entrepot toEntity(EntrepotDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "stocks", ignore = true)
    @Mapping(target = "gestionnaires", ignore = true)
    void updateEntity(EntrepotDTO dto, @MappingTarget Entrepot entrepot);

    List<EntrepotDTO> toDTOList(List<Entrepot> entrepots);
}
