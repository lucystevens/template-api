![Develop Status][workflow-badge-develop]
![Main Status][workflow-badge-main]
![Version][version-badge] 

# template-api
**template-api** is an template repository for creating a new api service using the [server-lib][server-lib-repo].
It is closely linked with the [template-client][template-client-repo] project.

## Creating a project using the template
 - _Optionally_ create an api client library using the template-client repository
 - Create a new repository using this as a template on GitHub
 - Checkout the new repository and change marked fields in the `pom.xml` file.
 - Change necessary parts of this README, and remove this section
 - Create API service class(es), replacing `TemplateApiService`
 - Create route configuration class, replacing `TemplateRouteConfiguration`
 - Create Guice injection module to bind your api service, route configuration and anything else, replacing `TemplateApiModule`

## Installation

Install the latest version of server-lib using Maven:

```	
<dependency>
	<groupId>uk.co.lukestevens</groupId>
	<artifactId>server-lib</artifactId>
	<version>2.0.0</version>
</dependency>
```

### Github Packages Authentication
Currently public packages on Github require authentication to be installed by Maven. Add the following repository to your project's `.m2/settings.xml`

```
<repository>
	<id>github-lukecmstevens</id>
	<name>GitHub lukecmstevens Apache Maven Packages</name>
	<url>https://maven.pkg.github.com/lukecmstevens/packages</url>
	<snapshots><enabled>true</enabled></snapshots>
</repository>
```

For more information see here: [Authenticating with Github packages][gh-package-auth]

## Running

### Configuration
#### Environment variables
For connecting to the core database (for logging, config, and data retrieval)
 - `database.url` - String. The JDBC url for the core database.
 - `database.username` - String. The username for the core database.
 - `database.password` - String. The password for the core database.

For general running of the application
 - `database.logging.enabled` - Boolean. defines whether logs should be output to the database, or to the console. Defaults to false.
 - `logging.level` - One of DEBUG, INFO, WARNING, or ERROR. Defined the minimum level of logs that should be output. Defaults to INFO.
 - `app.port` - Integer. The port which the http server will listen on. Defaults to 8000.
 
Information about the application (generated automatically by the build script)
 - `application.name` - The name of the application
 - `application.group` - The group (package prefix) of the application
 - `application.version` - The version of the application
 
#### Database variables
These should be defined per application, and should all be optional with sensible defaults.


## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

New features, fixes, and bugs should be branched off of develop.

Please make sure to update tests as appropriate.

## License
[MIT][mit-license]

[gh-package-auth]: https://docs.github.com/en/free-pro-team@latest/packages/guides/configuring-apache-maven-for-use-with-github-packages#authenticating-to-github-packages
[workflow-badge-develop]: https://img.shields.io/github/workflow/status/lukecmstevens/server-lib/test/develop?label=develop
[workflow-badge-main]: https://img.shields.io/github/workflow/status/lukecmstevens/server-lib/release/main?label=main
[version-badge]: https://img.shields.io/github/v/release/lukecmstevens/server-lib
[mit-license]: https://choosealicense.com/licenses/mit/
[server-lib-repo]: https://github.com/lukecmstevens/server-lib
[template-client-repo]: https://github.com/lukecmstevens/template-client
