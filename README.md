# Imports released enforcer

The current maven enforcer plugin does not check if dependency management imports are released. This plugin will check.

The following dependency declaration will pass the enforcer checks:
```xml
<dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>org.glassfish.jersey</groupId>
      <artifactId>jersey-bom</artifactId>
      <version>2.21-SNAPSHOT</version>
      <type>pom</type>
      <scope>import</scope>
    </dependency>
  </dependencies>
</dependencyManagement>
```

When using this rule, no more.
```xml
<configuration>
  <rules>
    <enforceImportRelease implementation="com.synedge.enforcer.EnforceImportRelease" />
  </rules>
</configuration>
````
