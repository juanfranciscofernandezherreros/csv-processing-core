![version](https://img.shields.io/badge/version-1.0.0-blue)
# csv-processing-core

Módulo Java 21 pequeño y sin dependencias de framework para compartir infraestructura CSV estable entre los parsers de Basketball Stats.

## Alcance

Incluye únicamente piezas transversales que ya estaban repetidas entre microservicios:

- `SafeCsvPathValidator`: valida rutas absolutas, extensión `.csv`, existencia, fichero regular, legibilidad, pertenencia a una raíz permitida y escapes mediante symlink usando `toRealPath()`.
- validación opcional de filename esperado para parsers cuyo contrato exige un nombre concreto.
- `ChunkedProcessor`: divide un `Iterable<T>` en snapshots inmutables de tamaño configurable sin conocer DTOs, Kafka ni reglas de dominio.
- excepciones comunes `CsvProcessingException` y `CsvValidationException`.

El módulo **no depende de Spring, Kafka, Avro, Commons CSV ni de modelos de ningún microservicio**. Cada parser conserva sus reglas de cabecera, columnas, mapeo, keys Kafka y política de errores.

## Uso

```java
SafeCsvPathValidator validator = new SafeCsvPathValidator("/data/csv");
Path csv = validator.validate(eventFilePath);

ChunkedProcessor.process(records, 500, this::publishChunk);
```

Cuando un parser exige un filename concreto:

```java
Path csv = validator.validate(eventFilePath, "player_stats.csv");
```

## Seguridad de rutas

La validación sigue la política de KAN-20:

1. exige una ruta absoluta;
2. exige extensión `.csv`;
3. resuelve la raíz y el fichero mediante `toRealPath()`;
4. rechaza cualquier ruta real fuera de la raíz;
5. exige fichero regular y legible;
6. puede exigir un filename específico.

Esto protege frente a traversal y frente a symlinks ubicados dentro de la raíz que apunten fuera.

## Compatibilidad y versionado

Se usa SemVer:

- patch: correcciones compatibles;
- minor: nueva API compatible;
- major: cambios incompatibles de API o comportamiento.

Los consumidores deben fijar una versión concreta. La serie 1.x mantendrá compatibilidad binaria/fuente de las APIs públicas salvo correcciones de seguridad que requieran endurecer validaciones.

## Publicación

Coordenadas Maven:

```text
com.fernandez.basketball:csv-processing-core:1.0.0
```

El artefacto se publica en GitHub Packages al crear una release/tag `v*`.

## Validación

```bash
mvn -B test
```

Los tests cubren ruta permitida, traversal, symlink fuera de raíz, ruta relativa, fichero inexistente, extensión, filename esperado y chunking.
