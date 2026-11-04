# GameShelf Backend

Backend independiente para la aplicación Android **GameShelf**, desarrollado con Node.js, `json-server` (v0.17.4) y `json-server-auth` (v2.1.0).

---

## 🚀 Requisitos e Instalación

1. **Node.js**: Asegúrate de tener instalado Node.js (v16+).
2. Abrir la terminal en la carpeta `backend`:
   ```powershell
   cd backend
   npm install
   ```

---

## ▶️ Ejecución del Servidor

Para iniciar el servidor en modo desarrollo:
```powershell
npm run dev
```

El servidor estará escuchando en el puerto **3000**:
- **Localhost**: `http://localhost:3000`
- **Emulador Android**: `http://10.0.2.2:3000` *(IP reservada por el emulador de Android Studio para acceder al localhost de la máquina host)*.

---

## 🔐 Autenticación y Seguridad

`json-server-auth` gestiona la autenticación mediante tokens JWT y contraseñas encriptadas.

### 1. Registro de Usuario (`POST /register`)
- **URL**: `http://localhost:3000/register`
- **Body (JSON)**:
  ```json
  {
    "name": "Aiko",
    "email": "aiko@example.com",
    "password": "password123"
  }
  ```
- **Respuesta (201 Created)**:
  ```json
  {
    "accessToken": "<JWT_TOKEN>",
    "user": {
      "email": "aiko@example.com",
      "name": "Aiko",
      "id": 1
    }
  }
  ```

### 2. Inicio de Sesión (`POST /login`)
- **URL**: `http://localhost:3000/login`
- **Body (JSON)**:
  ```json
  {
    "email": "aiko@example.com",
    "password": "password123"
  }
  ```
- **Respuesta (200 OK)**:
  ```json
  {
    "accessToken": "<JWT_TOKEN>",
    "user": {
      "email": "aiko@example.com",
      "name": "Aiko",
      "id": 1
    }
  }
  ```

### 3. Encabezado de Autorización (`Bearer Token`)
Para acceder a los endpoints protegidos, se debe enviar la cabecera:
```text
Authorization: Bearer <accessToken>
```

---

## 🎮 Endpoints y Permisos (`games: 600`)

La regla `600` aplicada a `games` exige que:
- Solo el propietario (`userId` igual al `sub` del token JWT) puede consultar, crear, modificar o eliminar sus videojuegos.
- Usuarios no autenticados o usuarios diferentes reciben un error **401 Unauthorized** o **403 Forbidden**.

### Endpoints Disponibles:
- `GET /games`: Lista los videojuegos del usuario autenticado.
- `GET /games/:id`: Obtiene el detalle de un juego específico por ID (si pertenece al usuario).
- `POST /games`: Crea un nuevo juego (debe incluir `"userId": <id_usuario>`).
- `PUT /games/:id`: Actualiza un juego existente del usuario.
- `DELETE /games/:id`: Elimina un juego del usuario.

### Ejemplo de Body para `POST /games`:
```json
{
  "userId": 1,
  "title": "Minecraft",
  "platform": "PC",
  "genre": "Sandbox",
  "releaseYear": 2011,
  "completed": false,
  "notes": "Mi juego favorito",
  "createdAt": "2026-11-08"
}
```

---

## 🧪 Guía de Pruebas Manuales (PowerShell)

Para verificar el backend sin interfaz gráfica:

### 1. Registrar Usuario 1
```powershell
$reg1 = Invoke-RestMethod -Uri "http://localhost:3000/register" -Method Post -ContentType "application/json" -Body '{"name":"Aiko","email":"user1@example.com","password":"password123"}'
$token1 = $reg1.accessToken
$user1Id = $reg1.user.id
Write-Host "Usuario 1 Token:" $token1 "ID:" $user1Id
```

### 2. Login Usuario 1
```powershell
$login1 = Invoke-RestMethod -Uri "http://localhost:3000/login" -Method Post -ContentType "application/json" -Body '{"email":"user1@example.com","password":"password123"}'
Write-Host "Login exitoso. Token:" $login1.accessToken
```

### 3. Intento de Acceso a `/games` sin Token (Debe Fallar - 401/403)
```powershell
try {
    Invoke-RestMethod -Uri "http://localhost:3000/games" -Method Get
} catch {
    Write-Host "Acceso denegado correctamente sin token:" $_.Exception.Message
}
```

### 4. Crear Juego para Usuario 1
```powershell
$headers1 = @{ Authorization = "Bearer $token1" }
$gameBody = @{
    userId = $user1Id
    title = "Minecraft"
    platform = "PC"
    genre = "Sandbox"
    releaseYear = 2011
    completed = $false
    notes = "Mi juego favorito"
    createdAt = "2026-11-08"
} | ConvertTo-Json

$createdGame = Invoke-RestMethod -Uri "http://localhost:3000/games" -Method Post -Headers $headers1 -ContentType "application/json" -Body $gameBody
$gameId = $createdGame.id
Write-Host "Juego Creado con ID:" $gameId
```

### 5. Consultar Juegos del Usuario 1
```powershell
$myGames = Invoke-RestMethod -Uri "http://localhost:3000/games" -Method Get -Headers $headers1
$myGames | ConvertTo-Json
```

### 6. Registrar Usuario 2 y Verificar Aislamiento de Permisos
```powershell
$reg2 = Invoke-RestMethod -Uri "http://localhost:3000/register" -Method Post -ContentType "application/json" -Body '{"name":"Carlos","email":"user2@example.com","password":"password123"}'
$token2 = $reg2.accessToken
$headers2 = @{ Authorization = "Bearer $token2" }

# Intento de Usuario 2 de obtener el juego del Usuario 1 (Debe denegarse o retornar 404/Forbidden)
try {
    Invoke-RestMethod -Uri "http://localhost:3000/games/$gameId" -Method Get -Headers $headers2
} catch {
    Write-Host "Aislamiento verificado. Usuario 2 no puede acceder al juego del Usuario 1:" $_.Exception.Message
}
```

### 7. Actualizar y Eliminar Juego por Usuario 1
```powershell
# Actualizar
$updateBody = @{
    userId = $user1Id
    title = "Minecraft Java Edition"
    platform = "PC"
    genre = "Sandbox"
    releaseYear = 2011
    completed = $true
    notes = "Completado al 100%"
    createdAt = "2026-11-08"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:3000/games/$gameId" -Method Put -Headers $headers1 -ContentType "application/json" -Body $updateBody

# Eliminar
Invoke-RestMethod -Uri "http://localhost:3000/games/$gameId" -Method Delete -Headers $headers1
Write-Host "Juego eliminado con éxito."
```
