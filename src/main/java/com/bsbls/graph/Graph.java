package com.bsbls.graph;

import com.bsbls.test.GUITester;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.layout.Layout;
import org.graphstream.ui.layout.Layouts;
import org.graphstream.ui.swingViewer.ViewPanel;
import org.graphstream.ui.view.Viewer;

import java.awt.*;

public class Graph {


    public static void main(String[] args) {
        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
        org.graphstream.graph.Graph graph = new SingleGraph("I can see dead pixels");

        graph.addAttribute("ui.quality");
        graph.addAttribute("ui.antialias");

        graph.addAttribute("ui.stylesheet", "url(file:///C:\\Users\\user\\IdeaProjects\\routing\\src\\main\\resources\\graph.css)");

        graph.setAutoCreate(true);
        graph.setStrict(false);


        createNode(graph, "1", "own");

        createNode(graph, "1001", "link");

        createEdge(graph, "1-1001", "1", "1001");
        createEdge(graph, "7-1001", "7", "1001");
        createEdge(graph, "6-1001", "6", "1001");
        createEdge(graph, "10-1001", "10", "1001");
        createEdge(graph, "5-1001", "5", "1001");

        createNode(graph, "1003", "link");
        createEdge(graph, "2-1003", "1003", "2");
        createEdge(graph, "1-1003", "1003", "1");

        createNode(graph, "1002", "link");
        createEdge(graph, "1-1002", "1", "1002");
        createEdge(graph, "3-1002", "3", "1002");
        createEdge(graph, "4-1002", "4", "1002");


        createNode(graph, "1004", "link");
        createEdge(graph, "11-1004", "11", "1004");
        createEdge(graph, "12-1004", "12", "1004");
        createEdge(graph, "4-1004", "4", "1004");

        createNode(graph, "1005", "link");
        createEdge(graph, "4-1005", "1005", "4");
        createEdge(graph, "13-1005", "1005", "13");

        createNode(graph, "1006", "link");
        createEdge(graph, "6-1006", "1006", "6");
        createEdge(graph, "14-1006", "1006", "14");

        createNode(graph, "1007", "link");
        createEdge(graph, "6-1007", "1007", "6");
        createEdge(graph, "15-1007", "1007", "15");


        createNode(graph, "1008", "link");
        createEdge(graph, "10-1008", "1008", "10");
        createEdge(graph, "16-1008", "1008", "16");


        createEdge(graph, "1001-1006", "1001", "1006", "conn", true);
        createEdge(graph, "1007-1006", "1007", "1006", "conn", false);



        for (Node node : graph) {
            if (!node.hasAttribute("ui.label")) {
                node.addAttribute("ui.label", node.getId());
            }
        }


        Viewer viewer = new Viewer(graph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
        ViewPanel view = viewer.addDefaultView(false);   // false indicates "no JFrame"
        view.setPreferredSize(new Dimension(800, 600));


        Layout layout = Layouts.newLayoutAlgorithm();
        viewer.enableAutoLayout(layout);


        GUITester.test(view);

    }

    private static Edge createEdge(org.graphstream.graph.Graph graph, String id, String node1, String node2) {
        Edge edge = graph.addEdge(id, node1, node2);
        return edge;
    }

    private static Edge createEdge(org.graphstream.graph.Graph graph, String id, String node1, String node2, String className, boolean directed) {
        Edge edge = graph.addEdge(id, node1, node2, directed);

        edge.setAttribute("ui.class", className);
        return edge;
    }

    private static Node createNode(org.graphstream.graph.Graph g, String id) {
        Node node = g.addNode(id);
        node.addAttribute("ui.label", id);
        return node;
    }

    private static Node createNode(org.graphstream.graph.Graph g, String id, String className) {
        Node node = createNode(g, id);
        node.addAttribute("ui.class", className);
        return node;
    }
}
