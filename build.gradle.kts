plugins {
    `java-library`
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("org.sonarqube") version "3.3"
    id("io.freefair.lombok") version "6.4.2"
    checkstyle
    jacoco
}

allprojects {
    group = "org.kryonite"
    version = "1.0.0"

    apply(plugin = "java-library")
    apply(plugin = "com.github.johnrengelman.shadow")
    apply(plugin = "org.sonarqube")
    apply(plugin = "io.freefair.lombok")
    apply(plugin = "checkstyle")
    apply(plugin = "jacoco")

    repositories {
        mavenCentral()
        maven(url = "https://jitpack.io")
        maven(url = "https://nexus.velocitypowered.com/repository/maven-public/")
    }

    dependencies {
        val junitVersion = "5.8.2"
        val velocityVersion = "3.1.0"

        implementation("com.github.kryoniteorg:kryo-messaging:2.0.1")
        implementation("org.mariadb.jdbc:mariadb-java-client:3.0.5")
        implementation("com.zaxxer:HikariCP:5.0.1")

        compileOnly("com.velocitypowered:velocity-api:$velocityVersion")
        annotationProcessor("com.velocitypowered:velocity-api:$velocityVersion")

        testImplementation("com.velocitypowered:velocity-api:$velocityVersion")
        testAnnotationProcessor("com.velocitypowered:velocity-api:$velocityVersion")

        testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
        testImplementation("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
        testImplementation("org.mockito:mockito-junit-jupiter:4.4.0")
        testImplementation("org.awaitility:awaitility:4.2.0")
    }

    tasks.test {
        finalizedBy("jacocoTestReport")
        useJUnitPlatform()
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        withJavadocJar()
    }

    tasks.jacocoTestReport {
        reports {
            xml.required.set(true)
        }
    }

    checkstyle {
        toolVersion = "9.2.1"
        config = project.resources.text.fromUri("https://kryonite.org/checkstyle.xml")
    }

    sonarqube {
        properties {
            property("sonar.projectKey", "kryoniteorg_kryo-proxy-sync")
            property("sonar.organization", "kryoniteorg")
            property("sonar.host.url", "https://sonarcloud.io")
        }
    }
}
