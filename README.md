Elasticsearch Jenkins plugin
============================

Reports Jenkins build information to Elastichsearch on build completion.
Information reported includes:
* Name of the build.
* Start time of the build.
* Duration of the build.
* If the build was successful or not.
* System properties.
* Environment variables.

Building
--------

### Setup

In order to build the plugin Maven must be able to pull the necessary dependencies from Jenkins repository.
[docs/settings.xml](settings.xml) contains the necessary settings.

### Build

Run `mvn clean install package`.

Installation
------------

Launch Jenkins and go to 'Manage Jenkins', 'Manage Plugins', 'Advanced'. Use 'Upload Plugin' to upload the hpi file.

Usage
-----

Configure the plugin under 'Configure System'.
* `URL` - HTTP URL to an Elasticsearch instance, example: `https://username:password@cluster01/`.
* `Index name` - Name of the index to use, example: `jenkins`.
* `Type name` - Name of the type, example: `build`.

