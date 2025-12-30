package com.team.sys_ai.service;

import com.team.sys_ai.dto.StockDTO;
import com.team.sys_ai.entity.Entrepot;
import com.team.sys_ai.entity.Produit;
import com.team.sys_ai.entity.Role;
import com.team.sys_ai.entity.Stock;
import com.team.sys_ai.entity.User;
import com.team.sys_ai.exception.BusinessValidationException;
import com.team.sys_ai.exception.EntrepotAccessDeniedException;
import com.team.sys_ai.mapper.StockMapper;
import com.team.sys_ai.repository.EntrepotRepository;
import com.team.sys_ai.repository.ProduitRepository;
import com.team.sys_ai.repository.StockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    @Mock
    private StockRepository stockRepository;

    @Mock
    private ProduitRepository produitRepository;

    @Mock
    private EntrepotRepository entrepotRepository;

    @Mock
    private StockMapper stockMapper;

    @InjectMocks
    private StockService stockService;

    private Entrepot entrepot;
    private Produit produit;
    private Stock stock;
    private User adminUser;
    private User gestionnaireUser;
    private StockDTO stockDTO;

    @BeforeEach
    void setUp() {
        entrepot = Entrepot.builder()
                .id(1L)
                .nom("Entrepôt Test")
                .actif(true)
                .build();

        produit = Produit.builder()
                .id(1L)
                .nom("Produit Test")
                .prixVente(new BigDecimal("100.00"))
                .actif(true)
                .build();

        stock = Stock.builder()
                .id(1L)
                .entrepot(entrepot)
                .produit(produit)
                .quantiteDisponible(50)
                .seuilAlerte(10)
                .build();

        adminUser = User.builder()
                .id(1L)
                .login("admin")
                .role(Role.ADMIN)
                .actif(true)
                .build();

        gestionnaireUser = User.builder()
                .id(2L)
                .login("gestionnaire")
                .role(Role.GESTIONNAIRE)
                .actif(true)
                .entrepotAssigne(entrepot)
                .build();

        stockDTO = StockDTO.builder()
                .id(1L)
                .produitId(1L)
                .entrepotId(1L)
                .quantiteDisponible(50)
                .seuilAlerte(10)
                .enAlerte(false)
                .critique(false)
                .build();
    }


}


