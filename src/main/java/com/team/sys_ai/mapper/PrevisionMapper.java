package com.team.sys_ai.mapper;

import com.team.sys_ai.dto.PrevisionDTO;
import com.team.sys_ai.entity.Prevision;
import org.mapstruct.*;

import java.util.List;


@Mapper(componentModel = "spring")
public interface PrevisionMapper {

    @Mapping(source = "produit.id", target = "produitId")
    @Mapping(source = "produit.nom", target = "produitNom")
    @Mapping(source = "entrepot.id", target = "entrepotId")
    @Mapping(source = "entrepot.nom", target = "entrepotNom")
    @Mapping(target = "stockActuel", ignore = true) // Set by service
    @Mapping(target = "seuilAlerte", ignore = true) // Set by service
    PrevisionDTO toDTO(Prevision prevision);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "produit", ignore = true)
    @Mapping(target = "entrepot", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Prevision toEntity(PrevisionDTO dto);

    List<PrevisionDTO> toDTOList(List<Prevision> previsions);
}
