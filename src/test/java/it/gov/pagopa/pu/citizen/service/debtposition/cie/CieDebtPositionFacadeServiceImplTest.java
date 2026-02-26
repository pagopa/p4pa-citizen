package it.gov.pagopa.pu.citizen.service.debtposition.cie;

import it.gov.pagopa.pu.cie.dto.generated.DebtPositionCieRequestDTO;
import it.gov.pagopa.pu.citizen.connector.cie.CieDebtPositionService;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionRequestDTO;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionResponseDTO;
import it.gov.pagopa.pu.citizen.mapper.DebtPositionResponseDTOMapper;
import it.gov.pagopa.pu.citizen.mapper.cie.DebtPositionCieRequestDTOMapper;
import it.gov.pagopa.pu.citizen.service.debtpositiontypeorg.DebtPositionTypeOrgRetrieverService;
import it.gov.pagopa.pu.citizen.utils.TestUtils;
import it.gov.pagopa.pu.debtpositions.dto.generated.DebtPositionDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.jemos.podam.api.PodamFactory;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

@ExtendWith(MockitoExtension.class)
class CieDebtPositionFacadeServiceImplTest {
  @Mock
  private CieDebtPositionService cieDebtPositionServiceMock;
  @Mock
  private DebtPositionCieRequestDTOMapper debtPositionCieRequestDTOMapperMock;
  @Mock
  private DebtPositionTypeOrgRetrieverService debtPositionTypeOrgRetrieverServiceMock;
  @Mock
  private DebtPositionResponseDTOMapper debtPositionResponseDTOMapperMock;

  private CieDebtPositionFacadeService cieDebtPositionFacadeService;

  private final PodamFactory podamFactory = TestUtils.getPodamFactory();

  private final String accessToken = "TOKEN";

  @BeforeEach
  void setUp() {
    cieDebtPositionFacadeService = new CieDebtPositionFacadeServiceImpl(
      cieDebtPositionServiceMock,
      debtPositionCieRequestDTOMapperMock,
      debtPositionTypeOrgRetrieverServiceMock,
      debtPositionResponseDTOMapperMock
    );
  }

  @AfterEach
  void verifyNoMoreInteractions() {
    Mockito.verifyNoMoreInteractions(
      cieDebtPositionServiceMock,
      debtPositionCieRequestDTOMapperMock,
      debtPositionTypeOrgRetrieverServiceMock,
      debtPositionResponseDTOMapperMock
    );
  }

  @Test
  void whenCreateSpontaneousDebtPositionThenOk() {
    DebtPositionRequestDTO requestDTO = podamFactory.manufacturePojo(DebtPositionRequestDTO.class);
    DebtPositionCieRequestDTO debtPositionCieRequestDTO = podamFactory.manufacturePojo(DebtPositionCieRequestDTO.class);
    DebtPositionDTO debtPosition = podamFactory.manufacturePojo(DebtPositionDTO.class);
    DebtPositionResponseDTO expectedResult = podamFactory.manufacturePojo(DebtPositionResponseDTO.class);
    String debtPositionTypeOrgCode = "debtPositionTypeOrgCode";

    Mockito.when(debtPositionTypeOrgRetrieverServiceMock.getDebtPositionTypeOrgCode(requestDTO.getDebtPositionTypeOrgId(), requestDTO.getOrganizationId(),accessToken)).thenReturn(debtPositionTypeOrgCode);
    Mockito.when(debtPositionCieRequestDTOMapperMock.map(requestDTO,debtPositionTypeOrgCode)).thenReturn(debtPositionCieRequestDTO);
    Mockito.when(cieDebtPositionServiceMock.createDebtPositionCie(debtPositionCieRequestDTO)).thenReturn(debtPosition);
    Mockito.when(debtPositionResponseDTOMapperMock.map(debtPosition, null, true)).thenReturn(expectedResult);


    DebtPositionResponseDTO result = cieDebtPositionFacadeService.createSpontaneousDebtPosition( requestDTO, accessToken);

    assertNotNull(result);
    assertSame(expectedResult, result);
  }
}
