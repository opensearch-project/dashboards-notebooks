/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

import { NoteRouter } from './noteRouter';
import { ParaRouter } from './paraRouter';
import { vizRouter } from './vizRouter';
import { sqlRouter } from './sqlRouter';
import { ILegacyClusterClient, IRouter } from '../../../../src/core/server';
import QueryService from '../services/queryService';

export function serverRoute(router: IRouter, client: ILegacyClusterClient) {
  ParaRouter(router);
  NoteRouter(router);
  vizRouter(router);

  const queryService = new QueryService(client);
  sqlRouter(router, queryService);
}
