/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensearch.notebooks.action

import org.opensearch.commons.authuser.User
import org.opensearch.notebooks.model.GetAllNotebooksRequest
import org.opensearch.notebooks.model.GetAllNotebooksResponse
import org.opensearch.action.ActionType
import org.opensearch.action.support.ActionFilters
import org.opensearch.client.Client
import org.opensearch.common.inject.Inject
import org.opensearch.common.xcontent.NamedXContentRegistry
import org.opensearch.transport.TransportService

/**
 * Get all notebooks transport action
 */
internal class GetAllNotebooksAction @Inject constructor(
    transportService: TransportService,
    client: Client,
    actionFilters: ActionFilters,
    val xContentRegistry: NamedXContentRegistry
) : PluginBaseAction<GetAllNotebooksRequest, GetAllNotebooksResponse>(NAME,
    transportService,
    client,
    actionFilters,
    ::GetAllNotebooksRequest) {
    companion object {
        private const val NAME = "cluster:admin/opendistro/notebooks/list"
        internal val ACTION_TYPE = ActionType(NAME, ::GetAllNotebooksResponse)
    }

    /**
     * {@inheritDoc}
     */
    override fun executeRequest(request: GetAllNotebooksRequest, user: User?): GetAllNotebooksResponse {
        return NotebookActions.getAll(request, user)
    }
}
