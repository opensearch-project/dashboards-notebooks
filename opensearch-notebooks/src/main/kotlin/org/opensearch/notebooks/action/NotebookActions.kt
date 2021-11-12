/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */
 

package org.opensearch.notebooks.action

import org.opensearch.commons.authuser.User
import org.opensearch.notebooks.NotebooksPlugin.Companion.LOG_PREFIX
import org.opensearch.notebooks.index.NotebooksIndex
import org.opensearch.notebooks.model.CreateNotebookRequest
import org.opensearch.notebooks.model.CreateNotebookResponse
import org.opensearch.notebooks.model.DeleteNotebookRequest
import org.opensearch.notebooks.model.DeleteNotebookResponse
import org.opensearch.notebooks.model.GetAllNotebooksRequest
import org.opensearch.notebooks.model.GetAllNotebooksResponse
import org.opensearch.notebooks.model.GetNotebookRequest
import org.opensearch.notebooks.model.GetNotebookResponse
import org.opensearch.notebooks.model.NotebookDetails
import org.opensearch.notebooks.model.UpdateNotebookRequest
import org.opensearch.notebooks.model.UpdateNotebookResponse
import org.opensearch.notebooks.security.UserAccessManager
import org.opensearch.notebooks.util.logger
import org.opensearch.OpenSearchStatusException
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
}
