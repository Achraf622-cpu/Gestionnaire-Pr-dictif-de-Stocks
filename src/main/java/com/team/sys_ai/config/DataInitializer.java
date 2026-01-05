package com.team.sys_ai.config;

import com.team.sys_ai.entity.*;
import com.team.sys_ai.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Initializes the database with comprehensive sample data for
 * development/testing.
 * Includes: 5 warehouses, 30+ products, stocks, and 90 days of sales history.
 * Only runs when the database is empty.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

        private final UserRepository userRepository;
        private final EntrepotRepository entrepotRepository;
        private final ProduitRepository produitRepository;
        private final StockRepository stockRepository;
        private final HistoriqueVenteRepository historiqueVenteRepository;
        private final PasswordEncoder passwordEncoder;

        private final Random random = new Random(42); // Seed for reproducibility

        @Override
        @Transactional
        public void run(String... args) {
                if (userRepository.count() > 0) {
                        log.info("Database already initialized. Skipping data initialization.");
                        return;
                }

                log.info("🚀 Starting comprehensive data initialization...");

                // Create Warehouses
                List<Entrepot> entrepots = createEntrepots();

                // Create Products
                List<Produit> produits = createProduits();

                // Create Users
                createUsers(entrepots);

                // Create Stocks for each warehouse
                createStocks(entrepots, produits);

                // Create 90 days of sales history for AI predictions
                createSalesHistory(entrepots, produits);

                printSummary(entrepots, produits);
        }

        private List<Entrepot> createEntrepots() {
                log.info("📦 Creating warehouses...");

                List<Entrepot> entrepots = new ArrayList<>();

                entrepots.add(createEntrepot("Entrepôt Paris Nord", "15 Rue de la Logistique", "Paris", "75019"));
                entrepots.add(createEntrepot("Entrepôt Paris Sud", "42 Avenue du Commerce", "Paris", "75014"));
                entrepots.add(createEntrepot("Entrepôt Lyon", "78 Boulevard Industriel", "Lyon", "69007"));
                entrepots.add(createEntrepot("Entrepôt Marseille", "120 Quai du Port", "Marseille", "13002"));
                entrepots.add(createEntrepot("Entrepôt Bordeaux", "55 Rue des Vignobles", "Bordeaux", "33000"));

                return entrepots;
        }

        private List<Produit> createProduits() {
                log.info("🛒 Creating products...");

                List<Produit> produits = new ArrayList<>();

                // ===== BOISSONS (8 products) =====
                produits.add(createProduit("Café Arabica Premium", "Café en grains haute qualité", "Boissons",
                                new BigDecimal("24.99"), new BigDecimal("15.00"), Unite.KG));
                produits.add(createProduit("Café Robusta", "Café en grains corsé", "Boissons",
                                new BigDecimal("18.50"), new BigDecimal("10.00"), Unite.KG));
                produits.add(createProduit("Thé Vert Bio", "Thé vert biologique japonais", "Boissons",
                                new BigDecimal("12.99"), new BigDecimal("7.00"), Unite.KG));
                produits.add(createProduit("Thé Earl Grey", "Thé noir à la bergamote", "Boissons",
                                new BigDecimal("9.99"), new BigDecimal("5.50"), Unite.KG));
                produits.add(createProduit("Jus d'Orange", "Pur jus d'orange pressé", "Boissons",
                                new BigDecimal("3.50"), new BigDecimal("1.80"), Unite.LITRE));
                produits.add(createProduit("Eau Minérale", "Eau de source naturelle", "Boissons",
                                new BigDecimal("0.99"), new BigDecimal("0.40"), Unite.LITRE));
                produits.add(createProduit("Soda Cola", "Boisson gazeuse cola", "Boissons",
                                new BigDecimal("1.50"), new BigDecimal("0.70"), Unite.LITRE));
                produits.add(createProduit("Limonade Artisanale", "Limonade maison au citron", "Boissons",
                                new BigDecimal("2.99"), new BigDecimal("1.20"), Unite.LITRE));

                // ===== ALIMENTAIRE (10 products) =====
                produits.add(createProduit("Riz Basmati", "Riz long grain parfumé", "Alimentaire",
                                new BigDecimal("4.50"), new BigDecimal("2.20"), Unite.KG));
                produits.add(createProduit("Pâtes Spaghetti", "Pâtes italiennes premium", "Alimentaire",
                                new BigDecimal("2.99"), new BigDecimal("1.30"), Unite.KG));
                produits.add(createProduit("Huile d'Olive Extra Vierge", "Huile d'olive première pression",
                                "Alimentaire",
                                new BigDecimal("12.99"), new BigDecimal("7.50"), Unite.LITRE));
                produits.add(createProduit("Farine de Blé T55", "Farine blanche multi-usage", "Alimentaire",
                                new BigDecimal("1.50"), new BigDecimal("0.60"), Unite.KG));
                produits.add(createProduit("Sucre en Poudre", "Sucre blanc raffiné", "Alimentaire",
                                new BigDecimal("1.99"), new BigDecimal("0.80"), Unite.KG));
                produits.add(createProduit("Sel de Mer", "Sel marin gros", "Alimentaire",
                                new BigDecimal("2.50"), new BigDecimal("1.00"), Unite.KG));
                produits.add(createProduit("Miel de Fleurs", "Miel naturel multifloral", "Alimentaire",
                                new BigDecimal("8.99"), new BigDecimal("4.50"), Unite.KG));
                produits.add(createProduit("Confiture Fraise", "Confiture artisanale aux fraises", "Alimentaire",
                                new BigDecimal("4.50"), new BigDecimal("2.00"), Unite.KG));
                produits.add(createProduit("Chocolat Noir 70%", "Tablettes de chocolat noir", "Alimentaire",
                                new BigDecimal("3.99"), new BigDecimal("1.80"), Unite.UNITE));
                produits.add(createProduit("Biscuits Artisanaux", "Biscuits sablés traditionnels", "Alimentaire",
                                new BigDecimal("5.99"), new BigDecimal("2.50"), Unite.KG));

                // ===== PRODUITS LAITIERS (5 products) =====
                produits.add(createProduit("Lait Entier Bio", "Lait frais biologique", "Produits Laitiers",
                                new BigDecimal("1.80"), new BigDecimal("0.90"), Unite.LITRE));
                produits.add(createProduit("Beurre Doux", "Beurre fermier doux", "Produits Laitiers",
                                new BigDecimal("4.50"), new BigDecimal("2.50"), Unite.KG));
                produits.add(createProduit("Fromage Comté", "Comté AOP 12 mois", "Produits Laitiers",
                                new BigDecimal("22.00"), new BigDecimal("14.00"), Unite.KG));
                produits.add(createProduit("Yaourt Nature", "Yaourt brassé nature", "Produits Laitiers",
                                new BigDecimal("0.50"), new BigDecimal("0.25"), Unite.UNITE));
                produits.add(createProduit("Crème Fraîche", "Crème fraîche épaisse", "Produits Laitiers",
                                new BigDecimal("3.20"), new BigDecimal("1.60"), Unite.LITRE));

                // ===== CONSERVES (5 products) =====
                produits.add(createProduit("Tomates Pelées", "Tomates pelées en conserve", "Conserves",
                                new BigDecimal("1.99"), new BigDecimal("0.80"), Unite.KG));
                produits.add(createProduit("Haricots Verts", "Haricots verts extra-fins", "Conserves",
                                new BigDecimal("2.50"), new BigDecimal("1.10"), Unite.KG));
                produits.add(createProduit("Thon à l'Huile", "Thon entier à l'huile d'olive", "Conserves",
                                new BigDecimal("4.99"), new BigDecimal("2.50"), Unite.UNITE));
                produits.add(createProduit("Sardines", "Sardines à l'huile", "Conserves",
                                new BigDecimal("2.99"), new BigDecimal("1.20"), Unite.UNITE));
                produits.add(createProduit("Maïs Doux", "Maïs en grains", "Conserves",
                                new BigDecimal("1.80"), new BigDecimal("0.70"), Unite.KG));

                // ===== HYGIÈNE (5 products) =====
                produits.add(createProduit("Savon de Marseille", "Savon traditionnel", "Hygiène",
                                new BigDecimal("4.99"), new BigDecimal("2.00"), Unite.KG));
                produits.add(createProduit("Shampoing Bio", "Shampoing naturel sans sulfate", "Hygiène",
                                new BigDecimal("8.99"), new BigDecimal("4.50"), Unite.LITRE));
                produits.add(createProduit("Dentifrice Menthe", "Dentifrice à la menthe fraîche", "Hygiène",
                                new BigDecimal("3.50"), new BigDecimal("1.50"), Unite.UNITE));
                produits.add(createProduit("Gel Douche", "Gel douche hydratant", "Hygiène",
                                new BigDecimal("4.50"), new BigDecimal("2.00"), Unite.LITRE));
                produits.add(createProduit("Papier Toilette", "Papier double épaisseur", "Hygiène",
                                new BigDecimal("6.99"), new BigDecimal("3.00"), Unite.UNITE));

                // ===== ENTRETIEN (4 products) =====
                produits.add(createProduit("Lessive Liquide", "Lessive concentrée", "Entretien",
                                new BigDecimal("9.99"), new BigDecimal("5.00"), Unite.LITRE));
                produits.add(createProduit("Liquide Vaisselle", "Détergent vaisselle écologique", "Entretien",
                                new BigDecimal("2.99"), new BigDecimal("1.20"), Unite.LITRE));
                produits.add(createProduit("Nettoyant Multi-Surface", "Spray nettoyant universel", "Entretien",
                                new BigDecimal("3.99"), new BigDecimal("1.80"), Unite.LITRE));
                produits.add(createProduit("Éponges", "Lot de 3 éponges", "Entretien",
                                new BigDecimal("2.50"), new BigDecimal("1.00"), Unite.UNITE));

                log.info("✅ Created {} products", produits.size());
                return produits;
        }

        private void createUsers(List<Entrepot> entrepots) {
                log.info("👥 Creating users...");

                // 1 Admin
                createUser("admin", "admin123", "Admin", "Super", "admin@sysai.com", Role.ADMIN, null);

                // 1 Gestionnaire per warehouse
                createUser("gest_paris_nord", "gest123", "Dupont", "Pierre", "pierre.dupont@sysai.com",
                                Role.GESTIONNAIRE, entrepots.get(0));
                createUser("gest_paris_sud", "gest123", "Martin", "Marie", "marie.martin@sysai.com",
                                Role.GESTIONNAIRE, entrepots.get(1));
                createUser("gest_lyon", "gest123", "Bernard", "Jean", "jean.bernard@sysai.com",
                                Role.GESTIONNAIRE, entrepots.get(2));
                createUser("gest_marseille", "gest123", "Petit", "Sophie", "sophie.petit@sysai.com",
                                Role.GESTIONNAIRE, entrepots.get(3));
                createUser("gest_bordeaux", "gest123", "Durand", "Luc", "luc.durand@sysai.com",
                                Role.GESTIONNAIRE, entrepots.get(4));
        }

        private void createStocks(List<Entrepot> entrepots, List<Produit> produits) {
                log.info("📊 Creating stocks...");

                int stockCount = 0;

                for (Entrepot entrepot : entrepots) {
                        for (Produit produit : produits) {
                                // Generate varied stock levels for interesting AI predictions
                                int baseQuantity = 50 + random.nextInt(200);
                                int seuilAlerte = 20 + random.nextInt(30);

                                // Make some stocks critically low for testing
                                if (random.nextDouble() < 0.15) {
                                        baseQuantity = seuilAlerte / 2; // Critical
                                } else if (random.nextDouble() < 0.25) {
                                        baseQuantity = seuilAlerte + random.nextInt(10); // Near alert
                                }

                                Stock stock = Stock.builder()
                                                .produit(produit)
                                                .entrepot(entrepot)
                                                .quantiteDisponible(baseQuantity)
                                                .seuilAlerte(seuilAlerte)
                                                .build();
                                stockRepository.save(stock);
                                stockCount++;
                        }
                }

                log.info("✅ Created {} stock entries", stockCount);
        }

        private void createSalesHistory(List<Entrepot> entrepots, List<Produit> produits) {
                log.info("📈 Creating 90 days of sales history for AI predictions...");

                LocalDate today = LocalDate.now();
                int salesCount = 0;

                for (Entrepot entrepot : entrepots) {
                        for (Produit produit : produits) {
                                // Generate sales for past 90 days
                                for (int daysBack = 90; daysBack >= 1; daysBack--) {
                                        LocalDate saleDate = today.minusDays(daysBack);

                                        // Skip some days randomly (not every product sells every day)
                                        if (random.nextDouble() < 0.3)
                                                continue;

                                        // Generate realistic sales pattern
                                        int baseSales = getBaseSalesForProduct(produit);

                                        // Weekend boost for consumer products
                                        if (saleDate.getDayOfWeek().getValue() >= 6) {
                                                baseSales = (int) (baseSales * 1.3);
                                        }

                                        // Add some randomness
                                        int quantity = Math.max(1,
                                                        baseSales + random.nextInt(baseSales / 2 + 1) - baseSales / 4);

                                        HistoriqueVente vente = HistoriqueVente.builder()
                                                        .produit(produit)
                                                        .entrepot(entrepot)
                                                        .dateVente(saleDate)
                                                        .quantiteVendue(quantity)
                                                        .build();
                                        historiqueVenteRepository.save(vente);
                                        salesCount++;
                                }
                        }
                }

                log.info("✅ Created {} sales records", salesCount);
        }

        private int getBaseSalesForProduct(Produit produit) {
                // Different products have different sales volumes
                String categorie = produit.getCategorie();

                return switch (categorie) {
                        case "Boissons" -> 8 + random.nextInt(10);
                        case "Alimentaire" -> 5 + random.nextInt(8);
                        case "Produits Laitiers" -> 10 + random.nextInt(15);
                        case "Conserves" -> 3 + random.nextInt(5);
                        case "Hygiène" -> 4 + random.nextInt(6);
                        case "Entretien" -> 2 + random.nextInt(4);
                        default -> 5 + random.nextInt(5);
                };
        }

        private Entrepot createEntrepot(String nom, String adresse, String ville, String codePostal) {
                Entrepot entrepot = Entrepot.builder()
                                .nom(nom)
                                .adresse(adresse)
                                .ville(ville)
                                .codePostal(codePostal)
                                .actif(true)
                                .build();
                return entrepotRepository.save(entrepot);
        }

        private Produit createProduit(String nom, String description, String categorie,
                        BigDecimal prixVente, BigDecimal prixAchat, Unite unite) {
                Produit produit = Produit.builder()
                                .nom(nom)
                                .description(description)
                                .categorie(categorie)
                                .prixVente(prixVente)
                                .unite(unite)
                                .poids(1.0)
                                .actif(true)
                                .build();
                produit.setPrixAchatValue(prixAchat);
                produit.setMargeValue(prixVente.subtract(prixAchat));
                return produitRepository.save(produit);
        }

        private User createUser(String login, String password, String nom, String prenom,
                        String email, Role role, Entrepot entrepot) {
                User user = User.builder()
                                .login(login)
                                .password(passwordEncoder.encode(password))
                                .nom(nom)
                                .prenom(prenom)
                                .email(email)
                                .role(role)
                                .actif(true)
                                .entrepotAssigne(entrepot)
                                .build();
                return userRepository.save(user);
        }

        private void printSummary(List<Entrepot> entrepots, List<Produit> produits) {
                log.info("");
                log.info("═══════════════════════════════════════════════════════════════");
                log.info("✅ DATABASE INITIALIZATION COMPLETED!");
                log.info("═══════════════════════════════════════════════════════════════");
                log.info("");
                log.info("📦 WAREHOUSES ({}):", entrepots.size());
                for (int i = 0; i < entrepots.size(); i++) {
                        log.info("   {}. {} ({})", i + 1, entrepots.get(i).getNom(), entrepots.get(i).getVille());
                }
                log.info("");
                log.info("🛒 PRODUCTS: {} products across {} categories", produits.size(),
                                produits.stream().map(Produit::getCategorie).distinct().count());
                log.info("");
                log.info("👥 USERS:");
                log.info("   ┌──────────────────┬───────────┬──────────────────────────┐");
                log.info("   │ Login            │ Password  │ Role / Warehouse         │");
                log.info("   ├──────────────────┼───────────┼──────────────────────────┤");
                log.info("   │ admin            │ admin123  │ ADMIN (all warehouses)   │");
                log.info("   │ gest_paris_nord  │ gest123   │ GESTIONNAIRE (Paris N)   │");
                log.info("   │ gest_paris_sud   │ gest123   │ GESTIONNAIRE (Paris S)   │");
                log.info("   │ gest_lyon        │ gest123   │ GESTIONNAIRE (Lyon)      │");
                log.info("   │ gest_marseille   │ gest123   │ GESTIONNAIRE (Marseille) │");
                log.info("   │ gest_bordeaux    │ gest123   │ GESTIONNAIRE (Bordeaux)  │");
                log.info("   └──────────────────┴───────────┴──────────────────────────┘");
                log.info("");
                log.info("📊 STOCKS: {} entries (some at critical/alert levels)", entrepots.size() * produits.size());
                log.info("📈 SALES HISTORY: ~90 days of data for AI predictions");
                log.info("");
                log.info("🌐 ACCESS POINTS:");
                log.info("   • Swagger UI: http://localhost:8080/swagger-ui.html");
                log.info("   • H2 Console: http://localhost:8080/h2-console (dev profile)");
                log.info("");
                log.info("🤖 AI PREDICTIONS: Ready! Call POST /api/previsions/entrepot/{id}/generate-all");
                log.info("═══════════════════════════════════════════════════════════════");
        }
}
