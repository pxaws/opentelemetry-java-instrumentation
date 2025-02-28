echo "========build =============="
./gradlew build


echo "========push to local maven =============="
#./gradlew publishToMavenLocal

echo "========copy =============="
cp build/libs/opentelemetry-javaagent.jar /home/pinxiang/work/coral-instrumentation/HowtoUseCoral/src/HowtoUseValidation/opentelemetry-javaagent.jar
cp build/libs/opentelemetry-javaagent.jar /home/pinxiang/work/coral-instrumentation/HowtoUseCoralRemoteServer/src/HowtoUseValidation/opentelemetry-javaagent.jar
cp build/libs/opentelemetry-javaagent.jar /home/pinxiang/work/coral-instrumentation/test-async/src/HowToUseUntypedData/opentelemetry-javaagent.jar
