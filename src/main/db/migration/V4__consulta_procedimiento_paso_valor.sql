-- Micro-tarea 6.1.3: formulario medico serializado como JSON en texto.
ALTER TABLE public.consulta_procedimiento_paso
    ADD COLUMN IF NOT EXISTS valor TEXT;

-- Permite ampliar tambien una columna valor existente de tipo VARCHAR.
ALTER TABLE public.consulta_procedimiento_paso
    ALTER COLUMN valor TYPE TEXT;
