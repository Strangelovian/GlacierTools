package com.strangelovian;

import java.util.Date;
import java.util.List;

public class VaultInventoryJson {
    private String VaultARN;
    private Date InventoryDate;
    private List<ArchiveJson> ArchiveList;

    public String getVaultARN() {
        return VaultARN;
    }

    public void setVaultARN(String vaultARN) {
        this.VaultARN = vaultARN;
    }

    public Date getInventoryDate() {
        return InventoryDate;
    }

    public void setInventoryDate(Date inventoryDate) {
        this.InventoryDate = inventoryDate;
    }

    public List<ArchiveJson> getArchiveList() {
        return ArchiveList;
    }

    public void setArchiveList(List<ArchiveJson> archiveList) {
        this.ArchiveList = archiveList;
    }
}
