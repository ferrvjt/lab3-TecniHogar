package model;

public class InstElectrica extends Instalacion {
    public static final double CARGO_CERTIFICADO = 120;
    public static final double PORC_MATERIAL = 0.05;

    private final double valorMateriales;

    public InstElectrica(String codigo, String cliente, String direccion,
                         boolean metropolitana, double horasEstimadas,
                         int cantPuntos, double valorMateriales) {
        super(codigo, cliente, direccion, metropolitana, horasEstimadas, cantPuntos);
        if (valorMateriales < 0) throw new IllegalArgumentException("El valor de materiales no puede ser negativo.");
        this.valorMateriales = valorMateriales;
    }

    /** Certificado (Q120) + 5 % del valor de los materiales. */
    public double calcularGarantiaCertificado(double materiales) {
        return CARGO_CERTIFICADO + PORC_MATERIAL * materiales;
    }

    @Override public double calcularCosto() { return super.calcularCosto() + calcularGarantiaCertificado(valorMateriales); }
    @Override public int calcularTiempoAtencion() { return super.calcularTiempoAtencion() + 2; }
    @Override public int getMaxVisitas() { return 2; }
    @Override public Servicio getCategoria() { return Servicio.INST_ELECTRICA; }

    public double getValorMateriales() { return valorMateriales; }
}