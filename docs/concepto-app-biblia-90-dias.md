# La Biblia en 90 Días — Documento de concepto de producto (Android)

> Versión 0.1 · Noviembre 2026 · Borrador para discusión

---

## 1. Visión y propuesta de valor

**Visión:** ayudar a cualquier persona, sin importar su experiencia, a leer la Biblia completa en 90 días con un plan claro, acompañamiento contextual y una comunidad que la sostenga.

**Propuesta de valor en una frase:** *“Sabe exactamente qué leer hoy, entiende lo que lees y no te rindas a mitad de camino.”*

| Problema del usuario | Respuesta de la app |
|---|---|
| “No sé por dónde empezar ni cuánto leer.” | Plan diario pre-calculado, equilibrado por volumen de texto. |
| “Me pierdo en Levítico / los profetas.” | Notas de contexto y guía de lectura por libro y por día. |
| “Me atraso y abandono.” | Modo de recuperación, días de gracia y reprogramación automática. |
| “Leo solo y pierdo motivación.” | Grupos de lectura, compañero de responsabilidad, rachas sin culpa. |
| “No siempre tengo internet.” | Arquitectura *offline-first*: todo el texto y el plan viven en el dispositivo. |

---

## 2. Público objetivo

### Personas

1. **Ana, la lectora nueva (22–35 años).** Nunca ha leído la Biblia completa. Necesita orientación, vocabulario sencillo, introducciones a cada libro y una experiencia que no intimide.
2. **Roberto, el disciplinado (35–55 años).** Ha leído partes o lo intentó antes. Busca estructura, métricas de progreso, horario fijo y quizá un orden distinto (cronológico).
3. **Pastora Lucía, la líder de grupo.** Quiere que su congregación o célula lea al mismo ritmo, ver la participación agregada y compartir reflexiones.
4. **Don Ernesto, lector mayor (60+).** Prioriza letra grande, audio, alto contraste y navegación muy simple.

### Mercado inicial
Hispanohablantes (Latinoamérica, EE. UU. hispano y España). La mayoría con Android de gama media/baja, conectividad intermitente y planes de datos limitados → **rendimiento y offline son requisitos, no extras**.

---

## 3. El plan de 90 días: diseño del contenido

### 3.1 Las cifras

| Métrica | Valor |
|---|---|
| Libros | 66 (39 AT + 27 NT) |
| Capítulos | 1.189 (929 AT + 260 NT) |
| Versículos | ~31.100 |
| Promedio diario | ~13 capítulos ≈ 346 versículos |
| Tiempo estimado | 40–50 min leyendo · ~35 min escuchando |

> **Decisión clave:** equilibrar por **versículos** (o mejor aún, por **palabras** de la traducción licenciada), no por capítulos. Un día de 13 capítulos de Salmos no equivale a 13 de Jeremías. El repositorio incluye un generador (`tools/generar_plan.py`) que produce un plan canónico de 90 días con un rango de 322–384 versículos/día (ver `docs/plan-canonico-90-dias.md`).

### 3.2 Órdenes de lectura

| Orden | Descripción | Para quién |
|---|---|---|
| **Canónico** | Génesis → Apocalipsis, en orden de la Biblia. | Quien quiere simplicidad. |
| **Cronológico** | Según el orden aproximado de los eventos: Job tras Génesis 11, Salmos intercalados con la vida de David, profetas insertos en Reyes/Crónicas, epístolas paulinas dentro de Hechos. | Quien quiere entender la historia como un todo. |
| **Mixto / “Equilibrado”** | Cada día: porción del AT + porción del NT + un Salmo o Proverbio. Evita semanas enteras en libros difíciles. | Lectores nuevos (recomendado por defecto para principiantes). |
| **Temático** | Recorridos por grandes temas (Pacto, Reino, Redención, Sabiduría, Esperanza) que igualmente cubren el 100 % del texto, agrupando libros por género/tema. | Lectores con experiencia o grupos de estudio. |
| **NT primero** | Empieza con Evangelios y Hechos (días 1–20) y luego el AT. | Lectores nuevos con riesgo de abandonar en el Pentateuco. |

