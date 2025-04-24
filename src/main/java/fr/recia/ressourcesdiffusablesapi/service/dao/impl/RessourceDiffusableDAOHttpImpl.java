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
package fr.recia.ressourcesdiffusablesapi.service.dao.impl;

import fr.recia.ressourcesdiffusablesapi.config.AppProperties;
import fr.recia.ressourcesdiffusablesapi.config.beans.GARProperties;
import fr.recia.ressourcesdiffusablesapi.service.dao.RessourceDiffusableDAOAbstractImpl;
import fr.recia.ressourcesdiffusablesapi.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Resource
public class RessourceDiffusableDAOHttpImpl extends RessourceDiffusableDAOAbstractImpl {

    private final GARProperties garProperties;

    @Autowired
    private RestTemplate restTemplate;

    public RessourceDiffusableDAOHttpImpl(AppProperties appProperties){
        this.garProperties = appProperties.getGar();
        log.info("Created Http DAO");
    }


    //        Path downloadFilePath =  Paths.get(downloadFileDirectoryPathString, garProperties.getDowloadFileName());
    //           return new URI(downloadFilePath.toString());
    @Override
    public void refreshRessourceDiffusableFile() throws IOException {
log.warn("will try download");



        String downloadFileDirectoryPathString = garProperties.getDownloadLocationPath();
        File downloadFileDirectory = new File(downloadFileDirectoryPathString);



        if(!downloadFileDirectory.exists()){
            log.warn("ici");
            try {
                Files.createDirectories(downloadFileDirectory.toPath());
            } catch (IOException e) {
                log.error("error ",e);
                throw new RuntimeException(e);
            }
        }else {
            log.warn("la");

            if(!downloadFileDirectory.isDirectory()){
                log.warn("aie");
                throw new IOException(String.format("Invalid download directory path: %s is not a directory", downloadFileDirectoryPathString));
            }
        }
        log.warn("here");

        Path downloadFilePath =  Paths.get(downloadFileDirectoryPathString, garProperties.getDownloadFileName());
        log.debug("combined download file path is: {}", downloadFilePath);




        URI uri = null;
        try {
            uri = new URI(downloadFilePath.toString());
        } catch (URISyntaxException e) {
            log.warn("throwing");
            throw new RuntimeException(e);
        }



        String responseBody ="";
        try {
            log.warn("uri is {}", garProperties.getRessourcesDiffusablesUri());
            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.setContentType(Utils.APPLICATION_JSON_UTF8);
            HttpEntity<Map<String, List<String>>> requestEntity = new HttpEntity<Map<String, List<String>>>(requestHeaders);

            ResponseEntity<String> response = restTemplate.exchange(garProperties.getRessourcesDiffusablesUri(), HttpMethod.GET, requestEntity, String.class);
            responseBody = Objects.requireNonNull(response.getBody());
            log.warn("test successful");
        } catch (RestClientException e) {
            log.error("test ",  e);
            throw new RuntimeException(e);
        } catch (RuntimeException e) {
            log.error("test runtime ex: ", e);

            throw new RuntimeException(e);
        }


        FileOutputStream outputStream = new FileOutputStream(uri.getPath());
        byte[] strToBytes = responseBody.getBytes();
        outputStream.write(strToBytes);

        outputStream.close();





        if(1+1 == 2){
            return;
        }

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
                throw new IOException("Could not create cache folder for download, file download cancelled");
            }
        }
        try {
            String contextURL = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();

            boolean isTargetUrlRelative = garProperties.getRessourcesDiffusablesUri().startsWith("/");
            URL finalTargetUrl;
            if(isTargetUrlRelative){

                Pattern pattern =  Pattern.compile("https?://([a-zA-Z0-9-.]+)");
                Matcher matcher = pattern.matcher(contextURL);
                boolean foundHostname = matcher.find();
                URL baseURL = new URL(matcher.group(0));
                finalTargetUrl = new URL(baseURL, garProperties.getRessourcesDiffusablesUri());
            }
            else{
                 finalTargetUrl = new URL(garProperties.getRessourcesDiffusablesUri());
            }

            log.info("Downloading json file from '{}' to '{}' ", finalTargetUrl, tempJsonFile.getPath());
            final FileOutputStream fileOutputStream = new FileOutputStream(tempJsonFile.getPath());
            try (fileOutputStream) {
                fileOutputStream.getChannel()
                        .transferFrom(
                                Channels.newChannel(finalTargetUrl.openStream()),
                                0,
                                Long.MAX_VALUE
                        );
            }catch (IOException ioException) {
                log.error("Ressources diffusables source file download: IOException, "+ ioException.getMessage());
                throw  ioException;
            }


        }  catch (MalformedURLException malformedURLException) {
            log.error("Ressources diffusables source file download: malformed URL exception, ", malformedURLException);
            throw malformedURLException;

        }
        catch(FileNotFoundException fileNotFoundException){

        }

        //update download date
        try {
            Files.move(Paths.get(tempJsonFile.getPath()), Paths.get(jsonFile.getPath()), StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ioException) {
            log.error("Ressources diffusables source file download: moving temp file into cached file IOException, "+ ioException.getMessage());
            throw ioException;
        }



        // Fin de téléchargement
        log.info("Ressources diffusables source file download: ressources diffusables source file successfully downloaded.");
    }


    @Override
    protected URI getFileLocalURI() {
        try {
            return new URI(garProperties.getDownloadLocationPath()+"/"+garProperties.getDownloadFileName());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }


}
