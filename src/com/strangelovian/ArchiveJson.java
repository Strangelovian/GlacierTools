package com.strangelovian;

import java.util.Date;

public class ArchiveJson {
    private String ArchiveId;
    private String ArchiveDescription;
    private Date CreationDate;
    private long Size;
    private String SHA256TreeHash;

    public String getArchiveId() {
        return ArchiveId;
    }

    public void setArchiveId(String archiveId) {
        this.ArchiveId = archiveId;
    }

    public String getArchiveDescription() {
        return ArchiveDescription;
    }

    public void setArchiveDescription(String archiveDescription) {
        this.ArchiveDescription = archiveDescription;
    }

    public Date getCreationDate() {
        return CreationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.CreationDate = creationDate;
    }

    public long getSize() {
        return Size;
    }

    public void setSize(long size) {
        this.Size = size;
    }

    public String getSHA256TreeHash() {
        return SHA256TreeHash;
    }

    public void setSHA256TreeHash(String SHA256TreeHash) {
        this.SHA256TreeHash = SHA256TreeHash;
    }
}
