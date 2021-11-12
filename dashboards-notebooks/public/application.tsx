/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

import React from 'react';
import ReactDOM from 'react-dom';
import { AppMountParameters, CoreStart } from '../../../src/core/public';
import { AppPluginStartDependencies } from './types';
import { NotebooksApp } from './components/app';

export const renderApp = (
  { notifications, http, chrome }: CoreStart,
  { navigation, dashboard }: AppPluginStartDependencies,
  { appBasePath, element }: AppMountParameters
) => {
  ReactDOM.render(
    <NotebooksApp
      basename={appBasePath}
      notifications={notifications}
      http={http}
      chrome={chrome}
      navigation={navigation}
      DashboardContainerByValueRenderer={dashboard.DashboardContainerByValueRenderer}
    />,
    element
  );

  return () => ReactDOM.unmountComponentAtNode(element);
};
