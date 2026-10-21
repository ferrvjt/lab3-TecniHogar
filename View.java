import java.util.Scanner;
import model.OrdenServicio;
import model.Diagnostico;
import model.MantAcc;
import model.Instalacion;
import model.InstElectrica;
import model.Cuadrilla;


public class View {
    private final Controller ctrl;
    private final Scanner in = new Scanner(System.in);

    public View(Controller ctrl) { this.ctrl = ctrl; }

    public void menu() {
        int op;
        do {
            System.out.println("\n=== TecniHogar ===");
            System.out.println("1. Cotizar (no registra)");
            System.out.println("2. Registrar orden");
            System.out.println("3. Registrar cuadrilla");
            System.out.println("4. Asignar jornadas");
            System.out.println("5. Iniciar jornada");
            System.out.println("6. Registrar resultado de visita");
            System.out.println("7. Reportes");
            System.out.println("0. Salir");
            op = leerInt("Opción: ");
            try {
                switch (op) {
                    case 1 -> cotizar();
                    case 2 -> registrarOrden();
                    case 3 -> registrarCuadrilla();
                    case 4 -> asignarJornadas();
                    case 5 -> iniciarJornadas();
                    case 6 -> registrarResultado();
                    case 7 -> generarReportes();
                    case 0 -> System.out.println("Hasta luego.");
                    default -> System.out.println("Opción inválida.");
                }
            } catch (RuntimeException e) {
                System.out.println("Operación rechazada: " + e.getMessage());
            }
        } while (op != 0);
    }

    private void cotizar() {
        OrdenServicio o = leerOrden("COTIZACION");
        System.out.printf("Costo: Q%.2f | Tiempo de atención: %d día(s) | Máx. visitas: %d%n",
                o.calcularCosto(), o.calcularTiempoAtencion(), o.getMaxVisitas());
    }

    private void registrarOrden() {
        String codigo = leerTexto("Código de la orden: ");
        OrdenServicio o = leerOrden(codigo);
        System.out.printf("Costo cotizado: Q%.2f, %d día(s).%n", o.calcularCosto(), o.calcularTiempoAtencion());
        if (leerTexto("¿El cliente acepta? (s/n): ").equalsIgnoreCase("s")) {
            ctrl.registrarOrden(o);
            System.out.println("Orden registrada.");
        } else System.out.println("Cotización rechazada; no se registró nada.");
    }

    private OrdenServicio leerOrden(String codigo) {
        System.out.println("Servicio: 1) Diagnóstico  2) Instalación  3) Inst. eléctrica  4) Mant. A/C");
        int tipo = leerInt("Tipo: ");
        String cliente = leerTexto("Cliente: ");
        String dir = leerTexto("Dirección: ");
        boolean metro = leerTexto("Zona (m = metropolitana / p = periférica): ").equalsIgnoreCase("m");
        double horas = leerDouble("Horas estimadas: ");
        return switch (tipo) {
            case 1 -> new Diagnostico(codigo, cliente, dir, metro, horas);
            case 2 -> new Instalacion(codigo, cliente, dir, metro, horas, leerInt("Puntos de instalación: "));
            case 3 -> new InstElectrica(codigo, cliente, dir, metro, horas,
                    leerInt("Puntos de instalación: "), leerDouble("Valor de materiales (Q): "));
            case 4 -> new MantAcc(codigo, cliente, dir, metro, horas, leerInt("Capacidad en BTU: "));
            default -> throw new IllegalArgumentException("Tipo de servicio inválido.");
        };
    }

    private void registrarCuadrilla() {
        String cod = leerTexto("Código de la cuadrilla: ");
        int cap = leerInt("Capacidad máxima (horas): ");
        boolean cert = leerTexto("¿Certificada en refrigeración? (s/n): ").equalsIgnoreCase("s");
        ctrl.registrarCuadrilla(new Cuadrilla(cod, cap, cert));
        System.out.println("Cuadrilla registrada.");
    }

    private void asignarJornadas() {
        System.out.println("1) Crear jornada  2) Agregar orden  3) Retirar orden");
        switch (leerInt("Opción: ")) {
            case 1 -> System.out.println("Jornada creada: " + ctrl.crearJornada(leerTexto("Cuadrilla: ")).getCodigo());
            case 2 -> { ctrl.asignarOrden(leerTexto("Jornada: "), leerTexto("Orden: ")); System.out.println("Orden asignada."); }
            case 3 -> { ctrl.retirarOrden(leerTexto("Jornada: "), leerTexto("Orden: ")); System.out.println("Orden retirada."); }
            default -> System.out.println("Opción inválida.");
        }
    }

    private void iniciarJornadas() {
        double cargo = ctrl.iniciarJornada(leerTexto("Jornada: "));
        System.out.printf("Jornada iniciada. Cargo por visitas adicionales: Q%.2f%n", cargo);
    }

    private void registrarResultado() {
        String j = leerTexto("Jornada: ");
        String o = leerTexto("Orden: ");
        boolean ok = leerTexto("¿Visita exitosa? (s/n): ").equalsIgnoreCase("s");
        ctrl.registrarResultado(j, o, ok);
        System.out.println("Resultado registrado.");
    }

    private void generarReportes() {
        System.out.println("\n-- Órdenes --\n" + ctrl.reporteOrdenes());
        System.out.println("-- Cuadrillas --\n" + ctrl.reporteCuadrillas());
        System.out.println("-- Jornadas --\n" + ctrl.reporteJornadas());
        System.out.println("-- Resumen --\n" + ctrl.reporteResumen());
    }

    // ---- lectura segura ----
    private String leerTexto(String msg) { System.out.print(msg); return in.nextLine().trim(); }
    private int leerInt(String msg) {
        System.out.print(msg);
        try { return Integer.parseInt(in.nextLine().trim()); }
        catch (NumberFormatException e) { throw new IllegalArgumentException("Se esperaba un número entero."); }
    }
    private double leerDouble(String msg) {
        System.out.print(msg);
        try { return Double.parseDouble(in.nextLine().trim()); }
        catch (NumberFormatException e) { throw new IllegalArgumentException("Se esperaba un número."); }
    }
}