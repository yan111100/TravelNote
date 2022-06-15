package com.cuz.travelnote.model;

import android.location.Location;
import android.provider.ContactsContract;
import android.provider.MediaStore;

import java.util.Date;
import java.util.List;

public class TravelNote {
    private Long id;
    private User user;      	//创建笔记的用户
    private Date date;		 	//创建笔记的时间
    private String title;     	//笔记标题
    private String content;		//笔记的详细内容
    private List<ImageModel> images;	//笔记中的图片资源
    private List<MediaStore.Audio> audios;	//音频资源
    private List<MediaStore.Video> videos;	//视频资源
    private List<Location> locations;  //定位
    private List<User> collectUsers;	//收藏此文章的用户
    private List<User> favouriteUsers;	//点赞过此文章的用户
    private Boolean collect;
    private Boolean favourite;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<ImageModel> getImages() {
        return images;
    }

    public void setImages(List<ImageModel> images) {
        this.images = images;
    }


    public List<MediaStore.Audio> getAudios() {
        return audios;
    }

    public void setAudios(List<MediaStore.Audio> audios) {
        this.audios = audios;
    }

    public List<MediaStore.Video> getVideos() {
        return videos;
    }

    public void setVideos(List<MediaStore.Video> videos) {
        this.videos = videos;
    }


    public List<Location> getLocations() {
        return locations;
    }

    public void setLocations(List<Location> locations) {
        this.locations = locations;
    }

    public List<User> getCollectUsers() {
        return collectUsers;
    }

    public Boolean getCollect() {
        return collect;
    }

    public void setCollect(Boolean collect) {
        this.collect = collect;
    }

    public Boolean getFavourite() {
        return favourite;
    }

    public void setFavourite(Boolean favourite) {
        this.favourite = favourite;
    }

    public void setCollectUsers(List<User> collectUsers) {
        this.collectUsers = collectUsers;
    }

    public List<User> getFavouriteUsers() {
        return favouriteUsers;
    }

    public void setFavouriteUsers(List<User> favouriteUsers) {
        this.favouriteUsers = favouriteUsers;
    }
}
