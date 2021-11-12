/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */
 
package org.opensearch.notebooks

import org.opensearch.notebooks.action.CreateNotebookAction
import org.opensearch.notebooks.action.DeleteNotebookAction
import org.opensearch.notebooks.action.GetAllNotebooksAction
import org.opensearch.notebooks.action.GetNotebookAction
import org.opensearch.notebooks.action.UpdateNotebookAction
import org.opensearch.notebooks.index.NotebooksIndex
import org.opensearch.notebooks.resthandler.NotebookListRestHandler
import org.opensearch.notebooks.resthandler.NotebookRestHandler
import org.opensearch.notebooks.settings.PluginSettings
import org.opensearch.action.ActionRequest
import org.opensearch.action.ActionResponse
import org.opensearch.client.Client
import org.opensearch.cluster.metadata.IndexNameExpressionResolver
import org.opensearch.cluster.node.DiscoveryNodes
import org.opensearch.cluster.service.ClusterService
import org.opensearch.common.io.stream.NamedWriteableRegistry
import org.opensearch.common.settings.ClusterSettings
import org.opensearch.common.settings.IndexScopedSettings
import org.opensearch.common.settings.Setting
import org.opensearch.common.settings.Settings
import org.opensearch.common.settings.SettingsFilter
import org.opensearch.common.xcontent.NamedXContentRegistry
import org.opensearch.env.Environment
import org.opensearch.env.NodeEnvironment
import org.opensearch.plugins.ActionPlugin
import org.opensearch.plugins.Plugin
import org.opensearch.repositories.RepositoriesService
import org.opensearch.rest.RestController
import org.opensearch.rest.RestHandler
import org.opensearch.script.ScriptService
import org.opensearch.threadpool.ThreadPool
import org.opensearch.watcher.ResourceWatcherService
import java.util.function.Supplier

/**
 * Entry point of the OpenSearch Notebooks plugin.
 * This class initializes the rest handlers.
 */
class NotebooksPlugin : Plugin(), ActionPlugin {

    companion object {
        const val PLUGIN_NAME = "opensearch-notebooks"
        const val LOG_PREFIX = "notebooks"
        const val BASE_NOTEBOOKS_URI = "/_plugins/_notebooks"
    }

    /**
     * {@inheritDoc}
     */
    override fun getSettings(): List<Setting<*>> {
        return PluginSettings.getAllSettings()
    }

    /**
     * {@inheritDoc}
     */
    override fun createComponents(
        client: Client,
        clusterService: ClusterService,
        threadPool: ThreadPool,
        resourceWatcherService: ResourceWatcherService,
        scriptService: ScriptService,
        xContentRegistry: NamedXContentRegistry,
        environment: Environment,
        nodeEnvironment: NodeEnvironment,
        namedWriteableRegistry: NamedWriteableRegistry,
        indexNameExpressionResolver: IndexNameExpressionResolver,
        repositoriesServiceSupplier: Supplier<RepositoriesService>
    ): Collection<Any> {
        PluginSettings.addSettingsUpdateConsumer(clusterService)
        NotebooksIndex.initialize(client, clusterService)
        return emptyList()
    }

    /**
     * {@inheritDoc}
     */
    override fun getRestHandlers(
        settings: Settings,
        restController: RestController,
        clusterSettings: ClusterSettings,
        indexScopedSettings: IndexScopedSettings,
        settingsFilter: SettingsFilter,
        indexNameExpressionResolver: IndexNameExpressionResolver,
        nodesInCluster: Supplier<DiscoveryNodes>
    ): List<RestHandler> {
        return listOf(
            NotebookRestHandler(),
            NotebookListRestHandler()
        )
    }

    /**
     * {@inheritDoc}
     */
    override fun getActions(): List<ActionPlugin.ActionHandler<out ActionRequest, out ActionResponse>> {
        return listOf(
            ActionPlugin.ActionHandler(CreateNotebookAction.ACTION_TYPE, CreateNotebookAction::class.java),
            ActionPlugin.ActionHandler(DeleteNotebookAction.ACTION_TYPE, DeleteNotebookAction::class.java),
            ActionPlugin.ActionHandler(GetAllNotebooksAction.ACTION_TYPE, GetAllNotebooksAction::class.java),
            ActionPlugin.ActionHandler(GetNotebookAction.ACTION_TYPE, GetNotebookAction::class.java),
            ActionPlugin.ActionHandler(UpdateNotebookAction.ACTION_TYPE, UpdateNotebookAction::class.java)
        )
    }
}
