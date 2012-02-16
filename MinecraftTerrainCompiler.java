/**
 * Program to take a bunch of separate picture files and make them into one file
 **/

import java.awt.*;
import java.text.*;
import java.util.*;
import java.io.*;
import java.awt.image.*;
import javax.imageio.*;

public class MinecraftTerrainCompiler
{
    //The current directory.
    private static final String workingDirectory = System.getProperty("user.dir");
    //The extension to use.
    private static final String EXTENSION = ".png";
    //The name of the base file
    private static final String baseName = "terrainbase";
    //The location of the base file.
    private static final String baseLocation = workingDirectory + "\\" + baseName + EXTENSION;
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
    //The base file's holder for editing.
    private static BufferedImage base = new BufferedImage(xTotalSize, yTotalSize, BufferedImage.TYPE_INT_ARGB);

    private static final ArrayList<String>[][] tileNames = new
        ArrayList[yTiles][xTiles];
    static {
        for (int i = 0; i < tileNames.length; i++) {
            for (int j = 0; j < tileNames[i].length; j++) {
                tileNames[i][j] = new ArrayList<String>();
            }
        }

        //Row one of terrain.png
        tileNames[0][0].add("grass");
        tileNames[0][1].add("stone");
        tileNames[0][2].add("dirt");
        tileNames[0][3].add("grassyDirt");
        tileNames[0][4].add("planks");
        tileNames[0][5].add("stoneSlabSides");
        tileNames[0][6].add("stoneSlabTop");
        tileNames[0][7].add("brick");
        tileNames[0][8].add("tntSide");
        tileNames[0][9].add("tntTop");
        tileNames[0][10].add("tntBottom");
        tileNames[0][11].add("spiderWeb");
        tileNames[0][12].add("flowerRed");
        tileNames[0][13].add("flowerYellow");
        tileNames[0][14].add("portal");
        tileNames[0][15].add("saplingOak");

        //Row two of terrain.png
        tileNames[1][0].add("cobblestone");
        tileNames[1][1].add("bedrock");
        tileNames[1][2].add("sand");
        tileNames[1][3].add("gravel");
        tileNames[1][4].add("logOak");
        tileNames[1][5].add("logTop");
        tileNames[1][6].add("oreBlockIron");
        tileNames[1][7].add("oreBlockGold");
        tileNames[1][8].add("oreBlockDiamond");
        tileNames[1][9].add("chestSmallTop");
        tileNames[1][10].add("chestSmallSide");
        tileNames[1][11].add("chestSmallFront");
        tileNames[1][12].add("mushroomRed");
        tileNames[1][13].add("mushroomBrown");
        tileNames[1][14].add("_blank0114");
        tileNames[1][15].add("fire1");
    
        //Row three of terrain.png
        tileNames[2][0].add("oreGold");
        tileNames[2][1].add("oreIron");
        tileNames[2][2].add("oreCoal");
        tileNames[2][3].add("bookshelf");
        tileNames[2][4].add("cobblestoneMossy");
        tileNames[2][5].add("obsidian");
        tileNames[2][6].add("grassSide");
        tileNames[2][7].add("tallGrass");
        tileNames[2][8].add("_unknown1");
        tileNames[2][9].add("chestLargeFrontLeft");
        tileNames[2][10].add("chestLargeFrontRight");
        tileNames[2][11].add("workbenchTop");
        tileNames[2][12].add("furnaceFront");
        tileNames[2][13].add("furnaceSide");
        tileNames[2][14].add("dispenser");
        tileNames[2][15].add("fire2");
    
        //Row four of terrain.png
        tileNames[3][0].add("sponge");
        tileNames[3][1].add("glass");
        tileNames[3][2].add("oreDiamond");
        tileNames[3][3].add("oreRedstone");
        tileNames[3][4].add("leavesNormalFancy");
        tileNames[3][5].add("leavesNormalFast");
        tileNames[3][6].add("stoneBrick");
        tileNames[3][7].add("deadShrub");
        tileNames[3][8].add("tallGrass2");
        tileNames[3][9].add("chestLargeBackLeft");
        tileNames[3][10].add("chestLargeBackRight");
        tileNames[3][11].add("workbenchSide1");
        tileNames[3][12].add("workbenchSide2");
        tileNames[3][13].add("furnaceLit");
        tileNames[3][14].add("furnaceTop");
        tileNames[3][15].add("saplingPine");
    
        //Row five of terrain.png
        tileNames[4][0].add("woolWhite");
        tileNames[4][1].add("mobSpawner");
        tileNames[4][2].add("snow");
        tileNames[4][3].add("ice");
        tileNames[4][4].add("snowyDirt");
        tileNames[4][5].add("cactusTop");
        tileNames[4][6].add("cactusSide");
        tileNames[4][7].add("cactusInside");
        tileNames[4][8].add("clay");
        tileNames[4][9].add("reeds");
        tileNames[4][10].add("jukeboxSide");
        tileNames[4][11].add("jukeboxTop");
        tileNames[4][12].add("lilyPad");
        tileNames[4][13].add("myceliumSide");
        tileNames[4][14].add("myceliumTop");
        tileNames[4][15].add("saplingBirch");
    
        //Row six of terrain.png
        tileNames[5][0].add("torch");
        tileNames[5][1].add("doorWoodTop");
        tileNames[5][2].add("doorIronTop");
        tileNames[5][3].add("ladder");
        tileNames[5][4].add("trapdoor");
        tileNames[5][5].add("ironBars");
        tileNames[5][6].add("farmlandWet");
        tileNames[5][7].add("farmlandDry");
        tileNames[5][8].add("wheat1");
        tileNames[5][9].add("wheat2");
        tileNames[5][10].add("wheat3");
        tileNames[5][11].add("wheat4");
        tileNames[5][12].add("wheat5");
        tileNames[5][13].add("wheat6");
        tileNames[5][14].add("wheat7");
        tileNames[5][15].add("wheat8");
    
        //Row seven of terrain.png
        tileNames[6][0].add("lever");
        tileNames[6][1].add("doorWoodBottom");
        tileNames[6][2].add("doorIronBottom");
        tileNames[6][3].add("redstoneTorchOn");
        tileNames[6][4].add("stoneBrickMossy");
        tileNames[6][5].add("stoneBrickCracked");
        tileNames[6][6].add("pumpkinTop");
        tileNames[6][7].add("netherrack");
        tileNames[6][8].add("soulSand");
        tileNames[6][9].add("glowstone");
        tileNames[6][10].add("pistonSticky");
        tileNames[6][11].add("piston");
        tileNames[6][12].add("pistonSide");
        tileNames[6][13].add("pistonBack");
        tileNames[6][14].add("pistonFront");
        tileNames[6][15].add("pumpkinVine");
    
        //Row eight of terrain.png
        tileNames[7][0].add("railCurved");
        tileNames[7][1].add("woolBlack");
        tileNames[7][2].add("woolGray");
        tileNames[7][3].add("redstoneTorchOff");
        tileNames[7][4].add("logPine");
        tileNames[7][5].add("logBirch");
        tileNames[7][6].add("pumpkinSide");
        tileNames[7][7].add("pumpkinFaceOff");
        tileNames[7][8].add("pumpkinFaceOn");
        tileNames[7][9].add("cakeTop");
        tileNames[7][10].add("cakeSide");
        tileNames[7][11].add("cakeInside");
        tileNames[7][12].add("cakeBottom");
        tileNames[7][13].add("mushroomGiantRed");
        tileNames[7][14].add("mushroomGiantBrown");
        tileNames[7][15].add("pumpkinVineAttached");
    
        //Row nine of terrain.png
        tileNames[8][0].add("railStraight");
        tileNames[8][1].add("woolRed");
        tileNames[8][2].add("woolPink");
        tileNames[8][3].add("repeaterOff");
        tileNames[8][4].add("leavesNeedlesFancy");
        tileNames[8][5].add("leavesNeedlesFast");
        tileNames[8][6].add("bedTopLower");
        tileNames[8][7].add("bedTopUpper");
        tileNames[8][8].add("melonSide");
        tileNames[8][9].add("melonTop");
        tileNames[8][10].add("caldronTop");
        tileNames[8][11].add("caldronBottom");
        tileNames[8][12].add("_blank0812");
        tileNames[8][13].add("mushroomGiantStem");
        tileNames[8][14].add("mushroomGiantInside");
        tileNames[8][15].add("vines");
    
        //Row ten of terrain.png
        tileNames[9][0].add("oreBlockLapis");
        tileNames[9][1].add("woolGreen");
        tileNames[9][2].add("woolGreenLime");
        tileNames[9][3].add("repeaterOn");
        tileNames[9][4].add("glassPaneEdge");
        tileNames[9][5].add("bedFoot");
        tileNames[9][6].add("bedSideLower");
        tileNames[9][7].add("bedSideUpper");
        tileNames[9][8].add("bedHead");
        tileNames[9][9].add("_blank0909");
        tileNames[9][10].add("caldronSide");
        tileNames[9][11].add("caldronFeet");
        tileNames[9][12].add("brewingStandBase");
        tileNames[9][13].add("brewingStandTop");
        tileNames[9][14].add("endPortalTop");
        tileNames[9][15].add("endPortalSide");
    
        //Row eleven of terrain.png
        tileNames[10][0].add("oreLapis");
        tileNames[10][1].add("woolBrown");
        tileNames[10][2].add("woolYellow");
        tileNames[10][3].add("railPoweredOff");
        tileNames[10][4].add("redstoneWireIntersection");
        tileNames[10][5].add("redstoneWireStraight");
        tileNames[10][6].add("enchantingTableTop");
        tileNames[10][7].add("dragonEgg");
        tileNames[10][8].add("_blank1008");
        tileNames[10][9].add("_blank1009");
        tileNames[10][10].add("_blank1010");
        tileNames[10][11].add("_blank1011");
        tileNames[10][12].add("_blank1012");
        tileNames[10][13].add("_blank1013");
        tileNames[10][14].add("endPortalEye");
        tileNames[10][15].add("endPortalBottom");
    
        //Row twelve of terrain.png
        tileNames[11][0].add("sandstoneTop");
        tileNames[11][1].add("woolBlue");
        tileNames[11][2].add("woolBlueLight");
        tileNames[11][3].add("railPoweredOn");
        tileNames[11][4].add("redstoneWireIntersectionGlow");
        tileNames[11][5].add("redstoneWireStraightGlow");
        tileNames[11][6].add("enchantingTableSide");
        tileNames[11][7].add("enchantingTableBottom");
        tileNames[11][8].add("_blank1108");
        tileNames[11][9].add("_blank1109");
        tileNames[11][10].add("_blank1110");
        tileNames[11][11].add("_blank1111");
        tileNames[11][12].add("_blank1112");
        tileNames[11][13].add("_blank1113");
        tileNames[11][14].add("_blank1114");
        tileNames[11][15].add("_blank1115");
    
        //Row thirteen of terrain.png
        tileNames[12][0].add("sandstoneSide");
        tileNames[12][1].add("woolPurple");
        tileNames[12][2].add("woolMagenta");
        tileNames[12][3].add("railDetector");
        tileNames[12][4].add("_blank1204");
        tileNames[12][5].add("_blank1205");
        tileNames[12][6].add("_blank1206");
        tileNames[12][7].add("_blank1207");
        tileNames[12][8].add("_blank1208");
        tileNames[12][9].add("_blank1209");
        tileNames[12][10].add("_blank1210");
        tileNames[12][11].add("_blank1211");
        tileNames[12][12].add("_blank1212");
        tileNames[12][13].add("water1");
        tileNames[12][14].add("water2");
        tileNames[12][15].add("water3");
    
        //Row fourteen of terrain.png
        tileNames[13][0].add("sandstoneBottom");
        tileNames[13][1].add("woolCyan");
        tileNames[13][2].add("woolOrange");
        tileNames[13][3].add("_blank1303");
        tileNames[13][4].add("_blank1304");
        tileNames[13][5].add("_blank1305");
        tileNames[13][6].add("_blank1306");
        tileNames[13][7].add("_blank1307");
        tileNames[13][8].add("_blank1308");
        tileNames[13][9].add("_blank1309");
        tileNames[13][10].add("_blank1310");
        tileNames[13][11].add("_blank1311");
        tileNames[13][12].add("_blank1312");
        tileNames[13][13].add("_blank1313");
        tileNames[13][14].add("water4");
        tileNames[13][15].add("water5");
    
        //Row fifteen of terrain.png
        tileNames[14][0].add("netherBrick");
        tileNames[14][1].add("woolGrayLight");
        tileNames[14][2].add("netherWart1");
        tileNames[14][3].add("netherWart2");
        tileNames[14][4].add("netherWart3");
        tileNames[14][5].add("_blank1405");
        tileNames[14][6].add("_blank1406");
        tileNames[14][7].add("_blank1407");
        tileNames[14][8].add("_blank1408");
        tileNames[14][9].add("_blank1409");
        tileNames[14][10].add("_blank1410");
        tileNames[14][11].add("_blank1411");
        tileNames[14][12].add("_blank1412");
        tileNames[14][13].add("lava1");
        tileNames[14][14].add("lava2");
        tileNames[14][15].add("lava3");
    
        //Row sixteen of terrain.png
        tileNames[15][0].add("breaking01");
        tileNames[15][1].add("breaking02");
        tileNames[15][2].add("breaking03");
        tileNames[15][3].add("breaking04");
        tileNames[15][4].add("breaking05");
        tileNames[15][5].add("breaking06");
        tileNames[15][6].add("breaking07");
        tileNames[15][7].add("breaking08");
        tileNames[15][8].add("breaking09");
        tileNames[15][9].add("breaking10");
        tileNames[15][10].add("_unknown2");
        tileNames[15][11].add("_unknown3");
        tileNames[15][12].add("_unknown4");
        tileNames[15][13].add("_unknown5");
        tileNames[15][14].add("lava4");
        tileNames[15][15].add("lava5");
    }
    
