/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 *
 * Modifications Copyright OpenSearch Contributors. See
 * GitHub history for details.
 */

package org.opensearch.notebooks.model

import org.opensearch.notebooks.NotebooksPlugin.Companion.LOG_PREFIX
import org.opensearch.notebooks.util.logger
import org.opensearch.action.ActionRequest
import org.opensearch.action.ActionRequestValidationException
import org.opensearch.common.io.stream.StreamInput
import org.opensearch.common.io.stream.StreamOutput
import org.opensearch.common.xcontent.ToXContent
import org.opensearch.common.xcontent.ToXContentObject
import org.opensearch.common.xcontent.XContentBuilder
import org.opensearch.common.xcontent.XContentFactory
import org.opensearch.common.xcontent.XContentParser
import org.opensearch.common.xcontent.XContentParser.Token
import org.opensearch.common.xcontent.XContentParserUtils
import org.opensearch.notebooks.model.RestTag.HEALTHCHECK_TRIGGER_TYPE
import org.opensearch.notebooks.util.createJsonParser
import java.io.IOException

/**
 * Notebook-delete request.
 * notebookId is from request query params
 * <pre> JSON format
 * {@code
 * {
 *   "notebookId":"notebookId"
 * }
 * }</pre>
 */
internal class LivenessNotebookRequest: ActionRequest, ToXContentObject {
    var triggerType: String = ""

    companion object {
        private val log by logger(LivenessNotebookRequest::class.java)
    }

    constructor(triggerType: String) : super() {
        this.triggerType = triggerType
    }

    @Throws(IOException::class)
    constructor(input: StreamInput) : this(input.createJsonParser())

    /**
     * Parse the data from parser and create [GetAllNotebooksResponse] object
     * @param parser data referenced at parser
     */
    constructor(parser: XContentParser) : super() {
        var triggerType: String? = null
        XContentParserUtils.ensureExpectedToken(Token.START_OBJECT, parser.currentToken(), parser)
        while (Token.END_OBJECT != parser.nextToken()) {
            val fieldName = parser.currentName()
            parser.nextToken()
            when (fieldName) {
                HEALTHCHECK_TRIGGER_TYPE -> triggerType = parser.text()
                else -> {
                    parser.skipChildren()
                    log.info("$LOG_PREFIX:Skipping Unknown field $fieldName")
                }
            }
        }
        triggerType ?: throw IllegalArgumentException("$HEALTHCHECK_TRIGGER_TYPE field absent")
        this.triggerType = triggerType
    }

    /**
     * {@inheritDoc}
     */
    @Throws(IOException::class)
    override fun writeTo(output: StreamOutput) {
        toXContent(XContentFactory.jsonBuilder(output), ToXContent.EMPTY_PARAMS)
    }

    /**
     * create XContentBuilder from this object using [XContentFactory.jsonBuilder()]
     * @param params XContent parameters
     * @return created XContentBuilder object
     */
    fun toXContent(params: ToXContent.Params = ToXContent.EMPTY_PARAMS): XContentBuilder? {
        return toXContent(XContentFactory.jsonBuilder(), params)
    }

    /**
     * {@inheritDoc}
     */
    override fun toXContent(builder: XContentBuilder?, params: ToXContent.Params?): XContentBuilder {
        return builder!!.startObject()
            .field(HEALTHCHECK_TRIGGER_TYPE, triggerType)
            .endObject()
    }

    /**
     * {@inheritDoc}
     */
    override fun validate(): ActionRequestValidationException? {
        return null
    }
}
