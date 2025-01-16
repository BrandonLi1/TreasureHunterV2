import java.util.Scanner;
import java.awt.Color;

/**
 * This class is responsible for controlling the Treasure Hunter game.<p>
 * It handles the display of the menu and the processing of the player's choices.<p>
 * It handles all the display based on the messages it receives from the Town object. <p>
 *
 * This code has been adapted from Ivan Turner's original program -- thank you Mr. Turner!
 */

public class TreasureHunter {
    // static variables
    public static OutputWindow window = new OutputWindow();
    private static final Scanner SCANNER = new Scanner(System.in);

    // instance variables
    private Town currentTown;
    private Hunter hunter;
    private boolean hardMode;
    private boolean easyMode;
    public static boolean canDig = true;
    public static boolean samurai = false;
    private int temp;

    /**
     * Constructs the Treasure Hunter game.
     */
    public TreasureHunter() {
        // these will be initialized in the play method
        currentTown = null;
        hunter = null;
        hardMode = false;
    }

    /**
     * Starts the game; this is the only public method
     */
    public void play() {
        welcomePlayer();
        enterTown();
        showMenu();
    }

    /**
     * Creates a hunter object at the beginning of the game and populates the class member variable with it.
     */
    private void welcomePlayer() {
        window.clear();
        window.addTextToWindow( "Welcome to TREASURE HUNTER! \n", Color.cyan);
        window.addTextToWindow("Going hunting for the big treasure, eh?\n", Color.black);
        window.addTextToWindow("What's your name, Hunter?\n ", Color.black);
        String name = SCANNER.nextLine().toLowerCase();

        // set hunter instance variable
        hunter = new Hunter(name, 20);

        window.addTextToWindow("Hard mode? (y/n/e): \n", Color.black);
        String hard = SCANNER.nextLine().toLowerCase();
        if (hard.equals("y")) {
            hardMode = true;
        }
        if (hard.equals("test")) {
            hunter.changeGold(80);
            hunter.addItem("water");
            hunter.addItem("rope");
            hunter.addItem("boots");
            hunter.addItem("machete");
            hunter.addItem("horse");
            hunter.addItem("boat");
            hunter.addItem("shovel");
        }
        if (hard.equals("e")) {
            easyMode=true;
            Town.easy=true;
        }
        if (hard.equals("s")){
            samurai = true;
        }
    }

    /**
     * Creates a new town and adds the Hunter to it.
     */
    private void enterTown() {
        double markdown = 0.5;
        double toughness = 0.4;
        if (hardMode) {
            // in hard mode, you get less money back when you sell items
            markdown = 0.25;

            // and the town is "tougher"
            toughness = 0.75;
        }
        if (easyMode) {
            hunter.changeGold(20);
            markdown=1;
        }

        // note that we don't need to access the Shop object
        // outside of this method, so it isn't necessary to store it as an instance
        // variable; we can leave it as a local variable
        Shop shop = new Shop(markdown);

        // creating the new Town -- which we need to store as an instance
        // variable in this class, since we need to access the Town
        // object in other methods of this class
        currentTown = new Town(shop, toughness);

        // calling the hunterArrives method, which takes the Hunter
        // as a parameter; note this also could have been done in the
        // constructor for Town, but this illustrates another way to associate
        // an object with an object of a different class
        currentTown.hunterArrives(hunter);
    }

