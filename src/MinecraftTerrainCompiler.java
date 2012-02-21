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

    //paths
    private static final String texturesDefFile = "config\\Textures.yml";
    private static final String splitOutPath = "splitTextures\\";
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
    //holds the map between the terrain.png to individual texture files
    private static final HashMap<String,Texture> textures =
        new HashMap<String,Texture>();
    //holds the mapping between textures and files to load when merging
    private static final HashMap<String,ArrayList<String>> textureFiles =
        new HashMap<String,ArrayList<String>>();

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

    //FUNCTIONS
    //manipulate file paths
    private static final String extensionSeparator = ".";
    private static final String pathSeparator = "\\";

    private static String extension(String fullPath) {
        int dot = fullPath.lastIndexOf(extensionSeparator);
        return fullPath.substring(dot + 1);
    }
    private static String filename(String fullPath) { // gets filename without extension
        int dot = fullPath.lastIndexOf(extensionSeparator);
        int sep = fullPath.lastIndexOf(pathSeparator);
        return fullPath.substring(sep + 1, dot);
    }
    private static String path(String fullPath) {
        int sep = fullPath.lastIndexOf(pathSeparator);
        if (sep < 0) {
            return ".";
        } else {
            return fullPath.substring(0, sep);
        }
    }

    public static void main(String[] args)
    {
        loadTextures(texturesDefFile);
        //System.out.println("Total tiles mapped: " + Texture.totalTiles);

        if (args.length != 2) {
            System.err.println("Invalid command line arguments!");
            System.exit(1);
        }

        if (args[0].equals("-m")) {
            //merge textures to create terrain.png
            loadMergeConfig(args[1]);

            BufferedImage outImage = mergeImage();
            if (outImage == null) return;
            try {
                File outFile = new File("terrain.png");
                ImageIO.write(outImage, "png", outFile);
            } catch(Exception ex) {
                System.err.println("Error writing terrain texture to file!"
                                   + ex);
                return;
            }
        } else if (args[0].equals("-s")) {
            //split a terrain file into textures
            BufferedImage inputImage;
            try {
                File inputFile = new File(args[1]);
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
        } else {
            System.err.println("Invalid command line arguments!");
            System.exit(1);
        }
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

    @SuppressWarnings("unchecked")
    private static void loadMergeConfig(String configFile) {
        String configFileDirectory = path(configFile);

        try {
            InputStream input = new FileInputStream(new File(configFile));
            Yaml yaml = new Yaml();
            Map<String,Object> data = (Map<String,Object>)yaml.load(input);

            //handle inheritance
            if (data.containsKey("inherit")) {
                //load the inherit file first
                loadMergeConfig(configFileDirectory + pathSeparator +
                                (String)data.get("inherit"));
                data.remove("inherit");
            }

            for (Map.Entry<String, Object> entry : data.entrySet()) {
                String textureName = entry.getKey();
                Object file = entry.getValue();

                if (!textures.containsKey(textureName)) {
                    System.err.format("Invalid texture name '%s' in config " +
                                      "file '%s'!\n", textureName, configFile);
                    System.exit(1);
                }

                ArrayList<String> files = new ArrayList<String>();

                if (file instanceof List) {
                    for (String fileName : (List<String>)file) {
                        files.add(configFileDirectory +
                                  pathSeparator + fileName);
                    }
                } else if (file instanceof String) { //single file only
                    files.add(configFileDirectory +
                              pathSeparator + (String)file);
                } else {
                    System.err.format("Invalid texture config '%s' in config " +
                                      "file '%s'!\n", textureName, configFile);
                }

                textureFiles.put(textureName, files);
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

    //NOTE: destructive operation on tilesIn because it nulls references
    //      to make sure no tile is used twice
    private static void writeTextures(BufferedImage[][] tiles) {
        //make sure the out directory exists
        new File(splitOutPath).mkdirs();

        for (Map.Entry<String, Texture> entry : textures.entrySet()) {
            String name = entry.getKey();
            Texture tex = entry.getValue();

            BufferedImage texImage = new  BufferedImage(
               tex.size.col * xTileSize, tex.size.row * yTileSize,
               BufferedImage.TYPE_INT_ARGB);
            Graphics g = texImage.createGraphics();

            for (TileMap map : tex.maps) {
                if (tiles[map.terrain.row][map.terrain.col] == null) {
                    System.err.format(
                        "The same tile in terrain.png was used twice while " +
                        "writing texture \"%s\"!\n", name);
                    System.exit(1);
                }

                g.drawImage(
                    tiles[map.terrain.row][map.terrain.col],
                    map.texture.col * xTileSize,
                    map.texture.row * yTileSize, null);

                //null the reference to the tile so it can't be used more than
                //once
                tiles[map.terrain.row][map.terrain.col] = null;
            }

            try {
                File outFile = new File(splitOutPath + name + ".png");
                ImageIO.write(texImage, "png", outFile);
            } catch(Exception ex) {
                System.err.println("Error writing texture to file!");
                return;
            }
        }
    }

    private static BufferedImage mergeImage() {
        //gather all the tiles from the different textures
        BufferedImage[][] tiles = new BufferedImage[tileRows][tileColumns];

        for (Map.Entry<String, ArrayList<String>> entry :
                 textureFiles.entrySet()) {
            String textureName = entry.getKey();
            ArrayList<String> filePaths = entry.getValue();
            Texture tex = textures.get(textureName);

            //merge all the images in the texture list (allows for overlays)
            BufferedImage texImage = new  BufferedImage(
                tex.size.col * xTileSize, tex.size.row * yTileSize,
                BufferedImage.TYPE_INT_ARGB);
            Graphics g = texImage.createGraphics();

            for (String filePath : filePaths) {
                try {
                    File texFile = new File(filePath);
                    BufferedImage texImageLayer = ImageIO.read(texFile);
                    g.drawImage(texImageLayer, 0, 0, null);
                } catch(IOException exception) {
                    System.err.println("Error reading texture: " + filePath);
                    return null;
                }
            }

            BufferedImage[][] texTiles = splitImage(texImage);

            for (TileMap map : tex.maps) {
                //make sure we don't overwrite a tile
                if (tiles[map.terrain.row][map.terrain.col] != null) {
                    System.err.format(
                        "The same tile in terrain.png was written twice " +
                        "while processing texture \"%s\"!\n", textureName);
                    System.exit(1);
                }

                tiles[map.terrain.row][map.terrain.col] =
                    texTiles[map.texture.row][map.texture.col];
            }
        }

        //draw all the tiles to a new image
        BufferedImage outImage = new  BufferedImage(
            xTotalSize, yTotalSize, BufferedImage.TYPE_INT_ARGB);
        Graphics g = outImage.createGraphics();
        for (int row = 0; row < tileRows; row++) {
            for (int col = 0; col < tileColumns; col++) {
                g.drawImage(tiles[row][col], col * xTileSize,
                            row * yTileSize, null);
            }
        }

        return outImage;
    }
}