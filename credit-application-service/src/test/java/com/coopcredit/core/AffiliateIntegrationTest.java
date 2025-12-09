package com.coopcredit.core;

import com.coopcredit.core.infrastructure.adapter.out.persistence.entity.AffiliateEntity;
import com.coopcredit.core.infrastructure.adapter.out.persistence.repository.JpaAffiliateRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration Test for Affiliate Use Cases.
 * <p>
 * This class inherits the Testcontainers configuration from {@link BaseIntegrationTest},
 * satisfying the requirement to use a real PostgreSQL database.
 */
@AutoConfigureMockMvc
class AffiliateIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JpaAffiliateRepository affiliateRepository;

    @Test
    @DisplayName("Should register a new affiliate successfully (HTTP 201 + DB Verify)")
    void shouldRegisterNewAffiliateSuccessfully() throws Exception {
        // Arrange
        String document = "DOC-INTEGRATION-001";
        RegisterAffiliatePayload payload = new RegisterAffiliatePayload(
                "Integration User",
                "integration@coopcredit.com",
                document,
                new BigDecimal("4500.00")
        );

        // Act
        mockMvc.perform(post("/api/affiliates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isCreated());

        // Assert (Database)
        Optional<AffiliateEntity> savedAffiliate = affiliateRepository.findByDocument(document);
        assertTrue(savedAffiliate.isPresent(), "Affiliate should be present in the database");
        assertEquals("Integration User", savedAffiliate.get().getName());
        assertEquals(new BigDecimal("4500.00"), savedAffiliate.get().getSalary());
    }

    /**
     * Inner DTO for the test payload to avoid dependency on main code DTOs if desired,
     * or to ensure clear test data structure.
     */
    static class RegisterAffiliatePayload {
        public String name;
        public String email;
        public String document;
        public BigDecimal salary;

        public RegisterAffiliatePayload(String name, String email, String document, BigDecimal salary) {
            this.name = name;
            this.email = email;
            this.document = document;
            this.salary = salary;
        }
    }
}
