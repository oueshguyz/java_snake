package com.example.snake;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.ArrayList;

public class Snake extends Application {

    // constantes
    public final static int TAILLE_CASE = 20;
    public final static int NB_COLONNES = 25;
    public final static int NB_LIGNES = 20;
    public final static int NB_MURS = 20;


    // attributs

    private ArrayList<Rectangle> serpent = new ArrayList<>();
    private GridPane gridPane;
    private Rectangle pomme;
    private ArrayList<Rectangle> murs = new ArrayList<>();
    private Scene s;

    @Override
    public void start(Stage stage) throws Exception {


        // tracer les lignes et les colonnes
         for (int i = 0; i < NB_COLONNES; i++) {
            for (int j = 0; j < NB_LIGNES; j++) {
                Rectangle r = new Rectangle(TAILLE_CASE, TAILLE_CASE);
                r.setFill(Color.BLUE);
                r.setStroke(Color.BLACK);
                r.setStrokeWidth(1);
                gridPane.add(r,i,j);
            }
         }
         this.afficherPomme();
         this.initAfficherSerpent();
         this.genererMurs();


        s.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                switch (keyEvent.getCode()) {
                    case UP:
                        deplacerSerpent(0,-1);
                        break;
                    case DOWN:
                        deplacerSerpent(0,1);
                        break;
                    case LEFT:
                        deplacerSerpent(-1,0);
                        break;
                    case RIGHT:
                        deplacerSerpent(1,0);
                        break;
                    default:
                        break;
                }
            }
        });


        stage.setScene(this.s);
        stage.show();
    }

    /**
     * Constructeur de la classe Snake
     */
    public Snake() {
        gridPane = new GridPane();
        s = new Scene(gridPane);
        this.serpent = new ArrayList<>();

        // création serpent
        for (int i = 0; i < 3; i++) {
            Rectangle r = new Rectangle(TAILLE_CASE, TAILLE_CASE);
            r.setFill(Color.ORANGE);
            r.setStroke(Color.BLACK);
            r.setStrokeWidth(1);
            this.serpent.add(r); // ajoute le corps du serpent à la liste
        }

        // création de la pomme
        pomme = new Rectangle(TAILLE_CASE, TAILLE_CASE);
        pomme.setFill(Color.RED);
        pomme.setStroke(Color.BLACK);
        pomme.setStrokeWidth(1);
    }

    public void initAfficherSerpent() {
        int i=0;
        for (Rectangle r : serpent) {
            gridPane.add(r, NB_COLONNES / 2, NB_LIGNES / 2 + i);
            i++;
        }
    }

    public void afficherPomme() {
        // ajouter la pomme à la grille
        gridPane.add(pomme, 4, 4);
    }

    public void deplacerSerpent(int x_add,int y_add) {
        Rectangle tete = serpent.getFirst();
        int old_x = GridPane.getColumnIndex(tete);
        int old_y = GridPane.getRowIndex(tete);

        if (old_x + x_add > NB_COLONNES - 1 || old_y + y_add > NB_LIGNES - 1 || old_x + x_add < 0 || old_y + y_add < 0) {
            finPartie(); // ne pas dépasser les limites de la grille
            return;
        }
        if (etreDansSerpent(old_x + x_add,old_y+ y_add)) {
            finPartie();
            return; // ne pas se déplacer sur la pomme
        }
        // serpent ne doit pas se déplacer contre un mur
        for (Rectangle mur : murs) {
            int mur_x = GridPane.getColumnIndex(mur);
            int mur_y = GridPane.getRowIndex(mur);
            if (mur_x == old_x + x_add && mur_y == old_y + y_add) {
                finPartie(); // ne pas se déplacer contre un mur
                return;
            }
        }

        serpentSurPomme();
        GridPane.setColumnIndex(tete, old_x + x_add);
        GridPane.setRowIndex(tete, old_y + y_add);

        for (Rectangle r : serpent) {
            if (r != tete) {
                int temp_x = GridPane.getColumnIndex(r);
                int temp_y = GridPane.getRowIndex(r);
                GridPane.setColumnIndex(r, old_x);
                GridPane.setRowIndex(r, old_y);
                old_x = temp_x;
                old_y = temp_y;
            }
        }
    }

    public void serpentSurPomme() {
        int serpent_x = GridPane.getColumnIndex(serpent.getFirst());
        int serpent_y = GridPane.getRowIndex(serpent.getFirst());
        int pomme_x = GridPane.getColumnIndex(pomme);
        int pomme_y = GridPane.getRowIndex(pomme);

        if (serpent_x == pomme_x && serpent_y == pomme_y) {
            // ajouter une nouvelle case au serpent
            Rectangle r = new Rectangle(TAILLE_CASE, TAILLE_CASE);
            r.setFill(Color.ORANGE);
            r.setStroke(Color.BLACK);
            r.setStrokeWidth(1);
            // ajouter la nouvelle case à la fin du serpent
            int last_x = GridPane.getColumnIndex(serpent.getLast());
            int last_y = GridPane.getRowIndex(serpent.getLast());
            serpent.add(r);
            gridPane.add(r, last_x+1, last_y);

            // déplacer la pomme à une nouvelle position aléatoire
            int new_x = (int) (Math.random() * NB_COLONNES);
            int new_y = (int) (Math.random() * NB_LIGNES);
            // pomme ne doit pas être sur le serpent
            while (etreDansSerpent(new_x, new_y)) {
                new_x = (int) (Math.random() * NB_COLONNES);
                new_y = (int) (Math.random() * NB_LIGNES);
            }
            // pomme ne doit pas être sur un mur
            while (true) {
                boolean surMur = false;
                for (Rectangle mur : murs) {
                    if (GridPane.getColumnIndex(mur) == new_x && GridPane.getRowIndex(mur) == new_y) {
                        surMur = true;
                        break;
                    }
                }
                if (!surMur) break;
                new_x = (int) (Math.random() * NB_COLONNES);
                new_y = (int) (Math.random() * NB_LIGNES);
            }

            GridPane.setColumnIndex(pomme, new_x);
            GridPane.setRowIndex(pomme, new_y);
            }
        }

    public boolean etreDansSerpent(int x,int y) {
        for (Rectangle r : serpent) {
            if (GridPane.getColumnIndex(r) == x && GridPane.getRowIndex(r) == y) {
                return true; // le serpent est sur cette case
            }
        }
        return false;
    }

    public void genererMurs() {
        for(int i = 0; i < NB_MURS; i++) {
            Rectangle mur = new Rectangle(TAILLE_CASE, TAILLE_CASE);
            mur.setFill(Color.GRAY);
            mur.setStroke(Color.BLACK);
            mur.setStrokeWidth(1);
            int x = (int) (Math.random() * NB_COLONNES);
            int y = (int) (Math.random() * NB_LIGNES);
            while (etreDansSerpent(x, y) || (GridPane.getColumnIndex(pomme) == x && GridPane.getRowIndex(pomme) == y)) {
                x = (int) (Math.random() * NB_COLONNES);
                y = (int) (Math.random() * NB_LIGNES);
            }
            murs.add(mur);
            gridPane.add(mur, x, y);
        }
    }

    public void finPartie() {
        s.setOnKeyPressed(null); // désactiver les touches
        System.out.println("Partie terminée !");
        for (Rectangle r : serpent) {
            r.setFill(Color.BLACK); // changer la couleur du serpent en noir
        }

    }

    public static void main(String[] args) {
        launch(args);
    }
}
