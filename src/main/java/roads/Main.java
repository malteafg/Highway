package roads;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.system.MemoryUtil.*;

import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

public class Main implements Runnable {

    private Thread thread;
    private boolean running;

    private int updates, frames;

    private long window;

    private void init() {
        // initting opengl
        if(glfwInit() != true) {
            System.err.println("Could not initialize GLFW!");
            return;
        }

        // creating window
        long monitor = glfwGetPrimaryMonitor();
        GLFWVidMode vidmode = glfwGetVideoMode(monitor);
        glfwWindowHint(GLFW_RED_BITS, vidmode.redBits());
        glfwWindowHint(GLFW_GREEN_BITS, vidmode.greenBits());
        glfwWindowHint(GLFW_BLUE_BITS, vidmode.blueBits());
        glfwWindowHint(GLFW_REFRESH_RATE, vidmode.refreshRate());
        window = glfwCreateWindow(1920, 1080, "Roads", monitor, NULL);
        if(window == NULL) {
            System.err.println("Could not create GLFW window!");
            return;
        }
        glfwSetWindowPos(window, vidmode.width() / 2 - 1920, vidmode.height() / 2 - 1080);

        glfwMakeContextCurrent(window);
        glfwShowWindow(window);

        GL.createCapabilities();

        // setting graphics
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        glEnable(GL_DEPTH_TEST);
        glActiveTexture(GL_TEXTURE1);
        System.out.println("OpenGL: " + glGetString(GL_VERSION));
    }

    public synchronized void start() {
        running = true;
        thread = new Thread(this, "Display");
        thread.start();
    }

    @Override
    public void run() {
        init();

        long lastTime = System.nanoTime();
        long timer = System.currentTimeMillis();
        final double ns = 1000000000.0 / 60.0;
        double delta = 0;
        int u = 0;
        int f = 0;

        while(running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            while (delta >= 1) {
                gameUpdate();
                u++;
                delta--;
            }

            gameRender();
            f++;

            if(System.currentTimeMillis() - timer > 1000) {
                timer += 1000;

                updates = u;
                frames = f;

                System.out.println("UPS: " + updates + "  FPS: " + frames);
                //if(updates < 15 || updates > 100) System.exit(0);

                u = 0;
                f = 0;
            }

            if(glfwWindowShouldClose(window) == true) {
                running = false;
            }
        }
    }

    private void gameUpdate() {
        glfwPollEvents();
    }

    private void gameRender() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        glfwSwapBuffers(window);
    }

    public static void main(String[] args) {
        new Main().start();
    }

}