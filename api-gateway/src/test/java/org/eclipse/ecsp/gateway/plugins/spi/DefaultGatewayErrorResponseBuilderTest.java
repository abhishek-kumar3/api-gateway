/********************************************************************************
 * Copyright (c) 2023-24 Harman International
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * <p>SPDX-License-Identifier: Apache-2.0
 ********************************************************************************/

package org.eclipse.ecsp.gateway.plugins.spi;

import org.eclipse.ecsp.gateway.exceptions.ApiGatewayException;
import org.eclipse.ecsp.gateway.plugins.spi.DefaultGatewayErrorResponseBuilder.ErrorResponseFormat;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.mock;

class DefaultGatewayErrorResponseBuilderTest {

    @Test
    void testDefaultConstructorBuildsObjectFormat() {
        DefaultGatewayErrorResponseBuilder builder = new DefaultGatewayErrorResponseBuilder();
        ApiGatewayException exception = new ApiGatewayException(HttpStatus.UNAUTHORIZED,
                "api.gateway.error.token.invalid", "Token verification failed");
        ServerRequest request = mock(ServerRequest.class);

        Object response = builder.build(exception, request);

        Assertions.assertTrue(response instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, String> map = (Map<String, String>) response;
        Assertions.assertEquals("api.gateway.error.token.invalid", map.get("code"));
        Assertions.assertEquals("Token verification failed", map.get("message"));
    }

    @Test
    void testListFormatConstructorBuildsListFormat() {
        DefaultGatewayErrorResponseBuilder builder =
                new DefaultGatewayErrorResponseBuilder(ErrorResponseFormat.LIST);
        ApiGatewayException exception = new ApiGatewayException(HttpStatus.UNAUTHORIZED,
                "api.gateway.error.token.invalid", "Token verification failed");
        ServerRequest request = mock(ServerRequest.class);

        Object response = builder.build(exception, request);

        Assertions.assertTrue(response instanceof List);
        @SuppressWarnings("unchecked")
        List<Map<String, String>> list = (List<Map<String, String>>) response;
        Assertions.assertEquals(1, list.size());
        Map<String, String> element = list.get(0);
        Assertions.assertEquals("api.gateway.error.token.invalid", element.get("detailedErrorCode"));
        Assertions.assertEquals("Token verification failed", element.get("message"));
    }

    @Test
    void testStatusCodeDelegation() {
        DefaultGatewayErrorResponseBuilder builder = new DefaultGatewayErrorResponseBuilder();
        ApiGatewayException exception = new ApiGatewayException(HttpStatus.FORBIDDEN,
                "api.gateway.error", "Access denied");

        HttpStatusCode status = builder.statusCode(exception);

        Assertions.assertEquals(HttpStatus.FORBIDDEN, status);
    }
}
