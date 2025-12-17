import com.github.jk1.license.filter.SpdxLicenseBundleNormalizer
import com.github.jk1.license.render.XmlReportRenderer
import java.util.*

plugins {
  java
  id("org.springframework.boot") version "4.0.0"
  id("io.spring.dependency-management") version "1.1.7"
  jacoco
  id("org.sonarqube") version "7.2.1.6560"
  id("com.github.ben-manes.versions") version "0.53.0"
  id("org.openapi.generator") version "7.17.0"
  id("com.gorylenko.gradle-git-properties") version "2.5.4"
  id("org.ajoberstar.grgit") version "5.3.2"
  id("com.github.jk1.dependency-license-report") version "3.0.1"
}

group = "it.gov.pagopa.payhub"
version = "0.0.1"
description = "p4pa-citizen"

java {
  toolchain {
    languageVersion = JavaLanguageVersion.of(21)
  }
}

configurations {
  compileOnly {
    extendsFrom(configurations.annotationProcessor.get())
  }
  compileClasspath {
    resolutionStrategy.activateDependencyLocking()
  }
}

licenseReport {
  renderers =
    arrayOf(XmlReportRenderer("third-party-libs.xml", "Back-End Libraries"))
  outputDir = "$projectDir/dependency-licenses"
  filters = arrayOf(SpdxLicenseBundleNormalizer())
}
tasks.classes {
  finalizedBy(tasks.generateLicenseReport)
}

repositories {
  mavenCentral()
}

val springDocOpenApiVersion = "3.0.0"
val janinoVersion = "3.1.12"
val openApiToolsVersion = "0.2.8"
val micrometerVersion = "1.6.1"
val httpClientVersion = "5.5.1"
val podamVersion = "8.0.2.RELEASE"
val javaJwtVersion = "4.5.0"
val jwksRsaVersion = "0.23.0"
val bouncycastleVersion = "1.83"
val nimbusVersion = "10.5"
val mapStructVersion = "1.6.3"
val commonsLang3Version = "3.20.0"

dependencies {
  implementation("org.springframework.boot:spring-boot-starter-webmvc")
  implementation("org.springframework.boot:spring-boot-starter-opentelemetry")
  implementation("org.springframework.boot:spring-boot-starter-restclient")
  implementation("org.springframework.boot:spring-boot-starter-validation")
  implementation("org.springframework.boot:spring-boot-starter-actuator")
  implementation("org.springframework.boot:spring-boot-starter-security")
  implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:$springDocOpenApiVersion") {
    exclude(group = "org.apache.commons", module = "commons-lang3")
  }
  implementation("org.apache.commons:commons-lang3:$commonsLang3Version")
  implementation("org.codehaus.janino:janino:$janinoVersion")
  implementation("io.micrometer:micrometer-tracing-bridge-otel:$micrometerVersion")
  implementation("io.micrometer:micrometer-registry-prometheus")
  implementation("org.openapitools:jackson-databind-nullable:$openApiToolsVersion")
  implementation("org.apache.httpcomponents.client5:httpclient5:$httpClientVersion")
  implementation("org.springframework.data:spring-data-commons")
  implementation("org.mapstruct:mapstruct:${mapStructVersion}")

  // validation token jwt
  implementation("com.nimbusds:nimbus-jose-jwt:${nimbusVersion}")
  implementation("com.auth0:java-jwt:${javaJwtVersion}")
  implementation("com.auth0:jwks-rsa:${jwksRsaVersion}")
  implementation("org.bouncycastle:bcprov-jdk18on:${bouncycastleVersion}")

  compileOnly("org.projectlombok:lombok")
  annotationProcessor("org.projectlombok:lombok")
  annotationProcessor("org.mapstruct:mapstruct-processor:$mapStructVersion")
  testAnnotationProcessor("org.projectlombok:lombok")
  testAnnotationProcessor("org.mapstruct:mapstruct-processor:$mapStructVersion")

  //	Testing
  testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
  testImplementation("org.springframework.boot:spring-boot-starter-security-test")
  testImplementation("org.mockito:mockito-core")
  testImplementation("org.projectlombok:lombok")
  testImplementation("uk.co.jemos.podam:podam:${podamVersion}")
}

tasks.withType<Test> {
  useJUnitPlatform()
  finalizedBy(tasks.jacocoTestReport)
}

val mockitoAgent = configurations.create("mockitoAgent")
dependencies {
  mockitoAgent("org.mockito:mockito-core") { isTransitive = false }
}
tasks {
  test {
    jvmArgs("-javaagent:${mockitoAgent.asPath}")
  }
}

tasks.jacocoTestReport {
  dependsOn(tasks.test)
  reports {
    xml.required = true
  }
}

val projectInfo = mapOf(
  "artifactId" to project.name,
  "version" to project.version
)

