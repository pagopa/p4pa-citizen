package it.gov.pagopa.pu.citizen.mapper;

import it.gov.pagopa.pu.citizen.dto.InstallmentDebtorExtendedDTO;
import it.gov.pagopa.pu.citizen.utils.InstallmentUtils;
import it.gov.pagopa.pu.citizen.utils.TestUtils;
import it.gov.pagopa.pu.debtpositions.dto.generated.InstallmentDebtorDTO;
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

import static org.junit.jupiter.api.Assertions.*;

class InstallmentDebtorExtendedDTOMapperTest {

  private final InstallmentDebtorExtendedDTOMapper mapper =  Mappers.getMapper(InstallmentDebtorExtendedDTOMapper.class);

  private final PodamFactory podam = TestUtils.getPodamFactory();

  @Test
  void whenMapThenOk() {
    List<InstallmentDebtorDTO> installments = podam.manufacturePojo(List.class, InstallmentDebtorDTO.class);
    Map<Long, InstallmentDebtorDTO> installmentMap = installments.stream().collect(Collectors.toMap(InstallmentDebtorDTO::getInstallmentId, Function.identity()));
    Map<Long, Organization> organizationMap = new HashMap<>();
    PostalIbanVerifyResponse postalIbanVerifyResponse = podam.manufacturePojo(PostalIbanVerifyResponse.class);

    try(MockedStatic<InstallmentUtils> installmentUtilsMock = Mockito.mockStatic(InstallmentUtils.class)) {
      for (InstallmentDebtorDTO installment : installments) {
        organizationMap.put(installment.getOrganizationId(),podam.manufacturePojo(Organization.class));
        installmentUtilsMock.when(()->InstallmentUtils.resolveInstallmentStatus(installment.getStatus())).thenReturn(installment.getStatus());
        installmentUtilsMock.when(()-> InstallmentUtils.extractAllCCP(installment.getInstallmentId(), postalIbanVerifyResponse)).thenReturn(true);
      }

      List<InstallmentDebtorExtendedDTO> result = mapper.map(installments, organizationMap, postalIbanVerifyResponse);

      assertNotNull(result);
      assertEquals(installments.size(), result.size());
      for (InstallmentDebtorExtendedDTO installmentDebtorDTO : result) {
        TestUtils.checkNotNullFields(installmentDebtorDTO);
        Long installmentId = installmentDebtorDTO.getInstallmentId();
        assertTrue(installmentMap.containsKey(installmentId));
        TestUtils.reflectionEqualsByName(installmentMap.get(installmentId), installmentDebtorDTO);
        Long organizationId = installmentDebtorDTO.getOrganizationId();
        assertTrue(organizationMap.containsKey(organizationId));
        assertEquals(organizationMap.get(organizationId).getOrgName(), installmentDebtorDTO.getOrgName());
        assertEquals(organizationMap.get(organizationId).getOrgFiscalCode(), installmentDebtorDTO.getOrgFiscalCode());
        assertEquals(Boolean.TRUE, installmentDebtorDTO.getAllCCP());
      }
      installmentUtilsMock.verify(()->InstallmentUtils.resolveInstallmentStatus(Mockito.any()), Mockito.times(installments.size()));
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
  void givenNullPostalIbanVerifyResponseWhenMapThenAllCcpIsNull() {
    // given
    List<InstallmentDebtorDTO> installments = podam.manufacturePojo(List.class, InstallmentDebtorDTO.class);

    Map<Long, Organization> organizationMap = new HashMap<>();

    for (InstallmentDebtorDTO installment : installments) {
      organizationMap.put(
        installment.getOrganizationId(),
        podam.manufacturePojo(Organization.class)
      );
    }

    try (MockedStatic<InstallmentUtils> installmentUtilsMock = Mockito.mockStatic(InstallmentUtils.class)) {

      for (InstallmentDebtorDTO installment : installments) {
        installmentUtilsMock.when(() ->
          InstallmentUtils.resolveInstallmentStatus(installment.getStatus())
        ).thenReturn(installment.getStatus());
      }

      // when
      List<InstallmentDebtorExtendedDTO> result =
        mapper.map(installments, organizationMap, null);

      // then
      assertNotNull(result);
      assertEquals(installments.size(), result.size());

      for (InstallmentDebtorExtendedDTO dto : result) {
        TestUtils.checkNotNullFields(dto, "allCCP");

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

        assertNull(dto.getAllCCP());
      }

      installmentUtilsMock.verify(() ->
          InstallmentUtils.resolveInstallmentStatus(Mockito.any()),
        Mockito.times(installments.size())
      );
    }
  }
}
