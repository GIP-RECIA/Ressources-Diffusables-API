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
import fr.recia.ressourcesdiffusablesapi.config.beans.CacheProperties;
import fr.recia.ressourcesdiffusablesapi.model.RessourceDiffusable;
import fr.recia.ressourcesdiffusablesapi.service.cache.impl.CacheServiceJsonImpl;
import fr.recia.ressourcesdiffusablesapi.service.cache.io.ICacheFileIO;
import fr.recia.ressourcesdiffusablesapi.service.dao.IRessourceDiffusableDAO;
import fr.recia.ressourcesdiffusablesapi.service.dao.exceptions.RessourceDiffusableDAOException;
import fr.recia.ressourcesdiffusablesapi.service.parser.IRessourceDiffusableParserService;
import fr.recia.ressourcesdiffusablesapi.test.utils.RandomlyGeneratedRessourceDiffusablesForMock;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.io.IOException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@Slf4j
@SpringBootTest
@ActiveProfiles({ "test" })
@SpringJUnitConfig
@DirtiesContext(classMode= DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class CacheServiceJsonImplTest {

    @Autowired
    private ApplicationContext ctx;

    @MockBean
    private Clock clock;

    @MockBean
    private IRessourceDiffusableDAO ressourceDiffusableDAO;

    @MockBean
    private ICacheFileIO cacheFileIO;

    @MockBean
    private IRessourceDiffusableParserService ressourceDiffusableParserService;

    @SpyBean
    private AppProperties appProperties;

    @MockBean
    CacheProperties cacheProperties;



    @Autowired
    CacheServiceJsonImpl serviceCacheJsonImpl;

    @Test
    public void dao_data_fetch_invoked_if_duration_expired() throws IOException, RessourceDiffusableDAOException {
        Clock fixedClockInit = Clock.fixed(LocalDateTime.now().toInstant(ZoneOffset.ofHours(0)), ZoneId.systemDefault());
        Clock fixedClockAfter2Days = Clock.fixed(LocalDateTime.now().plusDays(2).toInstant(ZoneOffset.ofHours(0)), ZoneId.systemDefault());
        List<RessourceDiffusable> listToReturn = RandomlyGeneratedRessourceDiffusablesForMock.get(4);
        String daoResponse = "daoResponse";

        doReturn(fixedClockInit.instant()).when(clock).instant();
        doReturn(fixedClockInit.getZone()).when(clock).getZone();

        // force a 24 hours duration before cache expiration
        doReturn((long) (60 * 60 * 24)).when(cacheProperties).getCacheLifetimeInSeconds();
        doReturn(daoResponse).when(ressourceDiffusableDAO).getRessourceDiffusableRawJsonString();
        doReturn(listToReturn).when(ressourceDiffusableParserService).parseRawJsonStringIntoRessourceDiffusableList(daoResponse);
        doReturn(cacheProperties).when(appProperties).getCache();

        // first call
        List<RessourceDiffusable> result = serviceCacheJsonImpl.getRessourceDiffusableList();
        // also test if the list returned by the tested service matches the list returned by the mocked parser
        assertEquals(listToReturn, result);

        // use a clock who simulate a date greater than the expiration date
        doReturn(fixedClockAfter2Days.instant()).when(clock).instant();
        doReturn(fixedClockAfter2Days.getZone()).when(clock).getZone();

        //second call
        List<RessourceDiffusable> result_2 = serviceCacheJsonImpl.getRessourceDiffusableList();
        // also test if the list returned by the tested service matches the list returned by the mocked parser
        assertEquals(listToReturn, result_2);

        // first call always invoke DAO, so target call is 2
        verify(ressourceDiffusableDAO, times(2)).getRessourceDiffusableRawJsonString();
    }

    @Test
    public void dao_data_fetch_not_invoked_if_duration_still_valid() throws IOException, RessourceDiffusableDAOException {
        Clock fixedClockInit = Clock.fixed(LocalDateTime.now().toInstant(ZoneOffset.ofHours(0)), ZoneId.systemDefault());
        Clock fixedClockAfter2Hours = Clock.fixed(LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.ofHours(0)), ZoneId.systemDefault());
        List<RessourceDiffusable> listToReturn = RandomlyGeneratedRessourceDiffusablesForMock.get(7);
        String daoResponse = "daoResponse";
        doReturn(fixedClockInit.instant()).when(clock).instant();
        doReturn(fixedClockInit.getZone()).when(clock).getZone();
        doReturn((long) (60 * 60 * 24)).when(cacheProperties).getCacheLifetimeInSeconds();
        doReturn(cacheProperties).when(appProperties).getCache();
        doReturn(daoResponse).when(ressourceDiffusableDAO).getRessourceDiffusableRawJsonString();
        doReturn(listToReturn).when(ressourceDiffusableParserService).parseRawJsonStringIntoRessourceDiffusableList(daoResponse);


        // first call
        List<RessourceDiffusable> result = serviceCacheJsonImpl.getRessourceDiffusableList();
        // also test if the list returned by the tested service matches the list returned by the mocked parser
        assertEquals(listToReturn, result);

        doReturn(fixedClockAfter2Hours.instant()).when(clock).instant();
        doReturn(fixedClockAfter2Hours.getZone()).when(clock).getZone();

        //second call
        List<RessourceDiffusable> result_2 = serviceCacheJsonImpl.getRessourceDiffusableList();
        // also test if the list returned by the tested service matches the list returned by the mocked parser
        assertEquals(listToReturn, result_2);

        // first call always invoke DAO, so target call is 1
        verify(ressourceDiffusableDAO, times(1)).getRessourceDiffusableRawJsonString();
    }

    @Test
    public void get_data_from_existing_file_if_dao_throw_exception() throws IOException, RessourceDiffusableDAOException {
        Clock fixedClockInit = Clock.fixed(LocalDateTime.now().toInstant(ZoneOffset.ofHours(0)), ZoneId.systemDefault());
        List<RessourceDiffusable> listToReturn = RandomlyGeneratedRessourceDiffusablesForMock.get(9);
        String rawJsonMockValue = "{\"json\":\"mock\"}";

        doReturn(fixedClockInit.instant()).when(clock).instant();
        doReturn(fixedClockInit.getZone()).when(clock).getZone();

        doReturn((long) (60 * 60 * 24)).when(cacheProperties).getCacheLifetimeInSeconds();
        doReturn(cacheProperties).when(appProperties).getCache();
        doThrow(RessourceDiffusableDAOException.class).when(ressourceDiffusableDAO).getRessourceDiffusableRawJsonString();

        doReturn(rawJsonMockValue).when(cacheFileIO).getRawJsonFromCacheFile();

        doReturn(listToReturn).when(ressourceDiffusableParserService).parseRawJsonStringIntoRessourceDiffusableList(rawJsonMockValue);

        List<RessourceDiffusable> result = serviceCacheJsonImpl.getRessourceDiffusableList();
        assertEquals(listToReturn, result);
        verify(cacheFileIO, times(1)).getRawJsonFromCacheFile();
    }

    @Test
    public void get_data_from_list_if_no_existing_file_and_dao_throw_exception() throws IOException, RessourceDiffusableDAOException {
        Clock fixedClockInit = Clock.fixed(LocalDateTime.now().toInstant(ZoneOffset.ofHours(0)), ZoneId.systemDefault());
        Clock fixedClockAfter2Days = Clock.fixed(LocalDateTime.now().plusDays(2).toInstant(ZoneOffset.ofHours(0)), ZoneId.systemDefault());
        String rawJsonMockValue = "{\"json\":\"mock\"}";
        List<RessourceDiffusable> listToReturn = RandomlyGeneratedRessourceDiffusablesForMock.get(3);

        doReturn(fixedClockInit.instant()).when(clock).instant();
        doReturn(fixedClockInit.getZone()).when(clock).getZone();
        doReturn((long) (60 * 60 * 24)).when(cacheProperties).getCacheLifetimeInSeconds();
        doReturn(cacheProperties).when(appProperties).getCache();
        doReturn(rawJsonMockValue).when(ressourceDiffusableDAO).getRessourceDiffusableRawJsonString();
        doReturn(listToReturn).when(ressourceDiffusableParserService).parseRawJsonStringIntoRessourceDiffusableList(rawJsonMockValue);
        doNothing().when(cacheFileIO).writeRawJsonToCacheFile(rawJsonMockValue);

        List<RessourceDiffusable> result = serviceCacheJsonImpl.getRessourceDiffusableList();
        assertEquals(listToReturn, result);

        // change mocks to throw exceptions instead
        doReturn(fixedClockAfter2Days.instant()).when(clock).instant();
        doReturn(fixedClockAfter2Days.getZone()).when(clock).getZone();
        doThrow(RessourceDiffusableDAOException.class).when(ressourceDiffusableDAO).getRessourceDiffusableRawJsonString();
        doThrow(IOException.class).when(cacheFileIO).getRawJsonFromCacheFile();

        List<RessourceDiffusable> result2 = serviceCacheJsonImpl.getRessourceDiffusableList();
        assertEquals(listToReturn, result2);
        verify(cacheFileIO, times(1)).getRawJsonFromCacheFile();
    }


    @Test
    public void write_cache_file_fail_does_not_KO() throws RessourceDiffusableDAOException, IOException {
        Clock fixedClockInit = Clock.fixed(LocalDateTime.now().toInstant(ZoneOffset.ofHours(0)), ZoneId.systemDefault());
        String rawJsonMockValue = "{\"json\":\"mock\"}";
        List<RessourceDiffusable> listToReturn = RandomlyGeneratedRessourceDiffusablesForMock.get(3);

        doReturn(fixedClockInit.instant()).when(clock).instant();
        doReturn(fixedClockInit.getZone()).when(clock).getZone();
        doReturn((long) (60 * 60 * 24)).when(cacheProperties).getCacheLifetimeInSeconds();
        doReturn(cacheProperties).when(appProperties).getCache();
        doReturn(rawJsonMockValue).when(ressourceDiffusableDAO).getRessourceDiffusableRawJsonString();
        doReturn(listToReturn).when(ressourceDiffusableParserService).parseRawJsonStringIntoRessourceDiffusableList(rawJsonMockValue);
        doThrow(IOException.class).when(cacheFileIO).writeRawJsonToCacheFile(rawJsonMockValue);

        assertDoesNotThrow(() -> {
            serviceCacheJsonImpl.getRessourceDiffusableList();
        });
        verify(cacheFileIO, times(1)).writeRawJsonToCacheFile(rawJsonMockValue);
    }

    @Test public void throw_exception_when_no_response_on_first_request_after_startup() throws IOException, RessourceDiffusableDAOException {
        Clock fixedClockInit = Clock.fixed(LocalDateTime.now().toInstant(ZoneOffset.ofHours(0)), ZoneId.systemDefault());
        doReturn(fixedClockInit.instant()).when(clock).instant();
        doReturn(fixedClockInit.getZone()).when(clock).getZone();
        doNothing().when(cacheFileIO).writeRawJsonToCacheFile(any());
        doThrow(RessourceDiffusableDAOException.class).when(ressourceDiffusableDAO).getRessourceDiffusableRawJsonString();
        doThrow(IOException.class).when(cacheFileIO).getRawJsonFromCacheFile();
        assertThrows(RuntimeException.class,()-> serviceCacheJsonImpl.getRessourceDiffusableList());
    }
}
