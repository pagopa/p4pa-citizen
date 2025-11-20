package it.gov.pagopa.pu.citizen.connector.debtpositions.client;

import it.gov.pagopa.pu.citizen.connector.debtpositions.config.DebtPositionsApisHolder;
import it.gov.pagopa.pu.citizen.utils.TestUtils;
import it.gov.pagopa.pu.debtpositions.controller.generated.DebtPositionViewSearchControllerApi;
import it.gov.pagopa.pu.debtpositions.dto.generated.PagedModelDebtPositionView;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import uk.co.jemos.podam.api.PodamFactory;

import java.util.ArrayList;
import java.util.List;


@ExtendWith(MockitoExtension.class)
class DebtPositionViewSearchClientTest {

  @Mock
  private DebtPositionsApisHolder debtPositionsApisHolderMock;
  @Mock
  private DebtPositionViewSearchControllerApi debtPositionViewSearchControllerApiMock;

  private final PodamFactory podamFactory = TestUtils.getPodamFactory();

  DebtPositionViewSearchClient debtPositionViewSearchClient;

  @BeforeEach
  void setUp() {
    debtPositionViewSearchClient =
      new DebtPositionViewSearchClient(debtPositionsApisHolderMock);
  }

  @AfterEach
  void mockitoVerify() {
    Mockito.verifyNoMoreInteractions(
      debtPositionsApisHolderMock,
      debtPositionViewSearchControllerApiMock
    );
  }

  @Test
  void givenFiltersWhenGetPagedModelDebtPositionViewThenReturnPagedModel() {
    // given
    String accessToken = "ACCESS_TOKEN";
    List<Long> organizationIds = List.of(10L, 20L);
    String debtorFiscalCode = "debtorFiscalCode";
    Pageable pageable = PageRequest.of(0, 10);

    PagedModelDebtPositionView expectedResult =
      podamFactory.manufacturePojo(PagedModelDebtPositionView.class);

    Mockito.when(debtPositionsApisHolderMock.getDebtPositionViewSearchControllerApi(accessToken))
      .thenReturn(debtPositionViewSearchControllerApiMock);

    Mockito.when(debtPositionViewSearchControllerApiMock
        .crudDebtPositionsViewFindPagedPrimaryDebtPositionViewByFilters(
          organizationIds,
          debtorFiscalCode,
          pageable.getPageNumber(),
          pageable.getPageSize(),
          new ArrayList<>()
        )
      )
      .thenReturn(expectedResult);

    // when
    PagedModelDebtPositionView result =
      debtPositionViewSearchClient.getPagedModelDebtPositionView(
        organizationIds,
        debtorFiscalCode,
        accessToken,
        pageable
      );

    // then
    Assertions.assertNotNull(result);
    Assertions.assertEquals(expectedResult, result);
  }
}
