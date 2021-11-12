/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

import React from 'react';
import { I18nProvider } from '@osd/i18n/react';

import { CoreStart } from '../../../../src/core/public';
import { NavigationPublicPluginStart } from '../../../../src/plugins/navigation/public';

import { DashboardStart } from '../../../../src/plugins/dashboard/public';
import { Main } from './main';

interface NotebooksAppDeps {
  basename: string;
  notifications: CoreStart['notifications'];
  http: CoreStart['http'];
  chrome: CoreStart['chrome'];
  navigation: NavigationPublicPluginStart;
  DashboardContainerByValueRenderer: DashboardStart['DashboardContainerByValueRenderer'];
}

export const NotebooksApp = ({
  basename,
  notifications,
  http,
  chrome,
  navigation,
  DashboardContainerByValueRenderer,
}: NotebooksAppDeps) => {
  // Render the application DOM.
  return (
    <I18nProvider>
      <>
        <Main
          basename={basename}
          DashboardContainerByValueRenderer={DashboardContainerByValueRenderer}
          http={http}
          setBreadcrumbs={chrome.setBreadcrumbs}
        />
      </>
    </I18nProvider>
  );
};
