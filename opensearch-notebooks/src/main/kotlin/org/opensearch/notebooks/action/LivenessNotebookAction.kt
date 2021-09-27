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

package org.opensearch.notebooks.action

import org.opensearch.commons.authuser.User
import org.opensearch.action.ActionType
import org.opensearch.action.support.ActionFilters
import org.opensearch.client.Client
import org.opensearch.common.inject.Inject
import org.opensearch.common.xcontent.NamedXContentRegistry
import org.opensearch.notebooks.model.LivenessNotebookRequest
import org.opensearch.notebooks.model.LivenessNotebookResponse
import org.opensearch.transport.TransportService

/**
 * Notebook Liveness Healthcheck Action
 */
internal class LivenessNotebookAction @Inject constructor(
    transportService: TransportService,
    client: Client,
    actionFilters: ActionFilters,
    val xContentRegistry: NamedXContentRegistry
) : PluginBaseAction<LivenessNotebookRequest, LivenessNotebookResponse>(NAME,
    transportService,
    client,
    actionFilters,
    ::LivenessNotebookRequest) {
    companion object {
        private const val NAME = "cluster:admin/opendistro/notebooks/liveness"
        internal val ACTION_TYPE = ActionType(NAME, ::LivenessNotebookResponse)
    }

    /**
     * {@inheritDoc}
     */
    override fun executeRequest(request: LivenessNotebookRequest, user: User?): LivenessNotebookResponse {
        return NotebookActions.livenessCheck(request, user)
    }
}

