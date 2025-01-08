package io.opentelemetry.javaagent.instrumentation.coralclient;
import com.amazon.coral.core.instrumentation.ClientInstrument;
import com.amazon.coral.core.instrumentation.ClientInstrumentProvider;


public class CoralClientInstrumentProvider implements ClientInstrumentProvider {
  @Override
  public ClientInstrument newClientInstrument() {
    return new CoralClientInstrument();
  }

}
