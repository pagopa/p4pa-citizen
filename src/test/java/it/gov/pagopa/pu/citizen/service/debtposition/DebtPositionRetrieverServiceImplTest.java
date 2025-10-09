package it.gov.pagopa.pu.citizen.service.debtposition;

import it.gov.pagopa.pu.auth.dto.generated.UserInfo;
import it.gov.pagopa.pu.citizen.connector.debtpositions.DebtPositionService;
import it.gov.pagopa.pu.citizen.connector.organization.OrganizationService;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionRequestDTO;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionResponseDTO;
import it.gov.pagopa.pu.citizen.exception.ResourceNotFoundException;
import it.gov.pagopa.pu.citizen.mapper.DebtPositionDTOMapper;
import it.gov.pagopa.pu.citizen.mapper.DebtPositionResponseDTOMapper;
import it.gov.pagopa.pu.citizen.utils.TestUtils;
import it.gov.pagopa.pu.debtpositions.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.jemos.podam.api.PodamFactory;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class DebtPositionRetrieverServiceImplTest {

  @Mock
  private DebtPositionService debtPositionServiceMock;
  @Mock
  private DebtPositionDTOMapper debtPositionDTOMapperMock;
  @Mock
  private OrganizationService organizationServiceMock;
  @Mock
  private DebtPositionResponseDTOMapper debtPositionResponseDTOMapperMock;

  private DebtPositionRetrieverService debtPositionRetrieverService;

  private final PodamFactory podamFactory = TestUtils.getPodamFactory();

  private final String accessToken = "TOKEN";

  @BeforeEach
  void setUp() {
    debtPositionRetrieverService = new DebtPositionRetrieverServiceImpl(debtPositionServiceMock, debtPositionDTOMapperMock, 1, organizationServiceMock, debtPositionResponseDTOMapperMock);
  }

  @AfterEach
  void verifyNoMoreInteractions() {
    Mockito.verifyNoMoreInteractions(
      debtPositionServiceMock,
      debtPositionDTOMapperMock,
      organizationServiceMock,
      debtPositionResponseDTOMapperMock
    );
  }

  @Test
  void givenValidDebtPositionRequestDTOWhenCreateDebtPositionThenOk() {
    DebtPositionRequestDTO requestDTO = podamFactory.manufacturePojo(DebtPositionRequestDTO.class);
    UserInfo loggedUser = new UserInfo();
    loggedUser.setMappedExternalUserId("mappedExternalUserId");
    DebtPositionDTO debtPosition = podamFactory.manufacturePojo(DebtPositionDTO.class);
    Organization organization = podamFactory.manufacturePojo(Organization.class);
    DebtPositionResponseDTO expectedResult = podamFactory.manufacturePojo(DebtPositionResponseDTO.class);

    Mockito.when(debtPositionDTOMapperMock.mapSpontaneousDebtPositionDTO(requestDTO, 1)).thenReturn(debtPosition);
    Mockito.when(debtPositionServiceMock.createDebtPosition(any(), eq(false), eq(accessToken)))
        .thenReturn(debtPosition);
    Mockito.when(organizationServiceMock.getOrganizationByOrganizationId(requestDTO.getOrganizationId(), accessToken)).thenReturn(organization);
    Mockito.when(debtPositionResponseDTOMapperMock.map(debtPosition, organization)).thenReturn(expectedResult);

    DebtPositionResponseDTO result = debtPositionRetrieverService.createSpontaneousDebtPosition(requestDTO, accessToken);

    assertNotNull(result);
    assertSame(expectedResult, result);
  }

  @Test
  void givenInvalidOrganizationIdWhenCreateDebtPositionThenThrowException() {
    DebtPositionRequestDTO requestDTO = podamFactory.manufacturePojo(DebtPositionRequestDTO.class);
    UserInfo loggedUser = new UserInfo();
    loggedUser.setMappedExternalUserId("mappedExternalUserId");
    DebtPositionDTO debtPosition = podamFactory.manufacturePojo(DebtPositionDTO.class);

    Mockito.when(debtPositionDTOMapperMock.mapSpontaneousDebtPositionDTO(requestDTO, 1)).thenReturn(debtPosition);
    Mockito.when(debtPositionServiceMock.createDebtPosition(any(), eq(false), eq(accessToken)))
      .thenReturn(debtPosition);
    Mockito.when(organizationServiceMock.getOrganizationByOrganizationId(requestDTO.getOrganizationId(), accessToken)).thenReturn(null);

    ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> debtPositionRetrieverService.createSpontaneousDebtPosition(requestDTO, accessToken));
    assertEquals("Organization with id %d not found".formatted(requestDTO.getOrganizationId()), ex.getMessage());
    Mockito.verifyNoInteractions(debtPositionResponseDTOMapperMock);
  }



}
