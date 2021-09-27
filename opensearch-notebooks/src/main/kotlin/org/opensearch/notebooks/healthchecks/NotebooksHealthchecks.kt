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

package org.opensearch.notebooks.healthchecks

import org.opensearch.action.admin.cluster.health.ClusterHealthRequest
import org.opensearch.action.admin.cluster.node.info.NodesInfoRequest
import org.opensearch.action.admin.cluster.node.info.PluginsAndModules
import org.opensearch.notebooks.util.SecureIndexClient
import org.opensearch.notebooks.util.logger
import org.opensearch.client.Client
import org.opensearch.cluster.health.ClusterHealthStatus
import org.opensearch.cluster.service.ClusterService
import org.opensearch.notebooks.NotebooksPlugin
import org.opensearch.notebooks.index.NotebooksIndex.NOTEBOOKS_INDEX_NAME
import org.opensearch.plugins.PluginInfo


/**
 * Class for doing OpenSearch healthchecks for notebooks plugin in cluster.
 */
internal object NotebooksHealthchecks {
    private val log by logger(NotebooksHealthchecks::class.java)
    const val KIBANA_INDEX_NAME = ".kibana"
    const val SQL_PLUGIN_NAME = "opensearch-sql"

    private lateinit var client: Client
    private lateinit var clusterService: ClusterService

    /**
     * Initialize the class
     * @param client The OpenSearch client
     * @param clusterService The OpenSearch cluster service
     */
    fun initialize(client: Client, clusterService: ClusterService) {
        this.client = SecureIndexClient(client)
        this.clusterService = clusterService
    }

    /**
     * Make the health check requests
     * @return [Map] index health status and dependency plugin availability
     */
    fun checks(): Map<String, String> {
        val kibanaIndexStatus = checkIndexHealth(KIBANA_INDEX_NAME)
        val notebooksIndexStatus = checkIndexHealth(NOTEBOOKS_INDEX_NAME)
        val checkPluginStatus = checkPluginAvailability(SQL_PLUGIN_NAME)

        return mapOf(KIBANA_INDEX_NAME to kibanaIndexStatus, NOTEBOOKS_INDEX_NAME to notebooksIndexStatus,
            SQL_PLUGIN_NAME to checkPluginStatus)
    }

    /**
     * Check the availability of a plugin
     * @param pluginName [String] to be checked for availability
     * @return [String] value if the plugin "available" or "not available"
     */
    private fun checkPluginAvailability(pluginName: String): String {
        val nodesInfoRequest = NodesInfoRequest()
        nodesInfoRequest.addMetric(NodesInfoRequest.Metric.PLUGINS.metricName())
        val nodesInfoResponse = this.client.admin().cluster().nodesInfo(nodesInfoRequest).actionGet()
        val pluginInfos = nodesInfoResponse.nodes[0].getInfo(PluginsAndModules::class.java).pluginInfos
        return if (pluginInfos.stream()
                .anyMatch { pluginInfo: PluginInfo -> pluginInfo.name == pluginName }) {
            "available"
        } else {
            "not available"
        }
    }

    /**
     * Check the health of an index
     * @param indexName to be used for health check
     * @return [String] health value of the index "red", "green" or "yellow"
     */
    private fun checkIndexHealth(indexName: String): String {
        val request = ClusterHealthRequest()
        val response = this.client.admin().cluster().health(request.indices(indexName)).actionGet()
        return when (response.status) {
            ClusterHealthStatus.GREEN -> "green"
            ClusterHealthStatus.YELLOW -> "yellow"
            ClusterHealthStatus.RED -> "red"
            else -> {
                log.warn("${NotebooksPlugin.LOG_PREFIX}:checkIndexHealth - issue in response:$response")
                ""
            }
        }
    }


}
