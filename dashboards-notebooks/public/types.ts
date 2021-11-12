/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

import { NavigationPublicPluginStart } from '../../../src/plugins/navigation/public';
import { DashboardStart } from '../../../src/plugins/dashboard/public';

export interface NotebooksPluginSetup {
  getGreeting: () => string;
}
// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface NotebooksPluginStart {}

export interface AppPluginStartDependencies {
  navigation: NavigationPublicPluginStart;
  dashboard: DashboardStart;
}
