/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.coral;

import com.amazon.coral.service.Job;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.instrumentation.api.instrumenter.Instrumenter;

public class CoralSingletons {
  private static final String INSTRUMENTATION_NAME = "com.amazon.coral-1.1";

  private static final Instrumenter<Job, Job> INSTRUMENTER;

  static {
    CoralAttributesGetter codeAttributesGetter = new CoralAttributesGetter();
    INSTRUMENTER =
        Instrumenter.<Job, Job>builder(
                GlobalOpenTelemetry.get(),
                INSTRUMENTATION_NAME,
                CoralSpanNameExtractor.create(codeAttributesGetter))
            .addAttributesExtractor(CoralServerAttributesExtractor.create(codeAttributesGetter))
            .setSpanStatusExtractor(CoralSpanStatusExtractor.create())
            .buildServerInstrumenter(HttpHeaderGetter.INSTANCE);
  }

  public static Instrumenter<Job, Job> instrumenter() {
    return INSTRUMENTER;
  }

  private CoralSingletons() {}
}
