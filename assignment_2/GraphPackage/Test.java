package Main;

import ADTPackage.*;
import GraphPackage.GraphAlgorithmsInterface;
import GraphPackage.GraphInterface;
import GraphPackage.UndirectedGraph;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.Random;
import java.util.Scanner;

public class Test {
    public static void main(String[] args) throws FileNotFoundException {

        MazeGen mazeGen = new MazeGen("maze1.txt");
        int[] dimensions = mazeGen.getMazeDimensions();
        String end = (dimensions[0]-2)+"-"+(dimensions[1]-1);

        GraphAlgorithmsInterface<String> mazeGraph = mazeGen.generateGraphFromFile();
        System.out.println("\n");

        QueueInterface<String> BFSQueue = mazeGraph.getBreadthFirstSearch("0-1",end);
        mazeGen.drawTraversal(BFSQueue);

        System.out.println("\n");

        QueueInterface<String> DFSQueue = mazeGraph.getDepthFirstSearch("0-1",end);
        mazeGen.drawTraversal(DFSQueue);

        System.out.println("\n");

        StackInterface<String> shortestPath = new LinkedStack<>();
        mazeGraph.getShortestPath("0-1", end, shortestPath);
        mazeGen.drawTraversal(shortestPath);

        System.out.println("\n");

        StackInterface<String> cheapestPath = new LinkedStack<>();
        double cost = mazeGraph.getCheapestPath("0-1", end, cheapestPath);
        cost = Math.floor(cost*100)/100;
        mazeGen.drawTraversal(cheapestPath);
        System.out.println("Path traversal cost = "+cost);

    }

    public static class MazeGen {
        Scanner sc;
        ListWithIteratorInterface<String> lineList;
        int height,width;

        public MazeGen(String fileName) throws FileNotFoundException {
            // read file and make a list of lines.
            sc = new Scanner(new File(fileName));
            height = 0;
            width = 0;
            lineList = new LinkedListWithIterator<>();
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if (line.length() > width) width = line.length();
                lineList.add(line);
                height++;
            }
        }

