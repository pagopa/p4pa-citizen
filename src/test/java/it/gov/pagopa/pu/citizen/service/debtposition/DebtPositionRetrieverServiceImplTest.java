package it.gov.pagopa.pu.citizen.service.debtposition;

import it.gov.pagopa.pu.auth.dto.generated.UserInfo;
import it.gov.pagopa.pu.citizen.connector.debtpositions.DebtPositionService;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionRequestDTO;
import it.gov.pagopa.pu.citizen.mapper.DebtPositionDTOMapper;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class DebtPositionRetrieverServiceImplTest {

  @Mock
  private DebtPositionService debtPositionServiceMock;
  @Mock
  private DebtPositionDTOMapper debtPositionDTOMapperMock;

  private DebtPositionRetrieverService debtPositionRetrieverService;

  private final PodamFactory podamFactory = TestUtils.getPodamFactory();

  private final String accessToken = "TOKEN";

  @BeforeEach
  void setUp() {
    debtPositionRetrieverService = new DebtPositionRetrieverServiceImpl(debtPositionServiceMock, debtPositionDTOMapperMock, 1);
  }

  @AfterEach
  void verifyNoMoreInteractions() {
    Mockito.verifyNoMoreInteractions(
      debtPositionServiceMock,
      debtPositionDTOMapperMock
    );
  }

  @Test
  void givenValidDebtPositionRequestDTOWhenCreateDebtPositionThenOk() {
    DebtPositionRequestDTO requestDTO = podamFactory.manufacturePojo(DebtPositionRequestDTO.class);
    UserInfo loggedUser = new UserInfo();
    loggedUser.setMappedExternalUserId("mappedExternalUserId");
    DebtPositionDTO expectedResult = podamFactory.manufacturePojo(DebtPositionDTO.class);

    Mockito.when(debtPositionDTOMapperMock.mapSpontaneousDebtPositionDTO(requestDTO, 1)).thenReturn(expectedResult);
    Mockito.when(debtPositionServiceMock.createDebtPosition(any(), eq(false), eq(accessToken)))
        .thenReturn(expectedResult);

    DebtPositionDTO result = debtPositionRetrieverService.createSpontaneousDebtPosition(requestDTO, accessToken);

    assertNotNull(result);
    assertSame(expectedResult, result);
  }

}
