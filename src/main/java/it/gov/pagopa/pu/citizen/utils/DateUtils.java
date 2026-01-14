package it.gov.pagopa.pu.citizen.utils;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

import static it.gov.pagopa.pu.citizen.utils.Constants.ZONEID;

public class DateUtils {

  private DateUtils(){}

  public static LocalDateTime toLocalDateTime(OffsetDateTime date) {
    return date != null ? date.atZoneSameInstant(ZONEID).toLocalDateTime() : null;
  }
}