        public GraphInterface<String> generateGraphFromFile() {
            //graph to be returned
            GraphInterface<String> newGraph = new UndirectedGraph<>();

            Iterator<String> iterator = lineList.getIterator();

            int readPosY = 0;
            char[] previousLine = null;
            Random random = new Random();

            while (iterator.hasNext()) { // start graph construction
                String textLine = iterator.next();

                int readPosX = 0;
                char previousChar = 0;

                for (char c : textLine.toCharArray()) {
                    if (c == ' ') {
                        newGraph.addVertex(readPosY +"-"+ readPosX);
                        //check the characters to the left and above while traversing through the lines of text.
                        if (previousChar == ' ')  {
                            double randomWeight = Math.floor(random.nextDouble(1,4) * 100) / 100;
                            newGraph.addEdge((readPosY +"-"+ (readPosX-1)),(readPosY +"-"+ readPosX),randomWeight);
                        }
                        if ((previousLine != null) && (previousLine[readPosX] == ' ')) {
                            double randomWeight = Math.floor(random.nextDouble(1,4) * 100) / 100;
                            newGraph.addEdge((readPosY +"-"+ readPosX),((readPosY - 1) +"-"+ readPosX),randomWeight);
                        }
                    }
                    else if (c != '#')
                        System.out.println("WARNING: Unknown character detected in maze file. Reading as '#'");
                    readPosX++;
                    previousChar = c;
                }
                readPosY++;
                previousLine = textLine.toCharArray();
            } //finish graph construction

            //draw adjacency list
            iterator = lineList.getIterator();
            readPosY = 1;

            ListWithIteratorInterface<String> nodeList = new LinkedListWithIterator<>();
            while (iterator.hasNext()) {
                String textLine = iterator.next();
                int readPosX = 0;
                for (char c : textLine.toCharArray()) {
                    if (c == ' ') {
                        nodeList.add((readPosY-1)+"-"+readPosX);

                        System.out.print((readPosY-1)+"-"+readPosX+" = ");

                        if (readPosX > 0 && lineList.getEntry(readPosY).charAt(readPosX-1) == ' ') System.out.print((readPosY-1)+"-"+(readPosX-1)+" ");
                        if (readPosX < width - 1 && lineList.getEntry(readPosY).charAt(readPosX+1) == ' ') System.out.print((readPosY-1)+"-"+(readPosX+1)+" ");
                        if (readPosY > 1 && lineList.getEntry(readPosY-1).charAt(readPosX) == ' ') System.out.print((readPosY-2)+"-"+readPosX+" ");
                        if (readPosY < height - 1 && lineList.getEntry(readPosY+1).charAt(readPosX) == ' ') System.out.print(readPosY+"-"+readPosX+" ");
                        System.out.println();

                    }
                    readPosX++;
                }
                readPosY++;
            } //finish drawing adjacency list

            //draw adjacency matrix
            iterator = nodeList.getIterator();
            System.out.print("   ");
            while (iterator.hasNext()) System.out.print(iterator.next()+" ");
            System.out.println();
            iterator = nodeList.getIterator();
            while (iterator.hasNext()) {
                String writtenNode = iterator.next();
                System.out.print(writtenNode+" ");
                String[] parse = writtenNode.split("-");
                int writePosX = Integer.parseInt(parse[1]);
                int writePosY = Integer.parseInt(parse[0]);
                Iterator<String> searchIterator = nodeList.getIterator();
                while (searchIterator.hasNext()){
                    String search = searchIterator.next();
                    String[] searchParse = search.split("-");
                    int searchPosX = Integer.parseInt(searchParse[1]);
                    int searchPosY = Integer.parseInt(searchParse[0]);
                    if ((searchPosX+1==writePosX && searchPosY==writePosY) || (searchPosX-1==writePosX && searchPosY==writePosY) || (searchPosX == writePosX && searchPosY+1==writePosY) || (searchPosX == writePosX && searchPosY-1==writePosY)) {
                        System.out.print("1");
                    }
                    else {
                        System.out.print(" ");
                    }
                    System.out.print("   ");
                }
                System.out.println();
            } // finish drawing adjacency matrix

            System.out.println("Edge count = "+newGraph.getNumberOfEdges());

            return newGraph;
        }

        public void drawTraversal(QueueInterface<String> traversal) { //put the maze text through a buffer and overwrite the traversed spots with '.', then print the buffer.
            ListWithIteratorInterface<String> drawBuffer = new LinkedListWithIterator<>();
            Iterator<String> iterator = lineList.getIterator();
            while (iterator.hasNext()) {
                drawBuffer.add(iterator.next());
            }

            while (!traversal.isEmpty()) {
                String step = traversal.dequeue();
                String[] parsed = step.split("-");
                char[] newLine = drawBuffer.getEntry(Integer.parseInt(parsed[0])+1).toCharArray();
                newLine[Integer.parseInt(parsed[1])] = '.';
                drawBuffer.replace(Integer.parseInt(parsed[0])+1,String.valueOf(newLine));
            }

            iterator = drawBuffer.getIterator();
            while (iterator.hasNext()) {
                System.out.println(iterator.next());
            }
        }

        public void drawTraversal(StackInterface<String> traversal) { //put the maze text through a buffer and overwrite the traversed spots with '.', then print the buffer.
            ListWithIteratorInterface<String> drawBuffer = new LinkedListWithIterator<>();
            Iterator<String> iterator = lineList.getIterator();
            while (iterator.hasNext()) {
                drawBuffer.add(iterator.next());
            }

            while (!traversal.isEmpty()) {
                String step = traversal.pop();
                String[] parsed = step.split("-");
                char[] newLine = drawBuffer.getEntry(Integer.parseInt(parsed[0])+1).toCharArray();
                newLine[Integer.parseInt(parsed[1])] = '.';
                drawBuffer.replace(Integer.parseInt(parsed[0])+1,String.valueOf(newLine));
            }

            iterator = drawBuffer.getIterator(); //draw buffer
            while (iterator.hasNext()) {
                System.out.println(iterator.next());
            }
        }

        public int[] getMazeDimensions() {
            return new int[] {height,width};
        }
    }
}
