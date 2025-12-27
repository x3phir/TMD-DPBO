package main;

import database.Database;
import presenter.GamePresenter;

public class Main {

    public static void main(String[] args) {
        // Entry point aplikasi
        // Tidak boleh ada logika game di sini

        Database.init();
        
        GamePresenter presenter = new GamePresenter();
        presenter.showMenu();
    }
}
