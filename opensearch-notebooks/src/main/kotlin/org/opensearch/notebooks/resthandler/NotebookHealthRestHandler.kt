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

package org.opensearch.notebooks.resthandler

import org.opensearch.client.node.NodeClient
import org.opensearch.notebooks.NotebooksPlugin
import org.opensearch.notebooks.action.LivenessNotebookAction
import org.opensearch.notebooks.action.NotebookActions
import org.opensearch.notebooks.action.StartupNotebookAction
import org.opensearch.notebooks.model.LivenessNotebookRequest
import org.opensearch.notebooks.model.StartupNotebookRequest
import org.opensearch.notebooks.util.contentParserNextToken

import org.opensearch.rest.BaseRestHandler
import org.opensearch.rest.BytesRestResponse
import org.opensearch.rest.RestRequest
import org.opensearch.rest.RestStatus
import org.opensearch.rest.RestHandler


/**
 * Rest handler for notebooks lifecycle management.
 * This handler uses [NotebookActions].
 */
internal class NotebookHealthRestHandler : BaseRestHandler() {
    companion object {
        private const val NOTEBOOKS_HEALTHCHECKS = "notebooks_healthchecks"
        private const val NOTEBOOKS_STARTUP_URL = "${NotebooksPlugin.BASE_HEALTHCHECKS_URI}/startup/${NotebooksPlugin.PLUGIN_NAME}"
        private const val NOTEBOOKS_LIVENESS_URL = "${NotebooksPlugin.BASE_HEALTHCHECKS_URI}/liveness/${NotebooksPlugin.PLUGIN_NAME}"
    }

    /**
     * {@inheritDoc}
     */
    override fun getName(): String {
        return NOTEBOOKS_HEALTHCHECKS
    }

    /**
     * {@inheritDoc}
     */
    override fun routes(): List<RestHandler.Route> {
        return listOf(
            /**
             * Report Health of notebooks post startup
             * Request URL: GET NOTEBOOKS_STARTUP_URL
             * Request body: Ref [org.opensearch.notebooks.model.StartupNotebookRequest]
             * Response body: Ref [org.opensearch.notebooks.model.StartupNotebookResponse]
             */
            RestHandler.Route(RestRequest.Method.GET, NOTEBOOKS_STARTUP_URL),
            /**
             * Report Health of notebooks post based on a trigger (Manual or Time-based)
             * Request URL: POST NOTEBOOKS_LIVENESS_URL
             * Request body: Ref [org.opensearch.notebooks.model.LivenessNotebookRequest]
             * Response body: Ref [org.opensearch.notebooks.model.LivenessNotebookResponse]
             */
            RestHandler.Route(RestRequest.Method.POST, NOTEBOOKS_LIVENESS_URL),
        )
    }

    /**
     * {@inheritDoc}
     */
    override fun prepareRequest(request: RestRequest, client: NodeClient): RestChannelConsumer {
        return when (request.method()) {
            RestRequest.Method.GET -> RestChannelConsumer {
                client.execute(StartupNotebookAction.ACTION_TYPE,
                    StartupNotebookRequest(),
                    RestResponseToXContentListener(it))
            }
            RestRequest.Method.POST -> RestChannelConsumer {
                client.execute(LivenessNotebookAction.ACTION_TYPE,
                    LivenessNotebookRequest(request.contentParserNextToken()),
                    RestResponseToXContentListener(it))
            }
            else -> RestChannelConsumer {
                it.sendResponse(BytesRestResponse(RestStatus.METHOD_NOT_ALLOWED, "${request.method()} is not allowed"))
            }
        }
    }
}
