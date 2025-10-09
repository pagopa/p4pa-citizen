package it.gov.pagopa.pu.citizen.connector.debtpositions;

import it.gov.pagopa.pu.citizen.connector.debtpositions.client.SpontaneousFormEntityClient;
import it.gov.pagopa.pu.citizen.utils.TestUtils;
import it.gov.pagopa.pu.debtpositions.dto.generated.SpontaneousForm;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.jemos.podam.api.PodamFactory;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SpontaneousFormServiceImplTest {

  @Mock
  private SpontaneousFormEntityClient spontaneousFormEntityClientMock;

  private final PodamFactory podamFactory = TestUtils.getPodamFactory();

  SpontaneousFormService spontaneousFormService;

  @BeforeEach
  void setUp() {
    spontaneousFormService = new SpontaneousFormServiceImpl(spontaneousFormEntityClientMock);
  }

  @AfterEach
  void mockitoVerify(){
    Mockito.verifyNoMoreInteractions(spontaneousFormEntityClientMock);
  }

  @Test
  void whenGetSpontaneousFormBySpontaneousFormIdThenInvokeClient() {
    String accessToken = "ACCESSTOKEN";
    Long spontaneousFormId = 1L;

    SpontaneousForm expectedResult = podamFactory.manufacturePojo(SpontaneousForm.class);

    when(spontaneousFormEntityClientMock.getSpontaneousForm(spontaneousFormId,accessToken))
      .thenReturn(expectedResult);

    SpontaneousForm result = spontaneousFormService.getSpontaneousForm(spontaneousFormId, accessToken);

    assertSame(expectedResult, result);
  }
}
