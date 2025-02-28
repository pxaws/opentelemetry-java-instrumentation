/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package com.amazon.opentelemetry.javaagent.instrumentation.coralclient;

import com.amazon.coral.core.instrumentation.ClientInstrument;
import com.amazon.coral.core.instrumentation.ClientInstrumentProvider;

public class CoralClientInstrumentProvider implements ClientInstrumentProvider {
  @Override
  public ClientInstrument newClientInstrument() {
    return new CoralClientInstrument();
  }
}
