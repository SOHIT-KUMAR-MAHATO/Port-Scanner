// Simple Post Scanner by Credit: @sohit_mahato
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class PortScanner {
    private static final int TIMEOUT = 1000; // Timeout in milliseconds
    private static final int NUM_THREADS = 50; // Number of threads to use

    // Checking the arguments length
    private static void checkLength(String[] args) {
        if (args.length == 0) {
            System.out.println("Error: No arguments received ");
            usage();

            System.exit(1);
        }
        if (args[0].equals("help")) {
            usage();

            System.exit(1);
        }
    }
    // Checking the Ports length
    private static boolean checkPortLength(final Integer startPort, final Integer endPort) {
        if (startPort >= 1 && endPort <= 65535) {
            return true;
        }
        System.out.println("Port Lengths should be between 1 and 65535");
        usage();

        return false;
    }

    // Usage 
    private static void usage() {
        System.out.println("------------------------------------------------");
        System.out.println("Usage: ");
        System.out.println(" java PortScanner help");
        System.out.println(" java PortScanner <target host> <startPort> <endPort>");
        System.out.println("Example: ");
        System.out.println(" java PortScanner google.com 1 100");
        System.out.println(" java PortScanner yahoo.com 20 50");
        System.out.println(" java PortScanner twitter.com 80 80");
        System.out.println("\t\t\t\t@sohit_mahato");
        System.out.println("------------------------------------------------");
    }
    // Main method
    public static void main(String[] args) {
        try {
            long startTime = System.currentTimeMillis();
            checkLength(args);
            String host = args[0];
            final Integer startPort = Integer.parseInt(args[1]);
            final Integer endPort = Integer.parseInt(args[2]);
            if (checkPortLength(startPort, endPort)) {
                try {
                    InetAddress targetAddress = InetAddress.getByName(host);
                    System.out.println("------------------------------------------------");
                    System.out.println("Scanning on " + targetAddress.getHostAddress());
                    Queue<Integer> openPorts = new LinkedList<>();
                    ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);
                    for (int port = startPort; port <= endPort; port++) {
                        final int currentPort = port;
                        executor.execute(() -> {
                            if (isPortOpen(targetAddress, currentPort, TIMEOUT)) {
                                synchronized (openPorts) {
                                    openPorts.add(currentPort);
                                }
                            }
                        });
                    }
                    executor.shutdown();
                    try {
                        executor.awaitTermination(1, TimeUnit.MINUTES);
                    } catch (Exception e) {
                        System.out.println("Something went wrong");
                    }
                    long endTime = System.currentTimeMillis();
                    if (!openPorts.isEmpty()) {
                        System.out.println("Open ports: " + openPorts);
                    } else {
                        System.out.println("No Ports Opened");
                    }
                    System.out.println("Time elapsed: " + (endTime - startTime) + "ms");
                    System.out.println("\t\t\t\t@sohit_mahato");
                    System.out.println("------------------------------------------------");
                } catch (Exception e) {
                    System.out.println("Something went wrong");
                    usage();

                }
            }
        } catch (Exception e) {
            System.out.println("Something went wrong");
            usage();

        }
    }
    // Checking if the port is open or not
    private static boolean isPortOpen(InetAddress address, int port, int timeout) {
        try {
            Socket socket = new Socket();
            InetSocketAddress socketAddress = new InetSocketAddress(address, port);
            socket.connect(socketAddress, timeout);
            socket.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

//Credit: Sohit Kumar Mahato
