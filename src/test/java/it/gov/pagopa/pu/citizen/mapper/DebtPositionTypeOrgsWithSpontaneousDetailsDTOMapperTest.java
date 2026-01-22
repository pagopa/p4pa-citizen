package it.gov.pagopa.pu.citizen.mapper;

import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionTypeOrgsWithSpontaneousDetailsDTO;
import it.gov.pagopa.pu.citizen.utils.TestUtils;
import it.gov.pagopa.pu.debtpositions.dto.generated.DebtPositionTypeOrg;
import it.gov.pagopa.pu.debtpositions.dto.generated.SpontaneousForm;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mapstruct.factory.Mappers;
import uk.co.jemos.podam.api.PodamFactory;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class DebtPositionTypeOrgsWithSpontaneousDetailsDTOMapperTest {

  DebtPositionTypeOrgsWithSpontaneousDetailsDTOMapper mapper = Mappers.getMapper(DebtPositionTypeOrgsWithSpontaneousDetailsDTOMapper.class);

  private final PodamFactory podamFactory = TestUtils.getPodamFactory();

  @Test
  void givenDebtPositionTypeOrgAndSpontaneousFormWhenMapThenFieldsAreMappedCorrectly() {
    // given
    DebtPositionTypeOrg debtPositionTypeOrg = podamFactory.manufacturePojo(DebtPositionTypeOrg.class);
    debtPositionTypeOrg.setAmountCents(null);
    debtPositionTypeOrg.setExternalPaymentUrl(null);
    debtPositionTypeOrg.setSpontaneousFormId(null);
    debtPositionTypeOrg.setFlagAnonymousFiscalCode(false);

    SpontaneousForm spontaneousForm = podamFactory.manufacturePojo(SpontaneousForm.class);
    // when
    DebtPositionTypeOrgsWithSpontaneousDetailsDTO result = mapper.map(debtPositionTypeOrg, spontaneousForm);

    // then
    assertNotNull(result);
    assertEquals(debtPositionTypeOrg.getCode(), result.getCode());
    assertEquals(debtPositionTypeOrg.getOrganizationId(), result.getOrganizationId());
    assertEquals(spontaneousForm, result.getFormCustom());
    assertFalse(result.getFlagAnonymousFiscalCode());
    assertEquals(DebtPositionTypeOrgsWithSpontaneousDetailsDTO.FormTypeEnum.STANDARD, result.getFormType());
    TestUtils.checkNotNullFields(result, "amountCents", "externalPaymentUrl", "spontaneousFormId");
  }



  @ParameterizedTest
  @MethodSource("debtPositionsTypeOrgSource")
  void givenDebtPositionTypeOrgWhenCalculateFormTypeThenCorrectEnumOrException( DebtPositionTypeOrg org, DebtPositionTypeOrgsWithSpontaneousDetailsDTO.FormTypeEnum expected) {

    if (expected != null){
      DebtPositionTypeOrgsWithSpontaneousDetailsDTO.FormTypeEnum formTypeEnum = mapper.calculateFormType(org);
      assertEquals(expected, formTypeEnum);
    }else {
      IllegalStateException ex = assertThrows(IllegalStateException.class, () -> mapper.calculateFormType(org));
      assertEquals("[INVALID_DEBT_POSITION_TYPE_ORG] Invalid combination of fields in DebtPositionTypeOrg", ex.getMessage());
    }

  }

  static Stream<Arguments> debtPositionsTypeOrgSource() {
    PodamFactory localPodamFactory = TestUtils.getPodamFactory();
    DebtPositionTypeOrg standard = localPodamFactory.manufacturePojo(DebtPositionTypeOrg.class);
    standard.setSpontaneousFormId(null);
    standard.setAmountCents(null);
    standard.setExternalPaymentUrl(null);

    DebtPositionTypeOrg presetAmount = localPodamFactory.manufacturePojo(DebtPositionTypeOrg.class);
    presetAmount.setAmountCents(1000L);
    presetAmount.setExternalPaymentUrl(null);
    presetAmount.setSpontaneousFormId(null);

    DebtPositionTypeOrg externalUrl = localPodamFactory.manufacturePojo(DebtPositionTypeOrg.class);
    externalUrl.setExternalPaymentUrl("https://pay.me");
    externalUrl.setAmountCents(null);
    externalUrl.setSpontaneousFormId(null);

    DebtPositionTypeOrg custom = localPodamFactory.manufacturePojo(DebtPositionTypeOrg.class);
    custom.setAmountCents(null);
    custom.setExternalPaymentUrl(null);
    custom.setSpontaneousFormId(1L);

    DebtPositionTypeOrg customAndAmount = localPodamFactory.manufacturePojo(DebtPositionTypeOrg.class);
    customAndAmount.setAmountCents(100L);
    customAndAmount.setExternalPaymentUrl(null);
    customAndAmount.setSpontaneousFormId(1L);

    DebtPositionTypeOrg customAndAmountInvalid = localPodamFactory.manufacturePojo(DebtPositionTypeOrg.class);
    customAndAmountInvalid.setAmountCents(100L);
    customAndAmountInvalid.setExternalPaymentUrl("https://x");
    customAndAmountInvalid.setSpontaneousFormId(1L);

    DebtPositionTypeOrg invalid = localPodamFactory.manufacturePojo(DebtPositionTypeOrg.class);
    invalid.setAmountCents(100L);
    invalid.setExternalPaymentUrl("https://x");

    return Stream.of(
      Arguments.of(standard, DebtPositionTypeOrgsWithSpontaneousDetailsDTO.FormTypeEnum.STANDARD),
      Arguments.of(presetAmount, DebtPositionTypeOrgsWithSpontaneousDetailsDTO.FormTypeEnum.PRESET_AMOUNT),
      Arguments.of(externalUrl, DebtPositionTypeOrgsWithSpontaneousDetailsDTO.FormTypeEnum.EXTERNAL_URL),
      Arguments.of(custom, DebtPositionTypeOrgsWithSpontaneousDetailsDTO.FormTypeEnum.CUSTOM),
      Arguments.of(customAndAmount, DebtPositionTypeOrgsWithSpontaneousDetailsDTO.FormTypeEnum.CUSTOM),
      Arguments.of(customAndAmountInvalid, null),
      Arguments.of(invalid, null)
    );
  }
}
