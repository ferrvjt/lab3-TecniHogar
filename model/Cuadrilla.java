package model;

public class Cuadrilla {
    private final String codigo;
    private final int capacidadMaxima;
    private final boolean certificada;

    public Cuadrilla(String codigo, int capacidadMaxima, boolean certificada) {
        if (codigo == null || codigo.isBlank()) throw new IllegalArgumentException("El código de la cuadrilla no puede estar vacío.");
        if (capacidadMaxima <= 0) throw new IllegalArgumentException("La capacidad máxima debe ser mayor que 0.");
        this.codigo = codigo.trim();
        this.capacidadMaxima = capacidadMaxima;
        this.certificada = certificada;
    }

    public String getCodigo() { return codigo; }
    public int getCapacidadMaxima() { return capacidadMaxima; }
    public boolean isCertificada() { return certificada; }

    @Override
    public String toString() {
        return String.format("[%s] capacidad %d h | certificada refrigeración: %s",
                codigo, capacidadMaxima, certificada ? "sí" : "no");
    }
}