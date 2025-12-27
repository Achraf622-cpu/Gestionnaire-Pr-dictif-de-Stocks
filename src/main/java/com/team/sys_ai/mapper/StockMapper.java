package com.team.sys_ai.mapper;

import com.team.sys_ai.dto.StockDTO;
import com.team.sys_ai.entity.Stock;
import org.mapstruct.*;

import java.util.List;


@Mapper(componentModel = "spring")
public interface StockMapper {

    @Mapping(source = "produit.id", target = "produitId")
    @Mapping(source = "produit.nom", target = "produitNom")
    @Mapping(source = "entrepot.id", target = "entrepotId")
    @Mapping(source = "entrepot.nom", target = "entrepotNom")
    @Mapping(target = "enAlerte", expression = "java(stock.isAlertLevel())")
    @Mapping(target = "critique", expression = "java(stock.isCritical())")
    StockDTO toDTO(Stock stock);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "produit", ignore = true)
    @Mapping(target = "entrepot", ignore = true)
    @Mapping(target = "lastUpdated", ignore = true)
    Stock toEntity(StockDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "produit", ignore = true)
    @Mapping(target = "entrepot", ignore = true)
    @Mapping(target = "lastUpdated", ignore = true)
    void updateEntity(StockDTO dto, @MappingTarget Stock stock);

    List<StockDTO> toDTOList(List<Stock> stocks);
}
