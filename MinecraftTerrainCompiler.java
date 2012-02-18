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
    //The width/height of tile.
    private static final int xTileSize = 64;
    private static final int yTileSize = 64;
    //The number of tiles in the each row/column
    private static final int tileRows = 16;
    private static final int tileColumns = 16;
    //The default height of the base and output files.
    private static final int xTotalSize = tileColumns * xTileSize;
    //The default width of the base and output files.
    private static final int yTotalSize = tileRows * yTileSize;

    //GLOBALS
    private static final String[][] tileNames =
        new String[tileRows][tileColumns];

    //holds the map between the terrain.png to individual texture files
    private static final HashMap<String,Texture> textures =
        new HashMap<String,Texture>();


    //HELPER CLASSES
    private static class Coord {
        public int row;
        public int col;

        public Coord(int row, int col) {
            this.row = row;
            this.col = col;
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

        public static int totalTiles = 0;

        public Texture(Coord size, ArrayList<TileMap> maps) throws Exception {
            this.size = size;
            this.maps = maps;

            //basic checking for right number of tile maps
            if (maps.size() > totalTiles()) {
                System.err.println("Error in texture definition: Too many " +
                                   "tile maps!");
                throw(new Exception());
            }
            /*
            if (totalTiles() - maps.size() > Math.max(size.row, size.col) - 1) {
                System.err.println("Error in texture definition: Not enough " +
                                   "tile maps!");
                throw(new Exception());
            }
            */

            //Check that all coords are in range for terrain and the texture
            for (TileMap map : maps) {
                if (map.terrain.row < 0 || map.terrain.row > tileRows - 1 ||
                    map.terrain.col < 0 || map.terrain.col > tileColumns - 1 ||
                    map.texture.row < 0 || map.texture.row > size.row - 1 ||
                    map.texture.col < 0 || map.texture.col > size.col - 1) {

                    System.err.println("Error in texture definition: Tile " +
                                       "map coordinates out of range!");
                    throw(new Exception());
                }

                totalTiles++;
            }

        }

        public int totalTiles() {
            return size.row * size.col;
        }
    }

    public static void main(String[] args)
    {
        loadTextures("Textures.yml");
        System.out.println("Total tiles mapped: " + Texture.totalTiles);

        if (args.length == 0) {
            BufferedImage outImage = mergeImage();
            try {
                File outFile = new File("merged.png");
                ImageIO.write(outImage, "png", outFile);
                return;
            } catch(Exception ex) {
                System.err.println("Error writing terrain texture to file!"
                                   + ex);
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

        BufferedImage[][] tiles = splitImage(inputImage);
        writeTextures(tiles);
        return;

    }

    @SuppressWarnings("unchecked")
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

                try {
                    textures.put(textureName, new Texture(size, maps));
                } catch (Exception ex) {
                    System.err.format("Error reading texture: %s!\n",
                                      textureName);
                    System.exit(1);
                }
            }
        } catch (Exception ex) {
            System.err.println("Error loading config file: "  + configFile +
                               " - " + ex);
            System.exit(1);
        }
    }


    private static BufferedImage[][] splitImage(BufferedImage inImage) {
        //calculate the number of tiles
        int rows = inImage.getHeight() / yTileSize;
        int cols = inImage.getWidth() / xTileSize;

        System.out.format("rows:%d, cols:%d\n", rows, cols);

        //split the texture into tiles
        BufferedImage[][] tiles = new BufferedImage[rows][cols];

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                tiles[row][col] = inImage.getSubimage(
                    col * xTileSize, row * yTileSize, xTileSize, yTileSize);
            }
        }

        return tiles;
    }

    private static void writeTextures(BufferedImage[][] tiles) {
        for (Map.Entry<String, Texture> entry : textures.entrySet()) {
            String name = entry.getKey();
            Texture tex = entry.getValue();
            BufferedImage texImage = new  BufferedImage(
               tex.size.col * xTileSize, tex.size.row * yTileSize,
               BufferedImage.TYPE_INT_ARGB);
            Graphics g = texImage.createGraphics();

            for (TileMap map : tex.maps) {
                g.drawImage(
                    tiles[map.terrain.row][map.terrain.col],
                    map.texture.col * xTileSize,
                    map.texture.row * yTileSize, null);

                try {
                    File outFile = new File("textures\\" + name + ".png");
                    ImageIO.write(texImage, "png", outFile);
                } catch(Exception ex) {
                    System.err.println("Error writing texture to file!");
                    return;
                }
            }
        }
    }

    private static BufferedImage mergeImage() {
        BufferedImage outImage = new  BufferedImage(
            xTotalSize, yTotalSize, BufferedImage.TYPE_INT_ARGB);
        Graphics g = outImage.createGraphics();

        for (Map.Entry<String, Texture> entry : textures.entrySet()) {
            String name = entry.getKey();
            Texture tex = entry.getValue();

            BufferedImage texImage;
            String texPath = "textures\\" + name + ".png";

            try {
                File texFile = new File(texPath);
                texImage = ImageIO.read(texFile);
            } catch(IOException exception) {
                System.err.println("Error reading texture: " + texPath);
                return null;
            }

            System.out.println("reading texutre: " + name);
            BufferedImage[][] texTiles = splitImage(texImage);

            for (TileMap map : tex.maps) {
                g.drawImage(
                    texTiles[map.texture.row][map.texture.col],
                    map.terrain.col * xTileSize,
                    map.terrain.row * yTileSize, null);

            }
        }

        return outImage;
    }
}