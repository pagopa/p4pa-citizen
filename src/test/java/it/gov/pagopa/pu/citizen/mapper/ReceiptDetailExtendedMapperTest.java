package it.gov.pagopa.pu.citizen.mapper;

import it.gov.pagopa.pu.citizen.dto.ReceiptDetailExtendedDTO;
import it.gov.pagopa.pu.citizen.utils.TestUtils;
import it.gov.pagopa.pu.debtpositions.dto.generated.ReceiptDetailDTO;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.co.jemos.podam.api.PodamFactory;

class ReceiptDetailExtendedMapperTest {

  private static final PodamFactory podamFactory = TestUtils.getPodamFactory();
  ReceiptDetailExtendedMapper mapper = Mappers.getMapper(ReceiptDetailExtendedMapper.class);

  @Test
  void givenReceiptDetailAndOrganizationWhenMapThenReturnReceiptDetailExtendedDTO(){
    //given
    ReceiptDetailDTO receiptDetailDTO = podamFactory.manufacturePojo(ReceiptDetailDTO.class);
    Organization organization = podamFactory.manufacturePojo(Organization.class);
    //when
    ReceiptDetailExtendedDTO result = mapper.map(organization, receiptDetailDTO);
    //then
    Assertions.assertNotNull(result);
    Assertions.assertEquals(organization.getOrganizationId(), result.getOrganizationId());
    Assertions.assertEquals(organization.getOrgName(), result.getOrgName());
    Assertions.assertEquals(organization.getOrgFiscalCode(), result.getOrgFiscalCode());
    TestUtils.checkNotNullFields(result);
    TestUtils.reflectionEqualsByName(receiptDetailDTO,result);
  }

}
