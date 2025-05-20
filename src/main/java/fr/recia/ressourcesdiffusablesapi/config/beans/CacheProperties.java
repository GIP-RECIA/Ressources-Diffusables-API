package fr.recia.ressourcesdiffusablesapi.config.beans;

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

import com.fasterxml.jackson.databind.util.ISO8601Utils;
import fr.recia.ressourcesdiffusablesapi.utils.Utils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;

@Data
@Slf4j
public class CacheProperties {



    private long cacheLifetimeInSeconds = 0;
    private String cacheLocationPath;
    private String cacheFileName;

    @PostConstruct
    private void init(){
        cacheLocationPath = Utils.emptyIfNull(cacheLocationPath);
        if(cacheLocationPath.isEmpty())
            log.warn("No value for cacheLocationPath property provided");
        cacheFileName = Utils.emptyIfNull(cacheFileName);
        if(cacheFileName.isEmpty())
            log.warn("No value for cacheFileName property provided");
    }

    @Override
    public String toString() {
        return "\"GARProperties\": {" +
                "\n\t\"cacheLifetimeInSeconds\": \"" + cacheLifetimeInSeconds + "\"" +
                "\n\t\"cacheLocationPath\": \"" + Utils.emptyIfNull(cacheLocationPath) + "\"" +
                "\n\t\"cacheFileName\": \"" + Utils.emptyIfNull(cacheFileName) + "\"" +
                "\n}";
    }

}
