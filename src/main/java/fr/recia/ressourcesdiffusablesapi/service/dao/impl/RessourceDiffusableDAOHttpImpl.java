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
import fr.recia.ressourcesdiffusablesapi.service.dao.exceptions.RessourceDiffusableDAOException;
import fr.recia.ressourcesdiffusablesapi.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
    public String getRessourceDiffusableRawJsonString() throws RessourceDiffusableDAOException {
//       log.warn("will try download");
//        String downloadFileDirectoryPathString = garProperties.getDownloadLocationPath();
//        File downloadFileDirectory = new File(downloadFileDirectoryPathString);
//        if(!downloadFileDirectory.exists()){
//            log.warn("ici");
//            try {
//                Files.createDirectories(downloadFileDirectory.toPath());
//            } catch (IOException e) {
//                log.error("error ",e);
//                throw new RuntimeException(e);
//            }
//        }else {
//            log.warn("la");
//
//            if(!downloadFileDirectory.isDirectory()){
//                log.warn("aie");
//                throw new IOException(String.format("Invalid download directory path: %s is not a directory", downloadFileDirectoryPathString));
//            }
//        }
//        log.warn("here");
//
//        Path downloadFilePath =  Paths.get(downloadFileDirectoryPathString, garProperties.getDownloadFileName());
//        log.debug("combined download file path is: {}", downloadFilePath);
//
//        URI uri = null;
//        try {
//            uri = new URI(downloadFilePath.toString());
//        } catch (URISyntaxException e) {
//            log.warn("throwing");
//            throw new RuntimeException(e);
//        }
        String responseBody ="";
        try {
            log.info("GAR RessourcesDiffusables URI is {}", garProperties.getRessourcesDiffusablesUri());
            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.setContentType(Utils.APPLICATION_JSON_UTF8);
            HttpEntity<Map<String, List<String>>> requestEntity = new HttpEntity<Map<String, List<String>>>(requestHeaders);

            ResponseEntity<String> response = restTemplate.exchange(garProperties.getRessourcesDiffusablesUri(), HttpMethod.GET, requestEntity, String.class);
            responseBody = Objects.requireNonNull(response.getBody());
        } catch (RestClientException | NullPointerException  e) {
            throw new RessourceDiffusableDAOException(e);
        }


        return responseBody;

//        log.warn(String.valueOf(responseBody.length()));
//
//        FileOutputStream outputStream = new FileOutputStream(uri.getPath());
//        byte[] strToBytes = responseBody.getBytes();
//        outputStream.write(strToBytes);
//
//        outputStream.close();
    }
}
