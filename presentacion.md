# 🎥 Guía de Presentación - Banking Microservice
## Demostración Paso a Paso para Video

---

## 📋 **AGENDA DE PRESENTACIÓN (15-20 minutos)**

### 1. **Introducción** (2 minutos)
- Bienvenida y presentación personal
- Objetivo del ejercicio práctico
- Overview de la solución desarrollada

### 2. **Revisión de Requerimientos** (2 minutos)
- Lectura del enunciado.txt
- Identificación de puntos clave a demostrar

### 3. **Arquitectura y Diseño** (4 minutos)
- Presentación del diagrama de arquitectura
- Explicación de patrones de diseño utilizados
- Estructura del proyecto

### 4. **Demostración Funcional** (8 minutos)
- Arranque de la aplicación
- Pruebas de endpoints con Swagger
- Demostración del endpoint de auto-consumo

### 5. **Aspectos Técnicos** (3 minutos)
- Manejo de excepciones
- Tests unitarios e integración
- Observabilidad y monitoreo

### 6. **Conclusiones** (1 minuto)
- Resumen de cumplimiento de requerimientos
- Próximos pasos o mejoras

---

## 🎬 **SCRIPT DETALLADO**

### **PARTE 1: INTRODUCCIÓN**

**[PANTALLA: Terminal/IDE]**

> "Hola, soy [tu nombre] y hoy voy a presentar la solución que desarrollé para el ejercicio práctico de Santander. 
> 
> He creado un microservicio bancario completo utilizando Spring Boot que cumple con todos los requerimientos técnicos y funcionales solicitados."

**[MOSTRAR: Estructura del proyecto en el IDE]**

### **PARTE 2: REQUERIMIENTOS**

**[PANTALLA: enunciado.txt]**

> "Primero, revisemos los requerimientos del ejercicio:"

**[LEER PUNTO POR PUNTO]**
- ✅ CRUD completo sobre entidades bancarias
- ✅ Endpoint que consume su propio endpoint de consulta
- ✅ Base de datos en memoria H2
- ✅ Diseño con patrones y arquitectura sólida
- ✅ Manejo de duplicidad y excepciones
- ✅ Tests incluidos

### **PARTE 3: ARQUITECTURA Y DISEÑO**

**[PANTALLA: Diagrama de arquitectura]**

> "La arquitectura que implementé sigue los principios SOLID y utiliza varios patrones de diseño:"

**[EXPLICAR CADA CAPA]**
1. **Controller Layer**: REST endpoints con validación
2. **Service Layer**: Lógica de negocio y transacciones
3. **Repository Layer**: Acceso a datos con JPA
4. **Entity Layer**: Modelos de dominio con Builder pattern

**[MOSTRAR: Estructura de paquetes]**
```
src/main/java/com/santander/banking/
├── controller/     # REST Controllers
├── service/        # Business Logic
├── repository/     # Data Access
├── entity/         # Domain Models
├── dto/           # Data Transfer Objects
├── exception/     # Exception Handling
├── config/        # Configuration
└── util/          # Utilities & Mappers
```

**[DESTACAR PATRONES]**
- Repository Pattern
- Builder Pattern (CuentaBancaria)
- Mapper Pattern (DTO conversions)
- Dependency Injection

### **PARTE 4: DEMOSTRACIÓN FUNCIONAL**

#### **4.1 Arranque de la Aplicación**

**[PANTALLA: Terminal]**

```bash
# Clonar el repositorio
git clone https://github.com/jp-developer0/takehomesntndr.git
cd takehomesntndr

# Ejecutar la aplicación
mvn spring-boot:run
```

> "La aplicación arranca en el puerto 8080 con contexto /api/v1"

#### **4.2 Exploración de la Documentación**

**[PANTALLA: Browser - Swagger UI]**
- Navegar a: `http://localhost:8080/api/v1/swagger-ui/index.html`

> "Como pueden ver, tenemos documentación completa con OpenAPI 3, modernizada desde SpringFox."

**[MOSTRAR ENDPOINTS]**
- Cuentas Bancarias (CRUD completo)
- Consulta Interna (auto-consumo)
- Operaciones bancarias (débito/crédito)

#### **4.3 Demostración del CRUD**

**[EN SWAGGER UI]**

**A. Crear Cuenta** (POST /cuentas)
```json
{
  "numeroCuenta": "1234567890",
  "titular": "Juan Pérez García",
  "saldo": 1000.00,
  "tipoCuenta": "CORRIENTE",
  "moneda": "EUR"
}
```

**B. Consultar Cuenta** (GET /cuentas/{id})
> "Vemos que la cuenta se creó correctamente con ID 1"

**C. Actualizar Cuenta** (PUT /cuentas/{id})
```json
{
  "numeroCuenta": "1234567890",
  "titular": "Juan Pérez García",
  "saldo": 1500.00,
  "tipoCuenta": "CORRIENTE",
  "moneda": "EUR"
}
```

**D. Operaciones Bancarias**
- POST /cuentas/1/acreditar?monto=500
- POST /cuentas/1/debitar?monto=200

#### **4.4 Demostración del Endpoint de Auto-Consumo**

