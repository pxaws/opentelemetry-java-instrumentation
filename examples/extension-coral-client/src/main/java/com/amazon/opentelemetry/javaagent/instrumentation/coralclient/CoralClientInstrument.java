/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package com.amazon.opentelemetry.javaagent.instrumentation.coralclient;

import static com.amazon.opentelemetry.javaagent.instrumentation.coralclient.CoralClientSingletons.instrumenter;

import com.amazon.coral.core.instrumentation.ClientInstrument;
import com.amazon.coral.service.Constant;
import com.amazon.coral.service.Job;
import com.amazon.coral.service.ServiceConstant;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.javaagent.bootstrap.Java8BytecodeBridge;

public final class CoralClientInstrument implements ClientInstrument {
  private static final Constant<Context> OPENTELEMETRY_CONTEXT =
      new Constant<>(Context.class, "OPENTELEMETRY_CONTEXT");
  private static final Constant<Scope> OPENTELEMETRY_SCOPE =
      new Constant<>(Scope.class, "OPENTELEMETRY_SCOPE");

  @Override
  public void beforeCall(Job job) {
    if (job == null) {
      return;
    }

    //    System.out.println("======== DEBUG: CoralInstrument.beforeCall() start");
    //    @SuppressWarnings("deprecation") // temporarily disable the warning
    //    String operationName =
    // job.getRequest().getAttribute(ServiceConstant.SERVICE_OPERATION_NAME);
    //    System.out.println("======== DEBUG: CoralInstrument.beforeCall() operationName = " +
    // operationName);
    //
    //    Context parentContext = Java8BytecodeBridge.currentContext();
    //
    //    if (!instrumenter().shouldStart(parentContext, job)) {
    //      return;
    //    }
    //
    //    Context context = instrumenter().start(parentContext, job);
    //    Scope scope = context.makeCurrent();
    //    // save scope so that we can close it properly
    //    job.setAttribute(OPENTELEMETRY_SCOPE, scope);
    //    // save context so that we can attach it to another thread (if thread switch happens)
    //    job.setAttribute(OPENTELEMETRY_CONTEXT, context);
    //    System.out.println("======== DEBUG: CoralInstrument.beforeCall() end");
  }

  @Override
  public void afterCall(Job job) {
    if (job == null) {
      return;
    }
    System.out.println("======== DEBUG: CoralInstrument.afterCall() start");
    Context context = job.getAttribute(OPENTELEMETRY_CONTEXT);
    Scope scope = job.getAttribute(OPENTELEMETRY_SCOPE);
    scope.close();
    instrumenter().end(context, job, job, job.getFailure());
    System.out.println("======== DEBUG: CoralInstrument.afterCall() end");
  }

  @Override
  public void beforeHttpRequestCreation(Job job) {
    //    if (job == null) {
    //      return;
    //    }
    if (job == null) {
      return;
    }

    System.out.println("======== DEBUG: CoralInstrument.beforeCall() start");
    @SuppressWarnings("deprecation") // temporarily disable the warning
    String operationName = job.getRequest().getAttribute(ServiceConstant.SERVICE_OPERATION_NAME);
    System.out.println(
        "======== DEBUG: CoralInstrument.beforeCall() operationName = " + operationName);

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
    System.out.println("======== DEBUG: CoralInstrument.beforeCall() end");
  }

  @Override
  public String getDebugInfo(Throwable t) {
    return getClass().getName();
  }
}
