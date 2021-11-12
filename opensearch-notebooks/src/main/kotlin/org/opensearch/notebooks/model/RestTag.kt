/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */
 
package org.opensearch.notebooks.model

import org.opensearch.common.xcontent.ToXContent
import org.opensearch.common.xcontent.ToXContent.Params

/**
 * Plugin Rest common Tags.
 */
internal object RestTag {
    const val ID_FIELD = "id"
    const val UPDATED_TIME_FIELD = "lastUpdatedTimeMs"
    const val CREATED_TIME_FIELD = "createdTimeMs"
    const val TENANT_FIELD = "tenant"
    const val ACCESS_LIST_FIELD = "access"
    const val NOTEBOOK_LIST_FIELD = "notebookDetailsList"
    const val NOTEBOOK_FIELD = "notebook"
    const val NOTEBOOK_ID_FIELD = "notebookId"
    const val NOTEBOOK_DETAILS_FIELD = "notebookDetails"
    const val FROM_INDEX_FIELD = "fromIndex"
    const val MAX_ITEMS_FIELD = "maxItems"
    private val INCLUDE_ID = Pair(ID_FIELD, "true")
    private val EXCLUDE_ACCESS = Pair(ACCESS_LIST_FIELD, "false")
    val REST_OUTPUT_PARAMS: Params = ToXContent.MapParams(mapOf(INCLUDE_ID))
    val FILTERED_REST_OUTPUT_PARAMS: Params = ToXContent.MapParams(mapOf(INCLUDE_ID, EXCLUDE_ACCESS))
}
