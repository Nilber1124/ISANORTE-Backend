-- Limpieza de tablas que ningún código usa (auditoría de BD del 2026-11-23).
-- anuncios: el módulo se retiró del panel; ninguna pantalla lo consulta.
-- permisos / rol_permiso: la seguridad solo comprueba el rol, nunca permisos sueltos.
-- solicitudes_contacto: no tiene entidad ni código; el contacto va por WhatsApp.
DROP TABLE IF EXISTS rol_permiso;
DROP TABLE IF EXISTS permisos;
DROP TABLE IF EXISTS anuncios;
DROP TABLE IF EXISTS solicitudes_contacto;
