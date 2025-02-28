/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package com.amazon.opentelemetry.javaagent.instrumentation.coral;

import com.amazon.coral.core.instrumentation.ServiceInstrument;
import com.amazon.coral.core.instrumentation.ServiceInstrumentProvider;

public class CoralInstrumentProvider implements ServiceInstrumentProvider {

  @Override
  public ServiceInstrument newServiceInstrument() {
    return new CoralInstrument();
  }
}
