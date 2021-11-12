/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */
 
package org.opensearch.notebooks.resthandler

import org.opensearch.notebooks.NotebooksPlugin.Companion.BASE_NOTEBOOKS_URI
import org.opensearch.notebooks.action.CreateNotebookAction
import org.opensearch.notebooks.action.DeleteNotebookAction
import org.opensearch.notebooks.action.GetNotebookAction
import org.opensearch.notebooks.action.NotebookActions
import org.opensearch.notebooks.action.UpdateNotebookAction
import org.opensearch.notebooks.model.CreateNotebookRequest
import org.opensearch.notebooks.model.DeleteNotebookRequest
import org.opensearch.notebooks.model.GetNotebookRequest
import org.opensearch.notebooks.model.RestTag.NOTEBOOK_ID_FIELD
import org.opensearch.notebooks.model.UpdateNotebookRequest
import org.opensearch.notebooks.util.contentParserNextToken
import org.opensearch.client.node.NodeClient
import org.opensearch.rest.BaseRestHandler
import org.opensearch.rest.BaseRestHandler.RestChannelConsumer
import org.opensearch.rest.BytesRestResponse
import org.opensearch.rest.RestHandler.Route
import org.opensearch.rest.RestRequest
import org.opensearch.rest.RestRequest.Method.DELETE
import org.opensearch.rest.RestRequest.Method.GET
import org.opensearch.rest.RestRequest.Method.POST
import org.opensearch.rest.RestRequest.Method.PUT
import org.opensearch.rest.RestStatus

/**
 * Rest handler for notebooks lifecycle management.
 * This handler uses [NotebookActions].
 */
internal class NotebookRestHandler : BaseRestHandler() {
    companion object {
        private const val NOTEBOOKS_ACTION = "notebooks_actions"
        private const val NOTEBOOKS_URL = "$BASE_NOTEBOOKS_URI/notebook"
    }

    /**
     * {@inheritDoc}
     */
    override fun getName(): String {
        return NOTEBOOKS_ACTION
    }

    /**
     * {@inheritDoc}
     */
    override fun routes(): List<Route> {
        return listOf(
            /**
             * Create a new notebook
             * Request URL: POST NOTEBOOKS_URL
             * Request body: Ref [org.opensearch.notebooks.model.CreateNotebookRequest]
             * Response body: Ref [org.opensearch.notebooks.model.CreateNotebookResponse]
             */
            Route(POST, NOTEBOOKS_URL),
            /**
             * Update notebook
             * Request URL: PUT NOTEBOOKS_URL/{notebookId}
             * Request body: Ref [org.opensearch.notebooks.model.UpdateNotebookRequest]
             * Response body: Ref [org.opensearch.notebooks.model.UpdateNotebookResponse]
             */
            Route(PUT, "$NOTEBOOKS_URL/{$NOTEBOOK_ID_FIELD}"),
            /**
             * Get a notebook
             * Request URL: GET NOTEBOOKS_URL/{notebookId}
             * Request body: Ref [org.opensearch.notebooks.model.GetNotebookRequest]
             * Response body: Ref [org.opensearch.notebooks.model.GetNotebookResponse]
             */
            Route(GET, "$NOTEBOOKS_URL/{$NOTEBOOK_ID_FIELD}"),
            /**
             * Delete notebook
             * Request URL: DELETE NOTEBOOKS_URL/{notebookId}
             * Request body: Ref [org.opensearch.notebooks.model.DeleteNotebookRequest]
             * Response body: Ref [org.opensearch.notebooks.model.DeleteNotebookResponse]
             */
            Route(DELETE, "$NOTEBOOKS_URL/{$NOTEBOOK_ID_FIELD}")
        )
    }

    /**
     * {@inheritDoc}
     */
    override fun responseParams(): Set<String> {
        return setOf(NOTEBOOK_ID_FIELD)
    }

    /**
     * {@inheritDoc}
     */
    override fun prepareRequest(request: RestRequest, client: NodeClient): RestChannelConsumer {
        return when (request.method()) {
            POST -> RestChannelConsumer {
                client.execute(CreateNotebookAction.ACTION_TYPE,
                    CreateNotebookRequest(request.contentParserNextToken()),
                    RestResponseToXContentListener(it))
            }
            PUT -> RestChannelConsumer {
                client.execute(
                    UpdateNotebookAction.ACTION_TYPE,
                    UpdateNotebookRequest(request.contentParserNextToken(), request.param(NOTEBOOK_ID_FIELD)),
                    RestResponseToXContentListener(it))
            }
            GET -> RestChannelConsumer {
                client.execute(GetNotebookAction.ACTION_TYPE,
                    GetNotebookRequest(request.param(NOTEBOOK_ID_FIELD)),
                    RestResponseToXContentListener(it))
            }
            DELETE -> RestChannelConsumer {
                client.execute(DeleteNotebookAction.ACTION_TYPE,
                    DeleteNotebookRequest(request.param(NOTEBOOK_ID_FIELD)),
                    RestResponseToXContentListener(it))
            }
            else -> RestChannelConsumer {
                it.sendResponse(BytesRestResponse(RestStatus.METHOD_NOT_ALLOWED, "${request.method()} is not allowed"))
            }
        }
    }
}
