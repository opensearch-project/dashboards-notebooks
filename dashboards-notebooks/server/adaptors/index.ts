/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

import { SELECTED_BACKEND } from '../../common';
import { ZeppelinBackend } from './zeppelin_backend';
import { DefaultBackend } from './default_backend';

// Selects backend based on config
let BACKEND = new DefaultBackend();

if (SELECTED_BACKEND == 'ZEPPELIN') {
  BACKEND = new ZeppelinBackend();
}

export default BACKEND;
