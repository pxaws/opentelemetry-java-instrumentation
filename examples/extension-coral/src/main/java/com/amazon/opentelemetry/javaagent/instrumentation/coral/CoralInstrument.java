/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package com.amazon.opentelemetry.javaagent.instrumentation.coral;

import static com.amazon.opentelemetry.javaagent.instrumentation.coral.CoralSingletons.instrumenter;

import com.amazon.coral.core.instrumentation.ServiceInstrument;
import com.amazon.coral.service.Constant;
import com.amazon.coral.service.Job;
import com.amazon.coral.service.ServiceConstant;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.javaagent.bootstrap.Java8BytecodeBridge;

public final class CoralInstrument implements ServiceInstrument {
  private static final Constant<Context> OPENTELEMETRY_CONTEXT =
      new Constant<>(Context.class, "OPENTELEMETRY_CONTEXT");
  private static final Constant<Scope> OPENTELEMETRY_SCOPE =
      new Constant<>(Scope.class, "OPENTELEMETRY_SCOPE");

  @Override
  public void beforeActivity(Job job) {
    if (job == null) {
      return;
    }

    System.out.println("======== DEBUG: CoralInstrument.beforeActivity() start");
    @SuppressWarnings("deprecation") // temporarily disable the warning
    String operationName = job.getRequest().getAttribute(ServiceConstant.SERVICE_OPERATION_NAME);
    System.out.println(
        "======== DEBUG: CoralInstrument.beforeActivity() operationName = " + operationName);

    Context parentContext = Java8BytecodeBridge.currentContext();

    if (!instrumenter().shouldStart(parentContext, job)) {
      return;
    }

    Context context = instrumenter().start(parentContext, job);
    Scope scope = context.makeCurrent();
    // save scope so that we can close it properly
    job.setAttribute(OPENTELEMETRY_SCOPE, scope);
    // save context so that we can attach it to another thread (if thread switch happens)
    job.setAttribute(OPENTELEMETRY_CONTEXT, context);
    System.out.println("======== DEBUG: CoralInstrument.beforeActivity() end");
  }

  @Override
  public void afterActivity(Job job) {
    if (job == null) {
      return;
    }
  }

  @Override
  public void afterActivityComplete(Job job, Object output, Throwable failure) {
    if (job == null) {
      return;
    }

    System.out.println("======== DEBUG: CoralInstrument.afterActivityComplete() start");
    Context context = job.getAttribute(OPENTELEMETRY_CONTEXT);
    Scope scope = job.getAttribute(OPENTELEMETRY_SCOPE);
    scope.close();
    instrumenter().end(context, job, job, job.getFailure());
    System.out.println("======== DEBUG: CoralInstrument.afterActivityComplete() end");
  }

  @Override
  public void beforeRequestExecution(Job job) {
    if (job == null) {
      return;
    }
  }

  @Override
  public void afterRequestExecution(Job job) {
    if (job == null) {
      return;
    }
  }

  @Override
  public void requestSuspended(Job job) {
    if (job == null) {
      return;
    }
    System.out.println("======== DEBUG: CoralInstrument.requestSuspended() start");
    Scope scope = job.getAttribute(OPENTELEMETRY_SCOPE);
    Span span = Span.current();
    span.setAttribute("coral.suspended", job.getId().toString());
    // close scope to clear the context saved in current thread
    // because the current thread will work on another job
    scope.close();
    System.out.println("======== DEBUG: CoralInstrument.requestSuspended() end");
  }

  @Override
  public void requestResumed(Job job) {
    if (job == null) {
      return;
    }
    System.out.println("======== DEBUG: CoralInstrument.requestResumed() start");
    // restore the context associated with current job
    Context context = job.getAttribute(OPENTELEMETRY_CONTEXT);
    // attach the context to current thread
    Scope scope = context.makeCurrent();
    // save scope so that we can close it later
    job.setAttribute(OPENTELEMETRY_SCOPE, scope);
    Span span = Span.current();
    span.setAttribute("coral.resumed", job.getId().toString());
    System.out.println("======== DEBUG: CoralInstrument.requestResumed() end");
  }

  @Override
  public String getDebugInfo(Throwable t) {
    return getClass().getName();
  }
}
