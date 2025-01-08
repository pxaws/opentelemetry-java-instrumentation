package io.opentelemetry.javaagent.instrumentation.coral;
import com.amazon.coral.core.instrumentation.ServiceInstrument;
import com.amazon.coral.core.instrumentation.ServiceInstrumentProvider;


public class CoralInstrumentProvider implements ServiceInstrumentProvider {

  @Override
  public ServiceInstrument newServiceInstrument() {
    return new CoralInstrument();
  }
}
