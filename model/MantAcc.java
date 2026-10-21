package model;

public class MantAcc extends OrdenServicio {
    public static final double TARIFA_HORA = 110;
    public static final int BTU_LIMITE = 12000;
    public static final double REFRIGERANTE_BAJO = 150;
    public static final double REFRIGERANTE_ALTO = 250;

    private final int capacidadBTU;

    public MantAcc(String codigo, String cliente, String direccion,
                   boolean metropolitana, double horasEstimadas, int capacidadBTU) {
        super(codigo, cliente, direccion, metropolitana, horasEstimadas);
        if (!validarMetropolitana(metropolitana))
            throw new IllegalArgumentException("El mantenimiento de A/C solo se presta en zona metropolitana.");
        if (!validarCapacidad(capacidadBTU))
            throw new IllegalArgumentException("La capacidad en BTU debe ser mayor que 0.");
        this.capacidadBTU = capacidadBTU;
    }

    public boolean validarCapacidad(int btu) { return btu > 0; }
    public boolean validarMetropolitana(boolean metropolitana) { return metropolitana; }

    @Override
    public double calcularCosto() {
        double refrigerante = capacidadBTU <= BTU_LIMITE ? REFRIGERANTE_BAJO : REFRIGERANTE_ALTO;
        return TARIFA_HORA * Math.ceil(getHorasEstimadas()) + refrigerante;
    }

    @Override public int calcularTiempoAtencion() { return 2; }
    @Override public int getMaxVisitas() { return 1; }
    @Override public Servicio getCategoria() { return Servicio.MANT_ACC; }
    @Override public boolean requiereRefrigeracion() { return true; }

    public int getCapacidadBTU() { return capacidadBTU; }
}