/*
 * DesktopApplication1.java
 */

package mptoosm;

import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

/**
 * The main class of the application.
 */
public class DesktopApplication1 extends SingleFrameApplication {

    /**
     * At startup create and show the main frame of the application.
     */
    @Override
    protected void startup() {
        show(new DesktopApplication1View(this));
    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override
    protected void configureWindow(java.awt.Window root) {
    }

    /**
     * A convenient static getter for the application instance.
     * 
     * @return the instance of DesktopApplication1
     */
    public static DesktopApplication1 getApplication() {
        return Application.getInstance(DesktopApplication1.class);
    }

    /**
     * Main method launching the application.
     */
    public static void main(String[] args) {
        if (args.length > 0) {
            String folderPath = args[0];
            processFolder(folderPath);
            System.exit(0);
        } else {
            launch(DesktopApplication1.class, args);
        }
    }

    private static void processFolder(String folderPath) {
        java.io.File folder = new java.io.File(folderPath);
        if (!folder.isDirectory()) {
            System.err.println("The provided path is not a directory: " + folderPath);
            return;
        }

        java.io.File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".mp"));
        if (files == null || files.length == 0) {
            System.out.println("No .mp files found in directory: " + folderPath);
            return;
        }

        System.out.println("*************** Processando mapas na pasta ***************");
        System.out.println("Encontrados " + files.length + " arquivos .mp. Iniciando conversão...\n");

        long tempoTotal = 0;

        for (int i = 0; i < files.length; i++) {
            java.io.File file = files[i];
            mptoosm.mp.LerMP lerMP = new mptoosm.mp.LerMP();
            lerMP.setArquivo(file);
            lerMP.maisDeUmMapaConvertido = i;
            if (i == files.length - 1) {
                lerMP.ultimoMapaParaProcessa = true;
            }

            // Usando valores padrão como na View
            lerMP.isRunMaptool = false;
            mptoosm.mp.LerMP.isCriarAlertas = false;

            long start = System.currentTimeMillis();
            lerMP.run();
            long elapsed = System.currentTimeMillis() - start;
            tempoTotal += elapsed;

            System.out.printf("Arquivo %s processado (%.2fs) - Linhas: %d\n", file.getName(), (elapsed / 1000.0),
                    lerMP.linhasTotal);
        }

        System.out.printf("\nTodos os %d arquivos foram processados no tempo total de %.2fs.\n", files.length,
                (tempoTotal / 1000.0));
    }
}
