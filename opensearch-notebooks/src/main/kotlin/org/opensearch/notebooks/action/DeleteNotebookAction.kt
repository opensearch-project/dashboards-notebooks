/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensearch.notebooks.action

import org.opensearch.commons.authuser.User
import org.opensearch.notebooks.model.DeleteNotebookRequest
import org.opensearch.notebooks.model.DeleteNotebookResponse
import org.opensearch.action.ActionType
import org.opensearch.action.support.ActionFilters
import org.opensearch.client.Client
import org.opensearch.common.inject.Inject
import org.opensearch.common.xcontent.NamedXContentRegistry
import org.opensearch.transport.TransportService

/**
 * Delete notebook transport action
 */
internal class DeleteNotebookAction @Inject constructor(
    transportService: TransportService,
    client: Client,
    actionFilters: ActionFilters,
    val xContentRegistry: NamedXContentRegistry
) : PluginBaseAction<DeleteNotebookRequest, DeleteNotebookResponse>(NAME,
    transportService,
    client,
    actionFilters,
    ::DeleteNotebookRequest) {
    companion object {
        private const val NAME = "cluster:admin/opendistro/notebooks/delete"
        internal val ACTION_TYPE = ActionType(NAME, ::DeleteNotebookResponse)
    }

    /**
     * {@inheritDoc}
     */
    override fun executeRequest(request: DeleteNotebookRequest, user: User?): DeleteNotebookResponse {
        return NotebookActions.delete(request, user)
    }
}
