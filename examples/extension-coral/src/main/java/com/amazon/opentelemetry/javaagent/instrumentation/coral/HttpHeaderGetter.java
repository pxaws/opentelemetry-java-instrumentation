/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package com.amazon.opentelemetry.javaagent.instrumentation.coral;

import com.amazon.coral.service.HttpConstant;
import com.amazon.coral.service.Job;
import com.amazon.coral.service.http.HttpHeaders;
import io.opentelemetry.context.propagation.TextMapGetter;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

enum HttpHeaderGetter implements TextMapGetter<Job> {
  INSTANCE;

  @Override
  public Iterable<String> keys(Job carrier) {
    HttpHeaders headers = carrier.getRequest().getAttribute(HttpConstant.HTTP_HEADERS);
    return StreamSupport.stream(headers.getHeaderNames().spliterator(), false)
        .map(CharSequence::toString)
        .collect(Collectors.toList());
  }

  @Override
  public String get(Job carrier, String key) {
    if (carrier == null) {
      return null;
    }

    HttpHeaders headers = carrier.getRequest().getAttribute(HttpConstant.HTTP_HEADERS);
    CharSequence value = headers.getValue(key);
    if (value != null) {
      System.out.println(
          "======== DEBUG: HttpHeaderGetter.get() key=" + key + ", value=" + value.toString());
    }
    return value != null ? value.toString() : null;
  }
}
