package org.fsdb;

import org.app.Color;
import org.app.pojo.Album;
import org.app.pojo.Artist;
import org.app.pojo.MusicObject;
import org.app.pojo.Song;
import org.fsdb.query.Query;
import org.fsdb.query.QueryResult;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Collectors;

public class Printer {

    public static void printResults(ArrayList<MusicObject> results, boolean printIndexed) {
        int index = 0;
        String artistStr = "", albumStr = "", songStr = "";

        var artists = (Artist[]) results.stream().filter(a ->
                Database.getInstance().getType(a).first == MusicClassEnum.ARTIST.ordinal()).toArray(Artist[]::new);
        if (artists.length > 0) {
            artistStr = artists.length + " Artist(s) ";
            System.out.printf("-- Artists (%d) --\n", artists.length);
            for (int i = 0; i < artists.length; i++, index++) {
                if (printIndexed) System.out.printf("[%d] %s\n", index + 1, artists[i].getNameColored());
                else System.out.println(artists[i].getNameColored());
            }
        }

        var allArtistsPaths = FileSystem.getDirFiles(Database.getInstance().getDbName() + "/artists");
        var artistArray = new ArrayList<Artist>();

        for (File file : Objects.requireNonNull(allArtistsPaths)) {
            artistArray.add((Artist) Converter.getObjectFromHashMap("artists"
                    , Database.getInstance().deserializeData(FileSystem.readFile(file.getPath()))));

        }

        var albums = (Album[]) results.stream().filter(a ->
                Database.getInstance().getType(a).first == MusicClassEnum.ALBUM.ordinal()).toArray(Album[]::new);
        if (albums.length > 0) {
            albumStr = albums.length + " Album(s) ";
            System.out.printf("-- Albums (%d) --\n", albums.length);
            for (int i = 0; i < albums.length; i++, index++) {
                int finalI = i;

                var artistName = "Unknown Artist";
                var artistObject = artistArray.stream().filter(a -> a.getId() == albums[finalI].getArtistId()).findFirst();
                if (artistObject.isPresent())
                    artistName = artistObject.get().getNameColored();

                if (printIndexed)
                    System.out.printf("[%d] %s - %s\n", index + 1, albums[i].getNameColored(), artistName);
                else System.out.println(albums[i].getNameColored());
            }
        }

        var songs = (Song[]) results.stream().filter(s ->
                Database.getInstance().getType(s).first == MusicClassEnum.SONG.ordinal()).toArray(Song[]::new);
        if (songs.length > 0) {
            songStr = songs.length + " Song(s) ";
            System.out.printf("-- Songs (%d) --\n", songs.length);
            for (int i = 0; i < songs.length; i++, index++) {
                int finalI = i;

                var artistName = "Unknown Artist";
                var artistObject = artistArray.stream().filter(a -> a.getId() == songs[finalI].getArtistId()).findFirst();
                if (artistObject.isPresent())
                    artistName = artistObject.get().getNameColored();

                if (printIndexed) System.out.printf("[%d] %s - %s\n", index + 1, songs[i].getNameColored(), artistName);
                else System.out.println(songs[i].getNameColored());
            }
        }

        if (index > 0) {
            System.out.printf("\nFound %s%s%s\n"
                    , Color.setArtistColor(artistStr)
                    , Color.setAlbumColor(albumStr)
                    , Color.setSongColor(songStr));
        } else {
            System.out.println("No results found!");
        }
    }


    public static void printArtistSongs(String artistName) {
        QueryResult fetchResult;
        fetchResult = Database.getInstance().executeQuery(new Query().from("artists").where("name", artistName).fetch());

        if (!fetchResult.success) System.out.println("Could not find artist!");

        MusicObject artist;
        artist = Converter.getObjectFromHashMap("artists", fetchResult.data);

        QueryResult songsResult;
        songsResult = Database.getInstance().executeQuery(new Query()
                .from("songs").where("artistId", String.valueOf(artist.getId()))
                .fetchAll());

        var songs = songsResult.dataArray
                .stream()
                .map(s -> s.get("title"))
                .collect(Collectors.toList());

        System.out.printf("\n----- Song(s) by %s -----\n", artist.getResolvedName());
        songs.forEach(s -> System.out.printf("%s - %s\n",
                Color.setSongColor(s), Color.setArtistColor(artist.getResolvedName())
        ));
    }

    public static ArrayList<MusicObject> sortResults(ArrayList<MusicObject> results) {
        results.sort(Comparator.naturalOrder());
        return results;
    }
}
