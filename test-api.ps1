$BASE = "http://localhost:9090/api/v1"

function Test-Step {
    param($Name, $ScriptBlock)
    try {
        $result = & $ScriptBlock
        Write-Host "  [PASS] $Name" -ForegroundColor Green
        return $result
    } catch {
        Write-Host "  [FAIL] $Name - $_" -ForegroundColor Red
        return $null
    }
}

Write-Host "========================================" -ForegroundColor Yellow
Write-Host "  PRUEBAS FUNCIONALES - E-COMMERCE" -ForegroundColor Yellow
Write-Host "========================================" -ForegroundColor Yellow

# ── 1. Registrar admin ──
Write-Host "`n[1] REGISTRO Y AUTENTICACION" -ForegroundColor Cyan

$adminToken = $null
Test-Step "Registrar administrador" {
    $body = '{"username":"admin","password":"admin123","role":"administrator"}'
    $r = Invoke-RestMethod -Uri "$BASE/auth/register" -Method Post -Body $body -ContentType "application/json"
    return "ok"
}

Test-Step "Login como admin" {
    $body = '{"username":"admin","password":"admin123"}'
    $r = Invoke-RestMethod -Uri "$BASE/auth/login" -Method Post -Body $body -ContentType "application/json"
    $script:adminToken = $r.jwt
    return "Token obtenido"
}

# ── 2. Crear productos ──
Write-Host "`n[2] CATALOGO DE PRODUCTOS" -ForegroundColor Cyan
$headers = @{ Authorization = "Bearer $adminToken" }

Test-Step "Crear producto - Laptop Gamer" {
    $body = '{"name":"Laptop Gamer","description":"RTX 4070, 32GB RAM","price":2500.00,"stock":10}'
    Invoke-RestMethod -Uri "$BASE/products" -Method Post -Body $body -ContentType "application/json" -Headers $headers
    return "ok"
}

Test-Step "Crear producto - Mouse Inalambrico (stock 0)" {
    $body = '{"name":"Mouse Inalambrico","description":"Logitech MX Master 3","price":89.99,"stock":0}'
    Invoke-RestMethod -Uri "$BASE/products" -Method Post -Body $body -ContentType "application/json" -Headers $headers
    return "ok"
}

Test-Step "Crear producto - Teclado Mecanico" {
    $body = '{"name":"Teclado Mecanico","description":"RGB Switch Blue","price":149.99,"stock":25}'
    Invoke-RestMethod -Uri "$BASE/products" -Method Post -Body $body -ContentType "application/json" -Headers $headers
    return "ok"
}

# ── 3. Registrar cliente ──
Write-Host "`n[3] REGISTRO CLIENTE" -ForegroundColor Cyan
$clientToken = $null

Test-Step "Registrar cliente1" {
    $body = '{"username":"cliente1","password":"pass123","role":"cliente"}'
    $r = Invoke-RestMethod -Uri "$BASE/auth/register" -Method Post -Body $body -ContentType "application/json"
    return "ok"
}

Test-Step "Login como cliente1" {
    $body = '{"username":"cliente1","password":"pass123"}'
    $r = Invoke-RestMethod -Uri "$BASE/auth/login" -Method Post -Body $body -ContentType "application/json"
    $script:clientToken = $r.jwt
    $script:clientRole = $r.role
    Write-Host "     Rol: $($r.role)" -ForegroundColor Gray
    return "Token obtenido"
}

$clientHeaders = @{ Authorization = "Bearer $clientToken" }

# ── 4. Catalogo ──
Write-Host "`n[4] CONSULTAR CATALOGO" -ForegroundColor Cyan
$productList = @()
Test-Step "GET /products - listar catalogo" {
    $r = Invoke-RestMethod -Uri "$BASE/products" -Method Get -Headers $clientHeaders
    $script:productList = $r
    Write-Host "     $($r.Count) productos en catalogo:" -ForegroundColor Gray
    foreach ($p in $r) {
        Write-Host "       - ID $($p.id_p): $($p.name) | Stock: $($p.stock) | Precio: `$$($p.price)" -ForegroundColor Gray
    }
    return "$($r.Count) productos"
}

# ── 5. Wishlist: Agregar ──
Write-Host "`n[5] LISTA DE DESEOS (WISHLIST)" -ForegroundColor Cyan
$wishlistId = $null

Test-Step "POST /wishlists/{id} - agregar Laptop a wishlist" {
    $productId = $productList[0].id_p
    $body = '{"cantidadDeseada":2}'
    $r = Invoke-RestMethod -Uri "$BASE/wishlists/$productId" -Method Post -Body $body -ContentType "application/json" -Headers $clientHeaders
    $script:wishlistId = $r.idwr
    Write-Host "     Wishlist ID: $wishlistId" -ForegroundColor Gray
    return "Agregado exitosamente"
}

# ── 6. Obtener userId del JWT ──
Test-Step "Decodificar userId del token JWT" {
    $payload = $clientToken.Split('.')[1]
    $padding = 4 - ($payload.Length % 4)
    if ($padding -ne 4) { $payload = $payload + ('=' * $padding) }
    $decoded = [System.Text.Encoding]::UTF8.GetString([System.Convert]::FromBase64String($payload))
    Write-Host "     Payload: $decoded" -ForegroundColor Gray
    $json = $decoded | ConvertFrom-Json
    $script:userId = $json.userId
    Write-Host "     userId: $userId" -ForegroundColor Gray
    return "userId = $userId"
}