**[PUNTO CLAVE DEL REQUERIMIENTO]**

**[EN SWAGGER UI - Sección "Consulta Interna"]**

> "Este es el requerimiento especial: un endpoint que consume su propio endpoint de consulta."

**Probar:**
- GET /consulta-interna/cuenta/1
- GET /consulta-interna/cuentas-activas
- GET /consulta-interna/estadisticas

**[MOSTRAR RESPUESTA]**
```json
{
  "origen": "consulta-interna",
  "endpointConsumido": "http://localhost:8080/api/v1/cuentas/1",
  "timestampConsulta": 1699123456789,
  "statusRespuesta": 200,
  "cuenta": { ... }
}
```

> "Como pueden ver, el endpoint realiza una llamada HTTP a sí mismo y retorna metadatos adicionales."

#### **4.5 Manejo de Excepciones y Validaciones**

**[DEMOSTRAR CASOS DE ERROR]**

**A. Cuenta Duplicada** (409 Conflict)
```json
{
  "numeroCuenta": "1234567890",  // ← Mismo número
  "titular": "Otro Usuario",
  "saldo": 500.00,
  "tipoCuenta": "AHORROS",
  "moneda": "EUR"
}
```

**B. Datos Inválidos** (400 Bad Request)
```json
{
  "numeroCuenta": "123",        // ← Muy corto
  "titular": "",               // ← Vacío
  "saldo": -100,              // ← Negativo
  "tipoCuenta": null,         // ← Nulo
  "moneda": "INVALID"         // ← Formato inválido
}
```

**C. Saldo Insuficiente**
- POST /cuentas/1/debitar?monto=10000

> "Cada error retorna un JSON estructurado con códigos específicos y mensajes descriptivos."

#### **4.6 Base de Datos H2**

**[PANTALLA: H2 Console]**
- Navegar a: `http://localhost:8080/api/v1/h2-console`
- URL: `jdbc:h2:mem:bankingdb`
- Usuario: `sa`, Password: (vacío)

**[MOSTRAR TABLA]**
```sql
SELECT * FROM cuentas_bancarias;
```

> "Aquí vemos los datos persistidos en la base de datos en memoria H2."

### **PARTE 5: ASPECTOS TÉCNICOS**

#### **5.1 Tests**

**[PANTALLA: Terminal]**

```bash
# Ejecutar tests unitarios
mvn test -Dtest=CuentaBancariaServiceImplTest

# Ejecutar tests de integración  
mvn test -Dtest=CuentaBancariaControllerIntegrationTest

# Todos los tests
mvn test
```

**[MOSTRAR RESULTADOS]**
> "Tenemos cobertura completa con tests unitarios para la lógica de negocio y tests de integración para el flujo completo HTTP."

#### **5.2 Observabilidad**

**[PANTALLA: Browser]**
- Health: `http://localhost:8080/api/v1/actuator/health`
- Metrics: `http://localhost:8080/api/v1/actuator/metrics`

> "La aplicación incluye monitoreo completo con Spring Actuator."

#### **5.3 Arquitectura del Código**

**[PANTALLA: IDE - Mostrando clases clave]**

**A. Entity con Builder Pattern**
```java
@Entity
public class CuentaBancaria {
    @Builder
    public static class CuentaBancariaBuilder { ... }
}
```

**B. Service con Transacciones**
```java
@Service
@Transactional
public class CuentaBancariaServiceImpl { ... }
```

**C. Exception Handler Global**
```java
@ControllerAdvice
public class GlobalExceptionHandler { ... }
```

### **PARTE 6: CONCLUSIONES**

**[PANTALLA: Checklist de requerimientos]**

> "Para resumir, la solución cumple completamente con todos los requerimientos:"

✅ **CRUD Completo**: Create, Read, Update, Delete + operaciones bancarias
✅ **Auto-consumo**: Endpoints de consulta interna funcionando
✅ **H2 Database**: Base de datos en memoria configurada
✅ **Arquitectura Sólida**: Patrones de diseño y principios SOLID
✅ **Manejo de Excepciones**: Validaciones y errores estructurados
✅ **Tests Completos**: Unitarios e integración con alta cobertura

> "Además, agregué funcionalidades extra como documentación OpenAPI 3, métricas, logging, y una arquitectura escalable lista para producción."

**[MOSTRAR: GitHub Repository]**
> "Todo el código está disponible en GitHub con documentación completa."

---

## 🎯 **PUNTOS CLAVE A ENFATIZAR**

### ✅ **Cumplimiento de Requerimientos**
1. **CRUD Completo**: Demostrar cada operación funcionando
2. **Auto-consumo**: Mostrar claramente el endpoint consumiendo a sí mismo
3. **H2 Database**: Verificar persistencia en la consola H2
4. **Arquitectura**: Explicar patrones y estructura del código
5. **Excepciones**: Mostrar validaciones y manejo de errores
6. **Tests**: Ejecutar y mostrar resultados

