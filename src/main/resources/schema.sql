-- Esquema y migracion idempotente para Parques-Master (PostgreSQL / Neon)

CREATE TABLE IF NOT EXISTS jugador (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    color VARCHAR(20) NOT NULL,
    puntaje INT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS partida (
    id BIGSERIAL PRIMARY KEY,
    fecha_inicio TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    estado VARCHAR(30) NOT NULL DEFAULT 'EN_CURSO',
    ganador VARCHAR(100)
);

-- Migracion en caso de que la tabla partida ya existiera sin la columna ganador
ALTER TABLE partida ADD COLUMN IF NOT EXISTS ganador VARCHAR(100);

CREATE TABLE IF NOT EXISTS estadistica (
    id BIGSERIAL PRIMARY KEY,
    jugador_id BIGINT NOT NULL REFERENCES jugador(id) ON DELETE CASCADE,
    partidas_jugadas INT NOT NULL DEFAULT 0,
    partidas_ganadas INT NOT NULL DEFAULT 0
);

-- Garantizar una unica fila de estadisticas por jugador (permite UPSERT)
CREATE UNIQUE INDEX IF NOT EXISTS idx_estadistica_jugador_unico
    ON estadistica (jugador_id);
