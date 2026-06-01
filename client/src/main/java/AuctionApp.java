import StageManager.SceneManager;
import StageManager.SceneName;
import StageManager.SwitchSceneController;
import javafx.application.Application;
import javafx.application.Platform;

import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.URISyntaxException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


public class AuctionApp extends Application {
    private static final int SERVER_PORT = 8080;
    private static final String SERVER_JAR_ENTRY = "BOOT-INF/lib/server-1.0-SNAPSHOT.jar";
    private ConfigurableApplicationContext serverContext;
    private Process serverProcess;

    @Override
    public void init() {
        if (isServerRunning()) {
            return;
        }

        startEmbeddedServer();
    }

    @Override
    public void start(Stage mainstage) throws Exception {
        SceneManager.setStage(mainstage);
        Scene scene = SwitchSceneController.loadScene(SceneName.LOGIN);
        mainstage.setScene(scene);
        mainstage.setOnCloseRequest(event -> Platform.exit());
        mainstage.show();
    }

    @Override
    public void stop() {
        if (serverContext != null) {
            serverContext.close();
            serverContext = null;
        }

        if (serverProcess != null) {
            serverProcess.destroy();
            if (serverProcess.isAlive()) {
                serverProcess.destroyForcibly();
            }
            serverProcess = null;
        }
    }

    public static void main(String[] args) {
        launch();
    }

    private boolean isServerRunning() {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress("localhost", SERVER_PORT), 300);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void startEmbeddedServer() {
        try {
            Path serverJar = locateNestedServerJar();
            if (serverJar != null) {
                startServerProcess(serverJar);
                waitForServerStartup();
                return;
            }

            Class<?> serverApplicationClass = Class.forName("Application.ServerApplication");
            serverContext = SpringApplication.run(serverApplicationClass);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Không thể khởi động server.", e);
        } catch (IOException | URISyntaxException | ClassNotFoundException e) {
            throw new IllegalStateException("Không thể khởi động server.", e);
        }
    }

    private Path locateNestedServerJar() throws IOException, URISyntaxException {
        Path archivePath = Path.of(AuctionApp.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        if (Files.isDirectory(archivePath)) {
            return null;
        }

        try (JarFile jarFile = new JarFile(archivePath.toFile())) {
            JarEntry serverJarEntry = jarFile.getJarEntry(SERVER_JAR_ENTRY);
            if (serverJarEntry == null) {
                return null;
            }

            Path extractDir = Path.of(System.getProperty("java.io.tmpdir"), "AuctionListApp");
            Files.createDirectories(extractDir);

            Path extractedServerJar = extractDir.resolve("server-1.0-SNAPSHOT.jar");
            try (InputStream inputStream = jarFile.getInputStream(serverJarEntry)) {
                Files.copy(inputStream, extractedServerJar, StandardCopyOption.REPLACE_EXISTING);
            }

            extractedServerJar.toFile().deleteOnExit();
            return extractedServerJar;
        }
    }

    private void startServerProcess(Path serverJar) throws IOException {
        File logFile = Path.of(System.getProperty("java.io.tmpdir"), "AuctionListApp", "server.log").toFile();
        File parentDir = logFile.getParentFile();
        if (parentDir != null) {
            parentDir.mkdirs();
        }

        String javaExecutable = Path.of(System.getProperty("java.home"), "bin", "java").toString();
        ProcessBuilder processBuilder = new ProcessBuilder(javaExecutable, "-jar", serverJar.toAbsolutePath().toString());
        processBuilder.redirectErrorStream(true);
        processBuilder.redirectOutput(ProcessBuilder.Redirect.appendTo(logFile));
        serverProcess = processBuilder.start();
    }

    private void waitForServerStartup() throws InterruptedException {
        long timeoutMillis = 20_000L;
        long deadline = System.currentTimeMillis() + timeoutMillis;

        while (System.currentTimeMillis() < deadline) {
            if (serverProcess != null && !serverProcess.isAlive()) {
                throw new IllegalStateException("Server process đã thoát trước khi khởi động xong.");
            }

            if (isServerRunning()) {
                return;
            }

            Thread.sleep(250L);
        }

        throw new IllegalStateException("Server không khởi động kịp thời gian.");
    }
}