tasks {
  val processResources by getting(ProcessResources::class) {
    filesMatching("**/application.yml") {
      expand(projectInfo)
    }
  }
}

tasks.compileJava {
  dependsOn("dependenciesBuild")
}

tasks.register("dependenciesBuild") {
  group = "AutomaticallyGeneratedCode"
  description = "grouping all together automatically generate code tasks"

  dependsOn(
    "openApiGenerate",
    "openApiGenerateP4PAAUTH",
    "openApiGenerateDEBTPOSITIONS",
    "openApiGenerateORGANIZATION",
    "openApiGeneratePAGOPAPAYMENTS"
  )
}

configure<SourceSetContainer> {
  named("main") {
    java.srcDir("$projectDir/build/generated/src/main/java")
  }
}

springBoot {
  buildInfo()
  mainClass.value("it.gov.pagopa.pu.citizen.PuCitizenApplication")
}

openApiGenerate {
  generatorName.set("spring")
  inputSpec.set("$rootDir/openapi/p4pa-citizen.openapi.yaml")
  outputDir.set("$projectDir/build/generated")
  apiPackage.set("it.gov.pagopa.pu.citizen.controller.generated")
  modelPackage.set("it.gov.pagopa.pu.citizen.dto.generated")
  configOptions.set(
    mapOf(
      "dateLibrary" to "java8",
      "requestMappingMode" to "api_interface",
      "useSpringBoot3" to "true",
      "interfaceOnly" to "true",
      "useTags" to "true",
      "useBeanValidation" to "true",
      "generateConstructorWithAllArgs" to "true",
      "generatedConstructorWithRequiredArgs" to "true",
      "enumPropertyNaming" to "original",
      "additionalModelTypeAnnotations" to "@lombok.experimental.SuperBuilder(toBuilder = true)"
    )
  )
  typeMappings.set(
    mapOf(
      "FormCustom" to "it.gov.pagopa.pu.debtpositions.dto.generated.SpontaneousForm",
      "DebtPositionDTO" to "it.gov.pagopa.pu.debtpositions.dto.generated.DebtPositionDTO",
      "PersonDTO" to "it.gov.pagopa.pu.debtpositions.dto.generated.PersonDTO",
      "DebtPositionDTO" to "it.gov.pagopa.pu.debtpositions.dto.generated.DebtPositionDTO",
      "ReceiptOrigin" to "it.gov.pagopa.pu.debtpositions.dto.generated.ReceiptOriginType",
      "ReceiptDetailExtendedDTO" to "it.gov.pagopa.pu.citizen.dto.ReceiptDetailExtendedDTO",
      "PaymentOptionType" to "it.gov.pagopa.pu.debtpositions.dto.generated.PaymentOptionType",
      "PaymentOptionStatus" to "it.gov.pagopa.pu.debtpositions.dto.generated.PaymentOptionStatus",
      "DebtPositionStatus" to "it.gov.pagopa.pu.debtpositions.dto.generated.DebtPositionStatus",
      "InstallmentStatus" to "it.gov.pagopa.pu.debtpositions.dto.generated.InstallmentStatus",
      "InstallmentDebtorExtendedDTO" to "it.gov.pagopa.pu.citizen.dto.InstallmentDebtorExtendedDTO"
    )
  )
}

var targetEnv = when (Objects.requireNonNullElse(
  System.getProperty("targetBranch"),
  grgit.branch.current().name
)) {
  "uat" -> "uat"
  "main" -> "main"
  else -> "develop"
}

tasks.register<org.openapitools.generator.gradle.plugin.tasks.GenerateTask>("openApiGenerateP4PAAUTH") {
  group = "openapi"
  description = "openapi"

  generatorName.set("java")
  remoteInputSpec.set("https://raw.githubusercontent.com/pagopa/p4pa-auth/refs/heads/$targetEnv/openapi/p4pa-auth.openapi.yaml")
  outputDir.set("$projectDir/build/generated")
  apiPackage.set("it.gov.pagopa.pu.auth.controller.generated")
  modelPackage.set("it.gov.pagopa.pu.auth.dto.generated")
  configOptions.set(
    mapOf(
      "swaggerAnnotations" to "false",
      "openApiNullable" to "false",
      "dateLibrary" to "java8",
      "serializableModel" to "true",
      "useSpringBoot3" to "true",
      "useJakartaEe" to "true",
      "useOneOfInterfaces" to "true",
      "useBeanValidation" to "true",
      "serializationLibrary" to "jackson",
      "generateSupportingFiles" to "true",
      "generateConstructorWithAllArgs" to "true",
      "generatedConstructorWithRequiredArgs" to "true",
      "enumPropertyNaming" to "original",
      "additionalModelTypeAnnotations" to "@lombok.experimental.SuperBuilder(toBuilder = true)"
    )
  )
  library.set("resttemplate")
}

