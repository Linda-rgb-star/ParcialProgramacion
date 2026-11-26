

PRAGMA foreign_keys = ON;

-- =====================================
-- TABLA DE MATERIALES
-- =====================================

CREATE TABLE IF NOT EXISTS materiales (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    codigo TEXT NOT NULL UNIQUE,
    nombre TEXT NOT NULL,
    tipo TEXT NOT NULL CHECK (
        tipo IN ('LIBRO', 'PORTATIL', 'KIT')
    ),
    valor_comercial REAL,
    componentes INTEGER,

    CHECK (
        (tipo = 'LIBRO'
            AND valor_comercial IS NULL
            AND componentes IS NULL)
        OR
        (tipo = 'PORTATIL'
            AND valor_comercial > 0
            AND componentes IS NULL)
        OR
        (tipo = 'KIT'
            AND componentes BETWEEN 1 AND 50
            AND valor_comercial IS NULL)
    )
);

-- =====================================
-- TABLA DE PRESTAMOS
-- =====================================

CREATE TABLE IF NOT EXISTS prestamos (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    material_id INTEGER NOT NULL,
    documento TEXT NOT NULL,
    fecha_prestamo TEXT NOT NULL,
    fecha_limite TEXT NOT NULL,
    fecha_devolucion TEXT,
    componentes_faltantes INTEGER DEFAULT 0,

    politica_multa TEXT NOT NULL DEFAULT 'NORMAL'
        CHECK (politica_multa IN (
            'NORMAL', 'DOCENTE', 'EXAMENES'
        )),

    seguro REAL NOT NULL DEFAULT 0,
    multa_base REAL NOT NULL DEFAULT 0,
    multa_final REAL NOT NULL DEFAULT 0,

    FOREIGN KEY (material_id)
        REFERENCES materiales(id)
        ON DELETE RESTRICT,

    CHECK (fecha_limite >= fecha_prestamo),

    CHECK (
        fecha_devolucion IS NULL
        OR fecha_devolucion >= fecha_prestamo
    ),

    CHECK (componentes_faltantes >= 0)
);

-- =====================================
-- EVITAR PRESTAMOS ACTIVOS DUPLICADOS
-- =====================================

CREATE UNIQUE INDEX IF NOT EXISTS
idx_material_prestamo_activo
ON prestamos(material_id)
WHERE fecha_devolucion IS NULL;

-- =====================================
-- INDICES DE CONSULTA
-- =====================================

CREATE INDEX IF NOT EXISTS idx_prestamos_fecha
ON prestamos(fecha_limite);

CREATE INDEX IF NOT EXISTS idx_prestamos_documento
ON prestamos(documento);
