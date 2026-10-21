package model;

public class Instalacion extends OrdenServicio {
    public static final double TARIFA_BASE = 300;
    public static final double TARIFA_HORA = 90;

    private final int cantPuntos;

    public Instalacion(String codigo, String cliente, String direccion,
                       boolean metropolitana, double horasEstimadas, int cantPuntos) {
        super(codigo, cliente, direccion, metropolitana, horasEstimadas);
        if (!validarPuntos(cantPuntos))
            throw new IllegalArgumentException("La cantidad de puntos debe ser mayor que 0.");
        this.cantPuntos = cantPuntos;
    }

    public boolean validarPuntos(int puntos) { return puntos > 0; }

    /** Horas facturables: lo mayor entre las horas estimadas y media hora por punto, redondeado hacia arriba. */
    public int calcularHorasFacturables(int puntos, double horasEstimadas) {
        return (int) Math.ceil(Math.max(horasEstimadas, puntos * 0.5));
    }

    @Override
    public double calcularCosto() {
        return TARIFA_BASE + TARIFA_HORA * calcularHorasFacturables(cantPuntos, getHorasEstimadas());
    }

    @Override public int calcularTiempoAtencion() { return esMetropolitana() ? 3 : 5; }
    @Override public int getMaxVisitas() { return 3; }
    @Override public Servicio getCategoria() { return Servicio.INSTALACION; }

    public int getCantPuntos() { return cantPuntos; }
}