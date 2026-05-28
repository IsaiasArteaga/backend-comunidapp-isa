-- Categorías (codigo fijo: 1-10)
INSERT INTO categorias (codigo, nombre, creado_en, eliminado_en) VALUES
(1, 'Electrónica', NOW(), NULL),
(2, 'Hogar', NOW(), NULL),
(3, 'Deportes', NOW(), NULL),
(4, 'Libros', NOW(), NULL),
(5, 'Herramientas', NOW(), NULL),
(6, 'Jardín', NOW(), NULL),
(7, 'Música', NOW(), NULL),
(8, 'Ropa', NOW(), NULL),
(9, 'Juegos', NOW(), NULL),
(10, 'Otro', NOW(), NULL);


-- Categorías de Comercios (codigo fijo: 1-8)
INSERT INTO categorias_comercio (codigo, nombre, descripcion, creado_en, eliminado_en) VALUES
(1, 'Alimentos y Bebidas', 'Comercios de comida y bebidas', NOW(), NULL),
(2, 'Ropa y Accesorios', 'Tiendas de ropa, zapatos y accesorios', NOW(), NULL),
(3, 'Tecnología', 'Tiendas de electrónica y tecnología', NOW(), NULL),
(4, 'Servicios', 'Servicios profesionales', NOW(), NULL),
(5, 'Salud y Belleza', 'Farmacias, peluquerías y salones de belleza', NOW(), NULL),
(6, 'Educación', 'Academias, tutorías y centros educativos', NOW(), NULL),
(7, 'Hogar y Decoración', 'Tiendas de muebles y decoración', NOW(), NULL),
(8, 'Otro', 'Otras categorías de comercios', NOW(), NULL);


-- Condiciones de artículos (codigo fijo: 1=Nuevo, 2=Poco Uso, 3=Usado, 4=Dañado, 5=Defectuoso)
INSERT INTO condiciones_articulo (codigo, nombre, creado_en, eliminado_en) VALUES
(1, 'Nuevo', NOW(), NULL),
(2, 'Poco Uso', NOW(), NULL),
(3, 'Usado', NOW(), NULL),
(4, 'Dañado', NOW(), NULL),
(5, 'Defectuoso', NOW(), NULL);


-- Estados del artículo (codigo fijo: 1=Disponible, 2=Prestado)
INSERT INTO estados_articulo (codigo, nombre, creado_en, eliminado_en) VALUES
(1, 'Disponible', NOW(), NULL),
(2, 'Prestado', NOW(), NULL);


-- Tipos de transacción (codigo fijo: 1=Venta, 2=Préstamo)
INSERT INTO tipos_transaccion (codigo, nombre, creado_en, eliminado_en) VALUES
(1, 'Venta', NOW(), NULL),
(2, 'Préstamo', NOW(), NULL);


-- Estados de transacción (codigo fijo: 1=Pendiente, 2=Aceptada, 3=Rechazada, 4=DevolucionPendiente, 5=Devuelto, 6=Cancelado)
INSERT INTO estado_transacciones (codigo, nombre, descripcion) VALUES
(1, 'Pendiente', 'La transacción está pendiente de respuesta'),
(2, 'Aceptada', 'La transacción ha sido aceptada'),
(3, 'Rechazada', 'La transacción ha sido rechazada'),
(4, 'DevolucionPendiente', 'El artículo fue devuelto pero la devolución está pendiente de confirmación'),
(5, 'Devuelto', 'El artículo ha sido devuelto y confirmado'),
(6, 'Cancelado', 'La transacción ha sido cancelada');


-- Estados de solicitud de comercio (codigo fijo: 1=Pendiente, 2=Aprobada, 3=Rechazada, 4=Suspendida)
INSERT INTO estados_solicitud_comercio (codigo, nombre, creado_en, eliminado_en) VALUES
(1, 'PENDIENTE', NOW(), NULL),
(2, 'APROBADA', NOW(), NULL),
(3, 'RECHAZADA', NOW(), NULL),
(4, 'SUSPENDIDA', NOW(), NULL);


