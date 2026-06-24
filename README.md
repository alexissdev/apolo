# Apolo

![Apolo](public/banner.png)

Plugin de utilidades para Spigot 1.8.8+ que incluye economía, teletransporte, warps, vuelo, modo dios, reparación, mensajería privada y herramientas de administración. Soporta sincronización entre servidores vía Redis PubSub.

## Requisitos

| Requisito | Versión mínima |
|-----------|----------------|
| Java | 11 |
| Spigot / Paper | 1.8.8 |
| MongoDB | 7.0 |
| Redis | 7.2 |
| Vault *(opcional)* | 1.7.1 |

## Instalación

### 1. Infraestructura

Levanta MongoDB y Redis con Docker Compose:

```bash
docker compose up -d
```

### 2. Compilar

```bash
./gradlew clean shadowJar
```

El JAR generado se encuentra en `apolo-plugin/build/libs/apolo-plugin.jar`.

### 3. Instalar

Copia el JAR a la carpeta `plugins/` de tu servidor y reinícialo. El plugin genera `config.yml` y `messages.yml` en `plugins/Apolo/` al primer inicio. En reinicios posteriores, las claves nuevas se agregan automáticamente al `config.yml` existente sin pisar los valores ya configurados.

---

## Configuración

**`plugins/Apolo/config.yml`**

```yaml
server-id: "lobby"         # Identificador único de este servidor (para cross-server)

redis:
  host: "localhost"
  port: 6379
  password: ""
  database: 0
  pool:
    max-total: 10
    max-idle: 5
    min-idle: 1

mongodb:
  uri: "mongodb://localhost:27017"
  database: "apolo"
  connection-timeout: 5000
  socket-timeout: 10000
  max-pool-size: 10

tpa:
  request-timeout: 30      # Segundos antes de que expire una solicitud TPA
  cooldown: 10             # Cooldown en segundos entre solicitudes

repair:
  cooldown: 60             # Cooldown en segundos entre reparaciones

fly:
  restore-on-join: true    # Restaura el estado de vuelo al reconectarse

godmode:
  restore-on-join: true    # Restaura el modo dios al reconectarse

warps:
  per-page: 6              # Warps por página en /warps
  cooldown: 5              # Cooldown en segundos entre usos de /warp

economy:
  starting-balance: 500.0
  currency-symbol: "$"
  currency-name-singular: "Dólar"
  currency-name-plural: "Dólares"
  decimal-digits: 2
  max-balance: 1000000000.0
  min-transfer-amount: 1.0  # Monto mínimo permitido en /pay

cache:
  balance-ttl: 300         # TTL del caché de saldo en Redis (segundos)
  user-ttl: 600            # TTL del caché de usuario en Redis (segundos)

messages-file: "messages.yml"
```

Los mensajes se personalizan en `plugins/Apolo/messages.yml`. Todos soportan códigos de color con `&` y el placeholder `{prefix}`.

---

## Comandos y Permisos

### Economía

| Comando | Alias | Descripción | Permiso |
|---------|-------|-------------|---------|
| `/balance [jugador]` | `bal`, `money` | Ver el saldo propio o de otro jugador | — |
| `/pay <jugador> <cantidad>` | `transfer` | Transferir dinero a otro jugador | — |
| `/baltop` | `balancetop` | Ver el ranking de saldos | — |
| `/eco set <jugador> <cantidad>` | `economy` | Establecer saldo de un jugador | `apolo.eco` |
| `/eco give <jugador> <cantidad>` | | Depositar dinero a un jugador | `apolo.eco` |
| `/eco take <jugador> <cantidad>` | | Retirar dinero de un jugador | `apolo.eco` |

> `/pay` rechaza montos menores al valor de `economy.min-transfer-amount` configurado.

### Teletransporte

| Comando | Alias | Descripción | Permiso |
|---------|-------|-------------|---------|
| `/tpa <jugador>` | | Solicitar teletransporte hacia un jugador | `apolo.tpa` |
| `/tphere <jugador>` | | Solicitar que un jugador venga a tu posición | `apolo.tphere` |
| `/tpaccept` | | Aceptar la solicitud de TPA pendiente | — |
| `/tpadeny` | | Rechazar la solicitud de TPA pendiente | — |
| `/tpacancel` | | Cancelar tu propia solicitud de TPA enviada | `apolo.tpa` |

> El cooldown entre solicitudes puede saltarse con el permiso `apolo.tpa.bypass-cooldown`.

### Warps

| Comando | Alias | Descripción | Permiso |
|---------|-------|-------------|---------|
| `/warp <nombre>` | | Teletransportarse a un warp | `apolo.warp` |
| `/warps [página]` | | Listar warps (click para teletransportarse) | `apolo.warp` |
| `/setwarp <nombre>` | | Crear un warp en tu posición actual | `apolo.warp.set` |
| `/delwarp <nombre>` | | Eliminar un warp | `apolo.warp.delete` |

> El listado de `/warps` muestra entradas clickeables que ejecutan `/warp <nombre>` directamente.  
> El cooldown entre teleports puede saltarse con el permiso `apolo.warp.bypass-cooldown`.

### Vuelo y Modo Dios

