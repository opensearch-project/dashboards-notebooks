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

/*
 * Copyright 2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 */

package org.opensearch.notebooks.action

import org.opensearch.commons.authuser.User
import org.opensearch.notebooks.NotebooksPlugin.Companion.LOG_PREFIX
import org.opensearch.notebooks.index.NotebooksIndex
import org.opensearch.notebooks.security.UserAccessManager
import org.opensearch.notebooks.util.logger
import org.opensearch.OpenSearchStatusException
import org.opensearch.notebooks.healthchecks.NotebooksHealthchecks
import org.opensearch.notebooks.healthchecks.NotebooksHealthchecks.KIBANA_INDEX_NAME
import org.opensearch.notebooks.healthchecks.NotebooksHealthchecks.SQL_PLUGIN_NAME
import org.opensearch.notebooks.index.NotebooksIndex.NOTEBOOKS_INDEX_NAME
import org.opensearch.notebooks.model.CreateNotebookRequest
import org.opensearch.notebooks.model.CreateNotebookResponse
import org.opensearch.notebooks.model.DeleteNotebookRequest
import org.opensearch.notebooks.model.DeleteNotebookResponse
import org.opensearch.notebooks.model.GetAllNotebooksRequest
import org.opensearch.notebooks.model.GetAllNotebooksResponse
import org.opensearch.notebooks.model.GetNotebookRequest
import org.opensearch.notebooks.model.GetNotebookResponse
import org.opensearch.notebooks.model.LivenessNotebookRequest
import org.opensearch.notebooks.model.LivenessNotebookResponse
import org.opensearch.notebooks.model.NotebookDetails
import org.opensearch.notebooks.model.StartupNotebookRequest
import org.opensearch.notebooks.model.StartupNotebookResponse
import org.opensearch.notebooks.model.UpdateNotebookRequest
import org.opensearch.notebooks.model.UpdateNotebookResponse
import org.opensearch.rest.RestStatus
import java.time.Instant

/**
 * Notebook index operation actions.
 */
internal object NotebookActions {
    private val log by logger(NotebookActions::class.java)

    /**
     * Create new notebook
     * @param request [CreateNotebookRequest] object
     * @return [CreateNotebookResponse]
     */
    fun create(request: CreateNotebookRequest, user: User?): CreateNotebookResponse {
        log.info("$LOG_PREFIX:Notebook-create")
        UserAccessManager.validateUser(user)
        val currentTime = Instant.now()
        val notebookDetails = NotebookDetails(
            "ignore",
            currentTime,
            currentTime,
            UserAccessManager.getUserTenant(user),
            UserAccessManager.getAllAccessInfo(user),
            request.notebook
        )
        val docId = NotebooksIndex.createNotebook(notebookDetails)
        docId ?: throw OpenSearchStatusException(
            "Notebook Creation failed",
            RestStatus.INTERNAL_SERVER_ERROR
        )
        return CreateNotebookResponse(docId)
    }

    /**
     * Update Notebook
     * @param request [UpdateNotebookRequest] object
     * @return [UpdateNotebookResponse]
     */
    fun update(request: UpdateNotebookRequest, user: User?): UpdateNotebookResponse {
        log.info("$LOG_PREFIX:Notebook-update ${request.notebookId}")
        UserAccessManager.validateUser(user)
        val currentNotebookDetails = NotebooksIndex.getNotebook(request.notebookId)
        currentNotebookDetails
            ?: throw OpenSearchStatusException("Notebook ${request.notebookId} not found", RestStatus.NOT_FOUND)
        if (!UserAccessManager.doesUserHasAccess(user, currentNotebookDetails.tenant, currentNotebookDetails.access)) {
            throw OpenSearchStatusException(
                "Permission denied for Notebook ${request.notebookId}",
                RestStatus.FORBIDDEN
            )
        }
        val currentTime = Instant.now()
        val notebookDetails = NotebookDetails(
            request.notebookId,
            currentTime,
            currentNotebookDetails.createdTime,
            UserAccessManager.getUserTenant(user),
            currentNotebookDetails.access,
            request.notebook
        )
        if (!NotebooksIndex.updateNotebook(request.notebookId, notebookDetails)) {
            throw OpenSearchStatusException("Notebook Update failed", RestStatus.INTERNAL_SERVER_ERROR)
        }
        return UpdateNotebookResponse(request.notebookId)
    }

    /**
     * Get Notebook info
     * @param request [GetNotebookRequest] object
     * @return [GetNotebookResponse]
     */
    fun info(request: GetNotebookRequest, user: User?): GetNotebookResponse {
        log.info("$LOG_PREFIX:Notebook-info ${request.notebookId}")
        UserAccessManager.validateUser(user)
        val notebookDetails = NotebooksIndex.getNotebook(request.notebookId)
        notebookDetails
            ?: throw OpenSearchStatusException("Notebook ${request.notebookId} not found", RestStatus.NOT_FOUND)
        if (!UserAccessManager.doesUserHasAccess(user, notebookDetails.tenant, notebookDetails.access)) {
            throw OpenSearchStatusException(
                "Permission denied for Notebook ${request.notebookId}",
                RestStatus.FORBIDDEN
            )
        }
        return GetNotebookResponse(notebookDetails, UserAccessManager.hasAllInfoAccess(user))
    }

