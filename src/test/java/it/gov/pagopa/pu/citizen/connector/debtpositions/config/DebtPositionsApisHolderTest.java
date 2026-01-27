package it.gov.pagopa.pu.citizen.connector.debtpositions.config;

import it.gov.pagopa.pu.citizen.connector.BaseApiHolderTest;
import it.gov.pagopa.pu.debtpositions.dto.generated.ReceiptOriginType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.time.OffsetDateTime;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class DebtPositionsApisHolderTest extends BaseApiHolderTest {
    @Mock
    private RestTemplateBuilder restTemplateBuilderMock;

    private DebtPositionsApisHolder apisHolder;

    @BeforeEach
    void setUp() {
        Mockito.when(restTemplateBuilderMock.build()).thenReturn(restTemplateMock);
        Mockito.when(restTemplateMock.getUriTemplateHandler()).thenReturn(new DefaultUriBuilderFactory());
        DebtPositionsApiClientConfig clientConfig = DebtPositionsApiClientConfig.builder()
          .baseUrl("http://example.com")
          .build();
        apisHolder = new DebtPositionsApisHolder(clientConfig, restTemplateBuilderMock);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                restTemplateBuilderMock,
                restTemplateMock
        );
    }

    @Test
    void whenGetDebtPositionTypeOrgSearchControllerApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
                accessToken -> apisHolder.getDebtPositionTypeOrgSearchControllerApi(accessToken)
                        .crudDebtPositionTypeOrgsFindByOrganizationIdAndCode(1L, "CODE"),
                new ParameterizedTypeReference<>() {},
                apisHolder::unload);
    }

    @Test
    void  whenGetDebtPositionTypeOrgWithActiveSpontaneousCountSearchControllerApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
      assertAuthenticationShouldBeSetInThreadSafeMode(
        accessToken -> apisHolder.getDebtPositionTypeOrgWithActiveSpontaneousCountSearchControllerApi(accessToken)
          .crudDebtPositionTypeOrgWithActiveSpontaneousCountCountByOrganizationIds(List.of(1L)),
        new ParameterizedTypeReference<>() {},
        apisHolder::unload);
    }

  @Test
  void whenGetDebtPositionTypeOrgEntityControllerApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
    assertAuthenticationShouldBeSetInThreadSafeMode(
      accessToken -> apisHolder.getDebtPositionTypeOrgEntityControllerApi(accessToken)
        .crudGetDebtpositiontypeorg("1"),
      new ParameterizedTypeReference<>() {},
      apisHolder::unload);
  }

  @Test
  void whenGetSpontaneousFormEntityControllerApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException  {
    assertAuthenticationShouldBeSetInThreadSafeMode(
      accessToken -> apisHolder.getSpontaneousFormEntityControllerApi(accessToken)
        .crudGetSpontaneousform("1"),
      new ParameterizedTypeReference<>() {},
      apisHolder::unload);
  }

  @Test
  void whenGetDebtPositionApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
    assertAuthenticationShouldBeSetInThreadSafeMode(
      accessToken -> apisHolder.getDebtPositionApi(accessToken)
        .getDebtPosition(
          1L),
      new ParameterizedTypeReference<>() {
      },
      apisHolder::unload);
  }

  @Test
  void whenGetReceiptNoPiiViewSearchControllerApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
    assertAuthenticationShouldBeSetInThreadSafeMode(
      accessToken -> apisHolder.getReceiptNoPiiViewSearchControllerApi(accessToken)
        .crudReceiptNoPiiViewGetPagedPrimaryReceiptByFilters(
          "debtorFiscalCode",
          List.of("orgFiscalCode"),
          List.of(ReceiptOriginType.RECEIPT_FILE),
          "noticeNumberOrIuv",
          OffsetDateTime.now(),
          OffsetDateTime.now(),
          0,1,null
        ),
      new ParameterizedTypeReference<>() {
      },
      apisHolder::unload);
  }

  @Test
  void whenGetReceiptApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
    assertAuthenticationShouldBeSetInThreadSafeMode(
      accessToken -> apisHolder.getReceiptApi(accessToken)
        .getReceiptDetail(
          1L,1L,null, null),
      new ParameterizedTypeReference<>() {
      },
      apisHolder::unload);
  }

  @Test
  void whenGetReceiptNoPiiSearchControllerApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
    assertAuthenticationShouldBeSetInThreadSafeMode(
      accessToken -> apisHolder.getReceiptNoPiiSearchControllerApi(accessToken)
        .crudReceiptsValidateReceiptDebtor(
          1L,2L,"debtorFiscalCode"),
      new ParameterizedTypeReference<>() {
      },
      apisHolder::unload);
  }

  @Test
  void whenGetInstallmentApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
    assertAuthenticationShouldBeSetInThreadSafeMode(
      accessToken -> apisHolder.getInstallmentApi(accessToken)
        .getInstallmentsByIuvOrNav(
          "iuvOrNav","debtorFiscalCode",1L),
      new ParameterizedTypeReference<>() {
      },
      apisHolder::unload);
  }
  @Test
  void whenGetInstallmentNoPiiSearchControllerApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
    assertAuthenticationShouldBeSetInThreadSafeMode(
      accessToken -> apisHolder.getInstallmentNoPiiSearchControllerApi(accessToken)
        .crudInstallmentsFindDebtorUnpaidOrPaidByDebtPositionIdAndPaymentOptionId(
          1L,2L,"debtorFiscalCode", 3L),
      new ParameterizedTypeReference<>() {
      },
      apisHolder::unload);
  }

  @Test
  void whenGetReceiptNoPiiEntityControllerApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
    assertAuthenticationShouldBeSetInThreadSafeMode(
      accessToken -> apisHolder.getReceiptNoPiiEntityControllerApi(accessToken)
        .crudGetReceiptnopii(
          "1"),
      new ParameterizedTypeReference<>() {
      },
      apisHolder::unload);
  }
}
