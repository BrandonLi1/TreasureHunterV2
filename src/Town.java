import java.awt.Color;
/**
 * The Town Class is where it all happens.
 * The Town is designed to manage all the things a Hunter can do in town.
 * This code has been adapted from Ivan Turner's original program -- thank you Mr. Turner!
 */

public class Town {
    // instance variables
    private Hunter hunter;
    private Shop shop;
    private Terrain terrain;
    private String printMessage;
    private boolean toughTown;
    public String treasure;
    public static boolean easy;
    public static boolean fightWin;
    public static boolean fought=false;
    public static int count=0;

    /**
     * The Town Constructor takes in a shop and the surrounding terrain, but leaves the hunter as null until one arrives.
     *
     * @param shop The town's shoppe.
     * @param toughness The surrounding terrain.
     */
    public Town(Shop shop, double toughness) {
        this.shop = shop;
        this.terrain = getNewTerrain();

        // the hunter gets set using the hunterArrives method, which
        // gets called from a client class
        hunter = null;
        printMessage = "";

        // higher toughness = more likely to be a tough town
        toughTown = (Math.random() < toughness);
    }

    public Terrain getTerrain() {
        return terrain;
    }

    public String getLatestNews() {
        if (fightWin&&fought&&count>1) {
            printMessage="You won a brawl";
        }
        if (!fightWin&fought&&count>1) {
            printMessage="You lost a brawl";
        }
        count++;
        return printMessage;
    }

    /**
     * Assigns an object to the Hunter in town.
     *
     * @param hunter The arriving Hunter.
     */
    public void hunterArrives(Hunter hunter) {
        this.hunter = hunter;
        printMessage = "Welcome to town, " + hunter.getHunterName() + ".";
        if (toughTown) {
            printMessage += "\nIt's pretty rough around here, so watch yourself.";
        } else {
            printMessage += "\nWe're just a sleepy little town with mild mannered folk.";
        }
    }

    /**
     * Handles the action of the Hunter leaving the town.
     *
     * @return true if the Hunter was able to leave town.
     */
    public boolean leaveTown() {
        boolean canLeaveTown = terrain.canCrossTerrain(hunter);
        if (canLeaveTown) {
            String item = terrain.getNeededItem();
            printMessage = "You used your " + item + " to cross the " + Colors.CYAN + terrain.getTerrainName() + "." + Colors.RESET;
            if (checkItemBreak()) {
                hunter.removeItemFromKit(item);
                printMessage += "\nUnfortunately, you lost your " + item + ".";
            }
            return true;
        }

        printMessage = "You can't leave town, " + hunter.getHunterName() + ". You don't have a " + terrain.getNeededItem() + ".";
        return false;
    }

    /**
     * Handles calling the enter method on shop whenever the user wants to access the shop.
     *
     * @param choice If the user wants to buy or sell items at the shop.
     */
    public void enterShop(String choice) {
        printMessage = shop.enter(hunter, choice);
    }

    /**
     * Gives the hunter a chance to fight for some gold.<p>
     * The chances of finding a fight and winning the gold are based on the toughness of the town.<p>
     * The tougher the town, the easier it is to find a fight, and the harder it is to win one.
     */
    public void lookForTrouble() {
        double noTroubleChance;
        if (toughTown) {
            noTroubleChance = 0.66;
        } else {
            noTroubleChance = 0.33;
        }
        if (easy) {
            noTroubleChance=.2;
        }
        fought=false;
        count=0;
        if (Math.random() > noTroubleChance) {
            printMessage = "You couldn't find any trouble";
            fought=false;
            count=0;
        } else {
            fought=true;
            count++;
            printMessage = Colors.RED + "You want trouble, stranger!  You got it!\nOof! Umph! Ow!\n" + Colors.RESET;
            int goldDiff = (int) (Math.random() * 10) + 1;
            if (shop.sword){
                printMessage += Colors.RED + "What you're a samurai? I am terribly sorry. Here, take my gold as payment." + Colors.RESET;
                printMessage += "\nYou won the brawl and receive " + Colors.YELLOW + goldDiff + " gold." + Colors.RESET;
                fightWin=true;
                hunter.changeGold(goldDiff);
            } else {
                if (Math.random() > noTroubleChance) {
                    printMessage += Colors.RED + "Okay, stranger! You proved yer mettle. Here, take my gold." + Colors.RESET;
                    printMessage += "\nYou won the brawl and receive " + Colors.YELLOW + goldDiff + " gold." + Colors.RESET;
                    fightWin=true;
                    hunter.changeGold(goldDiff);
                } else {
                    printMessage += Colors.RED + "That'll teach you to go lookin' fer trouble in MY town! Now pay up!" + Colors.RESET;
                    printMessage += "\nYou lost the brawl and pay " + goldDiff + " gold.";
                    hunter.changeGold(-goldDiff);
                    fightWin=false;
                }
            }
        }
    }

    public String infoString() {
        return "This nice little town is surrounded by " + terrain.getTerrainName() + ".";
    }

    /**
     * Determines the surrounding terrain for a town, and the item needed in order to cross that terrain.
     *
     * @return A Terrain object.
     */
    private Terrain getNewTerrain() {
        double rnd = (int)(Math.random() * 6 + 1);
        fought=false;
        count=0;
        if (rnd == 1) {
            treasure();
            TreasureHunter.canDig = true;
            return new Terrain("Mountains", "Rope");
        } else if (rnd == 2) {
            treasure();
            TreasureHunter.canDig = true;
            return new Terrain("Ocean", "Boat");
        } else if (rnd == 3) {
            treasure();
            TreasureHunter.canDig = true;
            return new Terrain("Plains", "Horse");
        } else if (rnd == 4) {
            treasure();
            TreasureHunter.canDig = true;
            return new Terrain("Desert", "Water");
        } else if (rnd == 5){
            treasure();
            TreasureHunter.canDig = true;
            return new Terrain("Jungle", "Machete");
        } else {
            treasure();
            TreasureHunter.canDig = true;
            return new Terrain("Marsh", "boots");
        }
    }

    /**
     * Determines whether a used item has broken.
     *
     * @return true if the item broke   .
     */
    private boolean checkItemBreak() {
        double rand = Math.random();
        if (easy) {
          rand=1;
        }
        return (rand < 0.5);
    }

    private void treasure() {
        int x=(int)(Math.random()*7);
        if (x==0) {
            treasure="crown";
        } else if (x==1) {
            treasure="trophy";
        } else if (x==2) {
            treasure="gem";
        } else {
            treasure="dust";
        }
    }
}