package model;
import java.util.ArrayList;
import java.util.List;

public class Jornada {
    public static final double COSTO_VISITA_ADICIONAL = 40;

    private final String codigo;
    private final Cuadrilla cuadrilla;
    private final List<OrdenServicio> ordenes = new ArrayList<>();
    private EstadoJornada estado = EstadoJornada.EN_PREPARACION;

    public Jornada(String codigo, Cuadrilla cuadrilla) {
        if (cuadrilla == null) throw new IllegalArgumentException("La jornada necesita una cuadrilla.");
        this.codigo = codigo;
        this.cuadrilla = cuadrilla;
    }

    public String getCodigo() { return codigo; }
    public Cuadrilla getCuadrilla() { return cuadrilla; }
    public EstadoJornada getEstado() { return estado; }
    public List<OrdenServicio> getOrdenes() { return new ArrayList<>(ordenes); }

    public double horasAsignadas() {
        double total = 0;
        for (OrdenServicio o : ordenes) total += o.getHorasEstimadas();
        return total;
    }

    /** Valida todo antes de modificar: si falla, no cambia nada. */
    public void agregarOrden(OrdenServicio orden) {
        if (estado != EstadoJornada.EN_PREPARACION)
            throw new IllegalStateException("Solo se pueden agregar órdenes a una jornada en preparación.");
        if (!orden.estaDisponible())
            throw new IllegalStateException("La orden " + orden.getCodigo() + " no está disponible (ya asignada o no pendiente).");
        if (!orden.esCompatible(cuadrilla))
            throw new IllegalStateException("La cuadrilla " + cuadrilla.getCodigo() + " no tiene la certificación requerida.");
        if (horasAsignadas() + orden.getHorasEstimadas() > cuadrilla.getCapacidadMaxima())
            throw new IllegalStateException("Se supera la capacidad de la cuadrilla (" + cuadrilla.getCapacidadMaxima() + " h).");
        ordenes.add(orden);
        orden.asignarA(this);
    }

    public void retirarOrden(OrdenServicio orden) {
        if (estado != EstadoJornada.EN_PREPARACION)
            throw new IllegalStateException("Solo se pueden retirar órdenes de una jornada en preparación.");
        if (!ordenes.contains(orden))
            throw new IllegalStateException("La orden no pertenece a esta jornada.");
        ordenes.remove(orden);
        orden.desasignar();
    }

    /** Inicia el trabajo y devuelve lo que se cobra por visitas adicionales. */
    public double iniciar() {
        if (estado != EstadoJornada.EN_PREPARACION)
            throw new IllegalStateException("La jornada ya fue iniciada o cerrada.");
        if (ordenes.isEmpty())
            throw new IllegalStateException("La jornada necesita al menos una orden para iniciar.");
        double cargo = 0;
        for (OrdenServicio o : ordenes) {
            o.iniciarVisita();
            if (o.getCantVisitas() > 1) cargo += COSTO_VISITA_ADICIONAL;
        }
        estado = EstadoJornada.INICIADA;
        return cargo;
    }

    public void registrarResultado(OrdenServicio orden, boolean exitoso) {
        if (estado != EstadoJornada.INICIADA)
            throw new IllegalStateException("La jornada no está iniciada.");
        if (!ordenes.contains(orden) || orden.getEstado() != Estado.EN_ATENCION)
            throw new IllegalStateException("La orden no está pendiente de resultado en esta jornada.");
        orden.registrarResultado(exitoso);
        boolean todas = true;
        for (OrdenServicio o : ordenes) if (o.getEstado() == Estado.EN_ATENCION) todas = false;
        if (todas) estado = EstadoJornada.CERRADA;
    }

    @Override
    public String toString() {
        return String.format("[%s] cuadrilla %s | %s | %d orden(es) | %.1f/%d h",
                codigo, cuadrilla.getCodigo(), estado, ordenes.size(), horasAsignadas(), cuadrilla.getCapacidadMaxima());
    }
}