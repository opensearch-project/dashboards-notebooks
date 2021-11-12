/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensearch.notebooks.model

import org.opensearch.notebooks.util.createJsonParser
import org.opensearch.common.io.stream.StreamInput
import org.opensearch.common.io.stream.StreamOutput
import org.opensearch.common.xcontent.ToXContent
import org.opensearch.common.xcontent.XContentBuilder
import org.opensearch.common.xcontent.XContentFactory
import org.opensearch.common.xcontent.XContentParser
import java.io.IOException

/**
 * Get all notebooks response.
 * <pre> JSON format
 * {@code
 * {
 *   "startIndex":"0",
 *   "totalHits":"100",
 *   "totalHitRelation":"eq",
 *   "notebookDetailsList":[
 *      // refer [org.opensearch.notebooks.model.notebookDetails]
 *   ]
 * }
 * }</pre>
 */
internal class GetAllNotebooksResponse : BaseResponse {
    val notebookList: NotebookDetailsSearchResults
    private val filterSensitiveInfo: Boolean

    constructor(notebookList: NotebookDetailsSearchResults, filterSensitiveInfo: Boolean) : super() {
        this.notebookList = notebookList
        this.filterSensitiveInfo = filterSensitiveInfo
    }

    @Throws(IOException::class)
    constructor(input: StreamInput) : this(input.createJsonParser())

    /**
     * Parse the data from parser and create [GetAllNotebooksResponse] object
     * @param parser data referenced at parser
     */
    constructor(parser: XContentParser) : super() {
        notebookList = NotebookDetailsSearchResults(parser)
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
        return notebookList.toXContent(builder, xContentParams)
    }
}