### 🎨 **Aspectos de Presentación**
- **Flujo Natural**: De requerimientos → arquitectura → demo → código
- **Mostrar, No Solo Contar**: Ejecutar todo en vivo
- **Preparar Datos**: Tener ejemplos listos para copiar/pegar
- **Timing**: Practicar para mantener ritmo adecuado

### 🔧 **Tips Técnicos**
- **Terminal Limpio**: Limpiar antes de grabar
- **Zoom Adecuado**: Texto legible en video
- **Conexión Estable**: Para evitar delays en Swagger
- **Backup Plan**: Tener screenshots por si algo falla

---

## 📱 **HERRAMIENTAS RECOMENDADAS**

### **Para Grabación**
- **OBS Studio**: Gratis, profesional
- **Loom**: Fácil de usar, buena calidad
- **Zoom**: Grabar reunión contigo mismo

### **Para Edición (Opcional)**
- **DaVinci Resolve**: Gratis, potente
- **OpenShot**: Simple y gratis
- **Camtasia**: Pago pero muy fácil

### **Para Diagramas**
- **Mermaid**: Ya incluido en el README
- **Draw.io**: Online, gratis
- **Lucidchart**: Profesional

---

## 🎬 **CHECKLIST PRE-GRABACIÓN**

### ⚙️ **Setup Técnico**
- [ ] Aplicación funcionando correctamente
- [ ] Base de datos limpia (restart si es necesario)
- [ ] Browser con pestañas preparadas
- [ ] Terminal con comandos listos
- [ ] IDE con código ordenado

### 📋 **Contenido**
- [ ] Script revisado y practicado
- [ ] Ejemplos JSON preparados
- [ ] Timing calculado (15-20 min max)
- [ ] Puntos clave memorizados

### 🎥 **Grabación**
- [ ] Audio claro y sin ruido
- [ ] Pantalla con resolución adecuada
- [ ] Grabación de pantalla completa o ventana
- [ ] Micrófono testeado

---

## 🚀 **PRÓXIMOS PASOS**

1. **Revisar**: Leer este script completo
2. **Preparar**: Setup técnico y datos de prueba
3. **Practicar**: Hacer una prueba sin grabar
4. **Grabar**: Seguir el script paso a paso
5. **Revisar**: Ver el video y hacer ajustes si es necesario

---

## 💡 **IDEAS ADICIONALES PARA UNA PRESENTACIÓN EXCELENTE**

### 🎨 **Elementos Visuales**
- **Pantalla Dividida**: IDE en una mitad, browser en la otra
- **Highlights**: Usar marcadores de color para código importante
- **Zoom Dinámico**: Acercar a líneas específicas de código
- **Cursor Tracking**: Usar herramientas que resalten el cursor

### 📊 **Diagramas Incluidos**
1. **Diagrama de Arquitectura General**: Muestra todos los componentes y sus relaciones
2. **Diagrama de Flujo de Auto-Consumo**: Secuencia específica del requerimiento especial
3. **Modelo de Datos**: Estructura de las entidades y relaciones

### 🗣️ **Tips de Presentación**
- **Velocidad**: Hablar claro y pausado, no apresurarse
- **Contexto**: Explicar ANTES de mostrar cada cosa
- **Interacción**: Hacer preguntas retóricas para mantener atención
- **Transiciones**: Usar frases de conexión entre secciones

### 📝 **Elementos de Storytelling**
- **Problema**: "El desafío era crear un microservicio bancario..."
- **Solución**: "Mi enfoque fue utilizar arquitectura por capas..."
- **Resultado**: "Como pueden ver, la solución cumple todos los requerimientos..."

### 🎯 **Momentos Clave para Destacar**
1. **Auto-consumo funcionando**: El momento "wow" del requerimiento especial
2. **Manejo de errores**: Mostrar profesionalismo en la implementación
3. **Tests pasando**: Demostrar calidad y confiabilidad
4. **Swagger UI**: Mostrar documentación profesional

### 📱 **Optimización del Video**
- **Duración**: Mantener entre 15-20 minutos máximo
- **Resolución**: 1080p mínimo para texto legible
- **Audio**: Usar micrófono externo si es posible
- **Ambiente**: Fondo silencioso, buena iluminación

### 🎪 **Cierre Impactante**
```
"En resumen, no solo cumplí con todos los requerimientos técnicos, 
sino que entregué una solución production-ready con:
- Arquitectura escalable
- Documentación completa  
- Tests exhaustivos
- Observabilidad integrada
- Código limpio y mantenible

Esta solución demuestra mi capacidad para entregar software de 
calidad empresarial que cumple y supera las expectativas."
```

### 🔗 **Referencias Finales**
- **GitHub**: "Código disponible en github.com/jp-developer0/takehomesntndr"
- **Documentación**: "README completo con instrucciones de instalación"
- **Contacto**: "Disponible para cualquier pregunta o aclaración"

### 🎊 **Bonus: Mejoras Futuras**
Mencionar brevemente posibles extensiones:
- **Seguridad**: Autenticación JWT
- **Cache**: Redis para mejor performance
- **Metrics**: Prometheus + Grafana
- **CI/CD**: Pipeline automatizado
- **Docker**: Containerización completa

¡Buena suerte con tu presentación! 🎯 