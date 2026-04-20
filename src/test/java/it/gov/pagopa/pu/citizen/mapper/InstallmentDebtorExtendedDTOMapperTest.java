package it.gov.pagopa.pu.citizen.mapper;

import it.gov.pagopa.pu.citizen.dto.InstallmentDebtorExtendedDTO;
import it.gov.pagopa.pu.citizen.utils.InstallmentUtils;
import it.gov.pagopa.pu.citizen.utils.TestUtils;
import it.gov.pagopa.pu.debtpositions.dto.generated.InstallmentDebtorDTO;
import it.gov.pagopa.pu.debtpositions.dto.generated.InstallmentStatus;
import it.gov.pagopa.pu.debtpositions.dto.generated.PostalIbanVerifyResponse;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import uk.co.jemos.podam.api.PodamFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static it.gov.pagopa.pu.debtpositions.dto.generated.InstallmentStatus.PAID;
import static it.gov.pagopa.pu.debtpositions.dto.generated.InstallmentStatus.REPORTED;
import static org.junit.jupiter.api.Assertions.*;

class InstallmentDebtorExtendedDTOMapperTest {

  private final InstallmentDebtorExtendedDTOMapper mapper =  Mappers.getMapper(InstallmentDebtorExtendedDTOMapper.class);

  private final PodamFactory podam = TestUtils.getPodamFactory();

  @Test
  void whenMapThenOk() {
    List<InstallmentDebtorDTO> installments =
      podam.manufacturePojo(List.class, InstallmentDebtorDTO.class);

    Map<Long, InstallmentDebtorDTO> installmentMap =
      installments.stream().collect(Collectors.toMap(
        InstallmentDebtorDTO::getInstallmentId,
        Function.identity()
      ));

    Map<Long, Organization> organizationMap = new HashMap<>();
    PostalIbanVerifyResponse postalIbanVerifyResponse =
      podam.manufacturePojo(PostalIbanVerifyResponse.class);

    try (MockedStatic<InstallmentUtils> installmentUtilsMock =
           Mockito.mockStatic(InstallmentUtils.class)) {

      for (InstallmentDebtorDTO installment : installments) {

        organizationMap.put(
          installment.getOrganizationId(),
          podam.manufacturePojo(Organization.class)
        );

        installmentUtilsMock.when(() ->
          InstallmentUtils.resolveInstallmentStatus(installment.getStatus())
        ).thenReturn(installment.getStatus());

        installmentUtilsMock.when(() ->
          InstallmentUtils.extractAllCCP(
            installment.getInstallmentId(),
            postalIbanVerifyResponse
          )
        ).thenReturn(true);
      }

      // when
      List<InstallmentDebtorExtendedDTO> result =
        mapper.map(installments, organizationMap, postalIbanVerifyResponse);

      // then
      assertNotNull(result);
      assertEquals(installments.size(), result.size());

      for (InstallmentDebtorExtendedDTO dto : result) {

        boolean isExcluded =
          dto.getStatus() == InstallmentStatus.PAID
            || dto.getStatus() == InstallmentStatus.REPORTED;

        if (isExcluded) {
          assertNull(dto.getAllCCP());
        } else {
          assertEquals(Boolean.TRUE, dto.getAllCCP());
        }

        TestUtils.checkNotNullFields(
          dto,
          isExcluded ? "allCCP" : null
        );

        Long installmentId = dto.getInstallmentId();
        assertTrue(installmentMap.containsKey(installmentId));

        TestUtils.reflectionEqualsByName(
          installmentMap.get(installmentId),
          dto
        );

        Long organizationId = dto.getOrganizationId();

        assertTrue(organizationMap.containsKey(organizationId));

        assertEquals(
          organizationMap.get(organizationId).getOrgName(),
          dto.getOrgName()
        );

        assertEquals(
          organizationMap.get(organizationId).getOrgFiscalCode(),
          dto.getOrgFiscalCode()
        );
      }

      installmentUtilsMock.verify(() ->
          InstallmentUtils.resolveInstallmentStatus(Mockito.any()),
        Mockito.times(installments.size())
      );
    }
  }

  @Test
  void givenNoMatchingOrganizationWhenMapThenIllegalStateException() {
    InstallmentDebtorDTO installment = podam.manufacturePojo(InstallmentDebtorDTO.class);
    installment.setInstallmentId(1L);
    List<InstallmentDebtorDTO> installments = List.of(installment);
    Map<Long,Organization> organizationMap = new HashMap<>();
    organizationMap.put(2L,podam.manufacturePojo(Organization.class));

    assertThrows(IllegalStateException.class,()-> mapper.map(installments,organizationMap, null));
  }

  @Test
  void givenNoOrganizationMapWhenMapThenIllegalStateException() {
    InstallmentDebtorDTO installment = podam.manufacturePojo(InstallmentDebtorDTO.class);
    installment.setInstallmentId(1L);
    List<InstallmentDebtorDTO> installments = List.of(installment);

    assertThrows(IllegalArgumentException.class,()-> mapper.map(installments,null, null));
  }

  @Test
  void givenPostalIbanVerifyResponseWhenMapThenAllCcpDependsOnStatus() {
    // given
    List<InstallmentDebtorDTO> installments =
      podam.manufacturePojo(List.class, InstallmentDebtorDTO.class);

    Map<Long, Organization> organizationMap = new HashMap<>();

    for (InstallmentDebtorDTO installment : installments) {
      organizationMap.put(
        installment.getOrganizationId(),
        podam.manufacturePojo(Organization.class)
      );
    }

    InstallmentDebtorDTO first = installments.get(0);
    first.setStatus(PAID);

    InstallmentDebtorDTO second = installments.get(1);
    second.setStatus(InstallmentStatus.UNPAID);

    PostalIbanVerifyResponse response = podam.manufacturePojo(PostalIbanVerifyResponse.class);

    try (MockedStatic<InstallmentUtils> installmentUtilsMock = Mockito.mockStatic(InstallmentUtils.class)) {

      for (InstallmentDebtorDTO installment : installments) {
        installmentUtilsMock.when(() ->
          InstallmentUtils.resolveInstallmentStatus(installment.getStatus())
        ).thenReturn(installment.getStatus());

        installmentUtilsMock.when(() ->
          InstallmentUtils.extractAllCCP(installment.getInstallmentId(), response)
        ).thenReturn(true);
      }

      // when
      List<InstallmentDebtorExtendedDTO> result =
        mapper.map(installments, organizationMap, response);

      // then
      assertNotNull(result);
      assertEquals(installments.size(), result.size());

      for (InstallmentDebtorExtendedDTO dto : result) {

        TestUtils.checkNotNullFields(dto, "allCCP");

        if (dto.getStatus() == PAID
          || dto.getStatus() == REPORTED) {

          assertNull(dto.getAllCCP());

        } else {

          assertNotNull(dto.getAllCCP());
        }

        Long organizationId = dto.getOrganizationId();

        assertEquals(
          organizationMap.get(organizationId).getOrgName(),
          dto.getOrgName()
        );

        assertEquals(
          organizationMap.get(organizationId).getOrgFiscalCode(),
          dto.getOrgFiscalCode()
        );
      }

      installmentUtilsMock.verify(() ->
          InstallmentUtils.resolveInstallmentStatus(Mockito.any()),
        Mockito.times(installments.size())
      );
    }
  }
}
