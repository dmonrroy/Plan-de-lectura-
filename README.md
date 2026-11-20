# Biblia en 90 días (Android)

App Android para leer la Biblia completa en 90 días: plan diario, progreso por capítulo, día y libro, y recordatorios. Funciona 100 % sin conexión.

- [Documento de concepto](docs/concepto-app-biblia-90-dias.md)
- [Plan canónico de 90 días](docs/plan-canonico-90-dias.md) · [JSON](data/plan_canonico_90.json)

## Estructura

```
app/                 App Android (Kotlin + Jetpack Compose + Material 3)
  data/              Room (capítulos leídos), DataStore (preferencias), carga de planes
  notifications/     Recordatorio diario con WorkManager
  ui/                Navegación, tema y pantallas: Bienvenida, Hoy, Plan, Día, Progreso, Ajustes
core/domain/         Lógica pura en Kotlin (sin Android): catálogo bíblico, planes,
                     validador de cobertura y cálculo de progreso. Con tests.
data/                Planes en JSON (fuente única; se empaquetan como assets de la app)
tools/               Generador de planes (Python)
```

## Funcionalidades de esta primera versión

- **Bienvenida:** eliges la fecha de inicio (hoy, mañana o el próximo lunes) y ves la fecha de fin.
- **Hoy:** la lectura del día con anillo de progreso, capítulos marcables uno a uno o el día completo, y mensajes de ritmo sin culpa ("vas al día", "pendientes de días anteriores").
- **Plan:** los 90 días con su estado (leído, en curso, pendiente, hoy, próximo).
- **Progreso:** porcentaje global, días completos, fecha prevista y fecha proyectada según tu ritmo, y un mapa de los 66 libros que se ilumina a medida que lees.
- **Ajustes:** recordatorio diario con hora configurable (sólo avisa si aún no has leído) y reinicio del plan.
- **Validación:** al cargar un plan, la app verifica que cubre los 1.189 capítulos.

## Compilar

Requisitos: Android Studio (Narwhal o posterior) o JDK 17+ con el SDK de Android (API 36).

```bash
./gradlew :core:domain:test   # tests de la lógica (plan, cobertura, progreso, recordatorio)
./gradlew :app:assembleDebug  # APK de depuración
./gradlew :app:installDebug   # instalar en un dispositivo o emulador
```

Para regenerar el plan: `python3 tools/generar_plan.py`.

## Próximos pasos

1. Texto bíblico offline (Reina-Valera 1909, dominio público) y lector integrado.
2. Planes cronológico, mixto y "NT primero".
3. Notas de contexto por libro y por día.
4. Reprogramación automática cuando el usuario se atrasa.
5. Cuentas y sincronización; después, grupos de lectura.
