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
 */

import { NoteRouter } from './noteRouter';
import { ParaRouter } from './paraRouter';
import { vizRouter } from './vizRouter';
import { sqlRouter } from './sqlRouter';
import { ILegacyClusterClient, IRouter } from '../../../../src/core/server';
import QueryService from '../services/queryService';
import { HealthRouter } from './healthRouter';
import { AccessInfoType } from '..';

export function serverRoute(router: IRouter, client: ILegacyClusterClient, accessInfo: AccessInfoType) {
  ParaRouter(router);
  NoteRouter(router);
  vizRouter(router);
  HealthRouter(router, accessInfo);

  const queryService = new QueryService(client);
  sqlRouter(router, queryService);
}
