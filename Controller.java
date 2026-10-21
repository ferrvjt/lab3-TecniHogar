import java.util.ArrayList;
import java.util.List;
import model.OrdenServicio;
import model.Jornada;
import model.Cuadrilla;
import model.Diagnostico;
import model.Instalacion;
import model.InstElectrica;
import model.MantAcc;
import model.EstadoJornada;
import model.Estado;

/** Parte administradora: colecciones, códigos únicos, ingresos y reportes. */
public class Controller {
    private final List<OrdenServicio> ordenes = new ArrayList<>();
    private final List<Cuadrilla> cuadrillas = new ArrayList<>();
    private final List<Jornada> jornadas = new ArrayList<>();
    private double ingresos = 0;
    private int contadorJornadas = 0;

    public void cargarDatosPrueba() {   // no generan ingresos
        cuadrillas.add(new Cuadrilla("C-01", 8, true));
        cuadrillas.add(new Cuadrilla("C-02", 6, false));
        ordenes.add(new Diagnostico("D-01", "Ana López", "Zona 10", true, 1));
        ordenes.add(new Instalacion("I-01", "Luis Pérez", "Mixco", false, 3, 4));
        ordenes.add(new InstElectrica("E-01", "Marta Díaz", "Zona 4", true, 4, 6, 800));
        ordenes.add(new MantAcc("M-01", "Pedro Ruiz", "Zona 15", true, 2, 18000));
    }

    // ---- consultas ----
    public OrdenServicio buscarOrden(String codigo) {
        for (OrdenServicio o : ordenes) if (o.getCodigo().equalsIgnoreCase(codigo)) return o;
        return null;
    }
    public Cuadrilla buscarCuadrilla(String codigo) {
        for (Cuadrilla c : cuadrillas) if (c.getCodigo().equalsIgnoreCase(codigo)) return c;
        return null;
    }
    public Jornada buscarJornada(String codigo) {
        for (Jornada j : jornadas) if (j.getCodigo().equalsIgnoreCase(codigo)) return j;
        return null;
    }
    private OrdenServicio ordenObligatoria(String cod) {
        OrdenServicio o = buscarOrden(cod);
        if (o == null) throw new IllegalArgumentException("No existe la orden " + cod + ".");
        return o;
    }
    private Jornada jornadaObligatoria(String cod) {
        Jornada j = buscarJornada(cod);
        if (j == null) throw new IllegalArgumentException("No existe la jornada " + cod + ".");
        return j;
    }

    // ---- operaciones ----
    /** Registrar = el cliente aceptó la cotización: se cobra el costo cotizado. */
    public void registrarOrden(OrdenServicio orden) {
        if (buscarOrden(orden.getCodigo()) != null)
            throw new IllegalArgumentException("Ya existe una orden con el código " + orden.getCodigo() + ".");
        ordenes.add(orden);
        ingresos += orden.calcularCosto();
    }

    public void registrarCuadrilla(Cuadrilla c) {
        if (buscarCuadrilla(c.getCodigo()) != null)
            throw new IllegalArgumentException("Ya existe una cuadrilla con el código " + c.getCodigo() + ".");
        cuadrillas.add(c);
    }

    public Jornada crearJornada(String codCuadrilla) {
        Cuadrilla c = buscarCuadrilla(codCuadrilla);
        if (c == null) throw new IllegalArgumentException("No existe la cuadrilla " + codCuadrilla + ".");
        for (Jornada j : jornadas)
            if (j.getCuadrilla() == c && j.getEstado() != EstadoJornada.CERRADA)
                throw new IllegalStateException("La cuadrilla ya tiene una jornada sin cerrar (" + j.getCodigo() + ").");
        Jornada nueva = new Jornada("J-" + (++contadorJornadas), c);
        jornadas.add(nueva);
        return nueva;
    }

    public void asignarOrden(String codJornada, String codOrden) {
        jornadaObligatoria(codJornada).agregarOrden(ordenObligatoria(codOrden));
    }
    public void retirarOrden(String codJornada, String codOrden) {
        jornadaObligatoria(codJornada).retirarOrden(ordenObligatoria(codOrden));
    }
    public double iniciarJornada(String codJornada) {
        double cargo = jornadaObligatoria(codJornada).iniciar();
        ingresos += cargo;
        return cargo;
    }
    public void registrarResultado(String codJornada, String codOrden, boolean exitoso) {
        jornadaObligatoria(codJornada).registrarResultado(ordenObligatoria(codOrden), exitoso);
    }

    // ---- reportes ----
    public String reporteOrdenes() {
        if (ordenes.isEmpty()) return "(sin órdenes)";
        StringBuilder sb = new StringBuilder();
        for (OrdenServicio o : ordenes) sb.append(o).append('\n');
        return sb.toString();
    }
    public String reporteCuadrillas() {
        if (cuadrillas.isEmpty()) return "(sin cuadrillas)";
        StringBuilder sb = new StringBuilder();
        for (Cuadrilla c : cuadrillas) sb.append(c).append('\n');
        return sb.toString();
    }
    public String reporteJornadas() {
        if (jornadas.isEmpty()) return "(sin jornadas)";
        StringBuilder sb = new StringBuilder();
        for (Jornada j : jornadas) {
            sb.append(j).append('\n');
            for (OrdenServicio o : j.getOrdenes())
                sb.append("    - ").append(o.getCodigo()).append(" ").append(o.getEstado()).append('\n');
        }
        return sb.toString();
    }
    public String reporteResumen() {
        StringBuilder sb = new StringBuilder();
        for (Estado e : Estado.values()) {
            int n = 0;
            for (OrdenServicio o : ordenes) if (o.getEstado() == e) n++;
            sb.append(String.format("Órdenes %-12s: %d%n", e, n));
        }
        sb.append(String.format("Ingresos totales: Q%.2f", ingresos));
        return sb.toString();
    }
    public double getIngresos() { return ingresos; }
}