// **********************************************************************
//
// Copyright (c) 2003-2010 ZeroC, Inc. All rights reserved.
//
// This copy of Ice is licensed to you under the terms described in the
// ICE_LICENSE file included in this distribution.
//
// **********************************************************************

// Ice version 3.4.0

package cn.joy.ggg.api.model;

// <auto-generated>
//
// Generated from file `community.ice'
//
// Warning: do not edit this file.
//
// </auto-generated>


public class Topic implements java.lang.Cloneable, java.io.Serializable
{
    public String topicid;

    public String title;

    public String content;

    public String type;

    public String ability;

    public String tags;

    public String category;

    public String owner;

    public String source;

    public long createTime;

    public long lastUpdateTime;

    public int status;

    public java.util.List<Video> videos;

    public Topic()
    {
    }

    public Topic(String topicid, String title, String content, String type, String ability, String tags, String category, String owner, String source, long createTime, long lastUpdateTime, int status, java.util.List<Video> videos)
    {
        this.topicid = topicid;
        this.title = title;
        this.content = content;
        this.type = type;
        this.ability = ability;
        this.tags = tags;
        this.category = category;
        this.owner = owner;
        this.source = source;
        this.createTime = createTime;
        this.lastUpdateTime = lastUpdateTime;
        this.status = status;
        this.videos = videos;
    }

    public boolean
    equals(java.lang.Object rhs)
    {
        if(this == rhs)
        {
            return true;
        }
        Topic _r = null;
        try
        {
            _r = (Topic)rhs;
        }
        catch(ClassCastException ex)
        {
        }

        if(_r != null)
        {
            if(topicid != _r.topicid && topicid != null && !topicid.equals(_r.topicid))
            {
                return false;
            }
            if(title != _r.title && title != null && !title.equals(_r.title))
            {
                return false;
            }
            if(content != _r.content && content != null && !content.equals(_r.content))
            {
                return false;
            }
            if(type != _r.type && type != null && !type.equals(_r.type))
            {
                return false;
            }
            if(ability != _r.ability && ability != null && !ability.equals(_r.ability))
            {
                return false;
            }
            if(tags != _r.tags && tags != null && !tags.equals(_r.tags))
            {
                return false;
            }
            if(category != _r.category && category != null && !category.equals(_r.category))
            {
                return false;
            }
            if(owner != _r.owner && owner != null && !owner.equals(_r.owner))
            {
                return false;
            }
            if(source != _r.source && source != null && !source.equals(_r.source))
            {
                return false;
            }
            if(createTime != _r.createTime)
            {
                return false;
            }
            if(lastUpdateTime != _r.lastUpdateTime)
            {
                return false;
            }
            if(status != _r.status)
            {
                return false;
            }
            if(videos != _r.videos && videos != null && !videos.equals(_r.videos))
            {
                return false;
            }

            return true;
        }

        return false;
    }

    public int
    hashCode()
    {
        int __h = 0;
        if(topicid != null)
        {
            __h = 5 * __h + topicid.hashCode();
        }
        if(title != null)
        {
            __h = 5 * __h + title.hashCode();
        }
        if(content != null)
        {
            __h = 5 * __h + content.hashCode();
        }
        if(type != null)
        {
            __h = 5 * __h + type.hashCode();
        }
        if(ability != null)
        {
            __h = 5 * __h + ability.hashCode();
        }
        if(tags != null)
        {
            __h = 5 * __h + tags.hashCode();
        }
        if(category != null)
        {
            __h = 5 * __h + category.hashCode();
        }
        if(owner != null)
        {
            __h = 5 * __h + owner.hashCode();
        }
        if(source != null)
        {
            __h = 5 * __h + source.hashCode();
        }
        __h = 5 * __h + (int)createTime;
        __h = 5 * __h + (int)lastUpdateTime;
        __h = 5 * __h + status;
        if(videos != null)
        {
            __h = 5 * __h + videos.hashCode();
        }
        return __h;
    }

    public java.lang.Object
    clone()
    {
        java.lang.Object o = null;
        try
        {
            o = super.clone();
        }
        catch(CloneNotSupportedException ex)
        {
            assert false; // impossible
        }
        return o;
    }

    public void
    __write(IceInternal.BasicStream __os)
    {
        __os.writeString(topicid);
        __os.writeString(title);
        __os.writeString(content);
        __os.writeString(type);
        __os.writeString(ability);
        __os.writeString(tags);
        __os.writeString(category);
        __os.writeString(owner);
        __os.writeString(source);
        __os.writeLong(createTime);
        __os.writeLong(lastUpdateTime);
        __os.writeInt(status);
        VideoListHelper.write(__os, videos);
    }

    public void
    __read(IceInternal.BasicStream __is)
    {
        topicid = __is.readString();
        title = __is.readString();
        content = __is.readString();
        type = __is.readString();
        ability = __is.readString();
        tags = __is.readString();
        category = __is.readString();
        owner = __is.readString();
        source = __is.readString();
        createTime = __is.readLong();
        lastUpdateTime = __is.readLong();
        status = __is.readInt();
        videos = VideoListHelper.read(__is);
    }

    public String getTopicid() {
      return topicid;
    }

    public void setTopicid(String topicid) {
      this.topicid = topicid;
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

    public String getType() {
      return type;
    }

    public void setType(String type) {
      this.type = type;
    }

    public String getAbility() {
      return ability;
    }

    public void setAbility(String ability) {
      this.ability = ability;
    }

    public String getTags() {
      return tags;
    }

    public void setTags(String tags) {
      this.tags = tags;
    }

    public String getCategory() {
      return category;
    }

    public void setCategory(String category) {
      this.category = category;
    }

    public String getOwner() {
      return owner;
    }

    public void setOwner(String owner) {
      this.owner = owner;
    }

    public String getSource() {
      return source;
    }

    public void setSource(String source) {
      this.source = source;
    }

    public long getCreateTime() {
      return createTime;
    }

    public void setCreateTime(long createTime) {
      this.createTime = createTime;
    }

    public long getLastUpdateTime() {
      return lastUpdateTime;
    }

    public void setLastUpdateTime(long lastUpdateTime) {
      this.lastUpdateTime = lastUpdateTime;
    }

    public int getStatus() {
      return status;
    }

    public void setStatus(int status) {
      this.status = status;
    }

    public java.util.List<Video> getVideos() {
      return videos;
    }

    public void setVideos(java.util.List<Video> videos) {
      this.videos = videos;
    }
}
