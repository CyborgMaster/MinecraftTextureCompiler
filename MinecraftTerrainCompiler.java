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
    private static final String baseLocation = workingDirectory + "\\" +
        baseName + EXTENSION;
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
    private static BufferedImage base = new BufferedImage(
        xTotalSize, yTotalSize, BufferedImage.TYPE_INT_ARGB);

    private static final String[][] tileNames = new String[yTiles][xTiles];

    static {
        //Row one of terrain.png
        tileNames[0][0] = "grass";
        tileNames[0][1] = "stone";
        tileNames[0][2] = "dirt";
        tileNames[0][3] = "grassyDirt";
        tileNames[0][4] = "planks";
        tileNames[0][5] = "stoneSlabSides";
        tileNames[0][6] = "stoneSlabTop";
        tileNames[0][7] = "brick";
        tileNames[0][8] = "tntSide";
        tileNames[0][9] = "tntTop";
        tileNames[0][10] = "tntBottom";
        tileNames[0][11] = "spiderWeb";
        tileNames[0][12] = "flowerRed";
        tileNames[0][13] = "flowerYellow";
        tileNames[0][14] = "portal";
        tileNames[0][15] = "saplingOak";

        //Row two of terrain.png
        tileNames[1][0] = "cobblestone";
        tileNames[1][1] = "bedrock";
        tileNames[1][2] = "sand";
        tileNames[1][3] = "gravel";
        tileNames[1][4] = "logOak";
        tileNames[1][5] = "logTop";
        tileNames[1][6] = "oreBlockIron";
        tileNames[1][7] = "oreBlockGold";
        tileNames[1][8] = "oreBlockDiamond";
        tileNames[1][9] = "chestSmallTop";
        tileNames[1][10] = "chestSmallSide";
        tileNames[1][11] = "chestSmallFront";
        tileNames[1][12] = "mushroomRed";
        tileNames[1][13] = "mushroomBrown";
        tileNames[1][14] = "_blank0114";
        tileNames[1][15] = "fire1";
    
        //Row three of terrain.png
        tileNames[2][0] = "oreGold";
        tileNames[2][1] = "oreIron";
        tileNames[2][2] = "oreCoal";
        tileNames[2][3] = "bookshelf";
        tileNames[2][4] = "cobblestoneMossy";
        tileNames[2][5] = "obsidian";
        tileNames[2][6] = "grassSide";
        tileNames[2][7] = "tallGrass";
        tileNames[2][8] = "_unknown1";
        tileNames[2][9] = "chestLargeFrontLeft";
        tileNames[2][10] = "chestLargeFrontRight";
        tileNames[2][11] = "workbenchTop";
        tileNames[2][12] = "furnaceFront";
        tileNames[2][13] = "furnaceSide";
        tileNames[2][14] = "dispenser";
        tileNames[2][15] = "fire2";
    
        //Row four of terrain.png
        tileNames[3][0] = "sponge";
        tileNames[3][1] = "glass";
        tileNames[3][2] = "oreDiamond";
        tileNames[3][3] = "oreRedstone";
        tileNames[3][4] = "leavesNormalFancy";
        tileNames[3][5] = "leavesNormalFast";
        tileNames[3][6] = "stoneBrick";
        tileNames[3][7] = "deadShrub";
        tileNames[3][8] = "tallGrass2";
        tileNames[3][9] = "chestLargeBackLeft";
        tileNames[3][10] = "chestLargeBackRight";
        tileNames[3][11] = "workbenchSide1";
        tileNames[3][12] = "workbenchSide2";
        tileNames[3][13] = "furnaceLit";
        tileNames[3][14] = "furnaceTop";
        tileNames[3][15] = "saplingPine";
    
        //Row five of terrain.png
        tileNames[4][0] = "woolWhite";
        tileNames[4][1] = "mobSpawner";
        tileNames[4][2] = "snow";
        tileNames[4][3] = "ice";
        tileNames[4][4] = "snowyDirt";
        tileNames[4][5] = "cactusTop";
        tileNames[4][6] = "cactusSide";
        tileNames[4][7] = "cactusInside";
        tileNames[4][8] = "clay";
        tileNames[4][9] = "reeds";
        tileNames[4][10] = "jukeboxSide";
        tileNames[4][11] = "jukeboxTop";
        tileNames[4][12] = "lilyPad";
        tileNames[4][13] = "myceliumSide";
        tileNames[4][14] = "myceliumTop";
        tileNames[4][15] = "saplingBirch";
    
        //Row six of terrain.png
        tileNames[5][0] = "torch";
        tileNames[5][1] = "doorWoodTop";
        tileNames[5][2] = "doorIronTop";
        tileNames[5][3] = "ladder";
        tileNames[5][4] = "trapdoor";
        tileNames[5][5] = "ironBars";
        tileNames[5][6] = "farmlandWet";
        tileNames[5][7] = "farmlandDry";
        tileNames[5][8] = "wheat1";
        tileNames[5][9] = "wheat2";
        tileNames[5][10] = "wheat3";
        tileNames[5][11] = "wheat4";
        tileNames[5][12] = "wheat5";
        tileNames[5][13] = "wheat6";
        tileNames[5][14] = "wheat7";
        tileNames[5][15] = "wheat8";
    
        //Row seven of terrain.png
        tileNames[6][0] = "lever";
        tileNames[6][1] = "doorWoodBottom";
        tileNames[6][2] = "doorIronBottom";
        tileNames[6][3] = "redstoneTorchOn";
        tileNames[6][4] = "stoneBrickMossy";
        tileNames[6][5] = "stoneBrickCracked";
        tileNames[6][6] = "pumpkinTop";
        tileNames[6][7] = "netherrack";
        tileNames[6][8] = "soulSand";
        tileNames[6][9] = "glowstone";
        tileNames[6][10] = "pistonSticky";
        tileNames[6][11] = "piston";
        tileNames[6][12] = "pistonSide";
        tileNames[6][13] = "pistonBack";
        tileNames[6][14] = "pistonFront";
        tileNames[6][15] = "pumpkinVine";
    
        //Row eight of terrain.png
        tileNames[7][0] = "railCurved";
        tileNames[7][1] = "woolBlack";
        tileNames[7][2] = "woolGray";
        tileNames[7][3] = "redstoneTorchOff";
        tileNames[7][4] = "logPine";
        tileNames[7][5] = "logBirch";
        tileNames[7][6] = "pumpkinSide";
        tileNames[7][7] = "pumpkinFaceOff";
        tileNames[7][8] = "pumpkinFaceOn";
        tileNames[7][9] = "cakeTop";
        tileNames[7][10] = "cakeSide";
        tileNames[7][11] = "cakeInside";
        tileNames[7][12] = "cakeBottom";
        tileNames[7][13] = "mushroomGiantRed";
        tileNames[7][14] = "mushroomGiantBrown";
        tileNames[7][15] = "pumpkinVineAttached";
    
        //Row nine of terrain.png
        tileNames[8][0] = "railStraight";
        tileNames[8][1] = "woolRed";
        tileNames[8][2] = "woolPink";
        tileNames[8][3] = "repeaterOff";
        tileNames[8][4] = "leavesNeedlesFancy";
        tileNames[8][5] = "leavesNeedlesFast";
        tileNames[8][6] = "bedTopLower";
        tileNames[8][7] = "bedTopUpper";
        tileNames[8][8] = "melonSide";
        tileNames[8][9] = "melonTop";
        tileNames[8][10] = "caldronTop";
        tileNames[8][11] = "caldronBottom";
        tileNames[8][12] = "_blank0812";
        tileNames[8][13] = "mushroomGiantStem";
        tileNames[8][14] = "mushroomGiantInside";
        tileNames[8][15] = "vines";
    
        //Row ten of terrain.png
        tileNames[9][0] = "oreBlockLapis";
        tileNames[9][1] = "woolGreen";
        tileNames[9][2] = "woolGreenLime";
        tileNames[9][3] = "repeaterOn";
        tileNames[9][4] = "glassPaneEdge";
        tileNames[9][5] = "bedFoot";
        tileNames[9][6] = "bedSideLower";
        tileNames[9][7] = "bedSideUpper";
        tileNames[9][8] = "bedHead";
        tileNames[9][9] = "_blank0909";
        tileNames[9][10] = "caldronSide";
        tileNames[9][11] = "caldronFeet";
        tileNames[9][12] = "brewingStandBase";
        tileNames[9][13] = "brewingStandTop";
        tileNames[9][14] = "endPortalTop";
        tileNames[9][15] = "endPortalSide";
    
        //Row eleven of terrain.png
        tileNames[10][0] = "oreLapis";
        tileNames[10][1] = "woolBrown";
        tileNames[10][2] = "woolYellow";
        tileNames[10][3] = "railPoweredOff";
        tileNames[10][4] = "redstoneWireIntersection";
        tileNames[10][5] = "redstoneWireStraight";
        tileNames[10][6] = "enchantingTableTop";
        tileNames[10][7] = "dragonEgg";
        tileNames[10][8] = "_blank1008";
        tileNames[10][9] = "_blank1009";
        tileNames[10][10] = "_blank1010";
        tileNames[10][11] = "_blank1011";
        tileNames[10][12] = "_blank1012";
        tileNames[10][13] = "_blank1013";
        tileNames[10][14] = "endPortalEye";
        tileNames[10][15] = "endPortalBottom";
    
        //Row twelve of terrain.png
        tileNames[11][0] = "sandstoneTop";
        tileNames[11][1] = "woolBlue";
        tileNames[11][2] = "woolBlueLight";
        tileNames[11][3] = "railPoweredOn";
        tileNames[11][4] = "redstoneWireIntersectionGlow";
        tileNames[11][5] = "redstoneWireStraightGlow";
        tileNames[11][6] = "enchantingTableSide";
        tileNames[11][7] = "enchantingTableBottom";
        tileNames[11][8] = "_blank1108";
        tileNames[11][9] = "_blank1109";
        tileNames[11][10] = "_blank1110";
        tileNames[11][11] = "_blank1111";
        tileNames[11][12] = "_blank1112";
        tileNames[11][13] = "_blank1113";
        tileNames[11][14] = "_blank1114";
        tileNames[11][15] = "_blank1115";
    
        //Row thirteen of terrain.png
        tileNames[12][0] = "sandstoneSide";
        tileNames[12][1] = "woolPurple";
        tileNames[12][2] = "woolMagenta";
        tileNames[12][3] = "railDetector";
        tileNames[12][4] = "_blank1204";
        tileNames[12][5] = "_blank1205";
        tileNames[12][6] = "_blank1206";
        tileNames[12][7] = "_blank1207";
        tileNames[12][8] = "_blank1208";
        tileNames[12][9] = "_blank1209";
        tileNames[12][10] = "_blank1210";
        tileNames[12][11] = "_blank1211";
        tileNames[12][12] = "_blank1212";
        tileNames[12][13] = "water1";
        tileNames[12][14] = "water2";
        tileNames[12][15] = "water3";
    
        //Row fourteen of terrain.png
        tileNames[13][0] = "sandstoneBottom";
        tileNames[13][1] = "woolCyan";
        tileNames[13][2] = "woolOrange";
        tileNames[13][3] = "_blank1303";
        tileNames[13][4] = "_blank1304";
        tileNames[13][5] = "_blank1305";
        tileNames[13][6] = "_blank1306";
        tileNames[13][7] = "_blank1307";
        tileNames[13][8] = "_blank1308";
        tileNames[13][9] = "_blank1309";
        tileNames[13][10] = "_blank1310";
        tileNames[13][11] = "_blank1311";
        tileNames[13][12] = "_blank1312";
        tileNames[13][13] = "_blank1313";
        tileNames[13][14] = "water4";
        tileNames[13][15] = "water5";
    
        //Row fifteen of terrain.png
        tileNames[14][0] = "netherBrick";
        tileNames[14][1] = "woolGrayLight";
        tileNames[14][2] = "netherWart1";
        tileNames[14][3] = "netherWart2";
        tileNames[14][4] = "netherWart3";
        tileNames[14][5] = "_blank1405";
        tileNames[14][6] = "_blank1406";
        tileNames[14][7] = "_blank1407";
        tileNames[14][8] = "_blank1408";
        tileNames[14][9] = "_blank1409";
        tileNames[14][10] = "_blank1410";
        tileNames[14][11] = "_blank1411";
        tileNames[14][12] = "_blank1412";
        tileNames[14][13] = "lava1";
        tileNames[14][14] = "lava2";
        tileNames[14][15] = "lava3";
    
        //Row sixteen of terrain.png
        tileNames[15][0] = "breaking01";
        tileNames[15][1] = "breaking02";
        tileNames[15][2] = "breaking03";
        tileNames[15][3] = "breaking04";
        tileNames[15][4] = "breaking05";
        tileNames[15][5] = "breaking06";
        tileNames[15][6] = "breaking07";
        tileNames[15][7] = "breaking08";
        tileNames[15][8] = "breaking09";
        tileNames[15][9] = "breaking10";
        tileNames[15][10] = "_unknown2";
        tileNames[15][11] = "_unknown3";
        tileNames[15][12] = "_unknown4";
        tileNames[15][13] = "_unknown5";
        tileNames[15][14] = "lava4";
        tileNames[15][15] = "lava5";
    }
    
    public static void main(String[] args)
    {
        if (args.length == 0) {
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
                    File outFile = new File("temp\\" + tileNames[i][j] +
                                            ".png");
                    ImageIO.write(outImage, "png", outFile);
                }
                catch(Exception ex)
                {
                    System.err.println("Error writing tile to file!");
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