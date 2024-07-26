package com.ahmet.androidwebsocket.utils.scanner;

/**
 * @author Ahmet TOPAK
 * @version 1.0
 * @since 7/26/2024
 */

public class IPScannerManager {
    private final IPScanner ipScanner;
    private final PortScanner portScanner;
    private IPScanListener ipScanListener;
    private PortScanListener portScanListener;

    public IPScannerManager(IPScanListener ipScanListener, PortScanListener portScanListener) {
        this.ipScanner = new IPScanner(ipScanListener);
        this.portScanner = new PortScanner(portScanListener);
        this.ipScanListener = ipScanListener;
        this.portScanListener = portScanListener;
    }

    // Scan the network for reachable hosts
    public void scanNetwork(String subnet) {
        ipScanner.scanIPRange(subnet);
    }

    // Scan a specific IP address for a range of ports
    public void scanPorts(String ip, int startPort, int endPort) {
        portScanner.scanPortRange(ip, startPort, endPort);
    }

    // Scan a specific IP address for known ports
    public void scanKnownPorts(String ip) {
        portScanner.scanKnownPorts(ip);
    }

//    // Scan the network for reachable hosts and then scan each host for known ports
//    public void scanNetworkAndKnownPorts(String subnet) {
//        IPScanListener originalListener = this.ipScanListener;
//        this.ipScanListener = reachableHosts -> {
//            if (originalListener != null) {
//                originalListener.onIPScanCompleted(reachableHosts);
//            }
//            for (String host : reachableHosts) {
//                scanKnownPorts(host);
//            }
//        };
//        ipScanner.scanIPRange(subnet);
//    }
//
//    // Scan the network for reachable hosts and then scan each host for a range of ports
//    public void scanNetworkAndPortRange(String subnet, int startPort, int endPort) {
//        IPScanListener originalListener = this.ipScanListener;
//        this.ipScanListener = reachableHosts -> {
//            if (originalListener != null) {
//                originalListener.onIPScanCompleted(reachableHosts);
//            }
//            for (String host : reachableHosts) {
//                scanPorts(host, startPort, endPort);
//            }
//        };
//        ipScanner.scanIPRange(subnet);
//    }
}