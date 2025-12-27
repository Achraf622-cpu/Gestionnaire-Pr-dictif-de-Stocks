package com.team.sys_ai.mapper;

import com.team.sys_ai.dto.HistoriqueVenteDTO;
import com.team.sys_ai.entity.HistoriqueVente;
import org.mapstruct.*;

import java.util.List;


@Mapper(componentModel = "spring")
public interface HistoriqueVenteMapper {

    @Mapping(source = "produit.id", target = "produitId")
    @Mapping(source = "produit.nom", target = "produitNom")
    @Mapping(source = "entrepot.id", target = "entrepotId")
    @Mapping(source = "entrepot.nom", target = "entrepotNom")
    HistoriqueVenteDTO toDTO(HistoriqueVente historiqueVente);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "produit", ignore = true)
    @Mapping(target = "entrepot", ignore = true)
    @Mapping(target = "jourSemaine", ignore = true) // Auto-calculated
    @Mapping(target = "mois", ignore = true) // Auto-calculated
    @Mapping(target = "annee", ignore = true) // Auto-calculated
    HistoriqueVente toEntity(HistoriqueVenteDTO dto);

    List<HistoriqueVenteDTO> toDTOList(List<HistoriqueVente> historiqueVentes);
}
