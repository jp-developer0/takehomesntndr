# 🎬 Cheat Sheet para Video - Banking Microservice

## 📋 **COMANDOS LISTOS PARA COPIAR**

### **Setup Inicial**
```bash
# Clonar repositorio
git clone https://github.com/jp-developer0/takehomesntndr.git
cd takehomesntndr

# Ejecutar aplicación
mvn spring-boot:run

# En otra terminal - verificar que está corriendo
curl -s http://localhost:8080/api/v1/actuator/health

# Tests
mvn test
mvn test -Dtest=CuentaBancariaServiceImplTest
mvn test -Dtest=CuentaBancariaControllerIntegrationTest
```

### **URLs Importantes**
```
# Swagger UI
http://localhost:8080/api/v1/swagger-ui/index.html

# H2 Console
http://localhost:8080/api/v1/h2-console

# Actuator Health
http://localhost:8080/api/v1/actuator/health

# Actuator Metrics
http://localhost:8080/api/v1/actuator/metrics
```

---

## 📄 **DATOS JSON PARA SWAGGER**

### **1. Crear Cuenta Válida**
```json
{
  "numeroCuenta": "1234567890",
  "titular": "Juan Pérez García",
  "saldo": 1000.00,
  "tipoCuenta": "CORRIENTE",
  "moneda": "EUR"
}
```

### **2. Crear Segunda Cuenta**
```json
{
  "numeroCuenta": "9876543210",
  "titular": "María López Fernández",
  "saldo": 2500.50,
  "tipoCuenta": "AHORROS",
  "moneda": "EUR"
}
```

### **3. Actualizar Cuenta**
```json
{
  "numeroCuenta": "1234567890",
  "titular": "Juan Pérez García",
  "saldo": 1500.00,
  "tipoCuenta": "CORRIENTE",
  "moneda": "EUR"
}
```

### **4. Cuenta Duplicada (Error 409)**
```json
{
  "numeroCuenta": "1234567890",
  "titular": "Otro Usuario",
  "saldo": 500.00,
  "tipoCuenta": "AHORROS",
  "moneda": "EUR"
}
```

### **5. Datos Inválidos (Error 400)**
```json
{
  "numeroCuenta": "123",
  "titular": "",
  "saldo": -100,
  "tipoCuenta": null,
  "moneda": "INVALID"
}
```

---

## 🔍 **ENDPOINTS PARA DEMOSTRAR**

### **CRUD Básico**
```
POST   /api/v1/cuentas                    # Crear cuenta
GET    /api/v1/cuentas                    # Listar todas
GET    /api/v1/cuentas/1                  # Obtener por ID
PUT    /api/v1/cuentas/1                  # Actualizar
DELETE /api/v1/cuentas/1                  # Eliminar
```

### **Operaciones Bancarias**
```
POST   /api/v1/cuentas/1/acreditar?monto=500    # Depositar
POST   /api/v1/cuentas/1/debitar?monto=200      # Retirar
POST   /api/v1/cuentas/1/debitar?monto=10000    # Error: saldo insuficiente
POST   /api/v1/cuentas/1/activar               # Activar cuenta
POST   /api/v1/cuentas/1/desactivar            # Desactivar cuenta
```

### **Auto-Consumo (Requerimiento Especial)**
```
GET    /api/v1/consulta-interna/cuenta/1               # Consulta interna específica
GET    /api/v1/consulta-interna/cuentas-activas        # Consulta cuentas activas
GET    /api/v1/consulta-interna/estadisticas           # Estadísticas generales
```

### **Búsquedas y Filtros**
```
GET    /api/v1/cuentas/buscar/titular?nombre=Juan      # Por titular
GET    /api/v1/cuentas/buscar/tipo?tipo=CORRIENTE      # Por tipo
GET    /api/v1/cuentas/buscar/saldo?minimo=500&maximo=2000  # Por rango de saldo
GET    /api/v1/cuentas/estadisticas                    # Estadísticas
```

---

## 🗄️ **H2 DATABASE QUERIES**

