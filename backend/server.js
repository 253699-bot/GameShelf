const jsonServer = require('json-server');
const auth = require('json-server-auth');

const app = jsonServer.create();
const router = jsonServer.router('db.json');

// Asignar la base de datos del router a app.db según requiere json-server-auth
app.db = router.db;

const middlewares = jsonServer.defaults();

// Reglas de acceso (600: solo el propietario autenticado tiene permiso de lectura y escritura)
const rules = auth.rewriter({
  users: 600,
  games: 600
});

// Registro de middlewares en el orden oficial de json-server-auth
app.use(rules);
app.use(auth);
app.use(middlewares);
app.use(router);

const PORT = 3000;
app.listen(PORT, () => {
  console.log(`Servidor GameShelf Backend iniciado correctamente en el puerto ${PORT}`);
  console.log(`URL Local: http://localhost:${PORT}`);
  console.log(`URL para Emulador Android: http://10.0.2.2:${PORT}`);
});
