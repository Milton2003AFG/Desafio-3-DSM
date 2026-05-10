# AprendeApp

Sistema de Gestión de Recursos de Aprendizaje desarrollado como Tercer Desafío Práctico para la materia de **Desarrollo de Software para Móviles (DSM)** — Universidad Don Bosco.

---

## Descripción

AprendeApp es una aplicación Android que permite gestionar recursos de aprendizaje como libros, videos, artículos y tutoriales de educación. Consume una API externa de MockAPI.io para todas las operaciones de datos, implementa autenticación con roles diferenciados y persistencia de favoritos y calificaciones por usuario.

---

## Funcionalidades

### Autenticación
- Registro de usuario con validación segura de contraseña
- Login y Logout
- Contraseñas encriptadas con **SHA-256** antes de almacenarse
- Sistema de roles: **Estudiante** y **Docente**
- Persistencia de sesión con SharedPreferences

### Validación de Contraseña
- Mínimo 12 caracteres
- Al menos una letra mayúscula
- Al menos una letra minúscula
- Al menos un número
- Al menos un carácter especial (`!@#$%^&*`)

### Rol Estudiante
- Visualización de recursos en tarjetas
- Búsqueda en tiempo real por título, tipo o ID
- Filtrado por tipo de recurso (libro, video, artículo, tutorial)
- Marcar/desmarcar recursos como favoritos
- Calificar recursos de 1 a 5 estrellas
- Lista personalizada de favoritos
- Ver detalle completo y abrir enlace del recurso

### Rol Docente
- Panel exclusivo de administración
- **CRUD completo** de recursos:
    - Crear nuevo recurso con preview de imagen
    - Editar recurso existente con formulario prellenado
    - Eliminar recurso con confirmación previa
- Búsqueda avanzada de recursos

---

## Arquitectura

La aplicación implementa el patrón **MVC (Model-View-Controller)**:

```
app/src/main/java/com/example/desafio3dsm/
│
├── model/
│   ├── Recurso.kt
│   └── Usuario.kt
│
├── view/
│   ├── LoginActivity.kt
│   ├── RegisterActivity.kt
│   ├── MainActivity.kt
│   ├── DocenteActivity.kt
│   ├── DetalleRecursoActivity.kt
│   ├── AgregarEditarActivity.kt
│   ├── FavoritosActivity.kt
│   ├── RecursoAdapter.kt
│   └── RecursoDocenteAdapter.kt
│
├── controller/
│   ├── AuthController.kt
│   ├── RecursoController.kt
│   └── FavoritoController.kt
│
├── network/
│   ├── ApiService.kt
│   └── RetrofitClient.kt
│
└── utils/
    ├── SessionManager.kt
    ├── PasswordValidator.kt
    └── PasswordHasher.kt
```

---

## Tecnologías y Librerías

| Tecnología | Uso |
|---|---|
| **Kotlin** | Lenguaje principal |
| **Android Studio** | Entorno de desarrollo |
| **Retrofit** | Consumo de API REST |
| **Gson** | Conversión de JSON |
| **Coroutines** | Operaciones asíncronas |
| **Glide** | Carga de imágenes |
| **Material Design 3** | Componentes de UI |
| **RecyclerView** | Listas de recursos |
| **SharedPreferences** | Persistencia de sesión |
| **SHA-256** | Encriptación de contraseñas |
| **MockAPI.io** | Backend y base de datos |

---

## API — Endpoints

Base URL: `https://<tu-proyecto>.mockapi.io/api/`

### Recursos
| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/recursos` | Obtener todos los recursos |
| GET | `/recursos/{id}` | Obtener recurso por ID |
| POST | `/recursos` | Crear nuevo recurso |
| PUT | `/recursos/{id}` | Actualizar recurso |
| DELETE | `/recursos/{id}` | Eliminar recurso |

### Usuarios
| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/usuarios` | Obtener todos los usuarios |
| POST | `/usuarios` | Registrar nuevo usuario |
| PUT | `/usuarios/{id}` | Actualizar datos del usuario |

### Estructura JSON — Recurso
```json
{
  "id": "1",
  "titulo": "Clean Code",
  "descripcion": "Guía de buenas prácticas de programación.",
  "tipo": "libro",
  "enlace": "https://ejemplo.com/recurso",
  "imagen": "https://ejemplo.com/imagen.jpg",
  "rating": 4.5,
  "totalRating": 20
}
```

### Estructura JSON — Usuario
```json
{
  "id": "1",
  "nombre": "Juan Pérez",
  "email": "juan@email.com",
  "password": "a3f5c8d2e1b4f7a9...",
  "rol": "estudiante",
  "favoritos": ["1", "3", "5"]
}
```

---

## Instalación y Configuración

### Prerrequisitos
- Android Studio Hedgehog o superior
- SDK de Android 24 o superior
- Conexión a internet

### Pasos
1. Clona el repositorio:
```bash
git clone https://github.com/Milton2003AFG/Desafio-3-DSM.git
```

2. Abre el proyecto en Android Studio.

3. Configura tu URL de MockAPI en `RetrofitClient.kt`:
```kotlin
private const val BASE_URL = "https://tu-proyecto.mockapi.io/api/"
```

4. Sincroniza las dependencias con **Sync Now**.

5. Ejecuta la app en un emulador o dispositivo físico.

---

## Pantallas

| Pantalla | Descripción |
|---|---|
| Login | Inicio de sesión con validación |
| Registro | Creación de cuenta con selección de rol |
| Principal (Estudiante) | Lista de recursos con búsqueda y filtros |
| Detalle | Vista completa del recurso con calificación |
| Favoritos | Lista de recursos guardados por el usuario |
| Panel Docente | Gestión completa de recursos |
| Agregar/Editar | Formulario para crear o modificar recursos |

---

## Seguridad

Las contraseñas nunca se almacenan en texto plano. Antes de guardarse o compararse, se transforman con SHA-256:

```
Contraseña ingresada → SHA-256 → Hash almacenado en API
```

Esto significa que aunque alguien acceda a la base de datos, no puede obtener la contraseña original.

---

## Autor

Desarrollado por **Milton Antonio Flores Gómez**  
Carrera: Ingeniería en Ciencias de la Computación  
Universidad Don Bosco — 2026  
Materia: Desarrollo de Software para Móviles (DSM)