### **Conexión H2**
```
URL: jdbc:h2:mem:bankingdb
User: sa
Password: (vacío)
```

### **Consultas SQL**
```sql
-- Ver todas las cuentas
SELECT * FROM cuentas_bancarias;

-- Ver estructura de la tabla
SHOW COLUMNS FROM cuentas_bancarias;

-- Estadísticas básicas
SELECT 
    COUNT(*) as total_cuentas,
    SUM(saldo) as saldo_total,
    AVG(saldo) as saldo_promedio,
    tipo_cuenta
FROM cuentas_bancarias 
GROUP BY tipo_cuenta;

-- Cuentas activas
SELECT * FROM cuentas_bancarias WHERE activa = true;
```

---

## 🎯 **SECUENCIA DE DEMOSTRACIÓN**

### **1. Setup (2 min)**
1. `mvn spring-boot:run`
2. Abrir Swagger UI
3. Mostrar endpoints disponibles

### **2. CRUD Demo (4 min)**
1. POST crear cuenta 1
2. POST crear cuenta 2  
3. GET listar todas
4. GET obtener por ID
5. PUT actualizar cuenta 1
6. POST operaciones bancarias

### **3. Auto-Consumo (2 min)**
1. GET /consulta-interna/cuenta/1
2. Mostrar respuesta con metadatos
3. GET /consulta-interna/estadisticas

### **4. Errores (1 min)**
1. POST cuenta duplicada → 409
2. POST datos inválidos → 400
3. POST débito excesivo → 400

### **5. H2 Database (1 min)**
1. Abrir H2 Console
2. SELECT * FROM cuentas_bancarias
3. Mostrar datos persistidos

### **6. Tests (1 min)**
1. `mvn test`
2. Mostrar resultados

---

## 💡 **TIPS DURANTE LA GRABACIÓN**

### **Antes de Empezar**
- [ ] Limpiar terminal
- [ ] Cerrar aplicaciones innecesarias
- [ ] Tener este cheat sheet abierto
- [ ] Zoom adecuado en IDE y browser

### **Durante la Demo**
- [ ] Explicar ANTES de hacer cada acción
- [ ] Pausar después de cada resultado importante
- [ ] Señalar elementos clave en pantalla
- [ ] Mantener ritmo pausado pero dinámico

### **Frases de Transición**
- "Ahora vamos a probar..."
- "Como pueden ver aquí..."
- "Esto demuestra que..."
- "El siguiente paso es..."
- "Noten que la respuesta incluye..."

### **Si Algo Falla**
- Mantener la calma
- Explicar qué se esperaba
- Mostrar logs si es útil
- Continuar con el siguiente punto

---

## 🎬 **FRASES CLAVE MEMORIZADAS**

### **Auto-Consumo**
> "Este endpoint es especial porque consume su propio endpoint de consulta. Como pueden ver, realiza una llamada HTTP interna y retorna metadatos adicionales que demuestran el auto-consumo."

### **Arquitectura**
> "La arquitectura sigue principios SOLID con separación clara de responsabilidades: controladores para la API REST, servicios para lógica de negocio, y repositorios para acceso a datos."

### **Testing**
> "La aplicación incluye tanto tests unitarios para la lógica de negocio como tests de integración que prueban el flujo HTTP completo."

### **Cierre**
> "Esta solución no solo cumple todos los requerimientos técnicos, sino que demuestra buenas prácticas de desarrollo profesional con código limpio, documentación completa y arquitectura escalable."

---

## 📱 **BACKUP PLAN**

### **Si la App No Arranca**
- Verificar puerto 8080 libre: `lsof -i :8080`
- Usar puerto alternativo: `--server.port=8081`
- Mostrar código directamente si persiste el problema

### **Si Swagger No Carga**
- Usar curl para demostrar endpoints
- Mostrar documentación en README.md
- Usar Postman como alternativa

### **Screenshots de Respaldo**
- Tener capturas de Swagger UI funcionando
- Respuestas JSON típicas guardadas
- Diagramas como imágenes estáticas

¡Todo listo para una presentación exitosa! 🚀 