<div align="center">
  <a href="https://github.com/kryoniteorg/kryo-proxy-sync">
    <img src="https://raw.githubusercontent.com/kryoniteorg/.github/main/assets/kryonite_logo.svg" alt="Kryonite logo" width="80" height="80">
  </a>
</div>

<h1 align="center">kryo-proxy-sync</h1>
<div align="center">
    A Discord bot to whitelist players on <a href="https://github.com/PaperMC/Velocity">Velocity</a> with multi-proxy support.
    <br />
    <br />
    <a href="https://github.com/kryoniteorg/kryo-proxy-sync/issues/new?assignees=&labels=bug&template=bug_report.md">Report Bug</a>
    ·
    <a href="https://github.com/kryoniteorg/kryo-proxy-sync/issues/new?assignees=&labels=feature&template=feature_request.md">Request Feature</a>
    <br />
    <br />
    <img alt="Quality Gate Status" src="https://sonarcloud.io/api/project_badges/measure?project=kryoniteorg_kryo-proxy-sync&metric=alert_status">
    <img alt="Coverage" src="https://sonarcloud.io/api/project_badges/measure?project=kryoniteorg_kryo-proxy-sync&metric=coverage">
    <img alt="Maintainability Rating" src="https://sonarcloud.io/api/project_badges/measure?project=kryoniteorg_kryo-proxy-sync&metric=sqale_rating">
    <img alt="Reliability Rating" src="https://sonarcloud.io/api/project_badges/measure?project=kryoniteorg_kryo-proxy-sync&metric=reliability_rating">
    <img alt="Security Rating" src="https://sonarcloud.io/api/project_badges/measure?project=kryoniteorg_kryo-proxy-sync&metric=security_rating">
    <br />
    <br />
</div>


## About the project

kryo-proxy-sync is a Velocity proxy plugin which syncs proxy metadata information such as slots, motd and more.

## Prerequisites
kryo-proxy-sync needs the following services to be installed and configured to function properly:

- [MariaDB](https://mariadb.org/) database
- [RabbitMQ](https://www.rabbitmq.com/) message broker

It is not recommended using the root user of the [MariaDB](https://mariadb.org/) server for kryo-proxy-sync. Please create an extra database with an extra user that is limited to that database.

## Setup
To install the [Velocity](https://github.com/PaperMC/Velocity) plugin just copy the JAR-file into the plugin directory.

Furthermore, the [Velocity](https://github.com/PaperMC/Velocity) plugin needs some environment variables. Those can also be provided as startup parameters if the usage of environment variables is not possible.

| Environment variable | Start parameter     | Description                                              |
|----------------------|---------------------|----------------------------------------------------------|
| CONNECTION_STRING    | -DCONNECTION_STRING | Connection String for connecting to the MariaDB database |
| RABBITMQ_ADDRESS     | -DRABBITMQ_ADDRESS  | Address and port of the RabbitMQ message broker          |
| RABBITMQ_USERNAME    | -DRABBITMQ_USERNAME | Username of the RabbitMQ message broker                  |
| RABBITMQ_PASSWORD    | -DRABBITMQ_PASSWORD | Password of the RabbitMQ message broker                  |

A startup command of the [Velocity](https://github.com/PaperMC/Velocity) could look like the following:
```bash
java -Xms128M -Xmx1024M -XX:+UseG1GC -XX:G1HeapRegionSize=4M -XX:+UnlockExperimentalVMOptions -XX:+ParallelRefProcEnabled -XX:+AlwaysPreTouch -XX:MaxInlineLevel=15 -DCONNECTION_STRING=jdbc:mariadb://127.0.0.1:3306/database?user=user&password=password -DRABBITMQ_ADDRESS=127.0.0.1:5672 -DRABBITMQ_USERNAME=guest -DRABBITMQ_PASSWORD=guest -JAR velocity.JAR
```

## Velocity Commands
| Command                         | Permission         | Description                                                                                                                                                                                                                          |
|---------------------------------|--------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `/maxplayercount <count>`       | `player.count.max` | Change the maximum player count of your proxies.<br/><br/>Players that should be able to bypass this limitation needs ``player.count.max.bypass`` as a permission.                                                                   |
| `/maintenance [enable/disable]` | `maintenance`      | Manage the maintenance mode of your proxies. Maintenance lets only allowed users onto the server.<br/><br/>To allow players or groups to join the server when it is in maintenance give them ``maintenance.bypass`` as a permission. |

## Development

### Building
kryo-proxy-sync is built with [Gradle](https://gradle.org/). We recommend using the included wrapper script (`./gradlew`) to ensure you use the same [Gradle](https://gradle.org/) version as we do.

To build production-ready JAR files it is sufficient to run `./gradlew shadowJAR`.
You can find the JAR files in `./build/libs/*-all.JAR`.

### Testing
kryo-proxy-sync uses JUnit 5 and Mockito for testing.

To run all tests just execute `./gradlew test`
