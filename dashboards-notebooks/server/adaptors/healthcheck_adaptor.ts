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

import fetch from 'node-fetch';
import { AccessInfoType } from '..';

// Uses OpenSearch-Dashboard status API for check the availability of reporting dashboard plugin
export class HealtcheckAdaptor {
  statusAPIUrl: string;
  reportingAvailability = 'not available';
  customMessage = { warning: 'reporting functionality is not avaiable' };

  constructor(accessInfo: AccessInfoType) {
    // Create request URL using server access info
    this.statusAPIUrl =
      accessInfo.serverInfo.protocol +
      '://' +
      accessInfo.serverInfo.hostname +
      ':' +
      accessInfo.serverInfo.port +
      accessInfo.basePath +
      '/api/status';
  }

  runCheck = async (triggerType?: string) => {
    if (triggerType != undefined) {
      if (!(['Manual', 'Time-Based'].includes(triggerType))) {
        throw new Error('Invalid triggerType parameter for healthcheck.');
      }
    }

    try {
      const fetchResponse = await fetch(`${this.statusAPIUrl}`, {
        headers: {
          'osd-xsrf': 'true',
          'accept-language': 'en-US,en;q=0.9,zh-CN;q=0.8,zh;q=0.7,zh-TW;q=0.6',
          pragma: 'no-cache',
          'sec-fetch-dest': 'empty',
          'sec-fetch-mode': 'cors',
          'sec-fetch-site': 'same-origin',
        },
        method: 'GET',
      });
      const data = await fetchResponse.json();

      for (let i = 0; i < data.status.statuses.length; ++i) {
        if (data.status.statuses[i].id.includes('plugin:reportsDashboards')) {
          this.reportingAvailability = 'available';
          this.customMessage = { warning: '' };
        }
      }

      return {
        reportingAvailability: this.reportingAvailability,
        customMessage: this.customMessage,
      };
    } catch (error) {
      throw new Error('Healthcheck status request error:' + error);
    }
  };
}
