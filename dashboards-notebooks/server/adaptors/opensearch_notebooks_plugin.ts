/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

import { OPENSEARCH_NOTEBOOKS_API } from "../../common";

export function OpenSearchNotebooksPlugin(Client: any, config: any, components: any) {
  const clientAction = components.clientAction.factory;

  Client.prototype.notebooks = components.clientAction.namespaceFactory();
  const notebooks = Client.prototype.notebooks.prototype;

  notebooks.getNotebooks = clientAction({
    url: {
      fmt: OPENSEARCH_NOTEBOOKS_API.GET_NOTEBOOKS,
      params: {
        fromIndex: {
          type: 'number',
        },
        maxItems: {
          type: 'number',
        },
      },
    },
    method: 'GET',
  });

  notebooks.createNotebook = clientAction({
    url: {
      fmt: OPENSEARCH_NOTEBOOKS_API.NOTEBOOK,
    },
    method: 'POST',
    needBody: true,
  });

  notebooks.getNotebookById = clientAction({
    url: {
      fmt: `${OPENSEARCH_NOTEBOOKS_API.NOTEBOOK}/<%=notebookId%>`,
      req: {
        notebookId: {
          type: 'string',
          required: true,
        },
      },
    },
    method: 'GET',
  });

  notebooks.updateNotebookById = clientAction({
    url: {
      fmt: `${OPENSEARCH_NOTEBOOKS_API.NOTEBOOK}/<%=notebookId%>`,
      req: {
        notebookId: {
          type: 'string',
          required: true,
        },
      },
    },
    method: 'PUT',
    needBody: true,
  });

  notebooks.deleteNotebookById = clientAction({
    url: {
      fmt: `${OPENSEARCH_NOTEBOOKS_API.NOTEBOOK}/<%=notebookId%>`,
      req: {
        notebookId: {
          type: 'string',
          required: true,
        },
      },
    },
    method: 'DELETE',
  });

}
