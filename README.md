## minepkg-publish gradle plugin

Gradle plugin for publishing releases to [minepkg](https://minepkg.io).

### Setup

`build.gradle`:

```
plugins {
    id 'minepkg-publish' version 'latest.release'
}
```

`settings.gradle`:
```
pluginManagement {
    repositories {
        maven {
            name = "Fourmisain's Maven"
            url = "https://gitlab.com/api/v4/projects/37712942/packages/maven"
        }
    }
}
```

### Configuration Example

`build.gradle`:
```
minepkg {
    apiKey = System.getenv("MINEPKG_TOKEN")
    artifact = remapJar                    // file or task which produces the file to be published
    name = "projectname"
    version = "1.2.0+1.19.3"
    platform = "fabric"                    // optional, defaults to fabric
    license = "MIT"                        // optional, "Unknown" if left out, this is independent from the project license
    minecraftVersionRange = "~1.19.3"      // required
    fabricLoaderVersionRange = ">=0.14.21" // optional, defaults to '*'
    require("fabric", ">=0.86.0")          // adds a mod dependency
    require("cloth-config")                // version range is optional, defaults to "*"

    // for debugging purposes you can use toJson() or toPrettyJson() to view the generated manifest
    println toPrettyJson()
}
```

You can get an API key on https://minepkg.io/docs/ci.


### Publishing

Simply run

```gradle minepkg```
