package com.example.todolist;

import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
/*
*
*
* */
public class MainActivity extends AppCompatActivity {
    //b-Declaration du bouton b_test
    private Button b_test;
    private LinearLayout ll_tache; // Declaration du LinearLayout pour le relier a celui delcarer dans activity_main
    private Button b_nouvelle_categorie; /*Associer a un ecouteur d'evenement
    qui lance une nouvelle activite (categorie)*/
    private Spinner s_categorie;
    private TextView et_tache;
    private Button b_ajouter_tache;/*Associer a un ecouteur d'evenement
    qui ajoute une tache a la base de donnees DAO*/
    private Button b_tachefaite;/*Associer a un ecouteur d'evenement
    qui marque une tache comme faite*/
    private CategorieDAO uneCategorieDAO;
    private TacheDAO uneTacheDAO;
    private ArrayList<Tache> lstTaches;
    private ArrayList<Categorie> lstCategories;
    private ArrayList<CheckBox> lstChk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);//Appel de activity_main
        ll_tache = findViewById(R.id.ll_taches);//findViewById() reccupere une reference de l'element ll_tache dans le xml pour le relier a ll_tache

        //c-Reccuperation du button creer
        b_test=findViewById(R.id.b_tests);
        //d-Ajout de l'ecouteur d'evenement
        b_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Log.d("testLog","TEST ! ;)");
                Log.d("testLog",et_tache.getText().toString());
            }
        });
        s_categorie = findViewById(R.id.s_categorie); //pareil
        et_tache = findViewById(R.id.et_tache);//pareil
        b_ajouter_tache = findViewById(R.id.b_ajouter_tache);//pareil
        b_tachefaite = findViewById(R.id.b_tachefaite);//pareil

        lstChk = new ArrayList<CheckBox>(); // On initialise lstChk pour creer une liste d'objet de type checkBox
        /*Les objets CheckBox sont des éléments de l'interface utilisateur qui permettent à l'utilisateur de sélectionner ou
         de désélectionner une option en cochant ou en décochant une cas*/

        /*fPour faire appel à des objets d'accès aux données (DAO) pour accéder aux données de catégories et
        de tâches stockées dans une base de données*/
        uneCategorieDAO = new CategorieDAO(this);
        Log.d("testLog",uneCategorieDAO.getCategories().toString());//Affichage de la categorie sous forme de string

        uneTacheDAO = new TacheDAO(this);
        Log.d("testLog",uneTacheDAO.getTaches().toString());
        
        Log.d("testLog",uneTacheDAO.getTachesByCategorie("Culture").toString());
        lstTaches = uneTacheDAO.getTaches();
        affTaches();

        b_nouvelle_categorie = findViewById(R.id.b_nouvelle_categorie);
        b_nouvelle_categorie.setOnClickListener(new View.OnClickListener() {
            @Override
            /*Declaration de l'ecouteur d'evenement onClick qui est declanchee au moment
            * ou l'utilisateur click sur le bouton onClick*/
            public void onClick(View view) {
                Intent unIntent = new Intent(getApplicationContext(),NouvelleCategorieActivity.class);/*Pour creer une
                nouvelle instance getApplicationContext() utiliser pour l'activite NouvelleCategorieActivite en deuxieme param*/
                startActivity(unIntent); // Pour lancer l'activite generer par l'Intent unIntent
            }
            /*Prof: lorsque l'utilisateur clique sur le bouton b_nouvelle_categorie, cette méthode crée une intention de
             lancer une activité de la classe NouvelleCategorieActivity et démarre cette activité via la méthode
             startActivity()*/
        });

        lstCategories = uneCategorieDAO.getCategories();
        ArrayAdapter<String> adapteurCategorie = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
        for(int i=0;i<uneCategorieDAO.getCategories().size();i++){
            adapteurCategorie.add(lstCategories.get(i).getNomC());
        }
        s_categorie.setAdapter(adapteurCategorie);

        b_ajouter_tache.setOnClickListener(new View.OnClickListener() {
            @Override
            /*onclick pour lancer des actions lorsque les boutons sont clcke*/
            public void onClick(View view) {
                long idC;
                String libelleT;
                idC = lstCategories.get(s_categorie.getSelectedItemPosition()).getIdC();
                Log.d("testLog","Spinner : "+s_categorie.getSelectedItem());
                libelleT = et_tache.getText().toString();
                Tache nouvelleTache = new Tache(libelleT,idC);
                uneTacheDAO.addTache(nouvelleTache);
                //recharge lstTache
                lstTaches = uneTacheDAO.getTaches();
                affTaches();
                et_tache.setText("");
            }
        });

        b_tachefaite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                faireTache();
            }
        });
    }



    /*Methodes pour gerer les interaction avec l'interface utilisateur*/
    private void affTaches(){/*Pour gerer l'affichage des taches dans
     la liste deroulante*/

        ll_tache.removeAllViews();//Pour supprimer toutes les vues enfants du LinearLyout donc vider
        //tout les element de l'interface utilisateur

        lstChk.clear();//pour supprimer tous les éléments d'une liste qui contient des objets de type CheckBox
        for(int i=0;i<lstTaches.size();i++) {
            LinearLayout unlayout = new LinearLayout(MainActivity.this);
            unlayout.setOrientation(LinearLayout.HORIZONTAL);

            CheckBox unchk = new CheckBox(this);
            unchk.setText(lstTaches.get(i).getLibelleT());
            lstChk.add(unchk);
            TextView unTextView = new TextView(this);
            unTextView.setText(uneCategorieDAO.getCategorie(lstTaches.get(i).getIdC()).getNomC());
            unTextView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

            unTextView.setGravity(Gravity.RIGHT);
            unTextView.setPadding(20,0,20,0);
            unlayout.addView(unchk);
            unlayout.addView(unTextView);
            ll_tache.addView(unlayout);
        }
    }

    private void faireTache(){/*Pour gerer la modification des taches dans
     la liste deroulante*/
        for(int i=lstChk.size()-1;i>=0;i--) {
            if(lstChk.get(i).isChecked()){
                Log.d("testLog",lstTaches.get(i).getLibelleT());
                uneTacheDAO.delTache(lstTaches.get(i));

            }
        }
        //recharge lstTache
        lstTaches = uneTacheDAO.getTaches();
        affTaches();
    }
}