package model;

public abstract class OrdenServicio {
    private final String codigo;
    private final String cliente;
    private final String direccion;
    private final boolean metropolitana;
    private final double horasEstimadas;
    private Estado estado;
    private int cantVisitas;
    private Jornada jornada;

    protected OrdenServicio(String codigo, String cliente, String direccion,
                            boolean metropolitana, double horasEstimadas) {
        if (codigo == null || codigo.isBlank()) throw new IllegalArgumentException("El código no puede estar vacío.");
        if (cliente == null || cliente.isBlank()) throw new IllegalArgumentException("El cliente no puede estar vacío.");
        if (direccion == null || direccion.isBlank()) throw new IllegalArgumentException("La dirección no puede estar vacía.");
        if (horasEstimadas <= 0) throw new IllegalArgumentException("Las horas estimadas deben ser mayores que 0.");
        this.codigo = codigo.trim();
        this.cliente = cliente.trim();
        this.direccion = direccion.trim();
        this.metropolitana = metropolitana;
        this.horasEstimadas = horasEstimadas;
        this.estado = Estado.PENDIENTE;
        this.cantVisitas = 0;
    }

    // ---- métodos polimórficos ----
    public abstract double calcularCosto();
    public abstract int calcularTiempoAtencion();   // en días
    public abstract int getMaxVisitas();
    public abstract Servicio getCategoria();

    public boolean requiereRefrigeracion() { return false; }
    public boolean esCompatible(Cuadrilla c) { return !requiereRefrigeracion() || c.isCertificada(); }

    // ---- getters ----
    public String getCodigo() { return codigo; }
    public String getCliente() { return cliente; }
    public String getDireccion() { return direccion; }
    public boolean esMetropolitana() { return metropolitana; }
    public double getHorasEstimadas() { return horasEstimadas; }
    public Estado getEstado() { return estado; }
    public int getCantVisitas() { return cantVisitas; }
    public Jornada getJornada() { return jornada; }

    public boolean estaDisponible() { return estado == Estado.PENDIENTE && jornada == null; }

    // ---- transiciones (solo las usa Jornada) ----
    void asignarA(Jornada j) { this.jornada = j; }
    void desasignar() { this.jornada = null; }

    void iniciarVisita() {
        estado = Estado.EN_ATENCION;
        cantVisitas++;
    }

    void registrarResultado(boolean exitoso) {
        if (exitoso) estado = Estado.COMPLETADA;
        else estado = (cantVisitas < getMaxVisitas()) ? Estado.PENDIENTE : Estado.CANCELADA;
        jornada = null;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s | %s | %s | %.1f h | %s | visitas %d/%d | Q%.2f | %d día(s)",
                codigo, getCategoria(), cliente, metropolitana ? "Metropolitana" : "Periférica",
                horasEstimadas, estado, cantVisitas, getMaxVisitas(), calcularCosto(), calcularTiempoAtencion());
    }
}