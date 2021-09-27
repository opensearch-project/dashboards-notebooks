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
import org.opensearch.common.io.stream.StreamInput
import org.opensearch.common.io.stream.StreamOutput
import org.opensearch.common.xcontent.ToXContent
import org.opensearch.common.xcontent.XContentBuilder
import org.opensearch.common.xcontent.XContentFactory.jsonBuilder
import org.opensearch.common.xcontent.XContentParser
import org.opensearch.common.xcontent.XContentParser.Token
import org.opensearch.common.xcontent.XContentParserUtils
import org.opensearch.notebooks.healthchecks.NotebooksHealthchecks
import org.opensearch.notebooks.index.NotebooksIndex
import org.opensearch.notebooks.model.RestTag.HEALTHCHECK_CUSTOM_MESSAGE
import org.opensearch.notebooks.model.RestTag.HEALTHCHECK_DEPENDENCIES
import org.opensearch.notebooks.model.RestTag.HEALTHCHECK_DESCRIPTION
import org.opensearch.notebooks.model.RestTag.HEALTHCHECK_INDICES
import org.opensearch.notebooks.model.RestTag.HEALTHCHECK_KIBANA_INDEX
import org.opensearch.notebooks.model.RestTag.HEALTHCHECK_MESSAGE
import org.opensearch.notebooks.model.RestTag.HEALTHCHECK_NOTEBOOK_INDEX
import org.opensearch.notebooks.model.RestTag.HEALTHCHECK_SQL_PLUGIN
import org.opensearch.notebooks.util.createJsonParser
import java.io.IOException

/**
 * Get Notebook startup healthcheck response.
 * <pre> JSON format
 * {@code
 * {
 *   "message": "Initialized",
 *   "description": "Accepting Traffic",
 *   "dependencies": [
 *               {"dependency1": "available"},
 *               {"dependency2": "not available"}
 *             ],
 *
 *   "indices": [
 *              {"index1": "green"},
 *              {"index2": "green"}
 *          ],
 *   "customMessage":{}
 * }
 * }</pre>
 */
internal class StartupNotebookResponse : BaseResponse {

    var message: String = ""
    var description: String = ""
    var kibanaIndexStatus: String = ""
    var notebookIndexStatus: String = ""
    var sqlPluginStatus: String = ""

    companion object {
        private val log by logger(StartupNotebookResponse::class.java)
    }

    constructor(message: String, description: String, kibanaIndexStatus: String,
                notebookIndexStatus: String, sqlPluginStatus: String) : super() {
        this.message = message
        this.description = description
        this.kibanaIndexStatus = kibanaIndexStatus
        this.notebookIndexStatus = notebookIndexStatus
        this.sqlPluginStatus = sqlPluginStatus
    }

    @Throws(IOException::class)
    constructor(input: StreamInput) : this(input.createJsonParser())

    /**
     * Parse the data from parser and create [StartupNotebookResponse] object
     * @param parser data referenced at parser
     */
    constructor(parser: XContentParser) : super() {
        var message: String? = null
        var description: String? = null
        var kibanaIndexStatus: String? = null
        var notebookIndexStatus: String? = null
        var sqlPluginStatus: String? = null

        XContentParserUtils.ensureExpectedToken(Token.START_OBJECT, parser.currentToken(), parser)
        while (Token.END_OBJECT != parser.nextToken()) {
            val fieldName = parser.currentName()
            parser.nextToken()
            when (fieldName) {
                HEALTHCHECK_MESSAGE -> message = parser.text()
                HEALTHCHECK_DESCRIPTION -> description = parser.text()
                HEALTHCHECK_KIBANA_INDEX -> kibanaIndexStatus = parser.text()
                HEALTHCHECK_NOTEBOOK_INDEX -> notebookIndexStatus = parser.text()
                HEALTHCHECK_SQL_PLUGIN -> sqlPluginStatus = parser.text()
                else -> {
                    parser.skipChildren()
                    log.info("$LOG_PREFIX:Skipping Unknown field $fieldName")
                }
            }
        }
        message ?: throw IllegalArgumentException("$HEALTHCHECK_MESSAGE field absent")
        description ?: throw IllegalArgumentException("$HEALTHCHECK_DESCRIPTION field absent")
        kibanaIndexStatus ?: throw IllegalArgumentException("$HEALTHCHECK_KIBANA_INDEX field absent")
        notebookIndexStatus ?: throw IllegalArgumentException("$HEALTHCHECK_NOTEBOOK_INDEX field absent")
        sqlPluginStatus ?: throw IllegalArgumentException("$HEALTHCHECK_SQL_PLUGIN field absent")

        this.message = message
        this.description = description
        this.kibanaIndexStatus = kibanaIndexStatus
        this.notebookIndexStatus = notebookIndexStatus
        this.sqlPluginStatus = sqlPluginStatus
    }

    /**
     * {@inheritDoc}
     */
    @Throws(IOException::class)
    override fun writeTo(output: StreamOutput) {
        toXContent(jsonBuilder(output), ToXContent.EMPTY_PARAMS)
    }

    /**
     * {@inheritDoc}
     */
    override fun toXContent(builder: XContentBuilder?, params: ToXContent.Params?): XContentBuilder {

        return builder!!
            .startObject()
            .field(HEALTHCHECK_MESSAGE, message)
            .field(HEALTHCHECK_DESCRIPTION, description)
            .startArray(HEALTHCHECK_DEPENDENCIES)
            .startObject()
            .field(NotebooksHealthchecks.SQL_PLUGIN_NAME, sqlPluginStatus)
            .endObject()
            .endArray()
            .startArray(HEALTHCHECK_INDICES)
            .startObject()
            .field(NotebooksHealthchecks.KIBANA_INDEX_NAME, kibanaIndexStatus)
            .endObject()
            .startObject()
            .field(NotebooksIndex.NOTEBOOKS_INDEX_NAME, notebookIndexStatus)
            .endObject()
            .endArray()
            .startObject(HEALTHCHECK_CUSTOM_MESSAGE)
            .endObject()
            .endObject()

    }
}
