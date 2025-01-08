echo "========build =============="
./gradlew assemble


echo "========push to local maven =============="
./gradlew publishToMavenLocal

echo "========copy =============="
cp javaagent/build/libs/opentelemetry-javaagent-1.32.1.jar /home/pinxiang/work/coral-instrumentation/HowtoUseCoral/src/HowtoUseValidation/opentelemetry-javaagent.jar
cp javaagent/build/libs/opentelemetry-javaagent-1.32.1.jar /home/pinxiang/work/coral-instrumentation/HowtoUseCoralRemoteServer/src/HowtoUseValidation/opentelemetry-javaagent.jar
cp javaagent/build/libs/opentelemetry-javaagent-1.32.1.jar /home/pinxiang/work/coral-instrumentation/test-async/src/HowToUseUntypedData/opentelemetry-javaagent.jar
