package it.gov.pagopa.pu.citizen.mapper;


import it.gov.pagopa.pu.citizen.dto.DebtPositionExtendedDTO;
import it.gov.pagopa.pu.citizen.dto.InstallmentExtendedDTO;
import it.gov.pagopa.pu.citizen.dto.PaymentOptionExtendedDTO;
import it.gov.pagopa.pu.citizen.utils.InstallmentUtils;
import it.gov.pagopa.pu.debtpositions.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.debtpositions.dto.generated.InstallmentDTO;
import it.gov.pagopa.pu.debtpositions.dto.generated.PaymentOptionDTO;
import it.gov.pagopa.pu.debtpositions.dto.generated.PostalIbanVerifyResponse;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", imports = {InstallmentUtils.class})
public interface DebtPositionExtendedDTOMapper {

  DebtPositionExtendedDTO map(DebtPositionDTO debtPositionDTO, @Context PostalIbanVerifyResponse postalIbanVerifyResponse);

  PaymentOptionExtendedDTO map(PaymentOptionDTO paymentOptionDTO, @Context PostalIbanVerifyResponse postalIbanVerifyResponse);

  @Mapping(target = "allCCP", expression = "java(postalIbanVerifyResponse != null ? InstallmentUtils.extractAllCCP(installmentDTO.getInstallmentId(), postalIbanVerifyResponse): null)")
  InstallmentExtendedDTO map(InstallmentDTO installmentDTO, @Context PostalIbanVerifyResponse postalIbanVerifyResponse);
}