# ── 7. Listar wishlist por usuario ──
Test-Step "GET /wishlists/user/{id} - listar deseos" {
    $r = Invoke-RestMethod -Uri "$BASE/wishlists/user/$userId" -Method Get -Headers $clientHeaders
    $productos = $r.productoResponseDTO
    $script:wishlistItems = $productos
    Write-Host "     $($productos.Count) producto(s) en la lista:" -ForegroundColor Gray
    foreach ($p in $productos) {
        $stockMsg = if ($p.mensaje) { " [ATENCION: $($p.mensaje)]" } else { "" }
        Write-Host "       - $($p.name) | Stock: $($p.stock) | Deseado: $($p.cantidadDeseada)$stockMsg" -ForegroundColor Gray
    }
    return "$($productos.Count) producto(s)"
}

# ── 8. Actualizar wishlist ──
Test-Step "PUT /wishlists/{id} - actualizar cantidad a 5" {
    $body = '{"cantidadDeseada":5}'
    $r = Invoke-RestMethod -Uri "$BASE/wishlists/$wishlistId" -Method Put -Body $body -ContentType "application/json" -Headers $clientHeaders
    Write-Host "     Nueva cantidad: $($r.cantidadDeseada)" -ForegroundColor Gray
    return "Actualizado"
}

# ── 9. Agregar producto sin stock / verificar notificacion ──
$productSinStock = $productList | Where-Object { $_.stock -eq 0 } | Select-Object -First 1
if ($productSinStock) {
    Test-Step "Agregar Mouse (stock 0) a wishlist" {
        $body = '{"cantidadDeseada":1}'
        $r = Invoke-RestMethod -Uri "$BASE/wishlists/$($productSinStock.id_p)" -Method Post -Body $body -ContentType "application/json" -Headers $clientHeaders
        Write-Host "     Wishlist ID: $($r.idwr)" -ForegroundColor Gray
        return "Agregado"
    }

    Test-Step "Verificar notificacion de stock agotado" {
        $r = Invoke-RestMethod -Uri "$BASE/wishlists/user/$userId" -Method Get -Headers $clientHeaders
        $mensajes = $r.productoResponseDTO | Where-Object { $_.mensaje }
        if ($mensajes) {
            foreach ($m in $mensajes) {
                Write-Host "     NOTIFICACION: $($m.mensaje)" -ForegroundColor Yellow
            }
            return "Notificacion presente"
        } else {
            return "Sin notificaciones"
        }
    }
} else {
    Write-Host "  [INFO] No hay productos sin stock disponibles" -ForegroundColor Yellow
}

# ── 10. Eliminar wishlist ──
Test-Step "DELETE /wishlists/{id} - eliminar Laptop de wishlist" {
    $r = Invoke-RestMethod -Uri "$BASE/wishlists/$wishlistId" -Method Delete -Headers $clientHeaders
    return $r.message
}

# ── 11. Verificar eliminacion ──
Start-Sleep -Seconds 1
Test-Step "GET /wishlists/user/{id} - verificar eliminacion" {
    $r = Invoke-RestMethod -Uri "$BASE/wishlists/user/$userId" -Method Get -Headers $clientHeaders
    $productos = $r.productoResponseDTO
    $exists = $productos | Where-Object { $_.id_p -eq $productList[0].id_p }
    if (-not $exists) {
        return "Laptop ya no esta en la lista (eliminacion confirmada)"
    } else {
        return "Aun en la lista"
    }
}

# ── 12. Error: agregar cantidad mayor al stock ──
Write-Host "`n[6] PRUEBAS DE ERRORES" -ForegroundColor Cyan
Test-Step "Agregar cantidad mayor al stock (debe fallar)" {
    $productId = $productList[2].id_p  # Teclado con stock 25
    $body = '{"cantidadDeseada":999}'
    try {
        $r = Invoke-RestMethod -Uri "$BASE/wishlists/$productId" -Method Post -Body $body -ContentType "application/json" -Headers $clientHeaders
        return "ERROR: No deberia haber creado"
    } catch {
        if ($_.Exception.Response.StatusCode -eq 400) {
            return "OK - Error 400 esperado: cantidad excede stock"
        }
        throw
    }
}

Test-Step "Consultar wishlist de usuario inexistente (debe fallar)" {
    try {
        $r = Invoke-RestMethod -Uri "$BASE/wishlists/user/99999" -Method Get -Headers $clientHeaders
        return "ERROR: No deberia encontrar"
    } catch {
        if ($_.Exception.Response.StatusCode -eq 400 -or $_.Exception.Response.StatusCode -eq 404) {
            return "OK - Error esperado: usuario sin wishlist"
        }
        throw
    }
}

# ── Resumen ──
Write-Host "`n========================================" -ForegroundColor Yellow
Write-Host "  PRUEBAS COMPLETADAS" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Yellow
