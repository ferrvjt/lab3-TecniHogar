# Análisis del sistema TecniHogar

## 1. Requisitos funcionales

El programa debe permitir registrar y cotizar órdenes de servicio, administrar cuadrillas, organizar jornadas y consultar reportes. Cada orden guarda su código, cliente, dirección, zona, horas estimadas, estado y número de visitas. Dependiendo del servicio, también puede guardar puntos de instalación, valor de materiales o capacidad en BTU.

Las cuadrillas tienen un código, una capacidad máxima de horas y una certificación de refrigeración. Las jornadas relacionan una cuadrilla con las órdenes que atenderá.

El sistema debe validar que no existan códigos repetidos, que los datos sean válidos y que las órdenes cumplan las restricciones de su categoría. También debe impedir asignaciones que superen la capacidad, que requieran una certificación que la cuadrilla no tiene o que involucren órdenes ya asignadas. Las cotizaciones y las operaciones rechazadas no deben generar ingresos ni cambiar datos.

## 2. Decisiones según la categoría

La clase `OrdenServicio` reúne los atributos y métodos comunes. Las clases hijas se encargan de sus propias reglas mediante herencia y polimorfismo.

| Clase | Reglas principales |
| --- | --- |
| `Diagnostico` | Cobra Q150 en zona metropolitana o Q225 en periférica; tarda 1 o 2 días y permite 3 visitas. |
| `Instalacion` | Cobra una tarifa base más Q90 por hora facturable o fracción; tarda 3 o 5 días y permite 3 visitas. |
| `InstElectrica` | Hereda el cálculo de instalación y suma Q120 y el 5 % de materiales; agrega 2 días y permite 2 visitas. |
| `MantAcc` | Cobra por horas y refrigerante, solo acepta zona metropolitana, requiere cuadrilla certificada y permite 1 visita. |

Cada clase implementa o sobrescribe métodos como `calcularCosto()`, `calcularTiempoAtencion()` y `getMaxVisitas()`. Para la compatibilidad se puede usar `esCompatible(Cuadrilla)`, que `MantAcc` sobrescribe. Así, `Jornada` no necesita preguntar de qué clase es cada orden.

## 3. Estados de las órdenes y jornadas

Una orden comienza **pendiente** cuando se registra. Al iniciar su jornada pasa a **en atención**. Si la visita es exitosa queda **completada**; si falla, vuelve a **pendiente** mientras tenga visitas disponibles. Si agota el máximo permitido, queda **cancelada** y ya no se puede asignar.

Una jornada comienza **en preparación**, cuando se pueden agregar o retirar órdenes. Pasa a **iniciada** si tiene al menos una orden y se inicia el trabajo. Finalmente queda **cerrada** cuando todas sus órdenes tienen un resultado registrado. Una cuadrilla no puede tener dos jornadas sin cerrar.

## 4. Clases y responsabilidades

- **`OrdenServicio`:** clase padre abstracta que guarda los datos comunes y define los métodos para costo, tiempo, visitas y estado.
- **`Diagnostico`:** valida el límite de 1.5 horas y calcula su tarifa fija.
- **`Instalacion`:** guarda la cantidad de puntos y calcula las horas facturables.
- **`InstElectrica`:** hereda de `Instalacion` y agrega el valor de materiales, certificado y garantía.
- **`MantAcc`:** guarda los BTU, valida la zona y comprueba la certificación de la cuadrilla.
- **`Cuadrilla`:** guarda su código, capacidad de trabajo y certificación.
- **`Jornada`:** mantiene una lista de `OrdenServicio`, valida asignaciones, controla el inicio y registra resultados.

Los atributos se mantienen privados y se consultan mediante métodos cuando es necesario. Los constructores reciben los datos iniciales y los validan. Las clases hijas usan `super(...)` para inicializar la parte heredada; en `InstElectrica`, la llamada pasa primero por `Instalacion`.

Además, se necesita una clase con `main` para el menú y una parte administradora para guardar las colecciones, verificar códigos únicos, controlar los ingresos y generar reportes.

## 5. Relación entre Instalacion e InstElectrica

`InstElectrica` hereda de `Instalacion` porque también es una instalación y utiliza las mismas reglas para calcular horas facturables y tarifa base. Solo agrega el certificado, la garantía y sus diferencias de tiempo y visitas.

Si heredara directamente de `OrdenServicio`, habría que repetir cálculos que ya existen en `Instalacion`. Con la herencia actual se reutiliza código y resulta más fácil hacer cambios.

## 6. Nuevas categorías

Si en el futuro se agregan servicios como paneles solares o cámaras de seguridad, se puede crear otra clase hija de `OrdenServicio` o de `Instalacion` si comparte sus reglas. Esta clase definiría su costo, tiempo, compatibilidad con cuadrillas y máximo de visitas.

No sería necesario cambiar la lógica de `Jornada`, porque trabaja con una lista de `OrdenServicio` y utiliza polimorfismo. Solo habría que agregar la opción para crear la nueva categoría y asegurar que los reportes puedan mostrarla.

## Consideraciones generales

Los ingresos por registro se cobran cuando el cliente acepta la orden, mientras que los Q40 por visita adicional se cobran al iniciar la jornada correspondiente. Las órdenes de prueba que vienen cargadas al iniciar el programa no deben generar ingresos. Las operaciones inválidas deben mostrar un mensaje y dejar los datos como estaban.
