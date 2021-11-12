/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensearch.notebooks

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.junit.Assert
import java.time.Instant
import kotlin.test.assertTrue

private const val DEFAULT_TIME_ACCURACY_SEC = 5L

fun constructNotebookRequest(name: String = "test"): String {
    return """
        {
            "notebook":{
                "dateCreated" : "2020-12-11T20:51:15.509Z",
                "name" : "$name",
                "dateModified" : "2020-12-11T21:04:55.336Z",
                "backend" : "Default",
                "paragraphs" : [
                    {
                        "output" : [
                            {
                                "result" : "# This is a markdown paragraph",
                                "outputType" : "MARKDOWN",
                                "execution_time" : "0s"
                            }
                        ],
                        "input" : {
                            "inputText" : "# This is a markdown paragraph",
                            "inputType" : "MARKDOWN"
                        },
                        "dateCreated" : "2020-12-11T21:04:39.997Z",
                        "dateModified" : "2020-12-11T21:04:48.207Z",
                        "id" : "paragraph_61e96a10-af19-4c7d-ae4e-d2e449c65dff"
                    }
                ]
            }
        }
    """.trimIndent()
}

fun jsonify(text: String): JsonObject {
    return JsonParser.parseString(text).asJsonObject
}

fun validateTimeNearRefTime(time: Instant, refTime: Instant, accuracySeconds: Long) {
    assertTrue(time.plusSeconds(accuracySeconds).isAfter(refTime), "$time + $accuracySeconds > $refTime")
    assertTrue(time.minusSeconds(accuracySeconds).isBefore(refTime), "$time - $accuracySeconds < $refTime")
}

fun validateTimeRecency(time: Instant, accuracySeconds: Long = DEFAULT_TIME_ACCURACY_SEC) {
    validateTimeNearRefTime(time, Instant.now(), accuracySeconds)
}

fun validateErrorResponse(response: JsonObject, statusCode: Int, errorType: String = "status_exception") {
    Assert.assertNotNull("Error response content should be generated", response)
    val status = response.get("status").asInt
    val error = response.get("error").asJsonObject
    val rootCause = error.get("root_cause").asJsonArray
    val type = error.get("type").asString
    val reason = error.get("reason").asString
    Assert.assertEquals(statusCode, status)
    Assert.assertEquals(errorType, type)
    Assert.assertNotNull(reason)
    Assert.assertNotNull(rootCause)
    Assert.assertTrue(rootCause.size() > 0)
}
