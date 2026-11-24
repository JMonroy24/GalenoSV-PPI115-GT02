-- Ejecutar en una transacción y antes de desplegar el WAR HU3/HU4.
-- No se adivinan definiciones cuando hay cero o varios candidatos por procedimiento/rol.
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM public.consulta_procedimiento_paso cpp
        LEFT JOIN public.consulta_procedimiento cp USING (id_consulta_procedimiento)
        LEFT JOIN public.persona_rol pr ON pr.id_persona_rol = cpp.id_persona_rol
        LEFT JOIN public.procedimiento_paso pp
          ON pp.id_procedimiento = cp.id_procedimiento AND pp.id_rol = pr.id_rol
        WHERE cpp.id_procedimiento_paso IS NULL
        GROUP BY cpp.id_consulta_procedimiento_paso
        HAVING count(pp.id_procedimiento_paso) <> 1
    ) THEN
        RAISE EXCEPTION 'Hay pasos sin una definición inequívoca; regularícelos explícitamente antes de V6';
    END IF;
END $$;

WITH candidatos AS (
    SELECT cpp.id_consulta_procedimiento_paso,
           (array_agg(pp.id_procedimiento_paso))[1] AS definicion
    FROM public.consulta_procedimiento_paso cpp
    JOIN public.consulta_procedimiento cp USING (id_consulta_procedimiento)
    JOIN public.persona_rol pr ON pr.id_persona_rol = cpp.id_persona_rol
    JOIN public.procedimiento_paso pp
      ON pp.id_procedimiento = cp.id_procedimiento AND pp.id_rol = pr.id_rol
    WHERE cpp.id_procedimiento_paso IS NULL
    GROUP BY cpp.id_consulta_procedimiento_paso HAVING count(*) = 1
)
UPDATE public.consulta_procedimiento_paso cpp
SET id_procedimiento_paso = candidatos.definicion
FROM candidatos WHERE cpp.id_consulta_procedimiento_paso = candidatos.id_consulta_procedimiento_paso;

-- Fecha histórica aproximada autorizada: conserva cualquier finalización ya registrada.
UPDATE public.consulta_procedimiento_paso cpp
SET fecha_fin = COALESCE(cpp.fecha_inicio, c.fecha_inicio, CURRENT_TIMESTAMP)
FROM public.consulta_procedimiento cp JOIN public.consulta c USING (id_consulta)
WHERE cp.id_consulta_procedimiento = cpp.id_consulta_procedimiento
  AND upper(trim(cpp.estado)) = 'COMPLETADO' AND cpp.fecha_fin IS NULL;

ALTER TABLE public.consulta_procedimiento_paso ALTER COLUMN id_procedimiento_paso SET NOT NULL;
-- Puede haber ejecuciones históricas repetidas; el servicio serializa las altas por consulta.
CREATE INDEX IF NOT EXISTS ix_consulta_procedimiento_paso_definicion
    ON public.consulta_procedimiento_paso (id_consulta_procedimiento, id_procedimiento_paso);
CREATE UNIQUE INDEX IF NOT EXISTS uq_consulta_procedimiento_paso_activo
    ON public.consulta_procedimiento_paso (id_consulta_procedimiento, id_procedimiento_paso)
    WHERE estado IN ('PENDIENTE', 'EN_CURSO');
