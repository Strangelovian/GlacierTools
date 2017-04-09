package com.strangelovian;

import com.amazonaws.services.glacier.model.InitiateJobResult;
import com.amazonaws.services.glacier.model.ListVaultsResult;

import java.util.List;

public class SaveInitJobResult {
    private ListVaultsResult listVaultsResult;
    private List<InitiateJobResult> initiateJobResult;

    public SaveInitJobResult() {
    }

    public ListVaultsResult getListVaultsResult() {
        return listVaultsResult;
    }

    public void setListVaultsResult(ListVaultsResult listVaultsResult) {
        this.listVaultsResult = listVaultsResult;
    }

    public List<InitiateJobResult> getInitiateJobResult() {
        return initiateJobResult;
    }

    public void setInitiateJobResult(List<InitiateJobResult> initiateJobResult) {
        this.initiateJobResult = initiateJobResult;
    }
}
