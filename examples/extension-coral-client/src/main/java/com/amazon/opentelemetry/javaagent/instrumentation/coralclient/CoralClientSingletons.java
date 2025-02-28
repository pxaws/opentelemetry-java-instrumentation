/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package com.amazon.opentelemetry.javaagent.instrumentation.coralclient;

import com.amazon.coral.service.Job;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.instrumentation.api.instrumenter.Instrumenter;

public class CoralClientSingletons {
  private static final String INSTRUMENTATION_NAME = "com.amazon.coral.client-1.1";

  private static final Instrumenter<Job, Job> INSTRUMENTER;

  static {
    CoralAttributesGetter codeAttributesGetter = new CoralAttributesGetter();
    INSTRUMENTER =
        Instrumenter.<Job, Job>builder(
                GlobalOpenTelemetry.get(),
                INSTRUMENTATION_NAME,
                CoralSpanNameExtractor.create(codeAttributesGetter))
            .addAttributesExtractor(CoralServerAttributesExtractor.create(codeAttributesGetter))
            .addAttributesExtractor(new CoralNestedHttpClientSuppressionAttributesExtractor<>())
            .setSpanStatusExtractor(CoralSpanStatusExtractor.create())
            .buildClientInstrumenter(HttpHeaderSetter.INSTANCE);
  }

  public static Instrumenter<Job, Job> instrumenter() {
    return INSTRUMENTER;
  }

  private CoralClientSingletons() {}
}