| Comando | Alias | Descripción | Permiso |
|---------|-------|-------------|---------|
| `/fly [jugador]` | | Activar/desactivar el vuelo | `apolo.fly` / `apolo.fly.others` |
| `/godmode [jugador]` | `god` | Activar/desactivar el modo dios | `apolo.godmode` / `apolo.godmode.others` |

### Reparación

| Comando | Alias | Descripción | Permiso |
|---------|-------|-------------|---------|
| `/repair` | | Reparar el ítem en mano | `apolo.repair` |
| `/repair armor` | | Reparar la armadura equipada | `apolo.repair.armor` |
| `/repair all` | | Reparar mano y armadura | `apolo.repair.all` |

> El cooldown entre reparaciones puede saltarse con el permiso `apolo.repair.bypass-cooldown`.

### Modo de Juego

| Comando | Alias | Descripción | Permiso |
|---------|-------|-------------|---------|
| `/gamemode <modo> [jugador]` | `gm` | Cambiar modo de juego | `apolo.gamemode` / `apolo.gamemode.others` |

Modos válidos: `survival`, `creative`, `adventure`, `spectator` (también acepta `0`–`3`). Permiso por modo: `apolo.gamemode.<modo>`.

### Mensajería Privada

| Comando | Alias | Descripción | Permiso |
|---------|-------|-------------|---------|
| `/msg <jugador> <mensaje>` | `message`, `tell`, `whisper` | Enviar mensaje privado (funciona cross-server) | — |
| `/reply <mensaje>` | `r` | Responder al último mensaje recibido | — |
| `/socialspy` | `ss` | Activar/desactivar la escucha de mensajes privados | `apolo.socialspy` |
| `/commandspy` | `cs` | Activar/desactivar el espionaje de comandos | `apolo.commandspy` |

---

## Permisos completos

| Permiso | Descripción |
|---------|-------------|
| `apolo.gamemode` | Cambiar modo de juego propio |
| `apolo.gamemode.others` | Cambiar modo de juego de otros |
| `apolo.gamemode.<modo>` | Usar un modo específico (`survival`, `creative`, `adventure`, `spectator`) |
| `apolo.tpa` | Enviar solicitudes TPA |
| `apolo.tpa.bypass-cooldown` | Saltarse el cooldown de TPA |
| `apolo.tphere` | Enviar solicitudes TPHere |
| `apolo.warp` | Usar warps y listarlos |
| `apolo.warp.set` | Crear warps |
| `apolo.warp.delete` | Eliminar warps |
| `apolo.warp.bypass-cooldown` | Saltarse el cooldown de `/warp` |
| `apolo.fly` | Vuelo propio |
| `apolo.fly.others` | Vuelo de otros jugadores |
| `apolo.godmode` | Modo dios propio |
| `apolo.godmode.others` | Modo dios de otros jugadores |
| `apolo.repair` | Reparar ítem en mano |
| `apolo.repair.armor` | Reparar armadura |
| `apolo.repair.all` | Reparar mano y armadura |
| `apolo.repair.bypass-cooldown` | Saltarse el cooldown de `/repair` |
| `apolo.eco` | Administrar balances (admin) |
| `apolo.socialspy` | Espiar mensajes privados |
| `apolo.commandspy` | Espiar comandos en la red |

---

## Funcionalidades cross-server

El plugin sincroniza los siguientes eventos entre servidores de la misma red a través de canales Redis:

| Canal | Función |
|-------|---------|
| `apolo:events:economy-update` | Sincroniza cambios de saldo entre servidores |
| `apolo:events:private-message` | Entrega mensajes privados a jugadores en otros servidores |
| `apolo:events:command-spy` | Distribuye eventos de CommandSpy entre servidores |

Cada servidor debe tener un `server-id` único en `config.yml`.

---

## Arquitectura

El proyecto está organizado en módulos Gradle independientes:

```
apolo/
├── apolo-api          # Interfaces, modelos, eventos y MessageKey
├── apolo-core         # Implementaciones de servicios (Tpa, Fly, God, Repair, Warp...)
├── apolo-database     # MongoDB: repositorios, mappers, codecs
├── apolo-redis        # RedisManager y RedisPlayerStateRepository
├── apolo-economy      # Vault hook, use cases de economía, sincronización
├── apolo-messaging    # MessageService y resolución de placeholders
├── apolo-commands     # Implementaciones de comandos
├── apolo-listeners    # Listeners de Bukkit
└── apolo-plugin       # Bootstrap, DI (Guice), configuración y ensamblaje final
```

Las dependencias entre módulos son unidireccionales: `apolo-plugin` depende de todos los demás; ningún módulo inferior conoce a `apolo-plugin`.

La inyección de dependencias se gestiona con **Google Guice**. Las implementaciones internas (MongoDB, Redis, Guava) están reubicadas en el JAR con Shadow para evitar conflictos con otros plugins.

---

## Compilación para desarrollo

```bash
# Compilar
./gradlew clean shadowJar

# Solo compilar sin empaquetar
./gradlew compileJava

# Levantar infraestructura local
docker compose up -d

# Detener infraestructura
docker compose down
```
