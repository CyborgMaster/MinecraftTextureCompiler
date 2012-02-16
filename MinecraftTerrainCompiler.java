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
    private static final int xDefaultSize = 64;
    //The default height of tile pieces.
    private static final int yDefaultSize = 64;
    //The number of tiles in the x/y axis
    private static final int xTiles = 16;
    private static final int yTiles = 16;
    //The default height of the base and output files.
    private static final int xTotalSize = xTiles * xDefaultSize;
    //The default width of the base and output files.
    private static final int yTotalSize = yTiles * yDefaultSize;
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
        tileNames[0][3].add("grassydirt");
        tileNames[0][4].add("planks");
        tileNames[0][5].add("stepsides");
        tileNames[0][6].add("steptop");
        tileNames[0][7].add("brick");
        tileNames[0][8].add("tnt");
        tileNames[0][8].add("tntside");
        tileNames[0][9].add("tnttop");
        tileNames[0][10].add("tntbottom");
        tileNames[0][11].add("web");
        tileNames[0][12].add("redflower");
        tileNames[0][13].add("yellowflower");
        tileNames[0][14].add("portal");
        tileNames[0][15].add("sapling");

        //Row two of terrain.png
        tileNames[1][0].add("cobblestone");
        tileNames[1][1].add("bedrock");
        tileNames[1][2].add("sand");
        tileNames[1][3].add("gravel");
        tileNames[1][4].add("log");
        tileNames[1][4].add("logside");
        tileNames[1][5].add("logtop");
        tileNames[1][6].add("iron");
        tileNames[1][6].add("irontop");
        tileNames[1][7].add("gold");
        tileNames[1][7].add("goldtop");
        tileNames[1][8].add("diamond");
        tileNames[1][8].add("diamondtop");
        tileNames[1][9].add("chest");
        tileNames[1][9].add("smallchest");
        tileNames[1][9].add("smallchesttop");
        tileNames[1][10].add("smallchestside");
        tileNames[1][11].add("smallchestfront");
        tileNames[1][12].add("redmushroom");
        tileNames[1][13].add("brownmushroom");
        tileNames[1][14].add("blank001");
        tileNames[1][15].add("fire");
    
        //Row three of terrain.png
        tileNames[2][0].add("goldore");
        tileNames[2][1].add("ironore");
        tileNames[2][2].add("coalore");
        tileNames[2][3].add("bookshelf");
        tileNames[2][4].add("mossycobblestone");
        tileNames[2][5].add("obsidian");
        tileNames[2][6].add("ironside");
        tileNames[2][7].add("goldside");
        tileNames[2][8].add("diamondside");
        tileNames[2][9].add("largechest");
        tileNames[2][9].add("largechestfront");
        tileNames[2][9].add("largechestfrontleft");
        tileNames[2][10].add("largechestfrontright");
        tileNames[2][11].add("workbench");
        tileNames[2][11].add("workbenchtopside");
        tileNames[2][11].add("workbenchtop");
        tileNames[2][12].add("furnace");
        tileNames[2][12].add("furnacefrontside");
        tileNames[2][12].add("furnacefront");
        tileNames[2][13].add("furnaceside");
        tileNames[2][14].add("blank002");
        tileNames[2][15].add("blank003");
    
        //Row four of terrain.png
        tileNames[3][0].add("sponge");
        tileNames[3][1].add("glass");
        tileNames[3][2].add("diamondore");
        tileNames[3][3].add("redstoneore");
        tileNames[3][4].add("leavesandmask");
        tileNames[3][4].add("leaves");
        tileNames[3][5].add("leavesmask");
        tileNames[3][6].add("ironbottom");
        tileNames[3][7].add("goldbottom");
        tileNames[3][8].add("diamondbottom");
        tileNames[3][9].add("largechestback");
        tileNames[3][9].add("largechestbackleft");
        tileNames[3][10].add("largechestbackright");
        tileNames[3][11].add("workbenchsides");
        tileNames[3][11].add("workbenchside1");
        tileNames[3][12].add("workbenchside2");
        tileNames[3][13].add("furnacelit");
        tileNames[3][14].add("blank004");
        tileNames[3][15].add("blank005");
    
        //Row five of terrain.png
        tileNames[4][0].add("wool");
        tileNames[4][1].add("mobspawner");
        tileNames[4][2].add("snow");
        tileNames[4][3].add("ice");
        tileNames[4][4].add("snowydirt");
        tileNames[4][5].add("cactus");
        tileNames[4][5].add("cactustop");
        tileNames[4][6].add("cactusside");
        tileNames[4][7].add("cactusinside");
        tileNames[4][8].add("clay");
        tileNames[4][9].add("reeds");
        tileNames[4][10].add("jukebox");
        tileNames[4][10].add("jukeboxside");
        tileNames[4][11].add("jukeboxtop");
        tileNames[4][12].add("blank006");
        tileNames[4][13].add("blank007");
        tileNames[4][14].add("blank008");
        tileNames[4][15].add("blank009");
    
        //Row six of terrain.png
        tileNames[5][0].add("torch");
        tileNames[5][1].add("woodendoor");
        tileNames[5][1].add("woodendoortop");
        tileNames[5][2].add("irondoor");
        tileNames[5][2].add("irondoortop");
        tileNames[5][3].add("ladder");
        tileNames[5][4].add("redstone");
        tileNames[5][4].add("redstoneoff");
        tileNames[5][4].add("redstoneintersectoff");
        tileNames[5][5].add("redstonestraightoff");
        tileNames[5][6].add("tilleddirt");
        tileNames[5][6].add("freshtilleddirt");
        tileNames[5][7].add("messytilleddirt");
        tileNames[5][8].add("wheat");
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
        tileNames[6][1].add("woodendoorbottom");
        tileNames[6][2].add("irondoorbottom");
        tileNames[6][3].add("redstonetorch");
        tileNames[6][3].add("redstonetorchon");
        tileNames[6][4].add("redstoneon");
        tileNames[6][4].add("redstoneintersecton");
        tileNames[6][5].add("redstonestraighton");
        tileNames[6][6].add("pumpkin");
        tileNames[6][6].add("pumpkintop");
        tileNames[6][7].add("netherrack");
        tileNames[6][8].add("soulsand");
        tileNames[6][9].add("glowstone");
        tileNames[6][10].add("blank010");
        tileNames[6][11].add("blank011");
        tileNames[6][12].add("blank012");
        tileNames[6][13].add("blank013");
        tileNames[6][14].add("blank014");
        tileNames[6][15].add("blank015");
    
        //Row eight of terrain.png
        tileNames[7][0].add("rail");
        tileNames[7][0].add("curvedrail");
        tileNames[7][1].add("blank016");
        tileNames[7][2].add("blank017");
        tileNames[7][3].add("redstonetorchoff");
        tileNames[7][4].add("blank018");
        tileNames[7][5].add("blank019");
        tileNames[7][6].add("pumpkinside");
        tileNames[7][7].add("pumpkinfaceoff");
        tileNames[7][8].add("pumpkinfaceon");
        tileNames[7][9].add("blank020");
        tileNames[7][10].add("blank021");
        tileNames[7][11].add("blank022");
        tileNames[7][12].add("blank023");
        tileNames[7][13].add("blank024");
        tileNames[7][14].add("blank025");
        tileNames[7][15].add("blank026");
    
        //Row nine of terrain.png
        tileNames[8][0].add("straightrail");
        tileNames[8][1].add("blank027");
        tileNames[8][2].add("blank028");
        tileNames[8][3].add("blank029");
        tileNames[8][4].add("blank030");
        tileNames[8][5].add("blank031");
        tileNames[8][6].add("blank032");
        tileNames[8][7].add("blank033");
        tileNames[8][8].add("blank034");
        tileNames[8][9].add("blank035");
        tileNames[8][10].add("blank036");
        tileNames[8][11].add("blank037");
        tileNames[8][12].add("blank038");
        tileNames[8][13].add("blank039");
        tileNames[8][14].add("blank040");
        tileNames[8][15].add("blank041");
    
        //Row ten of terrain.png
        tileNames[9][0].add("blank042");
        tileNames[9][1].add("blank043");
        tileNames[9][2].add("blank044");
        tileNames[9][3].add("blank045");
        tileNames[9][4].add("blank046");
        tileNames[9][5].add("blank047");
        tileNames[9][6].add("blank048");
        tileNames[9][7].add("blank049");
        tileNames[9][8].add("blank050");
        tileNames[9][9].add("blank051");
        tileNames[9][10].add("blank052");
        tileNames[9][11].add("blank053");
        tileNames[9][12].add("blank054");
        tileNames[9][13].add("blank055");
        tileNames[9][14].add("blank056");
        tileNames[9][15].add("blank057");
    
        //Row eleven of terrain.png
        tileNames[10][0].add("blank058");
        tileNames[10][1].add("blank059");
        tileNames[10][2].add("blank060");
        tileNames[10][3].add("blank061");
        tileNames[10][4].add("blank062");
        tileNames[10][5].add("blank063");
        tileNames[10][6].add("blank064");
        tileNames[10][7].add("blank065");
        tileNames[10][8].add("blank066");
        tileNames[10][9].add("blank067");
        tileNames[10][10].add("blank068");
        tileNames[10][11].add("blank069");
        tileNames[10][12].add("blank070");
        tileNames[10][13].add("blank071");
        tileNames[10][14].add("blank072");
        tileNames[10][15].add("blank073");
    
        //Row twelve of terrain.png
        tileNames[11][0].add("blank074");
        tileNames[11][1].add("blank075");
        tileNames[11][2].add("blank076");
        tileNames[11][3].add("blank077");
        tileNames[11][4].add("blank078");
        tileNames[11][5].add("blank079");
        tileNames[11][6].add("blank080");
        tileNames[11][7].add("blank081");
        tileNames[11][8].add("blank082");
        tileNames[11][9].add("blank083");
        tileNames[11][10].add("blank084");
        tileNames[11][11].add("blank085");
        tileNames[11][12].add("blank086");
        tileNames[11][13].add("blank087");
        tileNames[11][14].add("blank088");
        tileNames[11][15].add("blank089");
    
        //Row thirteen of terrain.png
        tileNames[12][0].add("blank090");
        tileNames[12][1].add("blank091");
        tileNames[12][2].add("blank092");
        tileNames[12][3].add("blank093");
        tileNames[12][4].add("blank094");
        tileNames[12][5].add("blank095");
        tileNames[12][6].add("blank096");
        tileNames[12][7].add("blank097");
        tileNames[12][8].add("blank098");
        tileNames[12][9].add("blank099");
        tileNames[12][10].add("blank100");
        tileNames[12][11].add("blank101");
        tileNames[12][12].add("blank102");
        tileNames[12][13].add("water");
        tileNames[12][13].add("water1");
        tileNames[12][14].add("water2");
        tileNames[12][15].add("water3");
    
        //Row fourteen of terrain.png
        tileNames[13][0].add("blank103");
        tileNames[13][1].add("blank104");
        tileNames[13][2].add("blank105");
        tileNames[13][3].add("blank106");
        tileNames[13][4].add("blank107");
        tileNames[13][5].add("blank108");
        tileNames[13][6].add("blank109");
        tileNames[13][7].add("blank110");
        tileNames[13][8].add("blank111");
        tileNames[13][9].add("blank112");
        tileNames[13][10].add("blank113");
        tileNames[13][11].add("blank114");
        tileNames[13][12].add("blank115");
        tileNames[13][13].add("blank116");
        tileNames[13][14].add("water4");
        tileNames[13][15].add("water5");
    
        //Row fifteen of terrain.png
        tileNames[14][0].add("blank117");
        tileNames[14][1].add("blank118");
        tileNames[14][2].add("blank119");
        tileNames[14][3].add("blank120");
        tileNames[14][4].add("blank121");
        tileNames[14][5].add("blank122");
        tileNames[14][6].add("blank123");
        tileNames[14][7].add("blank124");
        tileNames[14][8].add("blank125");
        tileNames[14][9].add("blank126");
        tileNames[14][10].add("blank127");
        tileNames[14][11].add("blank128");
        tileNames[14][12].add("blank129");
        tileNames[14][13].add("lava");
        tileNames[14][13].add("lava1");
        tileNames[14][14].add("lava2");
        tileNames[14][15].add("lava3");
    
        //Row sixteen of terrain.png
        tileNames[15][0].add("breakingblock");
        tileNames[15][0].add("breakingblock01");
        tileNames[15][1].add("breakingblock02");
        tileNames[15][2].add("breakingblock03");
        tileNames[15][3].add("breakingblock04");
        tileNames[15][4].add("breakingblock05");
        tileNames[15][5].add("breakingblock06");
        tileNames[15][6].add("breakingblock07");
        tileNames[15][7].add("breakingblock08");
        tileNames[15][8].add("breakingblock09");
        tileNames[15][9].add("breakingblock10");
        tileNames[15][10].add("blank130");
        tileNames[15][11].add("blank131");
        tileNames[15][12].add("blank132");
        tileNames[15][13].add("blank133");
        tileNames[15][14].add("lava4");
        tileNames[15][15].add("lava5");
    }
    
    public static void main(String[] args)
    {
        //try and split the texture
        for (int i = 0; i < tileNames.length; i++) {
            for (int j = 0; j < tileNames[i].length; j++) {
                System.out.println(tileNames[i][j].get(0));
            }
        }

        return;

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
        int xStart = xLoc * xDefaultSize;
        int yStart = yLoc * yDefaultSize;
        int xPartMax = xDefaultSize;
        int yPartMax = yDefaultSize;                       
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