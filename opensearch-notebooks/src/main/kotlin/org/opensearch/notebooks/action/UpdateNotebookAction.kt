/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensearch.notebooks.action

import org.opensearch.commons.authuser.User
import org.opensearch.notebooks.model.UpdateNotebookRequest
import org.opensearch.notebooks.model.UpdateNotebookResponse
import org.opensearch.action.ActionType
import org.opensearch.action.support.ActionFilters
import org.opensearch.client.Client
import org.opensearch.common.inject.Inject
import org.opensearch.common.xcontent.NamedXContentRegistry
import org.opensearch.transport.TransportService

/**
 * Update notebook transport action
 */
internal class UpdateNotebookAction @Inject constructor(
    transportService: TransportService,
    client: Client,
    actionFilters: ActionFilters,
    val xContentRegistry: NamedXContentRegistry
) : PluginBaseAction<UpdateNotebookRequest, UpdateNotebookResponse>(NAME,
    transportService,
    client,
    actionFilters,
    ::UpdateNotebookRequest) {
    companion object {
        private const val NAME = "cluster:admin/opendistro/notebooks/update"
        internal val ACTION_TYPE = ActionType(NAME, ::UpdateNotebookResponse)
    }

    /**
     * {@inheritDoc}
     */
    override fun executeRequest(request: UpdateNotebookRequest, user: User?): UpdateNotebookResponse {
        return NotebookActions.update(request, user)
    }
}
