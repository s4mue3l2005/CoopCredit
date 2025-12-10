package com.coopcredit.core;

import com.coopcredit.core.domain.model.Affiliate;
import com.coopcredit.core.domain.model.Credit;
import com.coopcredit.core.domain.model.CreditStatus;
import com.coopcredit.core.domain.port.out.AffiliateRepositoryPort;
import com.coopcredit.core.domain.port.out.CreditRepositoryPort;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@DisplayName("Credit Evaluation Integration Tests")
@org.springframework.transaction.annotation.Transactional
class CreditEvaluationIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AffiliateRepositoryPort affiliateRepositoryPort;

    @Autowired
    private CreditRepositoryPort creditRepositoryPort;

    private Long creditId;

    @BeforeEach
    void setUp() {
        // Create test affiliate
        Affiliate affiliate = Affiliate.builder()
                .name("Test Affiliate")
                .email("test@example.com")
                .document("DOC-EVAL-001")
                .salary(new BigDecimal("5000.00"))
                .active(true)
                .build();
        Affiliate savedAffiliate = affiliateRepositoryPort.save(affiliate);

        // Create PENDING credit
        Credit credit = Credit.builder()
                .affiliate(savedAffiliate)
                .amount(new BigDecimal("10000.00"))
                .term(12)
                .status(CreditStatus.PENDING)
                .rationale("Initial application")
                .build();
        Credit savedCredit = creditRepositoryPort.save(credit);
        creditId = savedCredit.getId();
    }

    @Test
    @DisplayName("Should evaluate credit as APPROVED (ANALYST role)")
    @WithMockUser(roles = "ANALYST")
    void shouldEvaluateCreditAsApprovedWithAnalystRole() throws Exception {
        // Arrange
        EvaluateCreditRequest request = new EvaluateCreditRequest();
        request.status = "APPROVED";
        request.rationale = "Credit approved after thorough review. Risk assessment acceptable.";

        // Act & Assert
        mockMvc.perform(patch("/api/credits/" + creditId + "/evaluate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"))
                .andExpect(jsonPath("$.rationale")
                        .value("Credit approved after thorough review. Risk assessment acceptable."));

        // Verify database
        Optional<Credit> updated = creditRepositoryPort.findById(creditId);
        assertTrue(updated.isPresent());
        assertEquals(CreditStatus.APPROVED, updated.get().getStatus());
    }

    @Test
    @DisplayName("Should evaluate credit as REJECTED (ADMIN role)")
    @WithMockUser(roles = "ADMIN")
    void shouldEvaluateCreditAsRejectedWithAdminRole() throws Exception {
        // Arrange
        EvaluateCreditRequest request = new EvaluateCreditRequest();
        request.status = "REJECTED";
        request.rationale = "Credit rejected due to insufficient income verification.";

        // Act & Assert
        mockMvc.perform(patch("/api/credits/" + creditId + "/evaluate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REJECTED"))
                .andExpect(jsonPath("$.rationale").value("Credit rejected due to insufficient income verification."));

        // Verify database
        Optional<Credit> updated = creditRepositoryPort.findById(creditId);
        assertTrue(updated.isPresent());
        assertEquals(CreditStatus.REJECTED, updated.get().getStatus());
    }

    @Test
    @DisplayName("Should return 403 when user is AFFILIATE (unauthorized)")
    @WithMockUser(roles = "AFFILIATE")
    void shouldReturn403WhenUserIsAffiliate() throws Exception {
        // Arrange
        EvaluateCreditRequest request = new EvaluateCreditRequest();
        request.status = "APPROVED";
        request.rationale = "Rationale";

        // Act & Assert
        mockMvc.perform(patch("/api/credits/" + creditId + "/evaluate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should return 400 when credit is not PENDING")
    @WithMockUser(roles = "ANALYST")
    void shouldReturn400WhenCreditIsNotPending() throws Exception {
        // Arrange - Update credit to APPROVED first
        Optional<Credit> credit = creditRepositoryPort.findById(creditId);
        assertTrue(credit.isPresent());
        credit.get().setStatus(CreditStatus.APPROVED);
        creditRepositoryPort.save(credit.get());

        EvaluateCreditRequest request = new EvaluateCreditRequest();
        request.status = "APPROVED";
        request.rationale = "Rationale";

        // Act & Assert
        mockMvc.perform(patch("/api/credits/" + creditId + "/evaluate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Should return 400 when credit not found")
    @WithMockUser(roles = "ANALYST")
    void shouldReturn400WhenCreditNotFound() throws Exception {
        // Arrange
        EvaluateCreditRequest request = new EvaluateCreditRequest();
        request.status = "APPROVED";
        request.rationale = "Rationale";
        Long nonExistentId = 99999L;

        // Act & Assert
        mockMvc.perform(patch("/api/credits/" + nonExistentId + "/evaluate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 when status is invalid")
    @WithMockUser(roles = "ANALYST")
    void shouldReturn400WhenStatusIsInvalid() throws Exception {
        // Arrange
        EvaluateCreditRequest request = new EvaluateCreditRequest();
        request.status = "INVALID_STATUS";
        request.rationale = "Rationale";

        // Act & Assert
        mockMvc.perform(patch("/api/credits/" + creditId + "/evaluate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 when rationale is blank")
    @WithMockUser(roles = "ANALYST")
    void shouldReturn400WhenRationaleIsBlank() throws Exception {
        // Arrange
        EvaluateCreditRequest request = new EvaluateCreditRequest();
        request.status = "APPROVED";
        request.rationale = "   "; // Blank

        // Act & Assert
        mockMvc.perform(patch("/api/credits/" + creditId + "/evaluate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should normalize status to uppercase")
    @WithMockUser(roles = "ANALYST")
    void shouldNormalizeStatusToUppercase() throws Exception {
        // Arrange
        EvaluateCreditRequest request = new EvaluateCreditRequest();
        request.status = "approved"; // Lowercase
        request.rationale = "Rationale";

        // Act & Assert
        mockMvc.perform(patch("/api/credits/" + creditId + "/evaluate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    static class EvaluateCreditRequest {
        public String status;
        public String rationale;
    }
}
