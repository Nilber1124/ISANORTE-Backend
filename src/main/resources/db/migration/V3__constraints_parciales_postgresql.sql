-- Esta migración usa índices parciales, una capacidad específica de PostgreSQL.

CREATE UNIQUE INDEX uk_unidad_destacada_activa_empresa
    ON unidades_negocio(empresa_id)
    WHERE activo = TRUE AND destacado = TRUE;

CREATE UNIQUE INDEX uk_seo_pagina_estatica_sitio
    ON seo_paginas(configuracion_sitio_id, tipo_pagina)
    WHERE unidad_negocio_id IS NULL;

CREATE UNIQUE INDEX uk_seo_unidad_negocio
    ON seo_paginas(unidad_negocio_id)
    WHERE unidad_negocio_id IS NOT NULL;
