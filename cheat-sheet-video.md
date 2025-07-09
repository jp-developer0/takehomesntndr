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
Password: password
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