**Garantía de cobertura total:** todo plan se define como una lista de *asignaciones* (día → rangos de capítulos). Un validador automático en el pipeline de contenido verifica que **cada uno de los 1.189 capítulos aparece exactamente una vez** (o al menos una vez en el caso de planes mixtos con Salmos repetidos) antes de publicar un plan. Esto se ejecuta en CI y vuelve a verificarse en la app al importar el paquete.

### 3.3 Flexibilidad sin perder los 90 días

- **Días de gracia:** opción de plan de 84 días de lectura + 6 días de “ponerse al día” (uno cada dos semanas). Sigue siendo 90 días naturales.
- **Reprogramación inteligente:** si el usuario se atrasa, la app ofrece: (a) redistribuir lo pendiente en los días restantes (mostrando el nuevo minutos/día), (b) extender la fecha final, o (c) “saltar y continuar” marcando lo omitido para leer después.
- **Fecha de inicio libre** y **días de descanso** configurables (p. ej., domingos sin lectura → plan recalculado sobre 77 días hábiles en 90 naturales).
- **Lectura por audio** cuenta igual que lectura en texto.

---

## 4. Funcionalidades principales

### 4.1 Pantalla “Hoy” (núcleo de la app)
- Día X de 90, porciones de hoy, tiempo estimado y botón grande **“Empezar lectura”**.
- Nota de contexto del día (2–3 frases) antes de leer.
- Al terminar: marcar como leído (por capítulo o día completo), pregunta de reflexión y campo de diario opcional.

### 4.2 Seguimiento de progreso con indicadores visuales
- **Anillo diario** (capítulos de hoy completados).
- **Barra global** “Has leído el 34 % de la Biblia”.
- **Mapa de la Biblia:** cuadrícula de 66 libros que se “ilumina” a medida que se completan; tocar un libro muestra sus capítulos.
- **Calendario/heatmap** de 90 días (leído, parcial, pendiente, día de gracia).
- **Ritmo proyectado:** “A este ritmo terminarás el 15 de febrero” con indicador adelantado/atrasado.
- **Racha flexible:** cuenta días leídos por semana (p. ej., 6 de 7) en lugar de penalizar un único fallo.
- **Hitos y logros:** terminar el Pentateuco, el AT, los Evangelios, 30/60/90 días; tarjeta compartible al finalizar.

### 4.3 Notas devocionales y contextuales
Contenido vinculado a cada día y a cada libro, creado y revisado editorialmente:

| Tipo | Ejemplo | Ubicación |
|---|---|---|
| Introducción al libro | Autor, fecha, audiencia, propósito, estructura | Al empezar un libro nuevo |
| Contexto del día | “Hoy entramos al tabernáculo: por qué tantos detalles” | Antes de la lectura |
| Personajes y lugares | Fichas de Abraham, Moisés, Pablo; mapas | Enlazados desde el texto |
| Línea de tiempo | Dónde estamos en la historia bíblica | Pantalla de plan cronológico |
| Reflexión | Una pregunta y una breve aplicación | Después de la lectura |
| Glosario | Pacto, propiciación, efod… | Toque sobre término resaltado |

Recomendación: tono **pastoral pero ecuménico**, revisado por un consejo editorial de varias tradiciones, para maximizar el alcance. Las notas se escriben en Markdown con metadatos (día, plan, referencia) y se publican vía CMS (ver §6).

### 4.4 Recordatorios personalizables
- Hora(s) configurables por día de la semana; hasta 3 recordatorios (mañana, tarde, “última llamada”).
- **Recordatorio inteligente:** sugerir la hora en que el usuario suele leer.
- **No molestar al terminar:** si ya leyó hoy, se cancelan los recordatorios pendientes.
- Mensajes variados y alentadores, nunca culpabilizadores (“Hoy son 12 capítulos de Isaías, unos 40 min. ¿Te preparas un café?”).
- Recordatorio de grupo opcional (“5 personas de tu grupo ya leyeron hoy”).
- Implementación: `AlarmManager` (alarma exacta si el usuario lo permite) + `WorkManager` como respaldo; reprogramación tras reinicio (`BOOT_COMPLETED`); canal de notificación propio; solicitud del permiso `POST_NOTIFICATIONS` en contexto, no al abrir la app.

