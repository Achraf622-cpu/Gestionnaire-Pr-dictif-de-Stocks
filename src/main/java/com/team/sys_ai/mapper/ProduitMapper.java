package com.team.sys_ai.mapper;

import com.team.sys_ai.dto.ProduitAdminDTO;
import com.team.sys_ai.dto.ProduitDTO;
import com.team.sys_ai.entity.Produit;
import org.mapstruct.*;

import java.math.BigDecimal;
import java.util.List;


@Mapper(componentModel = "spring")
public interface ProduitMapper {


    ProduitDTO toDTO(Produit produit);


    @Mapping(target = "prixAchat", source = "produit", qualifiedByName = "getPrixAchat")
    @Mapping(target = "marge", source = "produit", qualifiedByName = "getMarge")
    ProduitAdminDTO toAdminDTO(Produit produit);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "stocks", ignore = true)
    @Mapping(target = "historiqueVentes", ignore = true)
    @Mapping(target = "prixAchat", ignore = true)
    @Mapping(target = "marge", ignore = true)
    @Mapping(target = "actif", constant = "true")
    Produit toEntity(ProduitDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "stocks", ignore = true)
    @Mapping(target = "historiqueVentes", ignore = true)
    @Mapping(target = "prixAchat", source = "dto", qualifiedByName = "setPrixAchat")
    @Mapping(target = "marge", source = "dto", qualifiedByName = "setMarge")
    Produit toEntityFromAdmin(ProduitAdminDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "stocks", ignore = true)
    @Mapping(target = "historiqueVentes", ignore = true)
    @Mapping(target = "prixAchat", ignore = true)
    @Mapping(target = "marge", ignore = true)
    void updateEntity(ProduitDTO dto, @MappingTarget Produit produit);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "stocks", ignore = true)
    @Mapping(target = "historiqueVentes", ignore = true)
    @Mapping(target = "prixAchat", source = "dto", qualifiedByName = "setPrixAchat")
    @Mapping(target = "marge", source = "dto", qualifiedByName = "setMarge")
    void updateEntityFromAdmin(ProduitAdminDTO dto, @MappingTarget Produit produit);

    List<ProduitDTO> toDTOList(List<Produit> produits);

    List<ProduitAdminDTO> toAdminDTOList(List<Produit> produits);

    @Named("getPrixAchat")
    default BigDecimal getPrixAchat(Produit produit) {
        return produit.getPrixAchatValue();
    }

    @Named("getMarge")
    default BigDecimal getMarge(Produit produit) {
        return produit.getMargeValue();
    }

    @Named("setPrixAchat")
    default String setPrixAchat(ProduitAdminDTO dto) {
        return dto.getPrixAchat() != null ? dto.getPrixAchat().toString() : null;
    }

    @Named("setMarge")
    default String setMarge(ProduitAdminDTO dto) {
        return dto.getMarge() != null ? dto.getMarge().toString() : null;
    }
}