    /**
     * Displays the menu and receives the choice from the user.<p>
     * The choice is sent to the processChoice() method for parsing.<p>
     * This method will loop until the user chooses to exit.
     */
    private void showMenu() {
        String choice = "";
        while (!choice.equals("x")) {

            TreasureHunter.window.addTextToWindow(currentTown.getLatestNews() + "\n", Color.black);
            TreasureHunter.window.addTextToWindow("***", Color.black);
            TreasureHunter.window.addTextToWindow(hunter.infoString() + "\n", Color.black);
            TreasureHunter.window.addTextToWindow(hunter.treasureString() + "\n", Color.black);
            TreasureHunter.window.addTextToWindow(currentTown.infoString() + "\n", Color.black);
            TreasureHunter.window.addTextToWindow("(B)uy something at the shop.\n", Color.black);
            TreasureHunter.window.addTextToWindow("(S)ell something at the shop.\n", Color.black);
            TreasureHunter.window.addTextToWindow("(E)xplore surrounding terrain.\n", Color.black);
            TreasureHunter.window.addTextToWindow("(M)ove on to a different town.\n", Color.black);
            TreasureHunter.window.addTextToWindow("(L)ook for trouble!\n", Color.black);
            TreasureHunter.window.addTextToWindow("(D)ig for gold\n", Color.black);
            TreasureHunter.window.addTextToWindow("(H)unt for treasure!\n", Color.black);
            TreasureHunter.window.addTextToWindow("Give up the hunt and e(X)it.\n", Color.black);
            TreasureHunter.window.addTextToWindow("\n", Color.black);
            TreasureHunter.window.addTextToWindow("What's your next move? \n", Color.black);
            choice = SCANNER.nextLine().toLowerCase();
            processChoice(choice);
        }
    }

    /**
     * Takes the choice received from the menu and calls the appropriate method to carry out the instructions.
     * @param choice The action to process.
     */
    public void processChoice(String choice) {
        if (choice.equals("b") || choice.equals("s")) {
            currentTown.enterShop(choice);
        } else if (choice.equals("e")) {
            TreasureHunter.window.addTextToWindow(currentTown.getTerrain().infoString(), Color.black);
        } else if (choice.equals("m")) {
            if (currentTown.leaveTown()) {
                Town.count=0;
                Town.fought=false;
                // This town is going away so print its news ahead of time.
                TreasureHunter.window.addTextToWindow(currentTown.getLatestNews(), Color.black);
                enterTown();
            }
        } else if (choice.equals("l")) {
            currentTown.lookForTrouble();
            if (hunter.gold<=0) {
                TreasureHunter.window.addTextToWindow( "Game Over!\n", Color.red);
                processChoice("x");
                System.exit(0);
            }
        } else if (choice.equals("d")) {
            if(hunter.hasItemInKit("shovel")){
                if(canDig){
                    if((int) (Math.random() * 2) + 1 == 1){
                        temp = (int)(Math.random()*20) + 1;
                        TreasureHunter.window.addTextToWindow("you dug and found " + temp + " gold.\n", Color.black);
                        hunter.changeGold(temp);
                        canDig = false;
                    } else{
                        TreasureHunter.window.addTextToWindow("you dug but only found dirt\n", Color.black);
                        canDig = false;
                    }
                } else {
                    TreasureHunter.window.addTextToWindow("you cannot hunt for gold until you move to another town\n", Color.black);
                }
            } else {
                TreasureHunter.window.addTextToWindow("you need a shovel to dig up gold\n", Color.black);
            }
        } else if (choice.equals("x")) {
            TreasureHunter.window.addTextToWindow("Fare thee well, " + hunter.getHunterName() + "!\n", Color.black);
        } else if (choice.equals("h")) {
            if (!hunter.hasTreasure(currentTown.treasure)&& !currentTown.treasure.equals("dust") && !currentTown.treasure.equals("")) {
                hunter.addTreasure(currentTown.treasure);
                TreasureHunter.window.addTextToWindow("You have found " + currentTown.treasure, Color.black);
                currentTown.treasure="";
                if (hunter.hasTreasure("crown") && hunter.hasTreasure("trophy") && hunter.hasTreasure("gem")) {
                    TreasureHunter.window.addTextToWindow("Congratulations, you have found the last of the three treasures, you win!\n", Color.black);
                    System.exit(0);
                }
            } else if (currentTown.treasure.equals("dust")){
                TreasureHunter.window.addTextToWindow("You found dust\n", Color.black);
                currentTown.treasure="";
            } else if (currentTown.treasure.equals("")) {
                TreasureHunter.window.addTextToWindow("You have already searched this town\n", Color.black);
            } else {
                TreasureHunter.window.addTextToWindow("You already have " + currentTown.treasure + "\n", Color.black);
            }
        }
        else {
            TreasureHunter.window.addTextToWindow("Yikes! That's an invalid option! Try again.\n", Color.black);
        }
    }
}
