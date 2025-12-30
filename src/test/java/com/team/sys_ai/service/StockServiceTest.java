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

    @Test
    @DisplayName("Should get stocks for entrepot when user has access")
    void getStocksByEntrepot_WithAccess_ReturnsStocks() {

        when(stockRepository.findByEntrepotIdWithDetails(1L)).thenReturn(List.of(stock));
        when(stockMapper.toDTOList(any())).thenReturn(List.of(stockDTO));

        List<StockDTO> result = stockService.getStocksByEntrepot(1L, adminUser);

        assertThat(result).hasSize(1);
        verify(stockRepository).findByEntrepotIdWithDetails(1L);
    }

    @Test
    @DisplayName("Should deny access when gestionnaire tries to access other entrepot")
    void getStocksByEntrepot_WithoutAccess_ThrowsException() {
        User otherGestionnaire = User.builder()
                .id(3L)
                .role(Role.GESTIONNAIRE)
                .entrepotAssigne(Entrepot.builder().id(2L).build())
                .build();

        assertThatThrownBy(() -> stockService.getStocksByEntrepot(1L, otherGestionnaire))
                .isInstanceOf(EntrepotAccessDeniedException.class);
    }

    @Test
    @DisplayName("Should add quantity to existing stock")
    void addQuantity_ExistingStock_IncreasesQuantity() {

        when(stockRepository.findByEntrepotIdAndProduitId(1L, 1L)).thenReturn(Optional.of(stock));
        when(stockRepository.save(any(Stock.class))).thenReturn(stock);
        when(stockMapper.toDTO(any(Stock.class))).thenReturn(stockDTO);

        // When
        StockDTO result = stockService.addQuantity(1L, 1L, 20, adminUser);

        // Then
        assertThat(result).isNotNull();
        verify(stockRepository).save(argThat(s -> s.getQuantiteDisponible() == 70));
    }

    @Test
    @DisplayName("Should throw exception when adding negative quantity")
    void addQuantity_NegativeQuantity_ThrowsException() {
        // When/Then
        assertThatThrownBy(() -> stockService.addQuantity(1L, 1L, -10, adminUser))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessageContaining("positive");
    }

    @Test
    @DisplayName("Should remove quantity from stock")
    void removeQuantity_SufficientStock_DecreasesQuantity() {
        // Given
        when(stockRepository.findByEntrepotIdAndProduitId(1L, 1L)).thenReturn(Optional.of(stock));
        when(stockRepository.save(any(Stock.class))).thenReturn(stock);
        when(stockMapper.toDTO(any(Stock.class))).thenReturn(stockDTO);

        // When
        StockDTO result = stockService.removeQuantity(1L, 1L, 20, adminUser);

        // Then
        assertThat(result).isNotNull();
        verify(stockRepository).save(argThat(s -> s.getQuantiteDisponible() == 30));
    }

    @Test
    @DisplayName("Should throw exception when removing more than available")
    void removeQuantity_InsufficientStock_ThrowsException() {
        // Given
        when(stockRepository.findByEntrepotIdAndProduitId(1L, 1L)).thenReturn(Optional.of(stock));

        // When/Then
        assertThatThrownBy(() -> stockService.removeQuantity(1L, 1L, 100, adminUser))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessageContaining("insuffisant");
    }

    @Test
    @DisplayName("Should create new stock when upserting non-existent stock")
    void upsertStock_NewStock_CreatesStock() {
        // Given
        when(stockRepository.findByEntrepotIdAndProduitId(1L, 1L)).thenReturn(Optional.empty());
        when(entrepotRepository.findById(1L)).thenReturn(Optional.of(entrepot));
        when(produitRepository.findById(1L)).thenReturn(Optional.of(produit));
        when(stockRepository.save(any(Stock.class))).thenReturn(stock);
        when(stockMapper.toDTO(any(Stock.class))).thenReturn(stockDTO);

        // When
        StockDTO result = stockService.upsertStock(1L, 1L, 100, 15, adminUser);

        // Then
        assertThat(result).isNotNull();
        verify(stockRepository).save(any(Stock.class));
    }

    @Test
    @DisplayName("Should get stocks at alert level")
    void getStocksAtAlert_ReturnsAlertStocks() {
        // Given
        Stock alertStock = Stock.builder()
                .id(2L)
                .entrepot(entrepot)
                .produit(produit)
                .quantiteDisponible(5)
                .seuilAlerte(10)
                .build();

        when(stockRepository.findStocksAtAlertLevelByEntrepot(1L)).thenReturn(List.of(alertStock));
        when(stockMapper.toDTOList(any())).thenReturn(List.of(stockDTO));

        // When
        List<StockDTO> result = stockService.getStocksAtAlert(1L, adminUser);

        // Then
        assertThat(result).isNotEmpty();
        verify(stockRepository).findStocksAtAlertLevelByEntrepot(1L);
    }
}


