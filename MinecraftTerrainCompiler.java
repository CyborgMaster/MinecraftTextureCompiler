/**
 * Program to take a bunch of separate picture files and make them into one file
 **/

import java.text.*;
import java.util.*;
import java.io.*;
import java.awt.image.*;
import java.awt.Graphics;
import javax.imageio.*;
import org.yaml.snakeyaml.Yaml;


public class MinecraftTerrainCompiler
{
    //CONSTANTS
    
    //The current directory.
    private static final String workingDirectory = System.getProperty("user.dir");
    //The extension to use.
    private static final String EXTENSION = ".png";
    //The name of the output file.
    private static final String outputFile = "terrain";
    //The default width of tile pieces.
    private static final int xTileSize = 64;
    //The default height of tile pieces.
    private static final int yTileSize = 64;
    //The number of tiles in the x/y axis
    private static final int xTiles = 16;
    private static final int yTiles = 16;
    //The default height of the base and output files.
    private static final int xTotalSize = xTiles * xTileSize;
    //The default width of the base and output files.
    private static final int yTotalSize = yTiles * yTileSize;

    //GLOBALS
    private static final String[][] tileNames = new String[yTiles][xTiles];
    //holds the map between the terrain.png to individual texture files
    private static final HashMap<String,Texture> textures =
        new HashMap<String,Texture>();


    //HELPER CLASSES
    private static class Coord {
        public int x;
        public int y;

        public Coord(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    private static class TileMap {
        public Coord terrain;
        public Coord texture;

        public TileMap(Coord terrain, Coord texture) {
            this.terrain = terrain;
            this.texture = texture;
        }
    }

    private static class Texture {
        public Coord size;
        public ArrayList<TileMap> maps;

        public Texture(Coord size, ArrayList<TileMap> maps) {
            this.size = size;
            this.maps = maps;

            //basic checking for right number of tile maps
            if (totalTiles() - maps.size() > Math.min(size.x, size.y) - 1) {
                System.err.println("Error in texture definition! Not enough tile " +
                                   "maps");
                System.exit(1);
            }

            //Check that all coords are in range for terrain and the texture
            for (TileMap map : maps) {
                if (map.terrain.x < 0 || map.terrain.x > xTiles - 1) {
                    System.err.println("Error in texture definition! Tile map " +
                                       "coordinates out of range maps");
                    System.exit(1);
                }
            }
                
        }

        /*
          public Texture(Coord size, TileMap... maps) {
          ArrayList<TileMap> mapList;
          for (TileMap map : maps) {
          mapList.add(map);
          }

          this(size, mapList);
          }
        */
    
        public int totalTiles() {
            return size.x * size.y;
        }
    }

    public static void main(String[] args)
    {
        loadTextures("Textures.yml");
        System.exit(0);
        
        if (args.length == 0) {
            try {
                File outFile = new File("merged.png");
                BufferedImage outImage = mergeImage();
                ImageIO.write(outImage, "png", outFile);
                return;
            } catch(Exception ex) {
                System.err.println("Error writing terrain texture to file!");
                return;
            }
        }

        BufferedImage inputImage;

        try {
            File inputFile = new File(args[0]);
            inputImage = ImageIO.read(inputFile);
        } catch(Exception ex) {
            System.err.println("Error reading input file: " + ex);
            return;
        }

        if (inputImage.getWidth() != xTotalSize ||
            inputImage.getHeight() != yTotalSize) {
            System.err.println("Input image wrong size, should be: " +
                               xTotalSize + " x " + yTotalSize);
            return;
        }

        splitImage(inputImage);

        return;

    }

    private static void loadTextures(String configFile) {
        try {
            InputStream input = new FileInputStream(new File(configFile));
            Yaml yaml = new Yaml();
            Map<String, Object> data = (Map<String, Object>)yaml.load(input);

            for (String textureName : data.keySet()) {
                Map<String, Object> textureMap =
                    (Map<String,Object>)data.get(textureName);
                List<Integer> sizeList = (List<Integer>)textureMap.get("size");
                Coord size = new Coord(sizeList.get(0), sizeList.get(1));

                List<Object> tileList = (List<Object>)textureMap.get("tiles");

                ArrayList<TileMap> maps = new ArrayList<TileMap>();
                
                for (Object tileMap : tileList) {
                    List<Object> coords = (List<Object>)tileMap;
                    List<Integer> terCoordList = (List<Integer>)coords.get(0);
                    List<Integer> texCoordList = (List<Integer>)coords.get(1);
                    
                    Coord terCoord = new Coord(terCoordList.get(0),
                                               terCoordList.get(1));
                    Coord texCoord = new Coord(texCoordList.get(0),
                                               texCoordList.get(1));

                    maps.add(new TileMap(terCoord, texCoord));
                }

                textures.put(textureName, new Texture(size, maps));
            }
        } catch(Exception ex) {
            System.err.println("Error loading config file: "  + configFile +
                               " - " + ex);
        }
    }


    private static void splitImage(BufferedImage inImage) {
        //split the texture
        for (int i = 0; i < tileNames.length; i++) {
            for (int j = 0; j < tileNames[i].length; j++) {
                BufferedImage outImage = inImage.getSubimage(
                    j * xTileSize, i * yTileSize, 64, 64);

                try {
                    File outFile = new File("temp\\" + tileNames[i][j] +
                                            ".png");
                    ImageIO.write(outImage, "png", outFile);
                } catch(Exception ex) {
                    System.err.println("Error writing tile to file!");
                    return;
                }
            }
        }
    }

    private static BufferedImage mergeImage() {
        BufferedImage outImage = new  BufferedImage(
            xTotalSize, yTotalSize, BufferedImage.TYPE_INT_ARGB);
        Graphics g = outImage.createGraphics();

        for (int i = 0; i < tileNames.length; i++) {
            for (int j = 0; j < tileNames[i].length; j++) {
                placeTile(g, "temp\\" + tileNames[i][j] + ".png", j, i);
            }
        }

        return outImage;
    }

    public static void placeTile(Graphics graphics, String tilePath,
                                 int xLoc, int yLoc) {
        BufferedImage tile;

        try {
            File tileFile = new File(tilePath);
            tile = ImageIO.read(tileFile);
        } catch(IOException exception) {
            System.err.println("Error reading tile: " + tilePath);
            return;
        }

        graphics.drawImage(tile, xLoc * xTileSize, yLoc * yTileSize, null);
    }

    public static void writeTexture()
    {
        /*
        try
        {
            boolean newDirectory = (new File(workingDirectory + "\\Compiled Textures")).mkdir();
        }
        catch(SecurityException exceptionSE)
        {
            File textureFile = new File(workingDirectory + outputFile + EXTENSION);
            try
            {
                ImageIO.write(base, "png", textureFile);
            }
            catch(IOException exceptionIO)
            {
                System.err.println("Critical failure attempting to write to file!");
                return;
            }
            return;
        }
        File textureFile = new File(workingDirectory + "\\Compiled Textures\\" + outputFile + EXTENSION);
        try
        {
            ImageIO.write(base, "png", textureFile);
        }
        catch(IOException exceptionIO)
        {
            System.err.println("Critical failure attempting to write to file!");
            return;
        }
        return;
        */
    }
}