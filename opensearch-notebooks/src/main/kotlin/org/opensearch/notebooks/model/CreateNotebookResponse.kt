/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensearch.notebooks.model

import org.opensearch.notebooks.NotebooksPlugin.Companion.LOG_PREFIX
import org.opensearch.notebooks.model.RestTag.NOTEBOOK_ID_FIELD
import org.opensearch.notebooks.util.logger
import org.opensearch.common.io.stream.StreamInput
import org.opensearch.common.io.stream.StreamOutput
import org.opensearch.common.xcontent.ToXContent
import org.opensearch.common.xcontent.XContentBuilder
import org.opensearch.common.xcontent.XContentParser
import org.opensearch.common.xcontent.XContentParser.Token
import org.opensearch.common.xcontent.XContentParserUtils
import java.io.IOException

/**
 * Notebook-create response.
 * <pre> JSON format
 * {@code
 * {
 *   "notebookId":"notebookId"
 * }
 * }</pre>
 */
internal class CreateNotebookResponse(
    val notebookId: String
) : BaseResponse() {

    @Throws(IOException::class)
    constructor(input: StreamInput) : this(
        notebookId = input.readString()
    )

    companion object {
        private val log by logger(CreateNotebookResponse::class.java)

        /**
         * Parse the data from parser and create [CreateNotebookResponse] object
         * @param parser data referenced at parser
         * @return created [CreateNotebookResponse] object
         */
        fun parse(parser: XContentParser): CreateNotebookResponse {
            var notebookId: String? = null
            XContentParserUtils.ensureExpectedToken(Token.START_OBJECT, parser.currentToken(), parser)
            while (Token.END_OBJECT != parser.nextToken()) {
                val fieldName = parser.currentName()
                parser.nextToken()
                when (fieldName) {
                    NOTEBOOK_ID_FIELD -> notebookId = parser.text()
                    else -> {
                        parser.skipChildren()
                        log.info("$LOG_PREFIX:Skipping Unknown field $fieldName")
                    }
                }
            }
            notebookId ?: throw IllegalArgumentException("$NOTEBOOK_ID_FIELD field absent")
            return CreateNotebookResponse(notebookId)
        }
    }

    /**
     * {@inheritDoc}
     */
    @Throws(IOException::class)
    override fun writeTo(output: StreamOutput) {
        output.writeString(notebookId)
    }

    /**
     * {@inheritDoc}
     */
    override fun toXContent(builder: XContentBuilder?, params: ToXContent.Params?): XContentBuilder {
        return builder!!.startObject()
            .field(NOTEBOOK_ID_FIELD, notebookId)
            .endObject()
    }
}
