package io.opentelemetry.javaagent.instrumentation.coral;

import com.amazon.coral.service.Job;
import com.amazon.coral.service.ServiceConstant;
import com.google.common.base.Throwables;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.javaagent.bootstrap.Java8BytecodeBridge;
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import io.opentelemetry.javaagent.extension.instrumentation.TypeTransformer;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;

import static io.opentelemetry.javaagent.instrumentation.coral.CoralSingletons.instrumenter;
import static net.bytebuddy.matcher.ElementMatchers.isMethod;
import static net.bytebuddy.matcher.ElementMatchers.isPublic;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.takesArgument;

@SuppressWarnings("deprecation") //we will fix the deprecation later
public class CoralServerHttpInstrumentation implements TypeInstrumentation {

  @Override
  public ElementMatcher<TypeDescription> typeMatcher() {
    return named("com.amazon.coral.service.HttpHandler");
  }

  @Override
  public void transform(TypeTransformer transformer) {
    transformer.applyAdviceToMethod(
        isMethod()
            .and(isPublic())
            .and(named("before"))
            .and(takesArgument(0, named("com.amazon.coral.service.Job"))),
        this.getClass().getName() + "$CoralReqBeforeAdvice");
    transformer.applyAdviceToMethod(
        isMethod()
            .and(isPublic())
            .and(named("after"))
            .and(takesArgument(0, named("com.amazon.coral.service.Job"))),
        this.getClass().getName() + "$CoralReqAfterAdvice");
  }

  @SuppressWarnings("unused")
  public static class CoralReqBeforeAdvice {

    @Advice.OnMethodExit(suppress = Throwable.class)
    public static void methodExit(
        @Advice.Argument(0) Job job,
        @Advice.Local("otelContext") Context context,
        @Advice.Local("otelScope") Scope scope) {
      String operationName = job.getRequest().getAttribute(
          ServiceConstant.SERVICE_OPERATION_NAME);

      if (operationName == null) {
        return;
      }

      Context parentContext = Java8BytecodeBridge.rootContext();

      if (!instrumenter().shouldStart(parentContext, job)) {
        return;
      }

      try {
        context = instrumenter().start(parentContext, job);
      } catch (RuntimeException e) {
        System.out.println("DEBUG: Coral server HTTP instrumentation - OnMethodExit for before() + ERROR: " + Throwables.getStackTraceAsString(e));
      }

      scope = context.makeCurrent();
    }
  }

  @SuppressWarnings("unused")
  public static class CoralReqAfterAdvice {

    @Advice.OnMethodEnter(suppress = Throwable.class)
    public static void methodEnter(
        @Advice.Argument(0) Job job,
        @Advice.Local("otelContext") Context context,
        @Advice.Local("otelScope") Scope scope) {
      String operationName = job.getRequest().getAttribute(
          ServiceConstant.SERVICE_OPERATION_NAME);
      if (operationName == null) {
        return;
      }
      Context parentContext = Java8BytecodeBridge.currentContext();
      scope = parentContext.makeCurrent();
      if (scope == null) {
        return;
      }

      Span span = Span.fromContext(parentContext);
      try {
        scope.close();
        Throwable failure = job.getFailure();
        instrumenter().end(parentContext, job, job, job.getFailure());
        Java8BytecodeBridge.rootContext().makeCurrent();
      } catch (Throwable e) {
        System.out.println("End span in error: " + Throwables.getStackTraceAsString(e));
      }

      job.getMetrics().addProperty("AwsXRayTraceId", span.getSpanContext().getTraceId());
    }
  }

}
