/*
 * Copyright (C) 2021 GIP-RECIA, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *                 http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.recia.ressourcesdiffusablesapi.service.cache;

import fr.recia.ressourcesdiffusablesapi.config.AppProperties;
import fr.recia.ressourcesdiffusablesapi.config.beans.GARProperties;
import fr.recia.ressourcesdiffusablesapi.model.RessourceDiffusable;
import fr.recia.ressourcesdiffusablesapi.service.jsonparser.ServiceGarJsonParser;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;

import static java.time.temporal.ChronoUnit.SECONDS;

@Slf4j
public class ServiceCacheJson implements  ServiceCache{

    private final GARProperties garProperties;

    private final ServiceGarJsonParser serviceGarJsonParser;

    private File cachedJsonFile;

    private LocalDateTime downloadLDT;

    private List<RessourceDiffusable> allRessourcesDiffusables;

    public ServiceCacheJson(AppProperties appProperties, ServiceGarJsonParser serviceGarJsonParser) {
        this.garProperties = appProperties.getGar();
        this.serviceGarJsonParser = serviceGarJsonParser;
        cachedJsonFile = new File(this.garProperties.getDownloadLocationPath());
    }


    boolean isFileValid(){
        return hasCachedJsonFile() && hasFileNotExpired();
    }

    @Override
    public List<RessourceDiffusable> getAllRessourcesDiffusables() {

        //check if file exist and is not considered as expired
        if(!isFileValid()){
            refreshFile();
        }

        return allRessourcesDiffusables;
    }


    private void refreshFile(){
       boolean downloadSuccessful = downloadJsonFileIntoCacheFolder();
       if(!downloadSuccessful){
           if(hasCachedJsonFile()){
               log.warn("Could not download json file. Will try to parse previously downloaded json file.");
           }else {
               log.error("Could not download json file. Can't respond to respond to client request.");
           }
       }
       parseCachedJsonFile();
    }


    private boolean hasCachedJsonFile(){
        if(cachedJsonFile == null)
            return false;
        return cachedJsonFile.exists();
    }

    private boolean hasFileNotExpired(){
        return  this.downloadLDT != null && SECONDS.between(this.downloadLDT, LocalDateTime.now()) <= garProperties.getCacheDuration();
    }

    private boolean downloadJsonFileIntoCacheFolder() {
        String downloadFileLocationFull = garProperties.getDownloadLocationPath();
        String tempDownloadFileLocationFull = downloadFileLocationFull+ ".temp";
        File jsonFile = new File(downloadFileLocationFull);
        File tempJsonFile = new File(tempDownloadFileLocationFull);

        boolean downloadFolderFileExist = jsonFile.getParentFile().exists();
        if(!downloadFolderFileExist){
            boolean createdDownloadFolderFile = jsonFile.getParentFile().mkdirs();
            if(createdDownloadFolderFile){
                log.info("Created folder "+jsonFile.getParentFile().getPath() + " ");
            }else {
                log.error("Could not create cache folder for download, canceling download.");
                return false;
            }

        }

        // Début du téléchargement.
        log.info("Ressources diffusables source file download: Starting download procedure");

        // Téléchargement du fichier.
        try {
            String contextURL = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();

            //temp code, to remove when json will be fetchable with the normal WS
            boolean isLocalServer = contextURL.contains("localhost");
            boolean isTargetUrlRelative = garProperties.getRessourcesDiffusablesUri().startsWith("/");

            if(isTargetUrlRelative){
                if(contextURL.contains("://")){
                    //remove the htpp*:// prefix
                    contextURL = contextURL.split("://")[1];
                }
                String domainURL = contextURL.split("/")[0];
                //make sure that prefix is httpS, bc some time (in test env at least) it use http
                String prefix = "https://";
                String combinedDomainURL = prefix + domainURL;
                URL baseURL = new URL(combinedDomainURL);
                URL mergedURL = new URL(baseURL, garProperties.getRessourcesDiffusablesUri());
                log.info("Downloading JSON file from : "+mergedURL);
                log.info("Downloading JSON file to : "+tempJsonFile.getPath());
                new FileOutputStream(tempJsonFile.getPath())
                        .getChannel()
                        .transferFrom(
                                Channels.newChannel(mergedURL.openStream()),
                                0,
                                Long.MAX_VALUE
                        );

            }
            else{
                URL mergedURL = new URL(garProperties.getRessourcesDiffusablesUri());
                log.info("Downloading JSON file from : "+mergedURL);
                log.info("Downloading JSON file to : "+tempJsonFile.getPath());
                new FileOutputStream(tempJsonFile.getPath())
                        .getChannel()
                        .transferFrom(
                                Channels.newChannel(mergedURL.openStream()),
                                0,
                                Long.MAX_VALUE
                        );
            }

        }  catch (MalformedURLException malformedURLException) {
            log.error("Ressources diffusables source file download: malformed URL exception, ", malformedURLException);
            return false;

        } catch (IOException e) {
            log.error("Ressources diffusables source file download: IOException, "+ e.getMessage());
            return false;
        }

        //update download date
        this.downloadLDT = LocalDateTime.now();

        try {
            Files.move(Paths.get(tempJsonFile.getPath()), Paths.get(jsonFile.getPath()), StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log.error("Ressources diffusables source file download: moving temp file into cached file IOException, "+ e.getMessage());
            throw new RuntimeException(e);
        }

        // Fin de téléchargement
        log.info("Ressources diffusables source file download: ressources diffusables source file successfully downloaded.");
        return true;
    }

    void parseCachedJsonFile()  {
        try {
            allRessourcesDiffusables = serviceGarJsonParser.parseJsonIntoRessourceDiffusableList(cachedJsonFile);
        } catch (IOException e) {
            log.error("Parsing of ressources diffusables source jsonFile: Could not parse jsonFile, "+e.getMessage());
            throw new RuntimeException(e);
        }
    }





}
