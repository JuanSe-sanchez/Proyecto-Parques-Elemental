# Parqués Master

Implementación en **Java 21 + Swing + Maven + PostgreSQL (Neon)** del clásico juego de **Parqués colombiano con 2 dados**, siguiendo una arquitectura por capas (`model`, `enums`, `dto`, `database`, `dao`, `service`, `controller`, `view`).

## Características Principales

- **Tablero oficial de 68 casillas** con 4 zonas de cárcel (`CASA`), 4 salidas (`ROJO`, `AZUL`, `VERDE`, `AMARILLO`), 8 casillas `SEGURO`, 4 pasillos de llegada de 7 casillas y zona central de `META`.
- **Reglas con 2 dados:**
  - Hasta **3 intentos** para sacar pares cuando todas las fichas del jugador están en `CASA`.
  - Con **pares (dobles)** puedes sacar fichas de `CASA` a tu `SALIDA`, mover con los valores obtenidos y obtienes **turno extra**.
  - **3 pares consecutivos** devuelven la última ficha movida a `CASA` (si no está en `SEGURO` ni en pasillo/meta) y pasan el turno.
  - Posibilidad de usar **Dado 1**, **Dado 2** o la **Suma** de ambos dados sobre la ficha elegida.
  - **Capturas** en casillas normales (+20 puntos) y protección en casillas `SALIDA` y `SEGURO`.
  - Llegada exacta a `META` (+50 puntos). Gana el primer jugador con sus **4 fichas en META**.
- **Persistencia en PostgreSQL (Neon):** registro de jugadores, historial de partidas con ganador y estadísticas acumuladas (partidas jugadas, ganadas y porcentaje de victoria). Si no hay conexión, el juego sigue funcionando localmente.

## Configuración de Base de Datos

1. Copia `db.properties.example` a `db.properties` en la raíz del proyecto y completa tus credenciales (o define las variables de entorno `PARQUES_DB_URL`, `PARQUES_DB_USER`, `PARQUES_DB_PASSWORD`).
2. El esquema SQL está documentado en `src/main/resources/schema.sql` y se aplica automáticamente de forma idempotente al iniciar la aplicación (`ConexionesBD.inicializarEsquema()`).

## Compilación, Pruebas y Ejecución

```bash
# Ejecutar pruebas unitarias y generar reporte de cobertura JaCoCo
mvn verify

# Reporte de cobertura generado en:
# target/site/jacoco/index.html
```

Para ejecutar el juego, corre la clase principal `org.example.Main` desde IntelliJ IDEA o mediante Maven.
