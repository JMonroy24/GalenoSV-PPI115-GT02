package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import jakarta.enterprise.context.ApplicationScoped;
import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.Cie10DTO;

@ApplicationScoped
public class Cie10Service {

    private static final String CATALOGO_TEXTO = """
            A00|Cólera
            A09|Diarrea y gastroenteritis de origen infeccioso
            B05|Sarampión
            C18|Neoplasia maligna del colon
            C34|Neoplasia maligna del pulmón
            C50|Neoplasia maligna de la mama
            D47|Otras neoplasias de comportamiento incierto del tejido hematopoyético
            E11|Diabetes mellitus tipo 2
            E66|Obesidad
            F20|Esquizofrenia
            F32|Trastorno depresivo
            G40|Epilepsia
            G43|Migraña
            H66|Otitis media supurativa
            I10|Hipertensión esencial
            I21|Infarto agudo de miocardio
            I50|Insuficiencia cardíaca
            I63|Infarto cerebral
            J10|Gripe por virus de la influenza estacional
            J18|Neumonía no especificada
            J45|Asma
            K21|Enfermedad por reflujo gastroesofágico
            K80|Colelitiasis
            L20|Dermatitis atópica
            M54|Dorsalgia
            M17|Osteoartrosis de rodilla
            N39|Otros trastornos del aparato urinario
            N18|Enfermedad renal crónica
            O80|Parto espontáneo
            P22|Síndrome de dificultad respiratoria del recién nacido
            Q90|Síndrome de Down
            R50|Fiebre de origen no especificado
            R07|Dolor de garganta y torácico
            S72|Fractura de fémur
            T36|Intoxicación por antibióticos
            U07|Exposición al virus de la influenza
            V03|Accidente de automóvil
            W19|Asfixia accidental
            X40|Lesión por avalancha
            Y84|Lesión por aire contaminado
            Z00|Examen general de salud
            Z34|Embarazo normal
            B08|Infecciones virales no clasificadas en otras categorías
            C08|Neoplasia maligna de otras glándulas salivales
            C17|Neoplasia maligna del intestino delgado
            C30|Neoplasia maligna de la cavidad nasal y el oído medio
            C41|Neoplasia maligna de hueso y tejido blando de miembro inferior
            C52|Neoplasia maligna de la vagina
            C62|Neoplasia maligna del testículo
            C71|Neoplasia maligna del cerebro
            C81|Enfermedad de Hodgkin
            C93|Leucemia monocítica
            D07|Neoplasia in situ de órganos genitales
            D17|Neoplasia benigna de tejido adiposo
            D27|Neoplasia benigna del ovario
            D36|Neoplasia benigna de sitios no especificados
            D46|Síndromes mielodisplásicos
            E06|Tiroiditis
            E20|Hipoparatiroidismo
            E30|Trastornos de la menopausia y la climatérica
            E43|Desnutrición proteicoenergética no especificada
            E56|Deficiencia de otras vitaminas
            E67|Otras formas de sobrepeso
            E78|Trastornos del metabolismo de los lípidos
            E87|Otros trastornos del equilibrio hidroelectrolítico y ácido-base
            F06|Otros trastornos mentales orgánicos
            F16|Trastornos mentales por alucinógenos
            F25|Trastornos esquizoafectivos
            F40|Trastornos de ansiedad fóbica
            F52|Disfunción sexual
            F66|Trastornos psicológicos y conductuales relacionados con la sexualidad
            F81|Trastornos específicos del desarrollo de las habilidades escolares
            F94|Trastornos de la función social de la infancia
            G05|Encefalitis en enfermedades clasificadas en otra parte
            G22|Síndrome de Parkinson en enfermedades clasificadas en otra parte
            G37|Otras enfermedades desmielinizantes del sistema nervioso central
            G51|Trastornos del nervio facial
            G62|Otras polineuropatías
            G81|Hemiplejía
            G96|Otros trastornos del sistema nervioso central
            H05|Trastornos de la órbita
            H22|Trastornos del iris y el cuerpo ciliar en enfermedades clasificadas en otra parte
            H35|Otros trastornos de la retina
            H48|Trastornos del nervio óptico en enfermedades clasificadas en otra parte
            H59|Trastornos del ojo y sus anexos posprocedimiento
            H70|Mastoiditis y trastornos relacionados
            H84|Otros trastornos del oído
            I02|Corea reumática
            I15|Hipertensión secundaria
            I32|Pericarditis en enfermedades clasificadas en otra parte
            I42|Miocardiopatía
            I51|Complicaciones y secuelas de enfermedad cardíaca
            I67|Otras enfermedades cerebrovasculares
            I79|Trastornos de arterias y capilares en enfermedades clasificadas en otra parte
            I88|Linfadenitis no específica
            J05|Croup agudo y epiglotitis
            J16|Neumonía por otros organismos infecciosos
            J34|Otros trastornos de la nariz y los senos paranasales
            J43|Enfisema
            J65|Pneumoconiosis con tuberculosis
            J84|Otras enfermedades pulmonares intersticiales
            J96|Insuficiencia respiratoria
            K07|Anomalías dentofaciales
            K35|Apendicitis aguda
            K45|Otras hernias abdominales
            K60|Fisura y fístula anal
            K70|Enfermedad hepática alcohólica
            K82|Otras enfermedades de la vesícula biliar
            L00|Síndrome de estafilococo quemado de la piel
            L12|Pénfigo foliáceo
            L28|Liquen simple crónico y prurigo
            L50|Urticaria
            L60|Trastornos de las uñas
            L73|Otras enfermedades de los folículos pilosos
            L87|Trastornos de la queratinización
            L97|Úlcera de miembro inferior
            M08|Artritis inflamatoria infantil
            M30|Panarteritis nodosa y síndromes relacionados
            M43|Otras deformidades congénitas y adquiridas de la columna
            M71|Otras bursopatías
            M83|Osteomalacia
            M93|Otras osteopatías infantiles
            N04|Síndrome nefrótico
            N15|Otras nefropatías
            N25|Otros trastornos de la función tubular renal
            N34|Uretritis y síndrome uretral
            N45|Orquitis y epididimitis
            N62|Hipertrofia de la mama
            N76|Otras inflamaciones de la vulva y la vagina
            N85|Otras enfermedades no inflamatorias del útero
            N95|Esterilidad femenina
            O04|Aborto médico
            O14|Preeclampsia moderada
            O26|Otras complicaciones del embarazo
            O40|Hidrarnios
            O61|Inducción fallida del trabajo de parto
            O70|Desgarro perineal
            O84|Parto múltiple
            O95|Muerte obstétrica no especificada
            P05|Retraso del crecimiento fetal
            P20|Hipoxia intrauterina
            P29|Otros trastornos respiratorios del recién nacido
            P60|Coagulopatía del recién nacido
            P78|Otros trastornos digestivos del recién nacido
            Q00|Anencefalia y malformaciones similares
            Q11|Anomalías del globo ocular
            Q22|Anomalías de la válvula pulmonar y tricúspide
            Q32|Anomalías de la tráquea
            Q42|Anomalías del colon
            Q55|Otras anomalías de los órganos genitales masculinos
            Q65|Anomalías de la cadera
            Q75|Anomalías de los huesos del cráneo y la cara
            Q84|Otras anomalías tegumentarias
            Q96|Síndrome de Turner
            R05|Tos
            R15|Incontinencia fecal
            R24|Anomalías del pigmento
            R34|Anuria y oliguria
            R44|Otros síntomas generales y hallazgos sensoriales
            R53|Malestar y fatiga
            R63|Síntomas y hallazgos relativos a la ingesta de alimentos y líquidos
            R72|Anomalías de los glóbulos blancos
            R82|Otros hallazgos anormales de la orina
            R92|Hallazgos anormales de imagen de la mama
            S03|Luxación y esguince de articulaciones de la cabeza
            S12|Fractura y luxación de columna cervical
            S22|Fractura de costillas y esternón
            S31|Herida abierta del abdomen
            S40|Traumatismo superficial del hombro y el brazo
            S50|Traumatismo superficial del antebrazo
            S59|Otros traumatismos del antebrazo
            S69|Otros traumatismos de la mano
            S78|Amputación traumática de la cadera y el muslo
            S88|Amputación traumática de la pierna
            S97|Traumatismo con aplastamiento del tobillo y el pie
            T07|Traumatismos múltiples no especificados
            T16|Presencia de cuerpo extraño en el oído
            T25|Quemadura de tobillo y pie
            T35|Congelación de sitio no especificado
            T44|Intoxicación por antihistamínicos y antieméticos
            T54|Efecto tóxico de gases y vapores
            T63|Contacto con animales venenosos
            T74|Maltrato y negligencia
            T85|Complicaciones de otros implantes
            T95|Secuelas de quemaduras y congelaciones
            U05|VIH con otras enfermedades
            U14|Infección por organismos no especificados
            U24|Infección por virus de Epstein-Barr
            U33|Infección por Orthomyxoviridae
            U43|Infección latente
            U52|Resistencia a antimicóticos
            U62|Resistencia a radiación
            U71|Enfermedad injerto contra huésped
            U80|Infección por bacteria grampositiva
            U90|Infección por levadura
            U99|Infección no especificada
            V09|Otros accidentes de transporte
            V18|Accidente de ocupante de avión
            V28|Caída en la cocina
            W06|Contacto con explosivos
            """;

    private static final List<Cie10DTO> CATALOGO = CATALOGO_TEXTO.lines()
            .map(linea -> linea.strip().split("\\|", 2))
            .map(partes -> new Cie10DTO(partes[0], partes[1]))
            .toList();

    public List<Cie10DTO> findAll() {
        return CATALOGO;
    }

    public List<Cie10DTO> buscar(String texto) {
        if (texto == null || texto.isBlank()) {
            return CATALOGO;
        }
        String patron = normalizar(texto);
        return CATALOGO.stream()
                .filter(item -> normalizar(item.codigo()).contains(patron)
                        || normalizar(item.descripcion()).contains(patron))
                .toList();
    }

    private static String normalizar(String texto) {
        return Normalizer.normalize(texto.toLowerCase(Locale.ROOT), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
    }
}