tasks.register<org.openapitools.generator.gradle.plugin.tasks.GenerateTask>("openApiGenerateDEBTPOSITIONS") {
  group = "openapi"
  description = "openapi"

  generatorName.set("java")
  remoteInputSpec.set("https://raw.githubusercontent.com/pagopa/p4pa-debt-positions/refs/heads/$targetEnv/openapi/generated.openapi.json")
  outputDir.set("$projectDir/build/generated")
  apiPackage.set("it.gov.pagopa.pu.debtpositions.controller.generated")
  modelPackage.set("it.gov.pagopa.pu.debtpositions.dto.generated")
  typeMappings.set(
    mapOf(
      "LocalDateTime" to "java.time.LocalDateTime",
      "string+binary" to "Resource"
    )
  )
  configOptions.set(
    mapOf(
      "swaggerAnnotations" to "false",
      "openApiNullable" to "false",
      "dateLibrary" to "java8",
      "serializableModel" to "true",
      "useSpringBoot3" to "true",
      "useJakartaEe" to "true",
      "useOneOfInterfaces" to "true",
      "useBeanValidation" to "true",
      "serializationLibrary" to "jackson",
      "generateSupportingFiles" to "true",
      "generateConstructorWithAllArgs" to "true",
      "generatedConstructorWithRequiredArgs" to "true",
      "enumPropertyNaming" to "original",
      "additionalModelTypeAnnotations" to "@lombok.experimental.SuperBuilder(toBuilder = true)"
    )
  )
  importMappings.set(
    mapOf(
      "Resource" to "org.springframework.core.io.Resource"
    )
  )
  library.set("resttemplate")
}

tasks.register<org.openapitools.generator.gradle.plugin.tasks.GenerateTask>("openApiGenerateORGANIZATION") {
  group = "openapi"
  description = "description"

  generatorName.set("java")
  remoteInputSpec.set("https://raw.githubusercontent.com/pagopa/p4pa-organization/refs/heads/$targetEnv/openapi/generated.openapi.json")
  outputDir.set("$projectDir/build/generated")
  apiPackage.set("it.gov.pagopa.pu.organization.controller.generated")
  modelPackage.set("it.gov.pagopa.pu.organization.dto.generated")
  configOptions.set(
    mapOf(
      "swaggerAnnotations" to "false",
      "openApiNullable" to "false",
      "dateLibrary" to "java8",
      "serializableModel" to "true",
      "useSpringBoot3" to "true",
      "useJakartaEe" to "true",
      "useOneOfInterfaces" to "true",
      "useBeanValidation" to "true",
      "serializationLibrary" to "jackson",
      "generateSupportingFiles" to "true",
      "generateConstructorWithAllArgs" to "true",
      "generatedConstructorWithRequiredArgs" to "true",
      "enumPropertyNaming" to "original",
      "additionalModelTypeAnnotations" to "@lombok.experimental.SuperBuilder(toBuilder = true)"
    )
  )
  library.set("resttemplate")
}

tasks.register<org.openapitools.generator.gradle.plugin.tasks.GenerateTask>("openApiGeneratePAGOPAPAYMENTS") {
  group = "openapi"
  description = "description"

  generatorName.set("java")
  remoteInputSpec.set("https://raw.githubusercontent.com/pagopa/p4pa-pagopa-payments/refs/heads/$targetEnv/openapi/p4pa-pagopa-payments.openapi.yaml")
  outputDir.set("$projectDir/build/generated")
  apiPackage.set("it.gov.pagopa.pu.pagopapayments.controller.generated")
  modelPackage.set("it.gov.pagopa.pu.pagopapayments.dto.generated")
  configOptions.set(
    mapOf(
      "swaggerAnnotations" to "false",
      "openApiNullable" to "false",
      "dateLibrary" to "java8",
      "serializableModel" to "true",
      "useSpringBoot3" to "true",
      "useJakartaEe" to "true",
      "useOneOfInterfaces" to "true",
      "useBeanValidation" to "true",
      "serializationLibrary" to "jackson",
      "generateSupportingFiles" to "true",
      "generateConstructorWithAllArgs" to "true",
      "generatedConstructorWithRequiredArgs" to "true",
      "enumPropertyNaming" to "original",
      "additionalModelTypeAnnotations" to "@lombok.experimental.SuperBuilder(toBuilder = true)"
    )
  )
  typeMappings.set(
    mapOf(
      "string+binary" to "Resource",
      "DebtPositionDTO" to "it.gov.pagopa.pu.debtpositions.dto.generated.DebtPositionDTO"
    )
  )
  importMappings.set(
    mapOf(
      "Resource" to "org.springframework.core.io.Resource"
    )
  )
  library.set("resttemplate")
}
