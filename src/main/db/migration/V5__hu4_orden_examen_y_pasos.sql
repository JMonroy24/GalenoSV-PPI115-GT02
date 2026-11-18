ALTER TABLE public.orden_examen ADD COLUMN IF NOT EXISTS id_examen uuid;
ALTER TABLE public.consulta_procedimiento_paso ADD COLUMN IF NOT EXISTS id_procedimiento_paso uuid;
ALTER TABLE public.orden_examen ALTER COLUMN indicaciones DROP NOT NULL;
ALTER TABLE public.orden_examen DROP CONSTRAINT IF EXISTS ck_orden_examen_indicaciones_texto;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_orden_examen_examen') THEN
        ALTER TABLE public.orden_examen ADD CONSTRAINT fk_orden_examen_examen
            FOREIGN KEY (id_examen) REFERENCES public.examen (id_examen);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_consulta_procedimiento_paso_procedimiento_paso') THEN
        ALTER TABLE public.consulta_procedimiento_paso ADD CONSTRAINT fk_consulta_procedimiento_paso_procedimiento_paso
            FOREIGN KEY (id_procedimiento_paso) REFERENCES public.procedimiento_paso (id_procedimiento_paso);
    END IF;
END $$;