### 4.5 Lector bíblico
- Texto limpio, tipografía ajustable (tamaño, interlineado, fuente serif/sans), temas claro/sepia/oscuro.
- Desplazamiento continuo entre los capítulos del día; marca automática al llegar al final (con confirmación).
- Resaltados, notas personales y marcadores sincronizados.
- **Audio** (texto-a-voz del sistema en MVP; audio narrado licenciado en Premium), con velocidad 0,8×–2×, temporizador y reproducción en segundo plano (`Media3`).
- Comparar traducciones (si la licencia lo permite).

### 4.6 Diario personal
Entradas por día, etiquetas, búsqueda y exportación a PDF/texto. Privadas por defecto y cifradas en reposo.

---

## 5. UX / UI

### 5.1 Principios
1. **Un solo toque hasta la lectura de hoy.** La app abre en “Hoy”.
2. **Motivar sin culpa.** Lenguaje de gracia; atrasarse tiene solución visible.
3. **Carga cognitiva baja para principiantes,** profundidad opcional para expertos (divulgación progresiva).
4. **Accesible por diseño:** WCAG AA, TalkBack, texto dinámico hasta 200 %, objetivos táctiles ≥ 48 dp.
5. **Rápida en gama baja:** arranque en frío < 1,5 s, APK < 15 MB (texto descargado aparte).

### 5.2 Navegación (barra inferior, 4 pestañas)
`Hoy` · `Plan` (calendario + lista de días) · `Progreso` (mapa, estadísticas, logros) · `Comunidad` — y el perfil/ajustes desde el avatar superior.

### 5.3 Onboarding (≤ 60 segundos)
1. Bienvenida + promesa (“La Biblia completa en 90 días, ~45 min al día”).
2. ¿Es tu primera vez leyendo la Biblia completa? → recomienda orden *Mixto* o *NT primero*.
3. Elige orden de lectura (con vista previa de los días 1–3).
4. Fecha de inicio y días de descanso → muestra fecha de finalización.
5. Hora del recordatorio → solicitud de permiso de notificaciones.
6. Traducción (descarga en segundo plano mientras sigue el onboarding).
7. Cuenta **opcional** (se puede usar todo sin registrarse; se invita a crearla para sincronizar o unirse a grupos).

