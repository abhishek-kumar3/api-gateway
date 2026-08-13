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

import org.eclipse.ecsp.gateway.exceptions.IgniteGlobalExceptionHandler;
import org.eclipse.ecsp.gateway.utils.GatewayConstants;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.server.ServerRequest;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Default implementation of {@link GatewayErrorResponseBuilder}.
 *
 * <p>
 * Supports two response formats via {@link ErrorResponseFormat}:
 * <ul>
 * <li>{@link ErrorResponseFormat#OBJECT} (default):
 * 
 * <pre>{@code {"code": "...", "message": "..."}}</pre>
 * 
 * </li>
 * <li>{@link ErrorResponseFormat#LIST}:
 * 
 * <pre>{@code [{"detailedErrorCode": "...", "message": "..."}]}</pre>
 * 
 * </li>
 * </ul>
 *
 * <p>
 * This bean is registered only when no other
 * {@link GatewayErrorResponseBuilder} bean
 * is present in the Spring application context
 * ({@code @ConditionalOnMissingBean}).
 */
public class DefaultGatewayErrorResponseBuilder implements GatewayErrorResponseBuilder {

    /**
     * Enum defining the error response format.
     */
    public enum ErrorResponseFormat {
        /** Standard object format: {"code": "...", "message": "..."}. */
        OBJECT,
        /** List format: [{"detailedErrorCode": "...", "message": "..."}]. */
        LIST
    }

    private final ErrorResponseFormat format;

    /**
     * Constructs a DefaultGatewayErrorResponseBuilder using the standard OBJECT
     * format.
     */
    public DefaultGatewayErrorResponseBuilder() {
        this(ErrorResponseFormat.OBJECT);
    }

    /**
     * Constructs a DefaultGatewayErrorResponseBuilder with the specified
     * {@link ErrorResponseFormat}.
     *
     * @param format the error response format to use (OBJECT or LIST)
     */
    public DefaultGatewayErrorResponseBuilder(ErrorResponseFormat format) {
        this.format = format != null ? format : ErrorResponseFormat.OBJECT;
    }

    @Override
    public Object build(Throwable throwable, ServerRequest request) {
        Map<String, String> errorMap = IgniteGlobalExceptionHandler.prepareResponse(throwable);
        if (format == ErrorResponseFormat.LIST) {
            Map<String, String> listElement = new LinkedHashMap<>();
            listElement.put("detailedErrorCode", errorMap.get(GatewayConstants.CODE));
            listElement.put("message", errorMap.get(GatewayConstants.MESSAGE));
            return List.of(listElement);
        }
        return new HashMap<>(errorMap);
    }

    @Override
    public HttpStatusCode statusCode(Throwable throwable) {
        return IgniteGlobalExceptionHandler.determineHttpStatus(throwable);
    }
}
