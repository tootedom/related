/*
 *
 *  * Licensed to Relateit under one or more contributor
 *  * license agreements. See the NOTICE file distributed with
 *  * this work for additional information regarding copyright
 *  * ownership. Relateit licenses this file to you under
 *  * the Apache License, Version 2.0 (the "License"); you may
 *  * not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *    http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing,
 *  * software distributed under the License is distributed on an
 *  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  * KIND, either express or implied.  See the License for the
 *  * specific language governing permissions and limitations
 *  * under the License.
 *
 */

package org.greencheek.related.indexing;

/**
 * Base exception for the parsing of request data into a
 * {@link org.greencheek.related.api.indexing.RelatedItemIndexingMessage}
 */
public class InvalidIndexingRequestException extends RuntimeException {
    public InvalidIndexingRequestException(String message) {
        super(message);
    }

    public InvalidIndexingRequestException(Throwable error) {
        super(error);
    }

    public InvalidIndexingRequestException(String message, Throwable error) {
        super(message,error);
    }

}
