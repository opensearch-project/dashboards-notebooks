/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensearch.notebooks.model

import org.opensearch.notebooks.model.RestTag.NOTEBOOK_LIST_FIELD
import org.opensearch.action.search.SearchResponse
import org.opensearch.common.xcontent.XContentParser

/**
 * Notebooks search results
 */
internal class NotebookDetailsSearchResults : SearchResults<NotebookDetails> {
    constructor(parser: XContentParser) : super(parser, NOTEBOOK_LIST_FIELD)

    constructor(from: Long, response: SearchResponse) : super(from, response, NOTEBOOK_LIST_FIELD)

    /**
     * {@inheritDoc}
     */
    override fun parseItem(parser: XContentParser, useId: String?): NotebookDetails {
        return NotebookDetails.parse(parser, useId)
    }
}
