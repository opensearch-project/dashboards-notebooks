# Open Distro for Elasticsearch Kibana Notebooks

Dashboards offer a solution for a few selected use cases, and are great tools if you’re focused on monitoring a known set of metrics over time. Notebooks enables contextual use of data with detailed explanations by allowing a user to combine saved visualizations, text, graphs and decorate data in elastic with other reference data sources.

## Documentation

Please see our technical [documentation](https://opendistro.github.io/for-elasticsearch-docs/docs/kibana/notebooks/) to learn more about its features.

## Setup

1. Download OpenSearch for the version that matches the [OpenSearch Dashboards version specified in package.json](./package.json#L7).
1. Download the OpenSearch Dashboards source code for the [version specified in package.json](./package.json#L7) you want to set up.

   See the [Kibana contributing guide](https://github.com/elastic/kibana/blob/master/CONTRIBUTING.md#setting-up-your-development-environment) for more instructions on setting up your development environment.

1. Change your node version to the version specified in `.node-version` inside the OpenSearch Dashboards root directory.
1. Create a `plugins` directory inside the OpenSearch Dashboards source code directory, if `plugins` directory doesn't exist.
1. Check out this package from version control into the `plugins` directory.
   ```
   git clone git@github.com:opendistro-for-elasticsearch/kibana-notebooks.git plugins --no-checkout
   cd plugins
   echo 'kibana-notebooks/*' >> .git/info/sparse-checkout
   git config core.sparseCheckout true
   git checkout dev
   ```
1. Run `yarn osd bootstrap` inside `OpenSearch-Dashboards/plugins/kibana-notebooks`.

Ultimately, your directory structure should look like this:

```md
.
├── OpenSearch Dashboards
│   └── plugins
│       └── kibana-notebooks
```

## Build

To build the plugin's distributable zip simply run `yarn build`.

Example output: `./build/opendistroNotebooks*.zip`


## Run

- `yarn start`

  Starts OpenSearch Dashboards and includes this plugin. OpenSearch Dashboards will be available on `localhost:5601`.

- `yarn test:cypress`

  Runs the plugin cypress tests.

## Contributing to Open Distro for Elasticsearch Kibana Notebooks

We welcome you to get involved in development, documentation, testing the notebooks plugin. See our [CONTRIBUTING.md](./../CONTRIBUTING.md) and join in.

Since this is a Kibana plugin, it can be useful to review the [Kibana contributing guide](https://github.com/elastic/kibana/blob/master/CONTRIBUTING.md) alongside the documentation around [Kibana plugins](https://www.elastic.co/guide/en/kibana/master/kibana-plugins.html) and [plugin development](https://www.elastic.co/guide/en/kibana/current/plugin-development.html).

## Bugs, Enhancements or Questions

Please file an issue to report any bugs you may find, enhancements you may need or questions you may have [here](https://github.com/opendistro-for-elasticsearch/kibana-notebooks/issues).

## License

This code is licensed under the Apache 2.0 License.

## Copyright

Copyright 2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.
