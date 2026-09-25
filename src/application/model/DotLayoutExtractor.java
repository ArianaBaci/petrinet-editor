package application.model;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class DotLayoutExtractor {
    static  int startId;
    static int endId;
    static int ID;
    public static class NodeInfo {
        public final String name;
        public final double x, y;
        public final int id;

        public NodeInfo(String name, double x, double y, int id) {
            this.name = name;
            this.x = x;
            this.y = y;
            this.id=id;
        }

        public String toString() {
            return name + String.format(" %.2f, %.2f", x, y);
        }
    }

    public static class EdgeInfo {
        public final int from;
        public final int to;
        public final List<double[]> points;

        public EdgeInfo(int from, int to, List<double[]> points) {
            this.from = from;
            this.to = to;
            this.points = points;
        }

        public String toString() {
            String result = "";
            for (double[] point : points) {
                result += String.format("%.2f, %.2f; ", point[0], point[1]);
            }
            return result;
        }
    }


    public static class DotLayoutResult {
        public final List<NodeInfo> nodes;
        public final List<EdgeInfo> edges;
        public final double scalex;
        public final double scaley;

        public DotLayoutResult(List<NodeInfo> nodes, List<EdgeInfo> edges, double scalex, double scaley) {
            this.nodes = nodes;
            this.edges = edges;
            this.scalex = scalex;
            this.scaley = scaley;
        }

        public String toString() {
            String result = "";
            for (NodeInfo node : nodes) {
                result += node.toString() + "\n";
            }
            for (EdgeInfo edge : edges) {
                result += edge.toString() + "\n";
            }
            return result;
        }
    }

        public static DotLayoutResult extractLayout(String dotCode, Set<Node> realNodes) throws IOException {

            ProcessBuilder builder = new ProcessBuilder("dot", "-Tplain");
            Process process = builder.start();

            try (OutputStream stdin = process.getOutputStream()) {
                stdin.write(dotCode.getBytes(StandardCharsets.UTF_8));
                stdin.flush();
            }

            List<NodeInfo> nodes = new ArrayList<>();
            List<EdgeInfo> edges = new ArrayList<>();
            double scalex = 0;
            double scaley = 0;

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {


                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split("\\s+");
                    switch (parts[0]) {
                        case "graph":
                            scalex = Double.parseDouble(parts[1]);
                            scaley = Double.parseDouble(parts[2]);
                            break;
                        case "node":
                            String id = parts[1];
                            for (Node node : realNodes) {
                                if(node.getName().equals(id)){
                                    ID=node.getID();
                                }
                            }
                            double x = Double.parseDouble(parts[2]);
                            double y = Double.parseDouble(parts[3]);
                            nodes.add(new NodeInfo(id, x, y, ID));

                            break;
                        case "edge":
                            String from = parts[1];
                            String to = parts[2];
                            int nPoints = Integer.parseInt(parts[3]);
                            List<double[]> points = new ArrayList<>();
                            for (int i = 0; i < nPoints; i++) {
                                double px = Double.parseDouble(parts[4 + i * 2]);
                                double py = Double.parseDouble(parts[5 + i * 2]);
                                points.add(new double[]{px, py});
                            }
                            for(Node node : realNodes) {
                                if(node.getName().equals(from)){
                                    startId=node.getID();
                                }
                                if(node.getName().equals(to)){
                                    endId=node.getID();
                                }
                            }

                            edges.add(new EdgeInfo(startId, endId, points));
                            break;
                    }
                }
            }

            DotLayoutResult result=new DotLayoutResult(nodes, edges, scalex, scaley);

            return result;
        }
    }
