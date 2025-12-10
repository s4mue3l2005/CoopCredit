package com.coopcredit.core;

import com.coopcredit.core.infrastructure.adapter.out.persistence.entity.AffiliateEntity;
import com.coopcredit.core.infrastructure.adapter.out.persistence.repository.JpaAffiliateRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@DisplayName("Affiliate Update Integration Tests")
class AffiliateUpdateIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JpaAffiliateRepository affiliateRepository;

    private Long affiliateId;

    @BeforeEach
    void setUp() {
        // Create a test affiliate
        AffiliateEntity affiliate = AffiliateEntity.builder()
                .name("Original Name")
                .email("original@example.com")
                .document("DOC-UPDATE-001")
                .salary(new BigDecimal("5000.00"))
                .active(true)
                .build();
        AffiliateEntity saved = affiliateRepository.save(affiliate);
        affiliateId = saved.getId();
    }

    @Test
    @DisplayName("Should update affiliate successfully")
    @WithMockUser
    void shouldUpdateAffiliateSuccessfully() throws Exception {
        // Arrange
        UpdateAffiliateRequest request = new UpdateAffiliateRequest();
        request.name = "Updated Name";
        request.email = "updated@example.com";
        request.salary = new BigDecimal("6000.00");

        // Act & Assert
        mockMvc.perform(put("/api/affiliates/" + affiliateId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.email").value("updated@example.com"))
                .andExpect(jsonPath("$.salary").value(6000.00));

        // Verify database
        Optional<AffiliateEntity> updated = affiliateRepository.findById(affiliateId);
        assertTrue(updated.isPresent());
        assertEquals("Updated Name", updated.get().getName());
        assertEquals("updated@example.com", updated.get().getEmail());
        assertEquals(new BigDecimal("6000.00"), updated.get().getSalary());
    }

    @Test
    @DisplayName("Should perform partial update with only name")
    @WithMockUser
    void shouldPerformPartialUpdateWithOnlyName() throws Exception {
        // Arrange
        UpdateAffiliateRequest request = new UpdateAffiliateRequest();
        request.name = "Partially Updated Name";

        // Act & Assert
        mockMvc.perform(put("/api/affiliates/" + affiliateId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Partially Updated Name"))
                .andExpect(jsonPath("$.email").value("original@example.com")) // Unchanged
                .andExpect(jsonPath("$.salary").value(5000.00)); // Unchanged
    }

    @Test
    @DisplayName("Should return 404 when affiliate not found")
    @WithMockUser
    void shouldReturn404WhenAffiliateNotFound() throws Exception {
        // Arrange
        UpdateAffiliateRequest request = new UpdateAffiliateRequest();
        request.name = "Updated Name";
        Long nonExistentId = 99999L;

        // Act & Assert
        mockMvc.perform(put("/api/affiliates/" + nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 when salary is zero")
    @WithMockUser
    void shouldReturn400WhenSalaryIsZero() throws Exception {
        // Arrange
        UpdateAffiliateRequest request = new UpdateAffiliateRequest();
        request.salary = BigDecimal.ZERO;

        // Act & Assert
        mockMvc.perform(put("/api/affiliates/" + affiliateId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 when salary is negative")
    @WithMockUser
    void shouldReturn400WhenSalaryIsNegative() throws Exception {
        // Arrange
        UpdateAffiliateRequest request = new UpdateAffiliateRequest();
        request.salary = new BigDecimal("-1000.00");

        // Act & Assert
        mockMvc.perform(put("/api/affiliates/" + affiliateId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 when email format is invalid")
    @WithMockUser
    void shouldReturn400WhenEmailFormatInvalid() throws Exception {
        // Arrange
        UpdateAffiliateRequest request = new UpdateAffiliateRequest();
        request.email = "invalid-email";

        // Act & Assert
        mockMvc.perform(put("/api/affiliates/" + affiliateId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    static class UpdateAffiliateRequest {
        public String name;
        public String email;
        public String address;
        public String phone;
        public BigDecimal salary;
    }
}

