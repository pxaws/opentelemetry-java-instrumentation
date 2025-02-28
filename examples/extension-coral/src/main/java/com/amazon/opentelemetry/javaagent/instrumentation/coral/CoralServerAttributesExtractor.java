/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package com.amazon.opentelemetry.javaagent.instrumentation.coral;

import static io.opentelemetry.instrumentation.api.internal.AttributesExtractorUtil.internalSet;
import static io.opentelemetry.semconv.ClientAttributes.CLIENT_ADDRESS;
import static io.opentelemetry.semconv.HttpAttributes.HTTP_REQUEST_METHOD;
import static io.opentelemetry.semconv.HttpAttributes.HTTP_RESPONSE_STATUS_CODE;
import static io.opentelemetry.semconv.ServerAttributes.SERVER_ADDRESS;
import static io.opentelemetry.semconv.UrlAttributes.URL_FULL;
import static io.opentelemetry.semconv.UrlAttributes.URL_PATH;
import static io.opentelemetry.semconv.UrlAttributes.URL_SCHEME;
import static io.opentelemetry.semconv.UserAgentAttributes.USER_AGENT_ORIGINAL;
import static io.opentelemetry.semconv.incubating.CodeIncubatingAttributes.CODE_FUNCTION;
import static io.opentelemetry.semconv.incubating.CodeIncubatingAttributes.CODE_NAMESPACE;
import static io.opentelemetry.semconv.incubating.RpcIncubatingAttributes.RPC_METHOD;
import static io.opentelemetry.semconv.incubating.RpcIncubatingAttributes.RPC_SERVICE;

import com.amazon.coral.service.HttpConstant;
import com.amazon.coral.service.Job;
import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.context.Context;
import io.opentelemetry.instrumentation.api.instrumenter.AttributesExtractor;
import io.opentelemetry.instrumentation.api.internal.SpanKey;
import io.opentelemetry.instrumentation.api.internal.SpanKeyProvider;
import javax.annotation.Nullable;

/**
 * Extractor of <a
 * href="https://github.com/open-telemetry/semantic-conventions/blob/main/docs/general/attributes.md#source-code-attributes">source
 * code attributes</a>.
 */
public final class CoralServerAttributesExtractor<REQUEST, RESPONSE>
    implements AttributesExtractor<REQUEST, RESPONSE>, SpanKeyProvider {

  /** Creates the code attributes extractor. */
  public static <REQUEST, RESPONSE> AttributesExtractor<REQUEST, RESPONSE> create(
      AttributesGetter<REQUEST> getter) {
    return new CoralServerAttributesExtractor<>(getter);
  }

  private final AttributesGetter<REQUEST> getter;

  private CoralServerAttributesExtractor(AttributesGetter<REQUEST> getter) {
    this.getter = getter;
  }

  @Override
  @SuppressWarnings("deprecation") // we will fix the deprecation later
  public void onStart(AttributesBuilder attributes, Context parentContext, REQUEST request) {
    Class<?> cls = getter.getCodeClass(request);
    if (cls != null) {
      internalSet(attributes, CODE_NAMESPACE, cls.getName());
    }
    internalSet(attributes, CODE_FUNCTION, getter.getMethodName(request));
    internalSet(attributes, SERVER_ADDRESS, getter.getServerAddress(request)); // http host
    internalSet(attributes, URL_PATH, getter.getUrlPath(request));
    internalSet(attributes, URL_FULL, getter.getServerAddress(request));
    internalSet(attributes, CLIENT_ADDRESS, getter.getClientAddress(request)); // HTTP_CLIENT_IP
    internalSet(attributes, HTTP_REQUEST_METHOD, getter.getHttpMethod(request)); // HTTP_VERB
    internalSet(attributes, USER_AGENT_ORIGINAL, getter.getUserAgent(request));
    internalSet(attributes, URL_SCHEME, getter.getHttpSchema(request));
    internalSet(attributes, RPC_SERVICE, getter.getServiceName(request));
    internalSet(attributes, RPC_METHOD, getter.getMethodName(request));
  }

  @Override
  @SuppressWarnings("deprecation") // we will fix the deprecation later
  public void onEnd(
      AttributesBuilder attributes,
      Context context,
      REQUEST request,
      @Nullable RESPONSE response,
      @Nullable Throwable error) {
    Integer statusCode = ((Job) response).getReply().getAttribute(HttpConstant.HTTP_STATUS_CODE);
    if (statusCode != null) {
      internalSet(attributes, HTTP_RESPONSE_STATUS_CODE, (long) statusCode);
    }
  }

  @Nullable
  @Override
  public SpanKey internalGetSpanKey() {
    return SpanKey.KIND_SERVER;
  }
}
