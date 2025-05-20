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
package fr.recia.ressourcesdiffusablesapi.service.cache.io.impl;

import fr.recia.ressourcesdiffusablesapi.config.AppProperties;
import fr.recia.ressourcesdiffusablesapi.service.cache.io.ICacheFileIO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
public class CacheFileIO implements ICacheFileIO {

    private final AppProperties appProperties;

    public CacheFileIO(AppProperties appProperties){
        this.appProperties = appProperties;
    }

    @Override
    public void writeRawJsonToCacheFile(String rawValue) throws IOException {
        try(FileOutputStream outputStream = new FileOutputStream(appProperties.getCache().getCacheLocationPath() + "/"+appProperties.getCache().getCacheFileName())){
            byte[] strToBytes = rawValue.getBytes();
            outputStream.write(strToBytes);
        }
    }

    @Override
    public String getRawJsonFromCacheFile() throws IOException {
        String rawValue = Files.readString(Path.of(appProperties.getCache().getCacheLocationPath()+"/"+appProperties.getCache().getCacheFileName()));
        log.debug(rawValue.substring(0,40));
        return  rawValue;
    }

}
