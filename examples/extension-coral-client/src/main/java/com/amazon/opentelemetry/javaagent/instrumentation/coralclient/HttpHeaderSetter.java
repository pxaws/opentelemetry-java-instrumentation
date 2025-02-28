/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package com.amazon.opentelemetry.javaagent.instrumentation.coralclient;

import com.amazon.coral.service.HttpConstant;
import com.amazon.coral.service.Job;
import com.amazon.coral.service.http.HttpHeaders;
import io.opentelemetry.context.propagation.TextMapSetter;
import javax.annotation.Nullable;

enum HttpHeaderSetter implements TextMapSetter<Job> {
  INSTANCE;

  @Override
  public void set(@Nullable Job carrier, String key, String value) {
    if (carrier == null) {
      return;
    }

    HttpHeaders headers = carrier.getRequest().getAttribute(HttpConstant.HTTP_HEADERS);
    if (headers == null) {
      //      headers = new HttpHeaders();
      //      carrier.getRequest().setAttribute(HttpConstant.HTTP_HEADERS, headers);
      System.out.println("======== DEBUG: HttpHeaderSetter.set() headers is null");
    }
    headers.addValue(key, value);
  }
}
