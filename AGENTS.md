# AGENTS.md

Estas reglas son obligatorias para cualquier agente, asistente o automatización que modifique este repositorio.

## Pre-flight obligatorio
La primera operación de lectura del repositorio en cada tarea o sesión debe ser leer completamente este `AGENTS.md` desde la rama por defecto. Una lectura de otra sesión no cuenta.

## Flujo de cambios
1. Partir de `main` actualizado.
2. Crear una rama dedicada antes de modificar código, documentación, CI o versionado.
3. Aplicar SemVer sobre `revision`.
4. Actualizar `CHANGELOG.md` y README cuando corresponda.
5. Ejecutar al menos `mvn -B test`.
6. Abrir PR a `main`, corregir checks y fusionar solo en verde.
7. Eliminar la rama origen tras merge y verificar su desaparición.

## Restricciones
- No escribir directamente en `main`, salvo el commit inicial imprescindible para bootstrap de un repositorio completamente vacío.
- Java 21.
- Maven CI-friendly: `${revision}${sha1}${changelist}`.
- El módulo debe contener solo infraestructura CSV reutilizable; no debe depender de Spring, Kafka, Avro ni de modelos de dominio de los microservicios.
- Los cambios incompatibles requieren incremento major.
