package com.strangelovian;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.glacier.AmazonGlacier;
import com.amazonaws.services.glacier.AmazonGlacierAsync;
import com.amazonaws.services.glacier.AmazonGlacierAsyncClientBuilder;
import com.amazonaws.services.glacier.model.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws Exception{

        List<Regions> regions = Arrays.asList(
                Regions.EU_CENTRAL_1,
                Regions.EU_WEST_1);

        // InitiateInventoriesAllRegions(regions);

        // DownloadVaultInventoriesAllRegions(regions);

        // DeleteVaultArchivesAllRegions(regions);
    }

    private static void DeleteVaultArchivesAllRegions(List<Regions> regions) throws Exception {

        for(Regions region: regions)
            DeleteVaultArchives(BuildGlacierClient(region), region);
    }

    private static void DeleteVaultArchives(AmazonGlacier glacier, Regions region) throws Exception {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        for(DescribeVaultOutput vaultDesc: getSaveInitJobResult(region, gson).getListVaultsResult().getVaultList()) {

            JsonReader reader = new JsonReader(new FileReader(region.getName() + "." + vaultDesc.getVaultName() +".txt"));
            VaultInventoryJson vaultInventory = gson.fromJson(reader, VaultInventoryJson.class);
            System.out.println(region.getName() + ": " + vaultInventory.getVaultARN());

            int reqNum=0;
            for (ArchiveJson archiveJson : vaultInventory.getArchiveList()) {

                DeleteArchiveRequest deleteArchiveRequest = new DeleteArchiveRequest();
                deleteArchiveRequest.setArchiveId(archiveJson.getArchiveId());
                deleteArchiveRequest.setVaultName(vaultDesc.getVaultName());
                DeleteArchiveResult deleteArchiveResult = glacier.deleteArchive(deleteArchiveRequest);
                deleteArchiveResult.getSdkHttpMetadata().getHttpStatusCode();
                ++reqNum;

                if(reqNum % 100 == 0)
                {
                    System.out.format("sent %d delete requests\n", reqNum);
                }
            }

            System.out.format("sent %d delete requests\n", reqNum);
        }
    }

    private static void DownloadVaultInventoriesAllRegions(List<Regions> regions) throws Exception {

        for(Regions region: regions)
            DownloadVaultInventories(BuildGlacierClient(region), region);
    }

    private static void DownloadVaultInventories(AmazonGlacier glacier, Regions region) throws Exception {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        SaveInitJobResult prevVaultResults = getSaveInitJobResult(region, gson);

        int vaultsNum = prevVaultResults.getListVaultsResult().getVaultList().size();

        for(int vaultIndex=0; vaultIndex<vaultsNum; ++vaultIndex) {

            DescribeVaultOutput vaultDesc = prevVaultResults.getListVaultsResult().getVaultList().get(vaultIndex);
            InitiateJobResult jobResult = prevVaultResults.getInitiateJobResult().get(vaultIndex);

            GetJobOutputRequest getJobOutputRequest = new GetJobOutputRequest();
            getJobOutputRequest.setVaultName(vaultDesc.getVaultName());
            getJobOutputRequest.setJobId(jobResult.getJobId());

            GetJobOutputResult jobOutput = glacier.getJobOutput(getJobOutputRequest);
            InputStream bodyStream = jobOutput.getBody();

            JsonParser parser = new JsonParser();
            JsonObject json = parser.parse(new InputStreamReader(bodyStream)).getAsJsonObject();
            PrintWriter writer = new PrintWriter(region.getName() + "." + vaultDesc.getVaultName() +".txt", "UTF-8");
            writer.write(gson.toJson(json));
            writer.close();
        }
    }

    private static SaveInitJobResult getSaveInitJobResult(Regions region, Gson gson) throws FileNotFoundException {
        JsonReader reader = new JsonReader(new FileReader(region.getName() + ".txt"));
        return gson.fromJson(reader, SaveInitJobResult.class);
    }

    private static void InitiateInventoriesAllRegions(List<Regions> regions)
            throws Exception, UnsupportedEncodingException {

        for(Regions region: regions)
            InitiateInventoriesAllVaults(BuildGlacierClient(region), region);
    }

    private static AmazonGlacierAsync BuildGlacierClient(Regions region) {
        AmazonGlacierAsyncClientBuilder amazonGlacierClientBuilder =
                AmazonGlacierAsyncClientBuilder.standard()
                        .withRegion(region)
                        .withCredentials(new ProfileCredentialsProvider());
        return amazonGlacierClientBuilder.build();
    }

    private static void InitiateInventoriesAllVaults(AmazonGlacier glacier, Regions region) throws Exception, UnsupportedEncodingException {

        ListVaultsResult listVaultsResult = glacier.listVaults(new ListVaultsRequest());

        System.out.println(String.join("\n", listVaultsResult.getVaultList().stream().map(Main::getDescribeVaultOutputStringFunction).collect(Collectors.toList())));

        JobParameters inventoryJobParameters = new JobParameters();
        inventoryJobParameters.setType("inventory-retrieval");

        ArrayList<InitiateJobResult> initiateJobResults = new ArrayList<InitiateJobResult>();
        for(DescribeVaultOutput vault: listVaultsResult.getVaultList())
        {
            initiateJobResults.add(glacier.initiateJob(new InitiateJobRequest(vault.getVaultName(), inventoryJobParameters)));
        }

        SaveInitJobResult saveInitJobResult = new SaveInitJobResult();
        saveInitJobResult.setListVaultsResult(listVaultsResult);
        saveInitJobResult.setInitiateJobResult(initiateJobResults);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        PrintWriter writer = new PrintWriter(region.getName() + ".txt", "UTF-8");
        writer.write(gson.toJson(saveInitJobResult));
        writer.close();
    }

    private static String getDescribeVaultOutputStringFunction(DescribeVaultOutput d) {
        return String.format(
                "ARN:%s name:%s size:%d numArchives:%d",
                d.getVaultARN(),
                d.getVaultName(),
                d.getSizeInBytes(),
                d.getNumberOfArchives());
    }

}
