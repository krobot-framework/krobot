![](logo.png)

# The Krobot Framework

The Krobot framework is a modern Discord bot framework based on **JDA** \(Java version\). 

## Get started

To get started, consider reading [the documentation](https://krobot.gitbooks.io/krobot/)

```groovy
repositories {
  jcenter()

  maven {
    url 'http://krobot-framework.github.io/maven/'
  }
}

dependencies {
  compile 'fr.litarvan.krobot:krobot-framework:2.3.2'
}
```

## Features

### Command engine

 - Command path compiling
 - Automatic syntax managing
 - Arguments parsing (including types like number or user)
 - Middlewares
 - Sub commands
 - Full lambda support
 - Command groups
 
### Config engine

 - JSON/Java Properties config
 - Default config (in classpath or not) loading
 - Simple and modern
 - Object serialization
 - Value path support
 
### Framework

 - Dependency injection
 - Pre-configured (overridable) Log4J logging
 - Simple embed dialog functions
