plugins {
  id("otel.javaagent-instrumentation")
}

dependencies {
  implementation(files("libs/CoralAttributes-1.1.jar"))
  implementation(files("libs/CoralHandler-1.1.jar"))
  implementation(files("libs/CoralModel-1.1.jar"))
  implementation(files("libs/CoralHttpSupport-1.1.jar"))
  implementation(files("libs/CoralMetrics-1.0.jar"))
  implementation(files("libs/CoralClient-1.1.jar"))
  implementation(files("libs/CoralCore-1.1.jar"))
}
