# Other Charges — Importación desde QR a Q

## Resumen

Los Other Charges en una Quotation (Q) ahora tienen dos fuentes:

1. **Propios** (`/ip/q/{id_quotation}/other_charges`) — CRUD completo, se almacenan en `t_ip_quotation_other_charges`
2. **Importados desde QR** (`/ip/q/{id_quotation}/other_charges/imported-from-qr`) — Solo lectura + eliminar, se almacenan en `t_ip_quotation_other_charges_quote_request`

El `totalOtherCharges` en la respuesta de la Q suma **ambas** fuentes.

---

## Endpoints Propios (sin cambios)

| Método | URL | Descripción |
|--------|-----|-------------|
| `POST` | `/ip/q/{id_quotation}/other_charges` | Crear other charge propio |
| `PUT` | `/ip/q/{id_quotation}/other_charges/{id_other_charge}` | Editar other charge propio |
| `GET` | `/ip/q/{id_quotation}/other_charges/{id_other_charge}` | Obtener other charge propio |
| `DELETE` | `/ip/q/{id_quotation}/other_charges/{id_other_charge}` | Eliminar other charge propio |

### Request Body (POST / PUT)

```json
{
  "description": "Freight charges",
  "value": 150.00
}
```

### Response (POST / PUT)

```json
{
  "title": "Success",
  "message": "The Other Charge has been successfully added to the Quotation",
  "data": {
    "id": "uuid",
    "value": 150.00,
    "description": "Freight charges"
  }
}
```

---

## Endpoints de Importación desde QR (nuevos)

### POST — Importar Other Charges desde QR

```
POST /ip/q/{id_quotation}/other_charges/import-from-qr
```

Importa otros charges desde las Quote Requests asociadas a la Q. Los valores se copian y quedan como read-only. Requiere permiso `UPDATE_IP_QUOTATIONS`.

El backend resuelve automáticamente el Q-QR junction (`quotations_quote_request_id`) a partir de la QR propietaria del other charge. Si el other charge ya fue importado previamente, se omite silenciosamente.

#### Request Body

```json
{
  "ids": [
    "uuid-del-other-charge-1",
    "uuid-del-other-charge-2",
    "uuid-del-other-charge-3"
  ]
}
```

- `ids`: Lista de UUIDs de los other charges en las QR (obtenidos del endpoint `available-from-qr` o del listado de la QR)

#### Response — 201 Created

```json
{
  "title": "Success",
  "message": "Other charges imported from QR successfully",
  "data": [
    {
      "id": "uuid",
      "quotationsQuoteRequestId": "uuid",
      "qrOtherCharge": {
        "id": "uuid",
        "value": 100.00,
        "description": "Handling fee"
      }
    }
  ]
}
```

---

### GET — Listar Other Charges disponibles desde QR

```
GET /ip/q/{id_quotation}/other_charges/available-from-qr
```

Retorna todos los other charges de las QR asociadas a la Q que **aún no han sido importados**. Requiere permiso `UPDATE_IP_QUOTATIONS`.

#### Response — 200 OK

```json
[
  {
    "id": "uuid",
    "value": 100.00,
    "description": "Handling fee",
    "qrNumber": "QR-001"
  },
  {
    "id": "uuid",
    "value": 75.50,
    "description": "Inspection",
    "qrNumber": "QR-002"
  }
]
```

---

### GET — Obtener un Other Charge importado

```
GET /ip/q/{id_quotation}/other_charges/imported-from-qr/{id}
```

#### Response — 200 OK

```json
{
  "id": "uuid",
  "quotationsQuoteRequestId": "uuid",
  "qrOtherCharge": {
    "id": "uuid",
    "value": 100.00,
    "description": "Handling fee"
  }
}
```

---

### DELETE — Eliminar un Other Charge importado

```
DELETE /ip/q/{id_quotation}/other_charges/imported-from-qr/{id}
```

Requiere permiso `UPDATE_IP_QUOTATIONS`. No se pueden editar, solo eliminar.

#### Response — 200 OK

```json
{
  "title": "Success",
  "message": "QR other charge removed from quotation",
  "data": "uuid-del-registro-eliminado"
}
```

---

## Q Response — Campos Relevantes

La respuesta de `GET /ip/q/{id_quotation}` incluye:

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `otherCharges` | `List<IpQuotationOtherChargeResponse>` | Other charges propios |
| `qrOtherCharges` | `List<IpQuotationOtherChargesQuoteRequestResponse>` | Other charges importados desde QR |
| `totalOtherCharges` | `BigDecimal` | Suma de ambas listas (cálculo automático) |

### Ejemplo parcial

```json
{
  "otherCharges": [
    { "id": "uuid", "value": 200.00, "description": "Customs" }
  ],
  "qrOtherCharges": [
    {
      "id": "uuid",
      "quotationsQuoteRequestId": "uuid",
      "qrOtherCharge": {
        "id": "uuid",
        "value": 100.00,
        "description": "Handling fee"
      }
    }
  ],
  "totalOtherCharges": 300.00,
  "subTotal": 5000.00,
  "total": 5300.00
}
```

---

## Flujo Recomendado para el Frontend

1. El usuario abre una Q en estado CREATED
2. La Q tiene QR asociadas (campo `listQuoteRequests` con `qqrId` e `id`)
3. **Opcional**: llamar a `GET available-from-qr` para ver qué other charges están disponibles
4. **Importar**: el usuario selecciona items de la lista y hace `POST import-from-qr` con `{ "ids": ["uuid-1", "uuid-2", ...] }` (solo los IDs de QR other charges)
5. Los imported charges aparecen en `qrOtherCharges` y se suman al total
6. Para eliminar: `DELETE imported-from-qr/{id}`

---

## Consideraciones

- Los otros charges importados **no se pueden editar** (solo eliminar)
- Al eliminar una QR de la Q, se eliminan automáticamente sus otros charges importados
- No se permiten duplicados del mismo `qrOtherChargeId` dentro de una misma Q (por unique constraint)
- La historia de la Q registra `ADD_OTHER_CHARGE` / `REMOVE_OTHER_CHARGE` tanto para propios como importados
