package me.cristiangomez.wolfreader.model;

import org.joda.time.DateTimeZone;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Cristian on 10/18/2014.
 */
public class News {
    private String title;
    private String description;
    private String author;
    private String link;
    private int upVotes;
    private Date publicationDate;
    private long linkId;
    private String sub;
    private int clicks;
    private int negatives;
    private int karma;
    private String imageUrl;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public int getUpVotes() {
        return upVotes;
    }

    public void setUpVotes(int upVotes) {
        this.upVotes = upVotes;
    }

    public Date getPublicationDate() {
        return publicationDate;
    }

    public void setPublicactionDate(String publicactionDate) throws ParseException {
        publicactionDate = publicactionDate.substring(5,24);
        publicactionDate = publicactionDate.replace(" ","-");
        SimpleDateFormat formater = new SimpleDateFormat("d-MMM-yyyy-HH:mm:ss");
        formater.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date since = formater.parse(publicactionDate);
        DateTimeZone tz = DateTimeZone.UTC;
        since = new Date(tz.convertUTCToLocal(since.getTime()));
        this.publicationDate = since;
    }
    public void setPublicationDate(Date publicationDate) {
        this.publicationDate = publicationDate;
    }

    public long getLinkId() {
        return linkId;
    }

    public void setLinkId(long linkId) {
        this.linkId = linkId;
    }

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public int getClicks() {
        return clicks;
    }

    public void setClicks(int clicks) {
        this.clicks = clicks;
    }

    public int getNegatives() {
        return negatives;
    }

    public void setNegatives(int negatives) {
        this.negatives = negatives;
    }

    public int getKarma() {
        return karma;
    }

    public void setKarma(int karma) {
        this.karma = karma;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSource() throws URISyntaxException{
        URI uri = new URI(link);
        String domain = uri.getHost();
        return domain.startsWith("www.") ? domain.substring(4) : domain;
    }
}