    /**
     * Delete Notebook
     * @param request [DeleteNotebookRequest] object
     * @return [DeleteNotebookResponse]
     */
    fun delete(request: DeleteNotebookRequest, user: User?): DeleteNotebookResponse {
        log.info("$LOG_PREFIX:Notebook-delete ${request.notebookId}")
        UserAccessManager.validateUser(user)
        val notebookDetails = NotebooksIndex.getNotebook(request.notebookId)
        notebookDetails
            ?: throw OpenSearchStatusException(
                "Notebook ${request.notebookId} not found",
                RestStatus.NOT_FOUND
            )
        if (!UserAccessManager.doesUserHasAccess(
                user,
                notebookDetails.tenant,
                notebookDetails.access
            )
        ) {
            throw OpenSearchStatusException(
                "Permission denied for Notebook ${request.notebookId}",
                RestStatus.FORBIDDEN
            )
        }
        if (!NotebooksIndex.deleteNotebook(request.notebookId)) {
            throw OpenSearchStatusException(
                "Notebook ${request.notebookId} delete failed",
                RestStatus.REQUEST_TIMEOUT
            )
        }
        return DeleteNotebookResponse(request.notebookId)
    }

    /**
     * Get all Notebooks
     * @param request [GetAllNotebooksRequest] object
     * @return [GetAllNotebooksResponse]
     */
    fun getAll(request: GetAllNotebooksRequest, user: User?): GetAllNotebooksResponse {
        log.info("$LOG_PREFIX:Notebook-getAll fromIndex:${request.fromIndex} maxItems:${request.maxItems}")
        UserAccessManager.validateUser(user)
        val notebooksList = NotebooksIndex.getAllNotebooks(
            UserAccessManager.getUserTenant(user),
            UserAccessManager.getSearchAccessInfo(user),
            request.fromIndex,
            request.maxItems
        )
        return GetAllNotebooksResponse(notebooksList, UserAccessManager.hasAllInfoAccess(user))
    }

    /**
     * Validates type of trigger for liveness healthcheck
     * @param triggerType [String]
     * @return [Boolean]
     */
    private fun validateTriggerType (triggerType : String): Boolean{
        return triggerType == "Manual" || triggerType == "Time-Based"
    }

    /**
     * Build a response for health checks using status information of dependencies and indices
     * @return [Map] map of message and description
     */
    private fun buildMessageResponse (): Map<String, String> {
        var message = ""
        var description = ""

        val healthChecks = NotebooksHealthchecks.checks()
        val kibanaIndexStatus = healthChecks[KIBANA_INDEX_NAME] ?: ""
        val notebookIndexStatus = healthChecks[NOTEBOOKS_INDEX_NAME] ?: ""
        val sqlPluginStatus = healthChecks[SQL_PLUGIN_NAME] ?: ""

        if (kibanaIndexStatus == "green" && notebookIndexStatus == "green" && sqlPluginStatus == "available"){
            message = "Alive"
            description = "Accepting Traffic"
        }

        if (kibanaIndexStatus == "red" || notebookIndexStatus == "red" || sqlPluginStatus == "not available"){
            message = "Waiting"
            description = "Waiting for a Dependency to load"
        }

        if (kibanaIndexStatus == "" || notebookIndexStatus == "" || sqlPluginStatus == ""){
            message = "Error"
            description = "Error in Fetching Index/Plugin status"
        }

        return mapOf("message" to message, "description" to description, KIBANA_INDEX_NAME to kibanaIndexStatus,
            NOTEBOOKS_INDEX_NAME to notebookIndexStatus, SQL_PLUGIN_NAME to sqlPluginStatus)
    }

    /**
     * Perform startupCheck
     * @param request [StartupNotebookRequest] object
     * @return [StartupNotebookResponse]
     */
    fun startupCheck(request: StartupNotebookRequest, user: User?): StartupNotebookResponse{
        log.info("$LOG_PREFIX:Notebook-startup healthcheck")
        UserAccessManager.validateUser(user)

        val builtMessageResponse = buildMessageResponse()
        val message = builtMessageResponse["message"]?: ""
        val description = builtMessageResponse["description"]?: ""
        val kibanaIndexStatus = builtMessageResponse[KIBANA_INDEX_NAME] ?: ""
        val notebookIndexStatus = builtMessageResponse[NOTEBOOKS_INDEX_NAME] ?: ""
        val sqlPluginStatus = builtMessageResponse[SQL_PLUGIN_NAME] ?: ""

        return StartupNotebookResponse(message, description, kibanaIndexStatus, notebookIndexStatus, sqlPluginStatus)
    }

    /**
     * Perform livenessCheck
     * @param request [LivenessNotebookRequest] object
     * @return [LivenessNotebookResponse]
     */
    fun livenessCheck(request: LivenessNotebookRequest, user: User?): LivenessNotebookResponse{
        log.info("$LOG_PREFIX:Notebook-liveness healthcheck")
        UserAccessManager.validateUser(user)

        if (!validateTriggerType(request.triggerType)){
            throw IllegalArgumentException("Invalid type of Trigger for liveness healthcheck request")
        }

        val builtMessageResponse = buildMessageResponse()
        val message = builtMessageResponse["message"]?: ""
        val description = builtMessageResponse["description"]?: ""
        val kibanaIndexStatus = builtMessageResponse[KIBANA_INDEX_NAME] ?: ""
        val notebookIndexStatus = builtMessageResponse[NOTEBOOKS_INDEX_NAME] ?: ""
        val sqlPluginStatus = builtMessageResponse[SQL_PLUGIN_NAME] ?: ""

        return LivenessNotebookResponse(message, description, kibanaIndexStatus, notebookIndexStatus, sqlPluginStatus)
    }
}
