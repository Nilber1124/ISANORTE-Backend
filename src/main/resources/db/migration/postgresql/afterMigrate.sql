-- Callback de Flyway: se ejecuta después de cada migración (en cada arranque), así que también
-- cubre las tablas que creen migraciones futuras.
--
-- Supabase publica todas las tablas del esquema public en su API REST (PostgREST) con la clave
-- anónima. La aplicación no usa esa API: el backend accede solo por JDBC. Activar Row-Level
-- Security sin políticas deja la API REST sin acceso a ninguna fila.
--
-- El backend no se ve afectado: es dueño de las tablas y el dueño no está sujeto a RLS mientras
-- no se use FORCE ROW LEVEL SECURITY (que aquí no se usa). Por eso solo se tocan las tablas cuyo
-- dueño es el usuario actual; si alguna pertenece a otro rol se omite y se avisa, en lugar de
-- bloquear el arranque o dejar sin acceso al backend.
DO $$
DECLARE
    tabla record;
BEGIN
    FOR tabla IN
        SELECT c.relname AS nombre, pg_get_userbyid(c.relowner) AS dueno
        FROM pg_class c
        JOIN pg_namespace n ON n.oid = c.relnamespace
        WHERE n.nspname = 'public'
          AND c.relkind IN ('r', 'p')
          AND NOT c.relrowsecurity
    LOOP
        IF tabla.dueno = current_user THEN
            EXECUTE format('ALTER TABLE public.%I ENABLE ROW LEVEL SECURITY', tabla.nombre);
        ELSE
            RAISE NOTICE 'RLS no activado en public.% (dueño %, no %)', tabla.nombre, tabla.dueno, current_user;
        END IF;
    END LOOP;
END
$$;
