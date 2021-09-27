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

import { schema } from '@osd/config-schema';
import { IRouter, IOpenSearchDashboardsResponse, ResponseError } from '../../../../src/core/server';
import { HEALTHCHECK_API_PREFIX} from '../../common';
import { AccessInfoType } from '..';
import { HealtcheckAdaptor } from '../adaptors/healthcheck_adaptor';

export function HealthRouter(router: IRouter, accessInfo: AccessInfoType) {
  // Perform Startup Healtchecks
  router.get(
    {
      path: `${HEALTHCHECK_API_PREFIX}/startup/notebooksDashboards`,
      validate: {},
    },
    async (
      context,
      request,
      response
    ): Promise<IOpenSearchDashboardsResponse<any | ResponseError>> => {
      try {
        const healthcheckResponse = await new HealtcheckAdaptor(accessInfo).runCheck();

        return response.ok({
          body: {
            message: 'Initialized',
            description: 'Accepting Traffic',
            dependencies: [{ reportsDashboards: healthcheckResponse.reportingAvailability }],
            indices: [],
            customMessage: healthcheckResponse.customMessage,
          },
        });
      } catch (error: any) {
        return response.custom({
          statusCode: error.statusCode || 500,
          body: {
            message: 'Error',
            description: error.message,
            dependencies: [],
            indices: [],
            customMessage: {},
          },
        });
      }
    }
  );

  // Perform Liveness Healtchecks
  router.post(
    {
      path: `${HEALTHCHECK_API_PREFIX}/liveness/notebooksDashboards`,
      validate: {
        body: schema.object({
          triggerType: schema.string(),
        }),
      },
    },
    async (
      context,
      request,
      response
    ): Promise<IOpenSearchDashboardsResponse<any | ResponseError>> => {
      try {
        const healthcheckResponse = await new HealtcheckAdaptor(accessInfo).runCheck(
          request.body.triggerType
        );

        return response.ok({
          body: {
            message: 'Alive',
            description: 'Accepting Traffic',
            dependencies: [{ reportsDashboards: healthcheckResponse.reportingAvailability }],
            indices: [],
            customMessage: healthcheckResponse.customMessage,
          },
        });
      } catch (error: any) {
        return response.custom({
          statusCode: error.statusCode || 500,
          body: {
            message: 'Error',
            description: error.message,
            dependencies: [],
            indices: [],
            customMessage: {},
          },
        });
      }
    }
  );
}
