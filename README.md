# Helfereinsatztool Backend Projekt
Backend Code zu [vbcl-frontend](https://github.com/toky03/vbcl-frontend)

Benötigte Tools für das Lokale Seutp
- JVM Version 11
- maven mindestens Version 3.0.0
- Optional Docker

Lokaler development Server starten mit Quarkus `mvn quarkus:dev`. Das Backend ist danach via http://localhost:8080 verfügbar

## Projekt bauen
### Erstellen einer ausführbaren Datei im Ordner target/
#### Bauen mit GraalVM
```shell script
mvn package -Pnative
```
#### Bauen in einem Docker Docker Container
```shell script
mvn package -Pnative -Dquarkus.native.container-build=true
```

### Erstellen eines Docker Images von einem bestehendem binary

```shell script
docker build -f src/main/docker/Dockerfile.native-micro -t <image-name> .
```

### Erstellen eines Docker Image mit einem Multi Stage Build
```shell script
docker build -t <image-name> .
```
