/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */
 
package org.opensearch.notebooks.resthandler

import org.opensearch.notebooks.NotebooksPlugin.Companion.BASE_NOTEBOOKS_URI
import org.opensearch.notebooks.action.GetAllNotebooksAction
import org.opensearch.notebooks.action.NotebookActions
import org.opensearch.notebooks.model.GetAllNotebooksRequest
import org.opensearch.notebooks.model.RestTag.FROM_INDEX_FIELD
import org.opensearch.notebooks.model.RestTag.MAX_ITEMS_FIELD
import org.opensearch.notebooks.settings.PluginSettings
import org.opensearch.client.node.NodeClient
import org.opensearch.rest.BaseRestHandler
import org.opensearch.rest.BaseRestHandler.RestChannelConsumer
import org.opensearch.rest.BytesRestResponse
import org.opensearch.rest.RestHandler.Route
import org.opensearch.rest.RestRequest
import org.opensearch.rest.RestRequest.Method.GET
import org.opensearch.rest.RestStatus

/**
 * Rest handler for getting list of notebooks.
 * This handler uses [NotebookActions].
 */
internal class NotebookListRestHandler : BaseRestHandler() {
    companion object {
        private const val NOTEBOOKS_LIST_ACTION = "notebooks_list_actions"
        private const val LIST_NOTEBOOKS_URL = "$BASE_NOTEBOOKS_URI/notebooks"
    }

    /**
     * {@inheritDoc}
     */
    override fun getName(): String {
        return NOTEBOOKS_LIST_ACTION
    }

    /**
     * {@inheritDoc}
     */
    override fun routes(): List<Route> {
        return listOf(
            /**
             * Get all notebooks (from optional fromIndex)
             * Request URL: GET LIST_NOTEBOOKS_URL[?[fromIndex=1000]&[maxItems=100]]
             * Request body: None
             * Response body: Ref [org.opensearch.notebooks.model.GetAllNotebooksResponse]
             */
            Route(GET, LIST_NOTEBOOKS_URL)
        )
    }

    /**
     * {@inheritDoc}
     */
    override fun prepareRequest(request: RestRequest, client: NodeClient): RestChannelConsumer {
        val from = request.param(FROM_INDEX_FIELD)?.toIntOrNull() ?: 0
        val maxItems = request.param(MAX_ITEMS_FIELD)?.toIntOrNull() ?: PluginSettings.defaultItemsQueryCount
        return when (request.method()) {
            GET -> RestChannelConsumer {
                client.execute(GetAllNotebooksAction.ACTION_TYPE,
                    GetAllNotebooksRequest(from, maxItems),
                    RestResponseToXContentListener(it))
            }
            else -> RestChannelConsumer {
                it.sendResponse(BytesRestResponse(RestStatus.METHOD_NOT_ALLOWED, "${request.method()} is not allowed"))
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    override fun responseParams(): Set<String> {
        return setOf(FROM_INDEX_FIELD, MAX_ITEMS_FIELD)
    }
}
