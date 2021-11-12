/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

import { 
  SQL_TRANSLATE_ROUTE, 
  SQL_QUERY_ROUTE, 
  PPL_QUERY_ROUTE, 
  PPL_TRANSLATE_ROUTE
} from '../../services/utils/constants';


export default function sqlPlugin(Client, config, components) {
  const ca = components.clientAction.factory;

  Client.prototype.sql = components.clientAction.namespaceFactory();
  const sql = Client.prototype.sql.prototype;

  sql.translateSQL = ca({
    url: {
      fmt: `${SQL_TRANSLATE_ROUTE}`,
    },
    needBody: true,
    method: 'POST',
  });

  sql.translatePPL = ca({
    url: {
      fmt: `${PPL_TRANSLATE_ROUTE}`,
    },
    needBody: true,
    method: 'POST',
  });

  sql.sqlQuery = ca({
    url: {
      fmt: `${SQL_QUERY_ROUTE}`,
    },
    needBody: true,
    method: 'POST',
  }); //default: jdbc

  sql.pplQuery = ca({
    url: {
      fmt: `${PPL_QUERY_ROUTE}`,
    },
    needBody: true,
    method: 'POST',
  }); //default: jdbc
}
