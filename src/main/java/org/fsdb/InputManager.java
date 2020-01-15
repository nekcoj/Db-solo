package org.fsdb;

import org.fsdb.database.Database;

import javax.xml.crypto.Data;
import java.util.Scanner;

public class InputManager {
    private Scanner userInput;
    private Database database;
    public InputManager(Database database) {
        this.database = database;
        this.userInput = new Scanner(System.in);
        init();
    }

    private void init(){
        int menuSelection;
        do {
            welcomeScreen();
            menuSelection = userChoice();
            userChosenAction(menuSelection);
        } while (menuSelection != 4);
    }




    private void welcomeScreen() {
        System.out.println("Vad vill du göra? \n1. Sök\n2. Lägg till låt\n3. Ta bort låt\n4. Avsluta");
    }

    private int userChoice(){
        try {
            return Integer.parseInt(userInput.next());
        } catch (Exception e){
            System.out.println("Felaktig inmatning, försök igen");
            return userChoice();
        }
    }
    private void userChosenAction(int userChoice){
        switch (userChoice){
            case 1: System.out.println("Sök");
                break;
            case 2: System.out.println("Lägga till låt");
                break;
            case 3: System.out.println("Ta bort");
                break;
            case 4: System.out.println("Hej då!");
                break;
            default: System.out.println("Felaktig inmatning, försök igen!");
                userChosenAction(userChoice());
        }
    }


}
