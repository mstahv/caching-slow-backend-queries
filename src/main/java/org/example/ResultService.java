package org.example;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Collections;
import java.util.List;

@Service
public class ResultService {

    private CacheManager cacheManager;
    private Cache<Integer, List> aggregateEntriesCache;

    @PostConstruct
    void init() {
        cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
                .withCache("aggregateEntries",
                        CacheConfigurationBuilder.newCacheConfigurationBuilder(
                                    Integer.class, // key in the cache
                                    List.class, // value in the cache
                                    ResourcePoolsBuilder.heap(100) // configuration for the cache,
                                    // here use JVM memory, max 100 items in the cache
                                )
                                .build())
                .build(true);

        aggregateEntriesCache = cacheManager.getCache("aggregateEntries", Integer.class, List.class);

    }

    @PreDestroy
    void destroy() {
        // in spring boot apps this is only relevant if using
        // disk for a large cache
        cacheManager.close();
    }

    public List<VeryComplexEntryDto> aggregateEntries(int competitionId, String competitionClassNameFilter, Long clubIdFilter) {
         List<VeryComplexEntryDto> list = aggregateEntriesCache.get(competitionId);
         if(list == null) {
            list = executeSlowBackendLogicWithMultipleDatabaseQueries();
            aggregateEntriesCache.put(competitionId, list);
         }
         if(clubIdFilter != null) {
             /*
              * TODO do filtering if filters present here in JVM memory
              * Depending on usage patterns, it may be better
              * to do actual backend queries instead of cache also
              * filtered requests (then a build compound key)
              */
         }
         return list;
    }

    private List<VeryComplexEntryDto> executeSlowBackendLogicWithMultipleDatabaseQueries() {
        // simulate slow backend query, making multiple loops, possibly
        // different databased. Numbers from a real world example
        simulatedOneDBQuery();
        for (int i = 0; i < 4; i++) {
            // for each class make a query
            simulatedOneDBQuery();
            for (int j = 0; j < 50; j++) {
                // for each entry make a query
                simulatedOneDBQuery();
            }
        }
        return Collections.emptyList();
    }

    private void simulatedOneDBQuery() {
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateEntry(int competitionId, String newValue) {
        //  do the actual db query...
        simulatedOneDBQuery();

        // manually invalidate cache
        aggregateEntriesCache.remove(competitionId);

    }

}
