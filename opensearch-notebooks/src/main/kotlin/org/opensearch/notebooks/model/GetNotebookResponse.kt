/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensearch.notebooks.model

import org.opensearch.notebooks.NotebooksPlugin.Companion.LOG_PREFIX
import org.opensearch.notebooks.util.createJsonParser
import org.opensearch.notebooks.util.logger
import org.opensearch.common.io.stream.StreamInput
import org.opensearch.common.io.stream.StreamOutput
import org.opensearch.common.xcontent.ToXContent
import org.opensearch.common.xcontent.XContentBuilder
import org.opensearch.common.xcontent.XContentFactory
import org.opensearch.common.xcontent.XContentParser
import org.opensearch.common.xcontent.XContentParser.Token
import org.opensearch.common.xcontent.XContentParserUtils
import java.io.IOException

/**
 * Get Notebook info response.
 * <pre> JSON format
 * {@code
 * {
 *   "notebookDetails":{
 *      // refer [org.opensearch.notebooks.model.NotebookDetails]
 *   }
 * }
 * }</pre>
 */
internal class GetNotebookResponse : BaseResponse {
    val notebookDetails: NotebookDetails
    private val filterSensitiveInfo: Boolean

    companion object {
        private val log by logger(GetNotebookResponse::class.java)
    }

    constructor(notebook: NotebookDetails, filterSensitiveInfo: Boolean) : super() {
        this.notebookDetails = notebook
        this.filterSensitiveInfo = filterSensitiveInfo
    }

    @Throws(IOException::class)
    constructor(input: StreamInput) : this(input.createJsonParser())

    /**
     * Parse the data from parser and create [GetNotebookResponse] object
     * @param parser data referenced at parser
     */
    constructor(parser: XContentParser) : super() {
        var notebook: NotebookDetails? = null
        XContentParserUtils.ensureExpectedToken(Token.START_OBJECT, parser.currentToken(), parser)
        while (Token.END_OBJECT != parser.nextToken()) {
            val fieldName = parser.currentName()
            parser.nextToken()
            when (fieldName) {
                RestTag.NOTEBOOK_DETAILS_FIELD -> notebook = NotebookDetails.parse(parser)
                else -> {
                    parser.skipChildren()
                    log.info("$LOG_PREFIX:Skipping Unknown field $fieldName")
                }
            }
        }
        notebook ?: throw IllegalArgumentException("${RestTag.NOTEBOOK_FIELD} field absent")
        this.notebookDetails = notebook
        this.filterSensitiveInfo = false // Sensitive info Must have filtered when created json object
    }

    /**
     * {@inheritDoc}
     */
    @Throws(IOException::class)
    override fun writeTo(output: StreamOutput) {
        toXContent(XContentFactory.jsonBuilder(output), ToXContent.EMPTY_PARAMS)
    }

    /**
     * {@inheritDoc}
     */
    override fun toXContent(builder: XContentBuilder?, params: ToXContent.Params?): XContentBuilder {
        val xContentParams = if (filterSensitiveInfo) {
            RestTag.FILTERED_REST_OUTPUT_PARAMS
        } else {
            RestTag.REST_OUTPUT_PARAMS
        }
        builder!!.startObject()
            .field(RestTag.NOTEBOOK_DETAILS_FIELD)
        notebookDetails.toXContent(builder, xContentParams)
        return builder.endObject()
    }
}