    public static void main(String[] args)
    {
        if (args.length == 0) {
            for (ArrayList<String>[] row : tileNames) {
                for (ArrayList<String> tile : row) {
                    if (tile.size() > 1) {
                        System.out.println("More than one name: " +
                                           tile.get(0));
                    }
                }
            }

            return;
        }
                
        
        BufferedImage inputImage;
        
        try
        {
            File inputFile = new File(args[0]);
            inputImage = ImageIO.read(inputFile);
        }
        catch(Exception ex)
        {
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

    private static void splitImage(BufferedImage inImage) {
        //split the texture
        for (int i = 0; i < tileNames.length; i++) {
            for (int j = 0; j < tileNames[i].length; j++) {
                BufferedImage outImage = inImage.getSubimage(
                    j * xTileSize, i * yTileSize, 64, 64);
        
                try
                {
                    File outFile = new File("temp\\" + tileNames[i][j].get(0) +
                        ".png");
                    ImageIO.write(outImage, "png", outFile);
                }
                catch(Exception ex)
                {
                    System.err.println("Critical failure attempting to write to file!");
                    return;
                }
            }
        }
    }
    private static void temp() {
        //Attempt to find the base file, exit program if failed.
        File baseFile = new File(baseLocation);
        try
        {
            base = ImageIO.read(baseFile);
        }
        catch(IOException exception)
        {
            return;
        }


            
        writeTexture();
    }
  
    public static void partPlacement(String partName, int xLoc, int yLoc) {
        int xStart = xLoc * xTileSize;
        int yStart = yLoc * yTileSize;
        int xPartMax = xTileSize;
        int yPartMax = yTileSize;                       
        BufferedImage part = new BufferedImage(xPartMax, yPartMax, BufferedImage.TYPE_INT_ARGB);
        File partFile = new File(workingDirectory + "\\" + partName + EXTENSION);

        try
        {
            part = ImageIO.read(partFile);
        }
        catch(IOException exception)
        {
            return;
        }
    
        for(int x = 0; x < xPartMax; x++)
        {
            for(int y = 0; y < yPartMax; y++)
            {
                int color = part.getRGB(x,y);
                base.setRGB(xStart + x, yStart + y, color);
            }
        }
        return;
    }
  
    public static void writeTexture()
    {
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
    }
}