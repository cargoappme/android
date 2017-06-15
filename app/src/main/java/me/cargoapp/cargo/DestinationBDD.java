package me.cargoapp.cargo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.List;

/**
 * Created by Mathieu on 14/06/2017.
 */

public class DestinationBDD {
    private static final int VERSION_BDD = 1;
    private static final String NOM_BDD = "destinations.db";

    private static final String TABLE_DESTINATIONS = "table_destinations";
    private static final String COL_ID = "ID";
    private static final int NUM_COL_ID = 0;
    private static final String COL_ADRESS = "ADRESS";
    private static final int NUM_COL_ADRESS = 1;
    private static final String COL_LON = "Longitude";
    private static final int NUM_COL_LON = 2;
    private static final String COL_LAT = "Latitude";
    private static final int NUM_COL_LAT = 3;

    private SQLiteDatabase bdd;

    private destinationHistoryBDD destinationHistoryBDD;

    public DestinationBDD(Context context){
        //On créer la BDD et sa table
        destinationHistoryBDD = new destinationHistoryBDD(context, NOM_BDD, null, VERSION_BDD);
    }

    public void open(){
        //on ouvre la BDD en écriture
        bdd = destinationHistoryBDD.getWritableDatabase();
    }

    public void close(){
        //on ferme l'accès à la BDD
        bdd.close();
    }

    public SQLiteDatabase getBDD(){
        return bdd;
    }

    public long insertDestination(Destination destination){
        //Création d'un ContentValues (fonctionne comme une HashMap)
        ContentValues values = new ContentValues();
        //on lui ajoute une valeur associé à une clé (qui est le nom de la colonne dans laquelle on veut mettre la valeur)
        values.put(COL_ADRESS, destination.getAdress());
        values.put(COL_LON, destination.getLon());
        values.put(COL_LAT, destination.getLat());
        //on insère l'objet dans la BDD via le ContentValues
        return bdd.insert(TABLE_DESTINATIONS, null, values);
    }

    public int updateDestination(int id, Destination destination){
        //La mise à jour d'un livre dans la BDD fonctionne plus ou moins comme une insertion
        //il faut simple préciser quelle livre on doit mettre à jour grâce à l'ID
        ContentValues values = new ContentValues();
        values.put(COL_ADRESS, destination.getAdress());
        values.put(COL_LON, destination.getLon());
        values.put(COL_LAT, destination.getLat());
        return bdd.update(TABLE_DESTINATIONS, values, COL_ID + " = " +id, null);
    }

    public int removeLivreWithID(int id){
        //Suppression d'un livre de la BDD grâce à l'ID
        return bdd.delete(TABLE_DESTINATIONS, COL_ID + " = " +id, null);
    }

    public Destination[] getAllDestinations(){
        List<Destination> list = null;
        Cursor c = bdd.rawQuery("Select * from table_destinations ORDER BY rowid DESC limit 10",null);
        int cnt = c.getCount();
        Destination[] listings;
        listings = new Destination[cnt];
        c.moveToFirst();
        cnt = 0;
        do {
            int index0 = c.getColumnIndex(COL_ADRESS);
            int index1 = c.getColumnIndex(COL_LON);
            int index2 = c.getColumnIndex(COL_LAT);
            String z = c.getString(index0);
            double a = c.getDouble(index1);
            double b = c.getDouble(index2);
            listings[cnt] = new Destination(z, a, b);
            cnt++;
        } while (c.moveToNext());


        return listings;
    }


    //Cette méthode permet de convertir un cursor en un livre
    private Destination cursorToDestination(Cursor c){
        //si aucun élément n'a été retourné dans la requête, on renvoie null
        if (c.getCount() == 0)
            return null;

        //Sinon on se place sur le premier élément
        c.moveToFirst();
        //On créé un livre
        Destination destination = new Destination();
        //on lui affecte toutes les infos grâce aux infos contenues dans le Cursor
        destination.setId(c.getInt(NUM_COL_ID));
        destination.setAdress(c.getString(NUM_COL_ADRESS));
        destination.setLon(c.getDouble(NUM_COL_LON));
        destination.setLat(c.getDouble(NUM_COL_LAT));
        //On ferme le cursor
        c.close();

        //On retourne le livre
        return destination;
    }
}
