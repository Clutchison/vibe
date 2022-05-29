package com.hutchison.vibe.spotify;


import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.ClientCredentials;
import se.michaelthelin.spotify.model_objects.specification.*;
import se.michaelthelin.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;
import se.michaelthelin.spotify.requests.data.playlists.GetPlaylistRequest;
import se.michaelthelin.spotify.requests.data.tracks.GetTrackRequest;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;

@Component
public class SpotifyLinkConverter {

    private SpotifyApi spotifyApi;
    private static SpotifyLinkConverter instance;
    private static final String linkRegex = ".*open\\.spotify\\.com.*";

    @Value("${spotify.client.id}")
    private String spotifyClientId;

    @Value("${spotify.client.secret}")
    private String spotifyClientSecret;

    public SpotifyLinkConverter() {
        instance = this;
    }

    @PostConstruct
    public void init() {
        try {
            initSpotify();
        } catch (ParseException | SpotifyWebApiException | IOException e) {
            e.printStackTrace();
        }
    }

    private void initSpotify() throws ParseException, SpotifyWebApiException, IOException {
        this.spotifyApi = new SpotifyApi.Builder()
                .setClientId(spotifyClientId)
                .setClientSecret(spotifyClientSecret)
                .build();
        setAccessToken();
    }

    private void setAccessToken() throws IOException, SpotifyWebApiException, ParseException {
        ClientCredentialsRequest.Builder request = new ClientCredentialsRequest.Builder(spotifyApi.getClientId(), spotifyApi.getClientSecret());
        ClientCredentials creds = request.grant_type("client_credentials").build().execute();
        spotifyApi.setAccessToken(creds.getAccessToken());
    }

    public ArrayList<String> convert(String link) throws ParseException, SpotifyWebApiException, IOException {
        String[] firstSplit = link.split("/");
        String[] secondSplit;

        String type;
        if (firstSplit.length > 5) {
            secondSplit = firstSplit[6].split("\\?");
            type = firstSplit[5];
        } else {
            secondSplit = firstSplit[4].split("\\?");
            type = firstSplit[3];
        }
        String id = secondSplit[0];
        ArrayList<String> listOfTracks = new ArrayList<>();

        if (type.contentEquals("track")) {
            listOfTracks.add(getArtistAndName(id));
            return listOfTracks;
        }

        if (type.contentEquals("playlist")) {
            GetPlaylistRequest playlistRequest = spotifyApi.getPlaylist(id).build();
            Playlist playlist = playlistRequest.execute();
            Paging<PlaylistTrack> playlistPaging = playlist.getTracks();
            PlaylistTrack[] playlistTracks = playlistPaging.getItems();

            for (PlaylistTrack i : playlistTracks) {
                Track track = (Track) i.getTrack();
                String trackID = track.getId();
                listOfTracks.add(getArtistAndName(trackID));
            }

            return listOfTracks;
        }

        return null;
    }

    private String getArtistAndName(String trackID) throws ParseException, SpotifyWebApiException, IOException {
        String artistNameAndTrackName = "";
        GetTrackRequest trackRequest = spotifyApi.getTrack(trackID).build();

        Track track = trackRequest.execute();
        artistNameAndTrackName = track.getName() + " - ";

        ArtistSimplified[] artists = track.getArtists();
        for (ArtistSimplified i : artists) {
            artistNameAndTrackName += i.getName() + " ";
        }

        return artistNameAndTrackName;
    }

    public static SpotifyLinkConverter getInstance() {
        return instance;
    }

    public static boolean isSpotifyLink(String link) {
        return link.matches(linkRegex);
    }
}