### 5.4 Lineamientos visuales
- **Material Design 3** con color dinámico opcional (Material You) y una paleta propia cálida (pergamino, azul profundo, dorado suave).
- Tipografía: serif legible para el texto bíblico (p. ej., *Literata* o *Source Serif*), sans para la interfaz (*Inter*/*Roboto*).
- Microinteracciones de celebración discretas al completar un día (confeti sutil o iluminación del libro en el mapa).
- Widgets de pantalla de inicio (Glance): “Hoy: Isaías 1–12 · 0/12” y progreso global.

---

## 6. Arquitectura técnica

### 6.1 Cliente Android

| Capa | Tecnología |
|---|---|
| Lenguaje | Kotlin 100 % |
| UI | Jetpack Compose + Material 3, Navigation Compose |
| Arquitectura | Clean Architecture + MVVM/UDF (StateFlow), módulos por feature |
| DI | Hilt |
| Persistencia | Room (SQLite) con **FTS4/FTS5** para búsqueda bíblica; DataStore para preferencias |
| Tareas en segundo plano | WorkManager (sincronización, descargas, notificaciones de respaldo) |
| Red | Retrofit/Ktor Client + kotlinx.serialization |
| Audio | Media3 (ExoPlayer) + MediaSession |
| Widgets | Jetpack Glance |
| Analítica / errores | Firebase Analytics (con consentimiento) + Crashlytics |
| Calidad | JUnit, Turbine, Compose UI tests, Baseline Profiles, Detekt/ktlint |
| minSdk / targetSdk | 24 / última estable |

**Módulos sugeridos:** `:app`, `:core:data`, `:core:database`, `:core:designsystem`, `:core:sync`, `:feature:today`, `:feature:reader`, `:feature:plan`, `:feature:progress`, `:feature:community`, `:feature:journal`, `:feature:settings`.

### 6.2 Modelo de datos (local)

```
Book(id, ordinal, name, testament, chapterCount)
Verse(bookId, chapter, verse, text, translationId)          -- + tabla FTS
Plan(id, name, order: CANONICAL|CHRONOLOGICAL|MIXED|THEMATIC, totalDays, version)
PlanDay(planId, dayNumber)
PlanAssignment(planId, dayNumber, seq, bookId, chapterStart, chapterEnd)
Note(id, planId?, dayNumber?, bookId?, type, markdown, version)
UserPlan(id, planId, startDate, restDays, status, graceMode)
ChapterRead(userPlanId, bookId, chapter, readAt, source: TEXT|AUDIO)  -- unidad atómica de progreso
JournalEntry(id, userPlanId, dayNumber, body, updatedAt, deleted)
Highlight(id, bookId, chapter, verseStart, verseEnd, color, note, updatedAt, deleted)
SyncOutbox(id, entity, entityId, op, payload, createdAt)
```

El progreso se registra **por capítulo**, no por día: así el usuario puede cambiar de orden de lectura sin perder lo leído, y la cobertura del 100 % es medible.

### 6.3 Backend

**Recomendación para MVP:** **Firebase** (Auth + Firestore + Cloud Storage + Cloud Functions + FCM + Remote Config) por velocidad de desarrollo y SDK offline nativo.
**Alternativa a escala / control de costos:** **Supabase** (Postgres + Auth + Storage + Edge Functions) o un backend propio en **Ktor/Spring + PostgreSQL**, especialmente si crecen las funciones comunitarias (consultas relacionales, moderación, analítica de grupos).

Componentes:

1. **Servicio de contenido (CMS):** Strapi, Sanity o Directus para que el equipo editorial gestione notas, introducciones, planes y traducciones de la interfaz. Un *pipeline* (CI) valida y empaqueta el contenido en **paquetes versionados** (`plan-cronologico-v3.json.gz`, `notas-es-v12.json.gz`, `biblia-rv1909.sqlite.gz`) publicados en un **CDN**.
2. **API de usuario:** autenticación (Google, correo, anónima convertible), perfil, progreso, diario, resaltados, preferencias.
3. **Sincronización:** endpoint `POST /sync` con cambios desde un `cursor` (o Firestore offline). Resolución de conflictos:
   - `ChapterRead`: **unión** (un capítulo leído en cualquier dispositivo cuenta).
   - Diario/resaltados: *last-write-wins* por campo `updatedAt` con *tombstones* para borrados.
4. **Comunidad:** grupos, membresías, feed por día, reacciones, moderación (filtro automático + reportes + roles de líder).
5. **Notificaciones push (FCM):** sólo para eventos sociales y campañas; los recordatorios diarios son **locales** (funcionan sin red).
6. **Analítica de producto:** eventos anónimos (día completado, abandono, uso de reprogramación) hacia BigQuery/PostHog para medir retención.

### 6.4 Licencias de traducciones (riesgo crítico)

| Traducción | Situación | Comentario |
|---|---|---|
| Reina-Valera 1909 | Dominio público | Ideal para MVP, gratis y sin restricciones. |
| Reina-Valera 1960 | Licenciada (Sociedades Bíblicas Unidas) | La más usada en Latinoamérica; requiere acuerdo. |
| NVI / NTV / LBLA / DHH | Licenciadas por sus editoriales | Negociar uso offline, límites de versículos y atribución. |
| Agregadores (p. ej., API.Bible) | Acceso por API con términos de cada editorial | Útil para empezar, pero revisar si permiten caché offline. |

Lanzar con **RV1909** y negociar RV1960 / NVI en paralelo. Verificar siempre que la licencia permita almacenamiento offline completo.

---

## 7. Estrategia *offline-first*

1. **El dispositivo es la fuente de verdad** para la experiencia diaria; la nube sólo sincroniza.
2. **Paquete base incluido o descargado en el onboarding:** texto completo de una traducción (~4–5 MB comprimido como SQLite), los planes (< 100 KB) y las notas del plan elegido (~1–2 MB).
3. **Descargas diferidas** con WorkManager sólo en Wi-Fi (configurable): traducciones adicionales, imágenes de mapas y audio (descarga por libro o por semana del plan; el audio completo pesa ~1 GB, por eso nunca de golpe). Limpieza automática del audio ya escuchado.
4. **Actualización de contenido por versiones:** al abrir la app (si hay red) se consulta un `manifest.json` en el CDN; se descargan sólo los paquetes cuya versión cambió (opcionalmente deltas).
5. **Cola de salida (outbox):** toda escritura del usuario se guarda localmente y se marca para sincronizar; WorkManager la envía con *backoff* exponencial cuando hay conexión.
6. **Recordatorios y cálculo de progreso 100 % locales.**
7. **Indicadores claros:** estado de sincronización discreto, aviso de “contenido disponible sin conexión”.
8. **Pruebas:** escenarios en modo avión, red lenta (2G/3G), cambio de dispositivo y conflicto de ediciones.

---

## 8. Comunidad y retención

### 8.1 Funciones comunitarias
- **Grupos de lectura** (iglesia, célula, familia, amigos): misma fecha de inicio y plan; código de invitación o enlace.
- **Panel del líder:** participación agregada (“18 de 24 al día”), sin exponer datos individuales salvo que cada miembro lo permita.
- **Hilo del día:** comentarios y reflexiones sobre la lectura del día dentro del grupo (moderado).
- **Compañero de responsabilidad:** emparejar a dos personas que se ven el progreso mutuamente y pueden enviarse un “ánimo”.
- **Peticiones de oración** dentro del grupo.
- **Retos públicos** periódicos (p. ej., “Empieza el 1 de enero con miles de personas”) con contador global.
- **Compartir** versículos como imagen y tarjetas de hitos.

### 8.2 Palancas de retención
| Momento de riesgo | Intervención |
|---|---|
| Días 1–3 | Onboarding corto, primer día “ganado” rápido, celebración. |
| Semana 2–4 (Levítico, Números) | Notas extra de contexto, mensaje anticipado (“Esta semana es exigente, aquí va por qué importa”), sugerir audio. |
| Tras 2 días sin leer | Ofrecer reprogramación con un toque, no culpa. |
| Mitad del plan (día 45) | Hito grande + resumen de lo aprendido. |
| Final (día 90) | Certificado/tarjeta, encuesta, invitar a un nuevo plan (otro orden, otra traducción, plan anual más pausado). |

**Métricas norte:** % de usuarios que completan los 90 días; retención D1/D7/D30; días leídos por usuario activo semanal; % de usuarios en grupos (se espera mayor finalización).

---

## 9. Modelos de monetización

Principio: **la lectura de la Biblia y el plan de 90 días son siempre gratuitos y sin anuncios.** Un público de fe es especialmente sensible a la sensación de comercializar las Escrituras.

| Modelo | Descripción | Valoración |
|---|---|---|
| **Donaciones / “Apoya el proyecto”** | Compras únicas o mensuales voluntarias (Google Play Billing). | Alta afinidad con el público; ingresos modestos. |
| **Freemium “Plus”** (≈ USD 2–4/mes o 20–30/año, precios regionales) | Audio narrado, traducciones premium licenciadas, notas de estudio profundas, planes temáticos adicionales, temas visuales, exportar diario, estadísticas avanzadas. | Recomendado como fuente principal. |
| **Licencias para iglesias (B2B)** | Grupos grandes, panel de líder avanzado, marca de la iglesia, contenido propio del pastor, informes. | Muy prometedor: las iglesias pagan por herramientas de discipulado. |
| **Patrocinio editorial** | Planes temáticos co-creados con editoriales o ministerios, claramente identificados. | Complementario; cuidar la neutralidad. |
| **Anuncios** | — | **No recomendado** dentro de la lectura; como mucho, nunca. |

**Recomendación:** Gratis + donaciones en el lanzamiento → **Plus** en v1 → **Iglesias** en v2. Si el proyecto es ministerial, valorar una estructura sin ánimo de lucro, que facilita licencias de traducciones y donaciones.

---

## 10. Privacidad, seguridad y cumplimiento
- Uso sin cuenta; datos mínimos; diario cifrado (SQLCipher o EncryptedFile) y nunca usado para analítica.
- Consentimiento explícito de analítica (GDPR para España; leyes locales en LATAM, como la LFPDPPP en México o la LGPD en Brasil si se expande).
- Exportar y borrar cuenta desde la app (requisito de Google Play).
- Sección *Data safety* de Play Console completa y precisa.
- Moderación de contenido comunitario y protección de menores (edad mínima, sin mensajes privados entre desconocidos).

---

## 11. Hoja de ruta propuesta

| Fase | Duración aprox. | Alcance |
|---|---|---|
| **0. Descubrimiento** | 2–3 semanas | Entrevistas a 10–15 usuarios, prototipo en Figma, prueba de usabilidad, negociación de licencias. |
| **1. MVP** | 8–10 semanas | Onboarding, planes canónico + mixto, pantalla Hoy, lector RV1909 offline, marcar capítulos, progreso (anillo, barra, mapa), recordatorios locales, reprogramación básica, notas de contexto por libro, analítica. Sin cuenta obligatoria. |
| **2. v1.0 pública** | +6–8 semanas | Cuentas y sincronización, plan cronológico, notas diarias completas, diario, resaltados, TTS, widgets, logros, donaciones. |
| **3. v1.5 Comunidad** | +8 semanas | Grupos, hilo del día, compañero, panel de líder, retos globales. |
| **4. v2.0 Plus e Iglesias** | +8–10 semanas | Suscripción Plus, audio narrado, traducciones licenciadas, plan temático, licencias B2B, versión iOS (Kotlin Multiplatform para compartir lógica de dominio). |

**Equipo mínimo para el MVP:** 1–2 desarrolladores Android, 1 desarrollador backend (parcial si se usa Firebase), 1 diseñador UX/UI, 1 editor de contenido teológico, 1 responsable de producto.

---

## 12. Riesgos y mitigaciones

| Riesgo | Mitigación |
|---|---|
| Licencias de traducciones caras o restrictivas | Lanzar con RV1909 (dominio público); negociar en paralelo; agregadores. |
| Abandono por la carga diaria (~45 min) | Comunicarlo con honestidad desde el onboarding; audio; días de gracia; ofrecer plan de 180/365 días como “salida digna” sin perder el progreso. |
| Contenido devocional percibido como sesgado | Consejo editorial plural; tono ecuménico; separar texto bíblico de comentarios. |
| Toxicidad en la comunidad | Comunidad sólo en grupos privados al inicio; moderación y reportes. |
| Rendimiento en dispositivos de gama baja | Baseline Profiles, pruebas en dispositivos de 2–3 GB de RAM, APK ligero. |
| Errores de cobertura en los planes | Validador automático de 1.189 capítulos en CI y en el cliente. |

---

## Anexos en este repositorio
- `docs/plan-canonico-90-dias.md` — Plan canónico de 90 días generado (tabla día por día).
- `data/plan_canonico_90.json` — El mismo plan en formato JSON, listo para importarse a la base de datos de la app.
- `tools/generar_plan.py` — Generador del plan equilibrado por versículos; sustituir el conteo estimado por el conteo real por capítulo de la traducción elegida.