-- Permisos (con tipo: USUARIO o ADMIN)
INSERT INTO permisos (codigo, nombre, descripcion, tipo, creado_en, eliminado_en) VALUES
(1, 'GESTIONAR_COMERCIOS', 'Permite gestionar sus propios comercios', 'USUARIO', NOW(), NULL),
(2, 'GESTIONAR_USUARIOS', 'Permite editar/banear usuarios', 'ADMIN', NOW(), NULL),
(3, 'PREMIUM', 'Acceso a plan premium', 'USUARIO', NOW(), NULL);


-- Roles (codigo fijo: 1=usuario, 2=admin, 3=admin-lider)
INSERT INTO roles (codigo, nombre, descripcion, created_at) VALUES
(1, 'usuario', 'Usuario estándar', NOW()),
(2, 'admin', 'Administrador', NOW()),
(3, 'admin-lider', 'Líder administrador', NOW());


-- Menús
INSERT INTO menus (nombre, ruta, icono, orden, created_at, por_defecto) VALUES
('Inicio', 'inicio', 'pi pi-home', 1, NOW(), TRUE),
('Explorar', 'explorar', 'pi pi-search', 2, NOW(), FALSE),
('Mis Artículos', 'mis-articulos', 'pi pi-box', 3, NOW(), FALSE),
('Mis Gestiones', 'mis-gestiones', 'pi pi-list', 4, NOW(), FALSE),
('Estadísticas', 'estadisticas', 'pi pi-chart-line', 5, NOW(), FALSE),
('Comercios', 'comercios', 'pi pi-shop', 6, NOW(), FALSE),
('Análisis Predictivo', 'predictivo', 'pi pi-chart-bar', 7, NOW(), FALSE),
-- sub rutas de comercio
('Explorar', 'comercios/explorar', 'pi pi-search', 1, NOW(), FALSE),
('Mis Comercios', 'comercios/mis-comercios', 'pi pi-bookmark', 2, NOW(), FALSE),
-- admin
('Inicio', 'admin-inicio', 'pi pi-home', 1, NOW(), TRUE),
('Artículos', 'articulos', 'pi pi-box', 2, NOW(), FALSE),
('Usuarios', 'usuarios', 'pi pi-users', 3, NOW(), FALSE),
('Gestión Premium', 'gestion-premium', 'pi pi-crown', 4, NOW(), FALSE);

-- Asignación de menús por rol
-- USUARIO (rol_id=1) obtiene menús principales 1-7 y submenús de Comercios
INSERT INTO rol_menus (rol_id, menu_id)
SELECT 1, id FROM menus WHERE ruta IN ('inicio', 'explorar', 'mis-articulos', 'mis-gestiones', 'estadisticas', 'comercios', 'predictivo', 'comercios/explorar', 'comercios/mis-comercios');

-- ADMIN (rol_id=2) obtiene menús admin
INSERT INTO rol_menus (rol_id, menu_id)
SELECT 2, id FROM menus WHERE ruta IN ('admin-inicio', 'articulos', 'usuarios', 'gestion-premium');

-- Establecer relaciones padre-hijo para menús
SET @comercios_id = (SELECT id FROM menus WHERE ruta = 'comercios' LIMIT 1);
UPDATE menus SET menu_padre_id = @comercios_id
WHERE ruta IN ('comercios/explorar', 'comercios/mis-comercios');

-- Asignar permiso GESTIONAR_COMERCIOS a los submenús de comercio
SET @permiso_gestionar = (SELECT id FROM permisos WHERE nombre = 'GESTIONAR_COMERCIOS' LIMIT 1);
UPDATE menus SET permiso_id = @permiso_gestionar
WHERE ruta IN ('comercios/explorar', 'comercios/mis-comercios');


-- -- Asignar rol ADMIN (rol_id=2) a un usuario por su ID
-- UPDATE usuarios SET rol_id = 2 WHERE id = 2;
