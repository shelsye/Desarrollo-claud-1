import { useEffect, useState } from "react";
import axios from "axios";

import {
  AuthenticatedTemplate,
  UnauthenticatedTemplate,
  useMsal
} from "@azure/msal-react";

import { loginRequest, apiConfig } from "./authConfig";
import "./App.css";

function App() {
  const { instance, accounts } = useMsal();

  const [roles, setRoles] = useState([]);

  const [catalogo, setCatalogo] = useState({
    categorias: [],
    prioridades: []
  });

  const [categoriasDisponibles, setCategoriasDisponibles] = useState([]);

  // CLIENTE
  const [tituloSolicitud, setTituloSolicitud] = useState("");
  const [descripcionSolicitud, setDescripcionSolicitud] = useState("");
  const [categoriaId, setCategoriaId] = useState("");
  const [prioridadId, setPrioridadId] = useState("");
  const [misSolicitudes, setMisSolicitudes] = useState([]);
  const [mensajeCliente, setMensajeCliente] = useState("");

  // OPERADOR
  const [solicitudes, setSolicitudes] = useState([]);
  const [solicitudId, setSolicitudId] = useState("");
  const [nuevoEstado, setNuevoEstado] = useState("ASIGNADA");
  const [mensajeOperador, setMensajeOperador] = useState("");

  // ADMINISTRADOR
  const [categoriaNombre, setCategoriaNombre] = useState("");
  const [prioridadNombre, setPrioridadNombre] = useState("");
  const [mensajeAdmin, setMensajeAdmin] = useState("");

  // PRUEBAS
  const [mensajePrueba, setMensajePrueba] = useState("");

  const [procesando, setProcesando] = useState(false);

  const usuario = accounts[0]?.username || "";
  const nombre = accounts[0]?.name || "Usuario MesaTech";

  const esCliente = roles.includes("CLIENTE");
  const esOperador = roles.includes("OPERADOR");
  const esAdmin = roles.includes("ADMINISTRADOR");

  // --------------------------------------------------
  // LOGIN / LOGOUT
  // --------------------------------------------------

  const iniciarSesion = () => {
    instance.loginRedirect(loginRequest);
  };

  const cerrarSesion = () => {
    instance.logoutRedirect();
  };

  // --------------------------------------------------
  // OBTENER TOKEN
  // --------------------------------------------------

  const obtenerToken = async () => {
    const respuesta = await instance.acquireTokenSilent({
      ...loginRequest,
      account: accounts[0]
    });

    return respuesta.accessToken;
  };

  // --------------------------------------------------
  // LEER CLAIMS DEL JWT
  // --------------------------------------------------

  const leerPayloadToken = (token) => {
    try {
      let parte = token
        .split(".")[1]
        .replace(/-/g, "+")
        .replace(/_/g, "/");

      while (parte.length % 4) {
        parte += "=";
      }

      const json = decodeURIComponent(
        atob(parte)
          .split("")
          .map(
            (c) =>
              "%" +
              ("00" + c.charCodeAt(0).toString(16)).slice(-2)
          )
          .join("")
      );

      return JSON.parse(json);
    } catch (error) {
      console.error("No se pudo leer el token:", error);
      return {};
    }
  };

  // --------------------------------------------------
  // CARGA INICIAL
  // --------------------------------------------------

  useEffect(() => {
    const cargarSesion = async () => {
      if (!accounts.length) {
        setRoles([]);
        return;
      }

      try {
        const token = await obtenerToken();

        const payload = leerPayloadToken(token);

        setRoles(payload.roles || []);

        const [respuestaCatalogo, respuestaCategoriasDisponibles] =
          await Promise.all([
            axios.get(
              `${apiConfig.baseUrl}/v1/catalogo`,
              {
                headers: {
                  Authorization: `Bearer ${token}`
                }
              }
            ),
            axios.get(
              `${apiConfig.baseUrl}/v1/solicitudes/categorias-disponibles`,
              {
                headers: {
                  Authorization: `Bearer ${token}`
                }
              }
            )
          ]);

        const datos = respuestaCatalogo.data || {
          categorias: [],
          prioridades: []
        };

        const categoriasLocales =
          respuestaCategoriasDisponibles.data || [];

        setCatalogo(datos);
        setCategoriasDisponibles(categoriasLocales);

        if (categoriasLocales.length > 0) {
          setCategoriaId(
            String(categoriasLocales[0].categoriaIdOrigen)
          );
        } else {
          setCategoriaId("");
        }

        if (datos.prioridades?.length > 0) {
          setPrioridadId(String(datos.prioridades[0].id));
        }
      } catch (error) {
        console.error("Error cargando sesión:", error);
      }
    };

    cargarSesion();

    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [accounts]);

  // --------------------------------------------------
  // CLIENTE - ACTUALIZAR CATEGORIAS DISPONIBLES
  // --------------------------------------------------

  const actualizarCategoriasDisponibles = async () => {
    try {
      const token = await obtenerToken();

      const response = await axios.get(
        `${apiConfig.baseUrl}/v1/solicitudes/categorias-disponibles`,
        {
          headers: {
            Authorization: `Bearer ${token}`
          }
        }
      );

      const categorias = response.data || [];

      setCategoriasDisponibles(categorias);

      if (categorias.length > 0) {
        const categoriaActualExiste = categorias.some(
          (categoria) =>
            String(categoria.categoriaIdOrigen) ===
            String(categoriaId)
        );

        if (!categoriaActualExiste) {
          setCategoriaId(
            String(categorias[0].categoriaIdOrigen)
          );
        }

        setMensajeCliente(
          `HTTP ${response.status} OK — Categorías disponibles actualizadas.`
        );
      } else {
        setCategoriaId("");

        setMensajeCliente(
          "Todavía no hay categorías disponibles en Solicitudes."
        );
      }
    } catch (error) {
      if (error.response) {
        setMensajeCliente(
          `HTTP ${error.response.status} — No se pudieron cargar las categorías disponibles.`
        );
      } else {
        setMensajeCliente(error.message);
      }
    }
  };

  // --------------------------------------------------
  // CLIENTE - CREAR SOLICITUD
  // --------------------------------------------------

  const crearSolicitud = async () => {
    if (
      !tituloSolicitud.trim() ||
      !descripcionSolicitud.trim() ||
      !categoriaId ||
      !prioridadId
    ) {
      setMensajeCliente(
        "Debes completar todos los datos de la solicitud."
      );
      return;
    }

    try {
      setProcesando(true);
      setMensajeCliente("Creando solicitud...");

      const token = await obtenerToken();

      const response = await axios.post(
        `${apiConfig.baseUrl}/v1/solicitudes`,
        {
          titulo: tituloSolicitud,
          descripcion: descripcionSolicitud,
          categoriaId: Number(categoriaId),
          prioridadId: Number(prioridadId)
        },
        {
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json"
          }
        }
      );

      setMensajeCliente(
        `HTTP ${response.status} OK — Solicitud #${response.data.id} creada en estado ${response.data.estado}.`
      );

      setTituloSolicitud("");
      setDescripcionSolicitud("");

      await verMisSolicitudes();
    } catch (error) {
      if (error.response) {
        let detalle = "";

        if (error.response.data?.mensaje) {
          detalle = error.response.data.mensaje;
        } else if (typeof error.response.data === "string") {
          detalle = error.response.data;
        }

        setMensajeCliente(
          `HTTP ${error.response.status} — ${
            detalle || "Error al crear solicitud."
          }`
        );
      } else {
        setMensajeCliente(error.message);
      }
    } finally {
      setProcesando(false);
    }
  };

  // --------------------------------------------------
  // CLIENTE - VER MIS SOLICITUDES
  // --------------------------------------------------

  const verMisSolicitudes = async () => {
    try {
      setProcesando(true);

      const token = await obtenerToken();

      const response = await axios.get(
        `${apiConfig.baseUrl}/v1/solicitudes/mias`,
        {
          headers: {
            Authorization: `Bearer ${token}`
          }
        }
      );

      setMisSolicitudes(response.data || []);

      setMensajeCliente(
        `HTTP ${response.status} OK — Mis solicitudes cargadas.`
      );
    } catch (error) {
      if (error.response) {
        setMensajeCliente(
          `HTTP ${error.response.status} — No se pudieron cargar las solicitudes.`
        );
      } else {
        setMensajeCliente(error.message);
      }
    } finally {
      setProcesando(false);
    }
  };

  // --------------------------------------------------
  // OPERADOR / ADMIN - VER TODAS
  // --------------------------------------------------

  const verTodasSolicitudes = async () => {
    try {
      setProcesando(true);
      setMensajeOperador("Cargando solicitudes...");

      const token = await obtenerToken();

      const response = await axios.get(
        `${apiConfig.baseUrl}/v1/solicitudes`,
        {
          headers: {
            Authorization: `Bearer ${token}`
          }
        }
      );

      setSolicitudes(response.data || []);

      setMensajeOperador(
        `HTTP ${response.status} OK — Solicitudes cargadas.`
      );
    } catch (error) {
      if (error.response) {
        setMensajeOperador(
          `HTTP ${error.response.status} — Acceso rechazado.`
        );
      } else {
        setMensajeOperador(error.message);
      }
    } finally {
      setProcesando(false);
    }
  };

  // --------------------------------------------------
  // OPERADOR / ADMIN - CAMBIAR ESTADO
  // --------------------------------------------------

  const cambiarEstado = async () => {
    if (!solicitudId) {
      setMensajeOperador(
        "Debes indicar el ID de una solicitud."
      );
      return;
    }

    try {
      setProcesando(true);
      setMensajeOperador("Actualizando estado...");

      const token = await obtenerToken();

      const response = await axios.patch(
        `${apiConfig.baseUrl}/v1/solicitudes/${solicitudId}/estado`,
        null,
        {
          params: {
            estado: nuevoEstado
          },
          headers: {
            Authorization: `Bearer ${token}`
          }
        }
      );

      setMensajeOperador(
        `HTTP ${response.status} OK — Solicitud #${solicitudId} actualizada a ${nuevoEstado}.`
      );

      await verTodasSolicitudes();
    } catch (error) {
      if (error.response) {
        let detalle = "";

        if (typeof error.response.data === "string") {
          detalle = error.response.data;
        } else {
          detalle = JSON.stringify(error.response.data);
        }

        setMensajeOperador(
          `HTTP ${error.response.status} — ${detalle}`
        );
      } else {
        setMensajeOperador(error.message);
      }
    } finally {
      setProcesando(false);
    }
  };

  // --------------------------------------------------
  // ADMIN - CREAR CATEGORIA
  // --------------------------------------------------

  const crearCategoria = async () => {
    if (!categoriaNombre.trim()) {
      setMensajeAdmin(
        "Debes escribir un nombre para la categoría."
      );
      return;
    }

    try {
      setProcesando(true);

      const token = await obtenerToken();

      const response = await axios.post(
        `${apiConfig.baseUrl}/v1/catalogo/categorias`,
        {
          nombre: categoriaNombre
        },
        {
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json"
          }
        }
      );

      setMensajeAdmin(
        `HTTP ${response.status} OK — Categoría "${response.data.nombre}" creada.`
      );

      setCategoriaNombre("");

      await actualizarCatalogo();
    } catch (error) {
      if (error.response) {
        let detalle = "";

        if (error.response.data?.mensaje) {
          detalle = error.response.data.mensaje;
        }

        setMensajeAdmin(
          `HTTP ${error.response.status} — ${
            detalle || "No se pudo crear la categoría."
          }`
        );
      } else {
        setMensajeAdmin(error.message);
      }
    } finally {
      setProcesando(false);
    }
  };

  // --------------------------------------------------
  // ADMIN - CREAR PRIORIDAD
  // --------------------------------------------------

  const crearPrioridad = async () => {
    if (!prioridadNombre.trim()) {
      setMensajeAdmin(
        "Debes escribir un nombre para la prioridad."
      );
      return;
    }

    try {
      setProcesando(true);

      const token = await obtenerToken();

      const response = await axios.post(
        `${apiConfig.baseUrl}/v1/catalogo/prioridades`,
        {
          nombre: prioridadNombre
        },
        {
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json"
          }
        }
      );

      setMensajeAdmin(
        `HTTP ${response.status} OK — Prioridad "${response.data.nombre}" creada.`
      );

      setPrioridadNombre("");

      await actualizarCatalogo();
    } catch (error) {
      if (error.response) {
        setMensajeAdmin(
          `HTTP ${error.response.status} — No se pudo crear la prioridad.`
        );
      } else {
        setMensajeAdmin(error.message);
      }
    } finally {
      setProcesando(false);
    }
  };

  // --------------------------------------------------
  // ACTUALIZAR CATALOGO
  // --------------------------------------------------

  const actualizarCatalogo = async () => {
    const token = await obtenerToken();

    const response = await axios.get(
      `${apiConfig.baseUrl}/v1/catalogo`,
      {
        headers: {
          Authorization: `Bearer ${token}`
        }
      }
    );

    setCatalogo(response.data);
  };

  // --------------------------------------------------
  // PROBAR API PROTEGIDA
  // --------------------------------------------------

  const probarApi = async () => {
    try {
      const token = await obtenerToken();

      const response = await axios.get(
        `${apiConfig.baseUrl}/v1/catalogo`,
        {
          headers: {
            Authorization: `Bearer ${token}`
          }
        }
      );

      setMensajePrueba(
        `API protegida → HTTP ${response.status} OK`
      );
    } catch (error) {
      if (error.response) {
        setMensajePrueba(
          `API protegida → HTTP ${error.response.status}`
        );
      } else {
        setMensajePrueba(error.message);
      }
    }
  };

  // --------------------------------------------------
  // VERSIONAMIENTO V1 / V2
  // --------------------------------------------------

  const probarVersiones = async () => {
    try {
      const token = await obtenerToken();

      const config = {
        headers: {
          Authorization: `Bearer ${token}`
        }
      };

      const v1 = await axios.get(
        `${apiConfig.baseUrl}/v1/solicitudes`,
        config
      );

      const v2 = await axios.get(
        `${apiConfig.baseUrl}/v2/solicitudes`,
        config
      );

      setMensajePrueba(
        `V1 → HTTP ${v1.status} OK | V2 → HTTP ${v2.status} OK`
      );
    } catch (error) {
      if (error.response) {
        setMensajePrueba(
          `Versionamiento → HTTP ${error.response.status}`
        );
      } else {
        setMensajePrueba(error.message);
      }
    }
  };

  // --------------------------------------------------
  // INTERFAZ
  // --------------------------------------------------

  return (
    <div className="app">

      <UnauthenticatedTemplate>

        <div className="login-container">

          <div className="login-card">

            <h1>MesaTech Cloud</h1>

            <p>
              Plataforma de gestión de solicitudes de soporte.
            </p>

            <button
              className="btn btn-primary"
              onClick={iniciarSesion}
            >
              Iniciar sesión con Microsoft
            </button>

          </div>

        </div>

      </UnauthenticatedTemplate>


      <AuthenticatedTemplate>

        <header className="topbar">

          <div>

            <h1>MesaTech Cloud</h1>

            <p className="subtitle">
              Gestión de solicitudes y soporte
            </p>

          </div>

          <div className="session">

            <div>
              <strong>{nombre}</strong>
              <br />
              <span>{usuario}</span>
            </div>

            {roles.map((rol) => (
              <span
                className="role-badge"
                key={rol}
              >
                {rol}
              </span>
            ))}

            <button
              className="btn btn-secondary"
              onClick={cerrarSesion}
            >
              Cerrar sesión
            </button>

          </div>

        </header>


        <main className="dashboard">

          {/* ---------------- CLIENTE ---------------- */}

          {esCliente && (

            <section className="card">

              <h2>Mis solicitudes</h2>

              <p className="section-description">
                Crea solicitudes de soporte y consulta las que
                ya has registrado.
              </p>

              <div className="form-grid">

                <div className="field">

                  <label>Título</label>

                  <input
                    value={tituloSolicitud}
                    onChange={(e) =>
                      setTituloSolicitud(e.target.value)
                    }
                    placeholder="Ej: Problema con computador"
                  />

                </div>


                <div className="field field-full">

                  <label>Descripción</label>

                  <textarea
                    value={descripcionSolicitud}
                    onChange={(e) =>
                      setDescripcionSolicitud(e.target.value)
                    }
                    placeholder="Describe el problema"
                  />

                </div>


                <div className="field">

                  <label>Categoría</label>

                  <select
                    value={categoriaId}
                    onChange={(e) =>
                      setCategoriaId(e.target.value)
                    }
                  >

                    {categoriasDisponibles.length === 0 ? (

                      <option value="">
                        Sin categorías disponibles todavía
                      </option>

                    ) : (

                      categoriasDisponibles.map(
                        (categoria) => (

                          <option
                            key={categoria.id}
                            value={categoria.categoriaIdOrigen}
                          >
                            {categoria.nombre}
                          </option>

                        )
                      )

                    )}

                  </select>

                </div>


                <div className="field">

                  <label>Prioridad</label>

                  <select
                    value={prioridadId}
                    onChange={(e) =>
                      setPrioridadId(e.target.value)
                    }
                  >

                    {catalogo.prioridades?.map(
                      (prioridad) => (

                        <option
                          key={prioridad.id}
                          value={prioridad.id}
                        >
                          {prioridad.nombre}
                        </option>

                      )
                    )}

                  </select>

                </div>

              </div>


              <div className="actions">

                <button
                  className="btn btn-primary"
                  onClick={crearSolicitud}
                  disabled={
                    procesando ||
                    categoriasDisponibles.length === 0
                  }
                >
                  {procesando
                    ? "Procesando..."
                    : "Crear solicitud"}
                </button>

                <button
                  className="btn btn-secondary"
                  onClick={actualizarCategoriasDisponibles}
                  disabled={procesando}
                >
                  Actualizar categorías
                </button>

                <button
                  className="btn btn-secondary"
                  onClick={verMisSolicitudes}
                  disabled={procesando}
                >
                  Ver mis solicitudes
                </button>

              </div>


              {mensajeCliente && (

                <div className="message">
                  {mensajeCliente}
                </div>

              )}


              {misSolicitudes.length > 0 && (

                <div className="table-wrapper">

                  <table>

                    <thead>

                      <tr>
                        <th>ID</th>
                        <th>Título</th>
                        <th>Estado</th>
                        <th>Fecha</th>
                      </tr>

                    </thead>

                    <tbody>

                      {misSolicitudes.map(
                        (solicitud) => (

                          <tr key={solicitud.id}>

                            <td>
                              {solicitud.id}
                            </td>

                            <td>
                              {solicitud.titulo}
                            </td>

                            <td>

                              <span className="status-badge">
                                {solicitud.estado}
                              </span>

                            </td>

                            <td>

                              {solicitud.fechaCreacion
                                ? new Date(
                                    solicitud.fechaCreacion
                                  ).toLocaleString()
                                : "-"}

                            </td>

                          </tr>

                        )
                      )}

                    </tbody>

                  </table>

                </div>

              )}

            </section>

          )}


          {/* ------------- OPERADOR / ADMIN ------------- */}

          {(esOperador || esAdmin) && (

            <section className="card">

              <h2>Gestión de solicitudes</h2>

              <p className="section-description">
                Consulta todas las solicitudes y administra su
                estado.
              </p>


              <div className="actions">

                <button
                  className="btn btn-primary"
                  onClick={verTodasSolicitudes}
                  disabled={procesando}
                >
                  Ver todas las solicitudes
                </button>

              </div>


              <div className="state-form">

                <div className="field">

                  <label>ID solicitud</label>

                  <input
                    type="number"
                    value={solicitudId}
                    onChange={(e) =>
                      setSolicitudId(e.target.value)
                    }
                    placeholder="Ej: 2"
                  />

                </div>


                <div className="field">

                  <label>Nuevo estado</label>

                  <select
                    value={nuevoEstado}
                    onChange={(e) =>
                      setNuevoEstado(e.target.value)
                    }
                  >

                    <option value="ASIGNADA">
                      ASIGNADA
                    </option>

                    <option value="EN_PROCESO">
                      EN_PROCESO
                    </option>

                    <option value="RESUELTA">
                      RESUELTA
                    </option>

                    <option value="CERRADA">
                      CERRADA
                    </option>

                    <option value="CANCELADA">
                      CANCELADA
                    </option>

                  </select>

                </div>


                <button
                  className="btn btn-primary"
                  onClick={cambiarEstado}
                  disabled={procesando}
                >
                  Cambiar estado
                </button>

              </div>


              {mensajeOperador && (

                <div className="message">
                  {mensajeOperador}
                </div>

              )}


              {solicitudes.length > 0 && (

                <div className="table-wrapper">

                  <table>

                    <thead>

                      <tr>
                        <th>ID</th>
                        <th>Título</th>
                        <th>Usuario</th>
                        <th>Estado</th>
                      </tr>

                    </thead>

                    <tbody>

                      {solicitudes.map(
                        (solicitud) => (

                          <tr key={solicitud.id}>

                            <td>
                              {solicitud.id}
                            </td>

                            <td>
                              {solicitud.titulo}
                            </td>

                            <td>
                              {solicitud.usuarioSolicitante}
                            </td>

                            <td>

                              <span className="status-badge">
                                {solicitud.estado}
                              </span>

                            </td>

                          </tr>

                        )
                      )}

                    </tbody>

                  </table>

                </div>

              )}

            </section>

          )}


          {/* ---------------- ADMIN ---------------- */}

          {esAdmin && (

            <section className="card">

              <h2>
                Administración del catálogo
              </h2>

              <p className="section-description">
                Gestiona categorías y prioridades disponibles
                para las solicitudes.
              </p>


              <div className="admin-grid">

                <div className="catalog-box">

                  <h3>
                    Nueva categoría
                  </h3>

                  <input
                    value={categoriaNombre}
                    onChange={(e) =>
                      setCategoriaNombre(e.target.value)
                    }
                    placeholder="Nombre de categoría"
                  />

                  <button
                    className="btn btn-primary"
                    onClick={crearCategoria}
                    disabled={procesando}
                  >
                    Crear categoría
                  </button>

                </div>


                <div className="catalog-box">

                  <h3>
                    Nueva prioridad
                  </h3>

                  <input
                    value={prioridadNombre}
                    onChange={(e) =>
                      setPrioridadNombre(e.target.value)
                    }
                    placeholder="Nombre de prioridad"
                  />

                  <button
                    className="btn btn-primary"
                    onClick={crearPrioridad}
                    disabled={procesando}
                  >
                    Crear prioridad
                  </button>

                </div>

              </div>


              {mensajeAdmin && (

                <div className="message">
                  {mensajeAdmin}
                </div>

              )}


              <div className="catalog-list">

                <div>

                  <h3>
                    Categorías
                  </h3>

                  {catalogo.categorias?.map(
                    (categoria) => (

                      <div
                        className="catalog-item"
                        key={categoria.id}
                      >
                        #{categoria.id} — {categoria.nombre}
                      </div>

                    )
                  )}

                </div>


                <div>

                  <h3>
                    Prioridades
                  </h3>

                  {catalogo.prioridades?.map(
                    (prioridad) => (

                      <div
                        className="catalog-item"
                        key={prioridad.id}
                      >
                        #{prioridad.id} — {prioridad.nombre}
                      </div>

                    )
                  )}

                </div>

              </div>

            </section>

          )}


          {/* ---------------- PRUEBAS ---------------- */}

          <section className="card">

            <h2>
              Estado técnico
            </h2>

            <p className="section-description">
              Validaciones de integración con la API protegida.
            </p>


            <div className="actions">

              <button
                className="btn btn-secondary"
                onClick={probarApi}
              >
                Probar API protegida
              </button>


              {(esOperador || esAdmin) && (

                <button
                  className="btn btn-secondary"
                  onClick={probarVersiones}
                >
                  Probar API v1 y v2
                </button>

              )}

            </div>


            {mensajePrueba && (

              <div className="message">
                {mensajePrueba}
              </div>

            )}

          </section>

        </main>

      </AuthenticatedTemplate>

    </div>
  );
}

export default App;