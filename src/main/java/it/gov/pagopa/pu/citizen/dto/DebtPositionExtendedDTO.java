package it.gov.pagopa.pu.citizen.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DebtPositionExtendedDTO {

  private Long debtPositionId;

  private String iupdOrg;

  private String description;

  private String status;

  private String debtPositionOrigin;

  private Long organizationId;

  private Long debtPositionTypeOrgId;

  private LocalDate validityDate;

  private Boolean multiDebtor;

  private Boolean flagPuPagoPaPayment;

  private OffsetDateTime creationDate;

  private OffsetDateTime updateDate;

  private String updateOperatorExternalId;

  private String updateTraceId;

  private List<PaymentOptionExtendedDTO> paymentOptions;
}
