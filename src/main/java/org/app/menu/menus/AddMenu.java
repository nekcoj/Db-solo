package org.app.menu.menus;

import org.app.App;
import org.app.Color;
import org.app.menu.AppMenu;
import org.app.pojo.Album;
import org.app.pojo.Artist;
import org.app.pojo.MusicObject;
import org.app.pojo.Song;
import org.fsdb.*;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class AddMenu extends AppMenu {
    private Artist artist;
    private Album album;
    private Song song;

    public AddMenu(App app) {
        super(app);
    }

    @Override
    public void handle() {
        reset();
        System.out.print("Song name> ");
        var songName = Input.getLine();
        int songId;
        songId = app.generateId("songs");

        setArtist(songId);
        // if no artist was create / found we abort
        if (artist == null) {
            System.out.println("Aborted, nothing was changed.");
            return;
        }
        setAlbum(artist.getId());
        if(album != null){
            Database.getInstance().addObject(album);
        }

        try {
            assert album != null;
            song = new Song(songId, (Album) Converter.getObjectFromId("albums", album.getId())/*album == null ? -1 : album.getId()*/, songName, -1, "Unknown genre", artist.getId());
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
        }

        Database.getInstance().addObject(song);
        Database.getInstance().addObject(artist);

        System.out.printf("Added song %s by %s on album %s\n",
                Color.setSongColor(songName),
                Color.setArtistColor(artist.getName()),
                Color.setAlbumColor(album == null ? "Unknown album" : album.getName()));
    }

    private void reset() {
        artist = null;
        album = null;
        song = null;
    }

    private void setArtist(int songId) {
        System.out.print("Artist name> ");
        var artistName = Input.getLine();

        // check for existing artist
        ArrayList<MusicObject> artists;
            artists = Printer.sortResults(Database.getInstance().search("artists", artistName));

        if (artists.size() > 0) {
            Printer.printResults(artists, true);
            System.out.println("Does one of these match your artist?");
            System.out.print("If yes enter the index, otherwise press 0 to create> ");

            int artistIndex;

            do artistIndex = Input.getIntInput();
            while (artistIndex < 0 || artistIndex > artists.size());

            if (artistIndex > 0) artist = (Artist) artists.get(artistIndex - 1);
        } else {
            System.out.println("Could not find any matching artists.");
            var createNewArtist = promptYesNo("Do you want to create this artist?");
            if (!createNewArtist) return;
        }

        if (artist == null) {
            System.out.printf("Is %s the correct name? (blank to keep): ", artistName);
            var input = Input.getLine(true);
            if (!input.isBlank()) artistName = input.strip();

            // create the new artist
            artist = new Artist(app.generateId("artists"), artistName, new ArrayList<>(List.of(songId)));
        }
    }

    private void setAlbum(int artistId) {
        var addToAlbum = promptYesNo("Do you want to add this song to an album?");
        if (!addToAlbum) return;

        ArrayList<MusicObject> albums;
        albums = Printer.sortResults(Database.getInstance().getAlbumList(artistId));

        if (albums.size() > 0) {
            Printer.printResults(albums, true);
            System.out.println("Does one of these match your album?");
            System.out.print("If yes enter the index, otherwise press 0 to create> ");

            int albumIndex;

            do albumIndex = Input.getIntInput();
            while (albumIndex < 0 || albumIndex > albums.size());

            if (albumIndex > 0) album = (Album) albums.get(albumIndex - 1);
        }

        if (album == null) {
            System.out.print("Enter album name to create> ");
            var albumName = Input.getLine();

            System.out.print("Album release year> ");
            var albumYear = Input.getIntInput();

            // create the new album
            album = new Album(app.generateId("albums"), artistId, albumName, albumYear);
        }
    }
}
