/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensearch.notebooks.action

import org.opensearch.commons.authuser.User
import org.opensearch.notebooks.model.CreateNotebookRequest
import org.opensearch.notebooks.model.CreateNotebookResponse
import org.opensearch.action.ActionType
import org.opensearch.action.support.ActionFilters
import org.opensearch.client.Client
import org.opensearch.common.inject.Inject
import org.opensearch.common.xcontent.NamedXContentRegistry
import org.opensearch.transport.TransportService

/**
 * Create notebook transport action
 */
internal class CreateNotebookAction @Inject constructor(
    transportService: TransportService,
    client: Client,
    actionFilters: ActionFilters,
    val xContentRegistry: NamedXContentRegistry
) : PluginBaseAction<CreateNotebookRequest, CreateNotebookResponse>(NAME,
    transportService,
    client,
    actionFilters,
    ::CreateNotebookRequest) {
    companion object {
        private const val NAME = "cluster:admin/opendistro/notebooks/create"
        internal val ACTION_TYPE = ActionType(NAME, ::CreateNotebookResponse)
    }

    /**
     * {@inheritDoc}
     */
    override fun executeRequest(request: CreateNotebookRequest, user: User?): CreateNotebookResponse {
        return NotebookActions.create(request, user)
    }
}
