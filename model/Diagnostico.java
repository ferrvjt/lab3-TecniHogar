package model;

public class Diagnostico extends OrdenServicio {
    public static final double LIMITE_HORAS = 1.5;

    public Diagnostico(String codigo, String cliente, String direccion,
                       boolean metropolitana, double horasEstimadas) {
        super(codigo, cliente, direccion, metropolitana, horasEstimadas);
        if (!validarHoras(horasEstimadas))
            throw new IllegalArgumentException("Un diagnóstico no puede superar " + LIMITE_HORAS + " horas.");
    }

    public boolean validarHoras(double horas) { return horas > 0 && horas <= LIMITE_HORAS; }

    @Override public double calcularCosto() { return esMetropolitana() ? 150 : 225; }
    @Override public int calcularTiempoAtencion() { return esMetropolitana() ? 1 : 2; }
    @Override public int getMaxVisitas() { return 3; }
    @Override public Servicio getCategoria() { return Servicio.DIAGNOSTICO; }
}