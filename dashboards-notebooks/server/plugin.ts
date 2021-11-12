/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

import {
  CoreSetup,
  CoreStart,
  ILegacyClusterClient,
  Logger,
  Plugin,
  PluginInitializerContext,
} from '../../../src/core/server';
import { OpenSearchNotebooksPlugin } from './adaptors/opensearch_notebooks_plugin';
import sqlPlugin from './clusters/sql/sqlPlugin';
import { serverRoute } from './routes';
import { NotebooksPluginSetup, NotebooksPluginStart } from './types';

export class NotebooksPlugin implements Plugin<NotebooksPluginSetup, NotebooksPluginStart> {
  private readonly logger: Logger;

  constructor(initializerContext: PluginInitializerContext) {
    this.logger = initializerContext.logger.get();
  }

  public setup(core: CoreSetup) {
    this.logger.debug('opensearch_dashboards_notebooks: Setup');
    const router = core.http.createRouter();

    const openSearchNotebooksClient: ILegacyClusterClient = core.opensearch.legacy.createClient(
      'opensearch_notebooks',
      {
        plugins: [OpenSearchNotebooksPlugin, sqlPlugin],
      }
    );
    core.http.registerRouteHandlerContext('notebooks_plugin', (context, request) => {
      return {
        logger: this.logger,
        opensearchNotebooksClient: openSearchNotebooksClient,
      };
    });

    // Register server side APIs
    serverRoute(router, openSearchNotebooksClient);

    return {};
  }

  public start(core: CoreStart) {
    this.logger.debug('opensearch_dashboards_notebooks: Started');
    return {};
  }

  public stop() {}
}
