/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package com.amazon.opentelemetry.javaagent.instrumentation.coral;

import static io.opentelemetry.javaagent.extension.matcher.AgentElementMatchers.hasClassesNamed;
// import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static net.bytebuddy.matcher.ElementMatchers.named;

import com.google.auto.service.AutoService;
import io.opentelemetry.javaagent.extension.instrumentation.HelperResourceBuilder;
import io.opentelemetry.javaagent.extension.instrumentation.InstrumentationModule;
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import io.opentelemetry.javaagent.extension.instrumentation.TypeTransformer;
import io.opentelemetry.javaagent.extension.instrumentation.internal.ExperimentalInstrumentationModule;
import io.opentelemetry.javaagent.extension.instrumentation.internal.injection.ClassInjector;
import io.opentelemetry.javaagent.extension.instrumentation.internal.injection.InjectionMode;
import java.util.List;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;

// import static net.bytebuddy.matcher.ElementMatchers.any;

@AutoService(InstrumentationModule.class)
public class CoralInstrumentationModule extends InstrumentationModule
    implements ExperimentalInstrumentationModule {

  public CoralInstrumentationModule() {
    super("coral-extension", "coral-extension-1.1");
  }

  @Override
  public String getModuleGroup() {
    return "coral-extension-1.1";
  }

  @Override
  public boolean isHelperClass(String className) {
    return className.startsWith("com.amazon.opentelemetry.javaagent.instrumentation.coral.");
  }

  /**
   * Injects resource file with reference to allow Coral framework service loading mechanism to pick
   * it up.
   */
  @Override
  public void registerHelperResources(HelperResourceBuilder helperResourceBuilder) {
    System.out.println(
        "======== DEBUG: register for com.amazon.coral.core.instrumentation.ServiceInstrumentProvider");
    helperResourceBuilder.register(
        "META-INF/services/com.amazon.coral.core.instrumentation.ServiceInstrumentProvider",
        "META-INF/services/com.amazon.coral.core.instrumentation.ServiceInstrumentProvider");
  }

  void doTransform(TypeTransformer transformer) {
    // Nothing to transform, this type instrumentation is only used for injecting resources.
  }

  @Override
  public boolean isIndyModule() {
    return false;
  }

  @Override
  public ElementMatcher.Junction<ClassLoader> classLoaderMatcher() {
    // We don't actually transform it but want to make sure we only apply the instrumentation when
    // our key dependency is present.
    System.out.println("======== DEBUG: CoralInstrumentationModule.classLoaderMatcher()");
    return hasClassesNamed("com.amazon.coral.core.instrumentation.ServiceInstrumentProvider");
    //    return any();
  }

  @Override
  public void injectClasses(ClassInjector injector) {
    injector
        .proxyBuilder(
            "com.amazon.opentelemetry.javaagent.instrumentation.coral.CoralInstrumentProvider")
        .inject(InjectionMode.CLASS_ONLY);
  }

  @Override
  public List<TypeInstrumentation> typeInstrumentations() {
    return singletonList(new ResourceInjectingTypeInstrumentation());
  }

  // A type instrumentation is needed to trigger resource injection.
  public class ResourceInjectingTypeInstrumentation implements TypeInstrumentation {
    @Override
    public ElementMatcher<TypeDescription> typeMatcher() {
      // This is essentially the entry point of the AWS SDK, all clients implement it. We can ensure
      // our interceptor service definition is injected as early as possible if we typematch against
      // it.
      System.out.println("======== DEBUG: ResourceInjectingTypeInstrumentation.typeMatcher()");
      //      throw new IllegalStateException("Should never be called");
      //      throw new RuntimeException("Should never be called");
      //      return named("com.amazon.coral.service.Job");
      return named("com.amazon.coral.model.basic.BasicModel");
    }

    @Override
    public void transform(TypeTransformer transformer) {
      doTransform(transformer);
    }
  }
}
