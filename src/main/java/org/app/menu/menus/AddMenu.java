package org.app.menu.menus;

import org.app.App;
import org.app.Color;
import org.app.menu.AppMenu;
import org.app.pojo.Album;
import org.app.pojo.Artist;
import org.app.pojo.Song;
import org.fsdb.Input;

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
    public void handle() throws IllegalAccessException {
        reset();
        System.out.print("Song name> ");
        var songName = Input.getLine();
        var songId = app.generateId("songs");

        setArtist(songId);
        // if no artist was create / found we abort
        if (artist == null) {
            System.out.println("Aborted, nothing was changed.");
            return;
        }
        setAlbum(artist.getId());

        song = new Song(songId, album == null ? -1 : album.getId(), songName, -1, "Unknown genre", artist.getId());

        app.addObject(song);
        app.addObject(artist);

        if (album != null)
            app.addObject(album);

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
        var artists = app.sortResults(app.search("artists", artistName));
        if (artists.size() > 0) {
            app.printResults(artists, true);
            System.out.println("Does one of these match your artist?");
            System.out.print("If yes enter the index, otherwise press 0 to create> ");

            int artistIndex;

            do artistIndex = app.getIntInput();
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

        var albums = app.sortResults(app.getAlbumList(artistId));
        if (albums.size() > 0) {
            app.printResults(albums, true);
            System.out.println("Does one of these match your album?");
            System.out.print("If yes enter the index, otherwise press 0 to create> ");

            int albumIndex;

            do albumIndex = app.getIntInput();
            while (albumIndex < 0 || albumIndex > albums.size());

            if (albumIndex > 0) album = (Album) albums.get(albumIndex - 1);
        }

        if (album == null) {
            System.out.print("Enter album name to create> ");
            var albumName = Input.getLine();

            System.out.print("Album release year> ");
            var albumYear = app.getIntInput();

            // create the new album
            album = new Album(app.generateId("albums"), artistId, albumName, albumYear);
        }
    }
}
