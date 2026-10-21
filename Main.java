
public class Main {
    public static void main(String[] args) {
        Controller ctrl = new Controller();
        ctrl.cargarDatosPrueba();
        new View(ctrl).menu();
    }
}